package br.com.zup.dataaccess;

import br.com.zup.model.DefinedEntity;

/**
 * Interface para criação das entidades (modelos)
 * @author Lucas
 *
 */
public interface DDLDataService {
	/**
	 * Cria a entidade (modelo) no sistema
	 * @param definedEntity Definicao da entidade
	 */
    void createDefinedEntity(DefinedEntity definedEntity);
}
