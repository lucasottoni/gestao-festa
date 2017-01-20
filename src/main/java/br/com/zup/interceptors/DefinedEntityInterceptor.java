package br.com.zup.interceptors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.zup.model.DefinedEntity;
import br.com.zup.services.DDLService;

@Aspect
@Component
public class DefinedEntityInterceptor {

    private final DDLService ddlService;

    @Autowired
    public DefinedEntityInterceptor(DDLService ddlService) {
        this.ddlService = ddlService;
    }

    @AfterReturning("execution(* br.com.zup.dataaccess.DefinedEntityRepository.save(..))")
	public void logServiceAccess(JoinPoint joinPoint) {
		DefinedEntity definedEntity = (DefinedEntity) joinPoint.getArgs()[0];
        ddlService.persistDefinedEntity(definedEntity);
    }

}