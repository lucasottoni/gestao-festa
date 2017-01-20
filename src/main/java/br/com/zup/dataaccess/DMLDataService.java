package br.com.zup.dataaccess;

import java.util.List;
import java.util.Map;

import br.com.zup.model.DefinedEntity;
import br.com.zup.model.DefinedEntityField;


public interface DMLDataService {
    List<Map<String, String>> getAll(DefinedEntity definedEntity);

    void addEntityRow(DefinedEntity definedEntity, Map<DefinedEntityField, String> typedFieldMap);

    Map<String,String> findById(DefinedEntity definedEntity, String id);

    void deleteById(DefinedEntity definedEntity, String id);

    boolean exists(DefinedEntity definedEntity, String id);

    void updateEntityRow(DefinedEntity definedEntity, String id, Map<DefinedEntityField, String> typedFieldMap);
}
