package br.com.zup.dataaccess.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.zup.dataaccess.DMLDataService;
import br.com.zup.model.DefinedEntity;
import br.com.zup.model.DefinedEntityField;
import br.com.zup.model.DefinedEntityFieldType;
import br.com.zup.util.Constantes;

@Repository
public class DMLDataServiceImpl implements DMLDataService {
	private static final String WHERE = " where ";

	private static final Logger log = LoggerFactory.getLogger(DMLDataServiceImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, String>> getAll(DefinedEntity definedEntity) {
		log.info("Querying Table...");

		StringBuilder sb = new StringBuilder("select ");

		int index = 0;
		for (DefinedEntityField definedEntityField : definedEntity.getFields()) {
			sb.append(definedEntityField.getPhysicalName());
			if (index + 1 < definedEntity.getFields().size()) {
				sb.append(",");
			}
			index++;
		}

		sb.append(" from ").append(definedEntity.getPhysicalName()).append(WHERE).append(getActiveCondition());

		log.info(sb.toString());

		return jdbcTemplate.query(sb.toString(), (rs, rowNum) -> {
			Map<String, String> result = new HashMap<>();
			for (DefinedEntityField definedEntityField : definedEntity.getFields()) {
				result.put(definedEntityField.getFriendlyName(), rs.getString(definedEntityField.getPhysicalName()));
			}

			return result;
		});

	}
	
	private boolean requireQuote(DefinedEntityFieldType typeField) {
		return typeField == DefinedEntityFieldType.STRING || typeField == DefinedEntityFieldType.TEXT;		
	}

	@Override
	public void addEntityRow(DefinedEntity definedEntity, Map<DefinedEntityField, String> typedFieldMap) {
		log.info("Creating record...");

		StringBuilder fieldsSb = new StringBuilder("(");
		StringBuilder valuesSb = new StringBuilder("(");

		for (DefinedEntityField definedEntityField : typedFieldMap.keySet()) {
			if (!definedEntityField.isFieldIdentity()) {
				fieldsSb.append(definedEntityField.getPhysicalName());

				if (requireQuote(definedEntityField.getType()))
					valuesSb.append("'");

				valuesSb.append(typedFieldMap.get(definedEntityField));

				if (requireQuote(definedEntityField.getType()))
					valuesSb.append("'");

				fieldsSb.append(",");
				valuesSb.append(",");
			}
		}
		fieldsSb.append(getActiveField()).append(") ");
		valuesSb.append("'S') ");

		StringBuilder insertSb = new StringBuilder("insert into ").append(definedEntity.getPhysicalName())
				.append(fieldsSb).append(" values").append(valuesSb);

		log.info(insertSb.toString());

		if (jdbcTemplate.update(insertSb.toString()) < 0) {
			throw new RuntimeException("Error insert value in " + definedEntity.getPhysicalName() + " : " + insertSb);
		}
	}

	@Override
	public Map<String, String> findById(DefinedEntity definedEntity, String id) {
		log.info("Find By Id ...");

		StringBuilder sb = new StringBuilder("select ");

		int index = 0;
		String fieldId = null;
		for (DefinedEntityField definedEntityField : definedEntity.getFields()) {
			sb.append(definedEntityField.getPhysicalName());
			if (index + 1 < definedEntity.getFields().size()) {
				sb.append(",");
			}
			if (definedEntityField.isFieldId()) {
				fieldId = definedEntityField.getPhysicalName();
			}
			index++;
		}

		if (fieldId == null) {
			throw new RuntimeException(
					"Find By id only available if Entity has a id field. Entity DOES not have a id field!");
		}

		sb.append(" from ").append(definedEntity.getPhysicalName()).append(WHERE).append(fieldId).append(" = ")
				.append(id).append(" and ").append(getActiveCondition());

		log.info(sb.toString());

		List<Map<String, String>> resultList = jdbcTemplate.query(sb.toString(), (rs, rowNum) -> {
			Map<String, String> result = new HashMap<>();
			for (DefinedEntityField definedEntityField : definedEntity.getFields()) {
				result.put(definedEntityField.getFriendlyName(), rs.getString(definedEntityField.getPhysicalName()));
			}

			return result;
		});

		if (resultList != null && resultList.size() > 1) {
			throw new RuntimeException("Expected 1 element but found " + resultList.size());
		}

		return resultList != null && !resultList.isEmpty() ? resultList.get(0) : new HashMap<>();
	}

	@Override
	public void deleteById(DefinedEntity definedEntity, String id) {
		log.info("Deleting by Id...");

		StringBuilder sb = new StringBuilder("update ");

		String fieldId = getFieldId(definedEntity);

		if (fieldId == null) {
			throw new RuntimeException(
					"delete By id only available if Entity has a id field. Entity DOES not have a id field!");
		}

		sb.append(definedEntity.getPhysicalName()).append(" set ").append(getInActiveCondition()).append(WHERE)
				.append(fieldId).append(" = ").append(id);

		log.info(sb.toString());

		if (jdbcTemplate.update(sb.toString()) < 0) {
			throw new RuntimeException("Error logical delete row in " + definedEntity.getPhysicalName() + " : " + sb);
		}
	}

	private String getFieldId(DefinedEntity definedEntity) {
		String fieldId = null;
		for (DefinedEntityField definedEntityField : definedEntity.getFields()) {
			if (definedEntityField.isFieldId()) {
				fieldId = definedEntityField.getPhysicalName();
			}
		}

		if (fieldId == null) {
			throw new RuntimeException("Entity DOES not have a id field!");
		}

		return fieldId;
	}

	@Override
	public boolean exists(DefinedEntity definedEntity, String id) {

		String fieldId = getFieldId(definedEntity);

		log.info("Querying Table...");

		StringBuilder sb = new StringBuilder("SELECT count(*) FROM ");
		sb.append(definedEntity.getPhysicalName()).append(WHERE).append(fieldId).append("=?");

		int count = jdbcTemplate.queryForObject(sb.toString(), new Object[] { new Integer(id) }, Integer.class);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void updateEntityRow(DefinedEntity definedEntity, String id, Map<DefinedEntityField, String> typedFieldMap) {
		log.info("Creating record...");

		StringBuilder updateSb = new StringBuilder("update ").append(definedEntity.getPhysicalName()).append(" set ");

		int index = 0;
		for (DefinedEntityField definedEntityField : typedFieldMap.keySet()) {
			if (!definedEntityField.isFieldId()) {
				updateSb.append(definedEntityField.getPhysicalName()).append("=");

				if (definedEntityField.getType().equals(DefinedEntityFieldType.STRING))
					updateSb.append("'");

				updateSb.append(typedFieldMap.get(definedEntityField));

				if (definedEntityField.getType().equals(DefinedEntityFieldType.STRING))
					updateSb.append("'");

				if (index + 1 < typedFieldMap.keySet().size()) {
					updateSb.append(",");
				}
			}
			index++;
		}

		updateSb.append(WHERE).append(getFieldId(definedEntity)).append("=").append(id);

		log.info(updateSb.toString());

		if (jdbcTemplate.update(updateSb.toString()) < 0) {
			throw new RuntimeException("Error updating value in " + definedEntity.getPhysicalName() + " : " + updateSb);
		}
	}

	private String getActiveCondition() {
		return new StringBuilder(getActiveField()).append("='S'").toString();
	}

	private String getInActiveCondition() {
		return new StringBuilder(getActiveField()).append("='N'").toString();
	}

	private String getActiveField() {
		return Constantes.ACTIVE_FIELD;
	}
}
