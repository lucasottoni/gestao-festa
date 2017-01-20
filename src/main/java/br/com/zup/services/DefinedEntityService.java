package br.com.zup.services;

/**
 * Interface para o service de gerenciamento CRUD das entidades
 * @author Lucas
 *
 */
public interface DefinedEntityService {
	/**
	 * Retorna as entidades cadastradas
	 * @param entityName Nome filtro das entidades
	 * @return
	 */
    String getAllEntities(String entityName);

    void saveEntity(String entityName, String json);

    String getEntity(String entity, String id);

    void removeEntity(String entityName, String json);

    void updateEntity(String entity, String id, String jsonBody);
}
