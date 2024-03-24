package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.FormulaDao;
import com.kalafche.model.formula.Attribute;
import com.kalafche.model.formula.AttributeContext;
import com.kalafche.model.formula.AttributeType;
import com.kalafche.model.formula.Formula;

@Service
public class FormulaDaoImpl extends JdbcDaoSupport implements FormulaDao {

	private static final String INSERT_FORMULA = "INSERT INTO formula (NAME, CREATE_TIMESTAMP, CREATED_BY, EXPRESSION, DESCRIPTION) "
			+ "VALUES (?, ?, ?, ?, ?); ";

	private static final String INSERT_ABSOLUTE_ATTRIBUTE = "INSERT INTO attribute (NAME, TYPE_ID, CONTEXT_ID, FROM_TIMESTAMP, TO_TIMESTAMP, CREATE_TIMESTAMP, CREATED_BY, LAST_UPDATE_TIMESTAMP, UPDATED_BY) "
			+ "VALUES (?, (select id from attribute_type where code = 'ABSOLUTE'), ?, ?, ?, ?, ?, ?, ?); ";
	
	private static final String INSERT_RELATIVE_ATTRIBUTE = "INSERT INTO attribute (NAME, TYPE_ID, CONTEXT_ID, OFFSET, OFFSET_START_DAY, OFFSET_END_DAY, CREATE_TIMESTAMP, CREATED_BY, LAST_UPDATE_TIMESTAMP, UPDATED_BY) "
			+ "VALUES (?, (select id from attribute_type where code = 'RELATIVE'), ?, ?, ?, ?, ?, ?, ?, ?); ";

	private static final String UPDATE_FORMULA = "update formula set name = ?, expression = ?, description = ?, last_update_timestamp = ?, updated_by = ? where id = ?";

	private static final String UPDATE_ATTRIBUTE = "update attribute set name = ?, context_id = ?, offset = ?, offset_start_day = ?, offset_end_day = ?, from_timestamp = ?, to_timestamp = ?, last_update_timestamp = ?, updated_by = ? where id = ?";

	private static final String SELECT_FORMULAS = "select " +
			"f.ID as id, " +
			"f.NAME as name, " +
			"f.CREATE_TIMESTAMP as createTimestamp, " +
			"f.CREATED_BY as createdByEmployeeId, " +
			"e.NAME as createdByEmployeeName, " +
			"f.LAST_UPDATE_TIMESTAMP as lastUpdateTimestamp, " +
			"f.UPDATED_BY as updatedByEmployeeId, " +
			"e1.NAME as updatedByEmployeeName, " +
			"f.EXPRESSION as expression, " +
			"f.DESCRIPTION as description " +
			"from formula f " +
			"join employee e on f.CREATED_BY = e.ID " +
			"left join employee e1 on f.UPDATED_BY = e1.ID ";
	
	private static final String FORMULA_ID_CLAUSE = " where f.id = ? ";
	
	private static final String SELECT_ATTRIBUTES = "select " +
			"a.ID as id, " +
			"a.NAME as name, " +
			"a.offset, " +
			"a.offset_start_day, " +
			"a.offset_end_day, " +
			"a.from_timestamp, " +
			"a.to_timestamp, " +
			"a.CREATE_TIMESTAMP, " +
			"a.CREATED_BY as createdByEmployeeId, " +
			"e.NAME as createdByEmployeeName, " +
			"a.LAST_UPDATE_TIMESTAMP, " +
			"a.UPDATED_BY as updatedByEmployeeId, " +
			"e1.NAME as updatedByEmployeeName, " +
			"a.CONTEXT_ID, " +
			"ac.CODE as contextCode, " +
			"ac.NAME as contextName, " +
			"a.TYPE_ID, " +
			"at.CODE as typeCode, " +
			"at.NAME as typeName " +
			"from attribute a " +
			"join attribute_type at on a.TYPE_ID = at.ID " +
			"join attribute_context ac on a.CONTEXT_ID = ac.ID " +
			"join employee e on a.CREATED_BY = e.ID " +
			"left join employee e1 on a.UPDATED_BY = e1.ID ";
	
	private static final String SELECT_ATTRIBUTE_CONTEXTS = "select ac.ID, ac.NAME, ac.CODE from attribute_context ac ";
	
	private static final String SELECT_ATTRIBUTE_TYPES = "select at.ID, at.NAME, at.CODE from attribute_type at ";
	
	private static final String CHECK_IF_ATTRIBUTE_NAME_EXISTS = "select count(*) from attribute where name = ? ";
	
	private static final String CHECK_IF_FORMULA_NAME_EXISTS = "select count(*) from formula where name = ? ";
	
	private static final String ID_NOT_CLAUSE = " and id <> ?";
	
	private static final String ATTRIBUTE_NAMES_CLAUSE = " where a.name in (%s)";
	
	private static final String ORDER_BY_CREATE_TIMESTAMP = " order by create_timestamp desc";
	
	private static final String DELETE_FORMULA = "delete from formula where id = ?";
	
	private static final String DELETE_ATTRIBUTE = "delete from attribute where id = ?";
	
	@Autowired
	public FormulaDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	private BeanPropertyRowMapper<Formula> formulaRowMapper;
	
	private BeanPropertyRowMapper<Attribute> attributeRowMapper;
	
	private BeanPropertyRowMapper<AttributeContext> attributeContextRowMapper;
	
	private BeanPropertyRowMapper<AttributeType> attributeTypeRowMapper;

