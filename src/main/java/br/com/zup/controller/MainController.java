package br.com.zup.controller;


import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import br.com.zup.services.DefinedEntityService;


/**
 * Controller principal para as requisicoes REST
 * @author Lucas
 *
 */
@RestController
public class MainController {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private DefinedEntityService definedEntityService;

    protected Session getCurrentSession()  {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Obtem todos os registros da entidade
     * @param entity Nome da entidade (nome amigavel)
     * @return Lista dos registros no formato JSON
     */
    @RequestMapping(value = "/api/{entity}")
    @ResponseBody
    public String getEntities(@PathVariable("entity") String entity){
        return definedEntityService.getAllEntities(entity);
    }

    /**
     * Obtem um registro específico da entidade pela PK
     * @param entity Nome da entidade (nome amigavel)
     * @param id Valor da PK
     * @return Registro no formato JSON
     */
    @RequestMapping(value = "/api/{entity}/{id}")
    @ResponseBody 
    public String getEntity(@PathVariable("entity") String entity,
                                            @PathVariable("id") String id){
        return definedEntityService.getEntity(entity, id);
    }

    /**
     * Exclui um registro específico da entidade pela PK
     * @param entity Nome da entidade (nome amigavel)
     * @param id Valor da PK
     * @return JsonResponse com OK
     */
    @RequestMapping(value = "/api/{entity}/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse removeEntity(@PathVariable("entity") String entity,
                                            @PathVariable("id") String id){
        definedEntityService.removeEntity(entity, id);
        return new JsonResponse("OK","");
    }

    @RequestMapping(value = "/api/{entity}", method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    @ResponseBody 
    public JsonResponse saveEntities(@PathVariable("entity") String entity, HttpServletRequest request) throws IOException {
        String jsonBody = IOUtils.toString(request.getInputStream());
        definedEntityService.saveEntity(entity, jsonBody);
        return new JsonResponse("OK","");
    }

    @RequestMapping(value = "/api/{entity}/{id}", method = RequestMethod.PUT,
            headers = {"Content-type=application/json"})
    @ResponseBody 
    public JsonResponse updateEntity(@PathVariable("entity") String entity,
               @PathVariable("id") String id, HttpServletRequest request) throws IOException {
        String jsonBody = IOUtils.toString(request.getInputStream());
        definedEntityService.updateEntity(entity, id, jsonBody);
        return new JsonResponse("OK","");
    }

}
