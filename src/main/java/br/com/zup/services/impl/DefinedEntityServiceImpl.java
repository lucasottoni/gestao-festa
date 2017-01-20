package br.com.zup.services.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import br.com.zup.dataaccess.DMLDataService;
import br.com.zup.dataaccess.DefinedEntityRepository;
import br.com.zup.model.DefinedEntity;
import br.com.zup.model.DefinedEntityField;
import br.com.zup.services.DefinedEntityService;

@Service
public class DefinedEntityServiceImpl implements DefinedEntityService {
    private static final Logger log = LoggerFactory.getLogger(DefinedEntityServiceImpl.class);

    private final DefinedEntityRepository definedEntityRepository;
    private final DMLDataService dmlDataService;
    private final Gson gson;
    @Autowired
    public DefinedEntityServiceImpl(DefinedEntityRepository definedEntityRepository, DMLDataService dmlDataService) {
        this.definedEntityRepository = definedEntityRepository;
        this.dmlDataService = dmlDataService;
        this.gson = new GsonBuilder().create();
    }
    
    private void checkEntityConsistency(List<DefinedEntity> byFriendlyName, String entityName) {
        if(byFriendlyName==null || byFriendlyName.isEmpty()) 
        	throw new RuntimeException("A entidade com o nome "+entityName+" nao existe no repositorio!");
        if(byFriendlyName.size()>1) 
        	throw new RuntimeException("A entidade "+entityName+" existe mais de uma vez no repositorio (inconsistencia)!");
    }
    
    private Map<DefinedEntityField, String> convertJsonToMapField(String jsonBody, DefinedEntity definedEntity) {
        Map<String,String> fieldMap = new Gson().fromJson(jsonBody, new TypeToken<HashMap<String, String>>(){}.getType());
        Map<DefinedEntityField, String> typedFieldMap = new HashMap<>();

        for (String friendlyFieldName : fieldMap.keySet()) {
            Optional<DefinedEntityField> definedEntityField = definedEntity.getFields().stream().filter(o -> o.getFriendlyName().equals(friendlyFieldName)).findFirst();
            if (definedEntityField.isPresent())
            	typedFieldMap.put(definedEntityField.get(), fieldMap.get(definedEntityField.get().getFriendlyName()));
        }

        return typedFieldMap;
    }

    @Override
    public String getAllEntities(String entityName){
        List<DefinedEntity> byFriendlyName = definedEntityRepository.findByFriendlyName(entityName);
        checkEntityConsistency(byFriendlyName, entityName);

        List<Map<String, String>> all = dmlDataService.getAll(byFriendlyName.get(0));

        return gson.toJson(all);
    }

    @Override
    public void saveEntity(String entityName, String jsonBody) {
        List<DefinedEntity> byFriendlyName = definedEntityRepository.findByFriendlyName(entityName);

        checkEntityConsistency(byFriendlyName, entityName);

        DefinedEntity definedEntity = byFriendlyName.get(0);
        
        Map<DefinedEntityField, String> typedFieldMap = convertJsonToMapField(jsonBody, definedEntity);

        dmlDataService.addEntityRow(definedEntity, typedFieldMap);

        log.info("Inserindo!!!"+jsonBody);
    }

    @Override
    public String getEntity(String entityName, String id) {
        List<DefinedEntity> byFriendlyName = definedEntityRepository.findByFriendlyName(entityName);

        checkEntityConsistency(byFriendlyName, entityName);
        
        Map<String, String> entity = dmlDataService.findById(byFriendlyName.get(0), id);

        return gson.toJson(entity);
    }

    @Override
    public void removeEntity(String entityName, String id) {
        List<DefinedEntity> byFriendlyName = definedEntityRepository.findByFriendlyName(entityName);

        checkEntityConsistency(byFriendlyName, entityName);
        
        dmlDataService.deleteById(byFriendlyName.get(0), id);
    }

    @Override
    public void updateEntity(String entityName, String id, String jsonBody) {
        List<DefinedEntity> byFriendlyName = definedEntityRepository.findByFriendlyName(entityName);

        checkEntityConsistency(byFriendlyName, entityName);
        
        DefinedEntity definedEntity = byFriendlyName.get(0);
        boolean exists = dmlDataService.exists(definedEntity, id);
        if(!exists) 
        	throw new RuntimeException("Entidade "+entityName+" com registro ID="+id+" nao existe!");
        
        Map<DefinedEntityField, String> typedFieldMap = convertJsonToMapField(jsonBody, definedEntity);

        dmlDataService.updateEntityRow(definedEntity, id, typedFieldMap);

        log.info("Atualizando!!!"+jsonBody);
    }
}
