package br.com.zup.dataaccess.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.zup.dataaccess.DDLDataService;
import br.com.zup.model.DefinedEntity;
import br.com.zup.model.DefinedEntityField;
import br.com.zup.model.DefinedEntityFieldType;
import br.com.zup.util.Constantes;

@Repository
public class DDLDataServiceImpl implements DDLDataService {
	private static final Logger log = LoggerFactory.getLogger(DDLDataServiceImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	private void appendField(DefinedEntityField definedEntityField, StringBuilder sb) {
		sb.append(definedEntityField.getPhysicalName());
		switch (definedEntityField.getType()) {
		case NUMBER:
			if (definedEntityField.isFieldIdentity()) sb.append(" SERIAL");
			else sb.append(" Numeric");
			break;
		case STRING:
			sb.append(" VARCHAR(100)");
			break;
		case TEXT:
			sb.append(" VARCHAR(1000)");
			break;
		default:
			break;
		}
	}

	@Override
	public void createDefinedEntity(DefinedEntity definedEntity) {
		log.info("Creating tables");

		StringBuilder sb = new StringBuilder("CREATE TABLE ").append(definedEntity.getPhysicalName()).append(" (");

		String fieldId = null;
		boolean fieldIdIdentity = false;
		for (DefinedEntityField definedEntityField : definedEntity.getFields()) {
			appendField(definedEntityField, sb);

			if (definedEntityField.isFieldId()) {
				if (fieldId != null) {
					throw new RuntimeException("A entidade pode ter apenas um campo como PK");
				}
				fieldId = definedEntityField.getPhysicalName();
				if (definedEntityField.isFieldIdentity()
						&& definedEntityField.getType().equals(DefinedEntityFieldType.NUMBER)) {
					fieldIdIdentity = true;
					//sb.append(" IDENTITY ");
				}
			}
			sb.append(",");
		}

		sb.append(Constantes.ACTIVE_FIELD).append(" char(1)");

		if (fieldId != null && !fieldIdIdentity) {
			sb.append(", PRIMARY KEY (").append(fieldId).append(")");
		}

		sb.append(")");
		log.info(sb.toString());
		jdbcTemplate.execute(sb.toString());
	}
}
