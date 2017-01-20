package br.com.zup.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.zup.dataaccess.DDLDataService;
import br.com.zup.model.DefinedEntity;
import br.com.zup.services.DDLService;

@Service
public class DDLServiceImpl implements DDLService {
    private final DDLDataService ddlDataService;

    @Autowired
    public DDLServiceImpl(DDLDataService ddlDataService) {
        this.ddlDataService = ddlDataService;
    }

    @Override
    public void persistDefinedEntity(DefinedEntity definedEntity) {
        ddlDataService.createDefinedEntity(definedEntity);
    }
}
