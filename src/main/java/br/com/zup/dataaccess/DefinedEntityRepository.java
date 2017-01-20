package br.com.zup.dataaccess;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.com.zup.model.DefinedEntity;

/**
 * Interface para repositorio das entidades.
 * Utiliza o framework spring para gerenciamento das entidades
 * @author Lucas
 *
 */
@RepositoryRestResource(collectionResourceRel = "entity", path = "entity")
public interface DefinedEntityRepository extends PagingAndSortingRepository<DefinedEntity, Long> {

	List<DefinedEntity> findByFriendlyName(@Param("name") String name);
}