	private BeanPropertyRowMapper<Formula> getFormulaRowMapper() {
		if (formulaRowMapper == null) {
			formulaRowMapper = new BeanPropertyRowMapper<Formula>(Formula.class);
			formulaRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return formulaRowMapper;
	}
	
	private BeanPropertyRowMapper<Attribute> getAttributeRowMapper() {
		if (attributeRowMapper == null) {
			attributeRowMapper = new BeanPropertyRowMapper<Attribute>(Attribute.class);
			attributeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return attributeRowMapper;
	}
	
	private BeanPropertyRowMapper<AttributeContext> getAttributeContextRowMapper() {
		if (attributeContextRowMapper == null) {
			attributeContextRowMapper = new BeanPropertyRowMapper<AttributeContext>(AttributeContext.class);
			attributeContextRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return attributeContextRowMapper;
	}
	
	private BeanPropertyRowMapper<AttributeType> getAttributeTypeRowMapper() {
		if (attributeTypeRowMapper == null) {
			attributeTypeRowMapper = new BeanPropertyRowMapper<AttributeType>(AttributeType.class);
			attributeTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return attributeTypeRowMapper;
	}

	@Override
	public Integer insertFormula(Formula formula) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_FORMULA,
						Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, formula.getName());
			statement.setLong(2, formula.getCreateTimestamp());
			statement.setInt(3, formula.getCreatedByEmployeeId());
			statement.setString(4, formula.getExpression());
			statement.setString(5, formula.getDescription());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating the discount campaign " + formula.getCreateTimestamp()
						+ " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException("Creating the discount campaign " + formula.getCreateTimestamp()
							+ " failed, no rows affected.");
				}
			}
		}
	}

	@Override
	public void insertAbsoluteAttribute(Attribute attribute) {
		getJdbcTemplate().update(INSERT_ABSOLUTE_ATTRIBUTE, attribute.getName(), attribute.getContextId(),
				attribute.getFromTimestamp(), attribute.getToTimestamp(), attribute.getCreateTimestamp(),
				attribute.getCreatedByEmployeeId(), attribute.getLastUpdateTimestamp(),
				attribute.getUpdatedByEmployeeId());
	}

	@Override
	public void insertRelativeAttribute(Attribute attribute) {
		getJdbcTemplate().update(INSERT_RELATIVE_ATTRIBUTE, attribute.getName(), attribute.getContextId(),
				attribute.getOffset(), attribute.getOffsetStartDay(), attribute.getOffsetEndDay(),
				attribute.getCreateTimestamp(), attribute.getCreatedByEmployeeId(), attribute.getLastUpdateTimestamp(),
				attribute.getUpdatedByEmployeeId());
	}

	@Override
	public void updateFormula(Formula formula) {
		getJdbcTemplate().update(UPDATE_FORMULA, formula.getName(), formula.getExpression(), formula.getDescription(),
				formula.getLastUpdateTimestamp(), formula.getUpdatedByEmployeeId(), formula.getId());
	}

	@Override
	public void updateAttribute(Attribute attribute) {
		getJdbcTemplate().update(UPDATE_ATTRIBUTE, attribute.getName(), attribute.getContextId(), attribute.getOffset(), attribute.getOffsetStartDay(),
				attribute.getOffsetEndDay(), attribute.getFromTimestamp(), attribute.getToTimestamp(),
				attribute.getLastUpdateTimestamp(), attribute.getUpdatedByEmployeeId(), attribute.getId());
	}

	@Override
	public List<Formula> getFormulas() {
		return getJdbcTemplate().query(SELECT_FORMULAS + ORDER_BY_CREATE_TIMESTAMP, getFormulaRowMapper());
	}

	@Override
	public List<Attribute> getAtributes() {
		return getJdbcTemplate().query(SELECT_ATTRIBUTES + ORDER_BY_CREATE_TIMESTAMP, getAttributeRowMapper());
	}

	@Override
	public List<Attribute> getAtributesByNames(String commaSeparatedAttributeNames) {
		return getJdbcTemplate().query(SELECT_ATTRIBUTES + String.format(ATTRIBUTE_NAMES_CLAUSE, commaSeparatedAttributeNames),
				getAttributeRowMapper());
	}

	@Override
	public Formula getFormula(Integer formulaId) {
		List<Formula> result = getJdbcTemplate().query(SELECT_FORMULAS + FORMULA_ID_CLAUSE, getFormulaRowMapper(), formulaId);
		
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public List<AttributeType> getAttributeTypes() {
		return getJdbcTemplate().query(SELECT_ATTRIBUTE_TYPES, getAttributeTypeRowMapper());
	}

	@Override
	public List<AttributeContext> getAttributeContexts() {
		return getJdbcTemplate().query(SELECT_ATTRIBUTE_CONTEXTS, getAttributeContextRowMapper());
	}

	@Override
	public boolean checkIfAttributeNameExists(Attribute attribute) {
		Integer exists = null;
		if (attribute.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_ATTRIBUTE_NAME_EXISTS, Integer.class, attribute.getName());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_ATTRIBUTE_NAME_EXISTS + ID_NOT_CLAUSE, Integer.class, attribute.getName(), attribute.getId());
		}
			
		return exists != null && exists > 0 ;
	}
	
	@Override
	public boolean checkIfFormulaNameExists(Formula formula) {
		Integer exists = null;
		if (formula.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_FORMULA_NAME_EXISTS, Integer.class, formula.getName());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_FORMULA_NAME_EXISTS + ID_NOT_CLAUSE, Integer.class, formula.getName(), formula.getId());
		}
		
		return exists != null && exists > 0 ;
	}

	@Override
	public void deleteFormula(Integer formulaId) {
		getJdbcTemplate().update(DELETE_FORMULA, formulaId);
	}

	@Override
	public void deleteAttribute(Integer attributeId) {
		getJdbcTemplate().update(DELETE_ATTRIBUTE, attributeId);
	}

}
