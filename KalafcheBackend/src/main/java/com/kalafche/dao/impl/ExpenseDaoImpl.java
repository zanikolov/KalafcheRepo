package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.ExpenseDao;
import com.kalafche.model.DataReport;
import com.kalafche.model.expense.Expense;
import com.kalafche.model.expense.ExpensePriceByType;
import com.kalafche.model.expense.ExpenseType;

@Service
public class ExpenseDaoImpl extends JdbcDaoSupport implements ExpenseDao {

	private static final String INSERT_EXPENSE = "insert into expense (create_timestamp, employee_id, price, description, store_id, file_id, invoice, type_id)"
			+ " values (?, ?, ?, ?, ?, ?, ?, (select id from expense_type where code = ?))";

	private static final String SELECT_EXPENSE_TYPES = "select " +
			"et.id as id, " +
			"et.code as code, " +
			"et.name as name, " +
			"et.is_admin, " +
			"et.tax_id, " +
			"t.name as taxName, " +
			"t.code as taxCode " +
			"from expense_type et " +
			"left join tax t on et.tax_id = t.id ";
	private static final String EXPENSE_TYPES_IS_ADMIN_CLAUSE = " where et.is_admin = false ";
	private static final String AND_EXPENSE_TYPES_IS_ADMIN_CLAUSE = " and et.is_admin = false ";
	
	private static final String GET_EXPENSES_QUERY = "select " +
			"e.id, " +
			"e.price, " +
			"e.description, " +
			"e.invoice, " +
			"et.id as type_id, " +
			"et.name as type_name, " +
			"et.code as type_code, " +
			"e.create_timestamp as timestamp, " +
			"e.file_id, " +
			"em.id as employee_id, " +
			"em.name as employee_name, " +
			"t.id as taxId, " +
			"t.code as taxCode, " +
			"t.name as taxName, " +
			"t.rate as taxRate, " +
			"ks.id as store_id, " +
			"CONCAT(ks.city, ', ', ks.name) as store_name " +
			"from expense e " +
			"left join expense_type et on e.TYPE_ID = et.id " +
			"left join tax t on et.tax_id = t.id " +
			"join store ks on ks.id = e.store_id " +
			"join employee em on em.id = e.employee_id ";
	
	private static final String PERIOD_CRITERIA_QUERY = " where create_timestamp between ? and ?";
	private static final String TYPE_CRITERIA_QUERY = " and type_id = ?";
	private static final String TYPE_CODE_EXCLUSION_CRITERIA_QUERY = " and et.code not in (%s)";
	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
	private static final String ORDER_BY = " order by create_timestamp";
	
	private static final String INSERT_EXPENSE_TYPE = "insert into expense_type (name, code, is_admin, tax_id) values (?, ?, ?, ?)";
	private static final String UPDATE_EXPENSE_TYPE = "update expense_type set name = ?,  is_admin = ?, tax_id = ? where id = ?";
	private static final String CHECK_IF_EXPENSE_TYPE_EXISTS = "select count(*) from expense_type where code = ?";
	private static final String BY_NO_ID_CLAUSE = " and id <> ?";

	private static final String GET_EXPENSE_TOTAL_AND_COUNT_QUERY = "select " +
			"count(e.id) as count, " +
			"sum(e.price) as totalAmount " +
			"from expense e " +
			"join expense_type et on e.type_id = et.id " +
			"where e.create_timestamp between ? and ? " +
			"and et.is_admin = false " +
			"and et.code != 'COLLECTION' ";
	
	private static final String NOT_REFUND_CRITERIA = "and et.code != 'REFUND' ";
	
	private static final String STORE_ID_CRITERIA = "and e.store_id = ? ";
	
	private static final String GET_COLLECTION_TOTAL_AND_COUNT_QUERY = "select " +
			"count(e.id) as count, " +
			"sum(e.price) as totalAmount " +
			"from expense e " +
			"join expense_type et on e.type_id = et.id " +
			"where e.create_timestamp between ? and ? " +
			"and e.store_id = ? " +
			"and et.code = 'COLLECTION'; ";
	
	private static final String GET_EXPENSE_PRICE_BY_TYPE = "select " +
			"et.name as name, " +
			"round(sum(e.price), 2) as price " +
			"from expense e " +
			"join expense_type et on e.type_id = et.id " +
			"where store_id = ? " +
			"and et.code not in ('COLLECTION', 'REFUND') " +
			"and CREATE_TIMESTAMP between ? and ? " +
			"group by et.name; ";
	
	private BeanPropertyRowMapper<Expense> rowMapper;
	private BeanPropertyRowMapper<ExpenseType> expenseTypeRowMapper;
	private BeanPropertyRowMapper<ExpensePriceByType> expensePriceByTypeRowMapper;
	private BeanPropertyRowMapper<DataReport> dataReportDataRowMapper;
	
	@Autowired
	public ExpenseDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Expense> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Expense>(Expense.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	private BeanPropertyRowMapper<ExpenseType> getExpenseTypeRowMapper() {
		if (expenseTypeRowMapper == null) {
			expenseTypeRowMapper = new BeanPropertyRowMapper<ExpenseType>(ExpenseType.class);
			expenseTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return expenseTypeRowMapper;
	}
	
	private BeanPropertyRowMapper<ExpensePriceByType> getExpensePriceByTypeRowMapper() {
		if (expensePriceByTypeRowMapper == null) {
			expensePriceByTypeRowMapper = new BeanPropertyRowMapper<ExpensePriceByType>(ExpensePriceByType.class);
			expensePriceByTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return expensePriceByTypeRowMapper;
	}
	
	private BeanPropertyRowMapper<DataReport> getDataReportDataRowMapper() {
		if (dataReportDataRowMapper == null) {
			dataReportDataRowMapper = new BeanPropertyRowMapper<DataReport>(DataReport.class);
			dataReportDataRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return dataReportDataRowMapper;
	}
	
	@Override
	public void insertExpense(Expense expense) {
		getJdbcTemplate().update(INSERT_EXPENSE, expense.getTimestamp(), expense.getEmployeeId(), expense.getPrice(),
				expense.getDescription(), expense.getStoreId(), expense.getFileId(), expense.getInvoice(), expense.getTypeCode());
	}
	
	@Override
	public List<ExpenseType> selectExpenseTypes(Boolean isAdmin) {
		String query = SELECT_EXPENSE_TYPES;
		
		if (!isAdmin) {
			query += EXPENSE_TYPES_IS_ADMIN_CLAUSE;
		}
		
		return getJdbcTemplate().query(query, getExpenseTypeRowMapper());
	}

	@Override
	public List<Expense> searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds, Integer typeId, List<String> typeCodesForExclusion, Boolean isAdmin) {
		List<Object> argsList = new ArrayList<Object>();
		String searchQuery = GET_EXPENSES_QUERY + PERIOD_CRITERIA_QUERY + String.format(STORE_CRITERIA_QUERY, storeIds); 
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		if (typeCodesForExclusion != null && !typeCodesForExclusion.isEmpty()) {
			String commaSeparatedTypeCodesForExclusion = typeCodesForExclusion.stream().map(typeCode -> "'" + typeCode + "'")
					.collect(Collectors.joining(","));
			searchQuery += String.format(TYPE_CODE_EXCLUSION_CRITERIA_QUERY, commaSeparatedTypeCodesForExclusion);
		}
		
		if (typeId != 0) {
			searchQuery += TYPE_CRITERIA_QUERY;
			argsList.add(typeId);
		}
		if (!isAdmin) {
			searchQuery += AND_EXPENSE_TYPES_IS_ADMIN_CLAUSE;
		}
		searchQuery += ORDER_BY;		
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);
		
		return getJdbcTemplate().query(searchQuery, argsArr, getRowMapper());
	}

	@Override
	public Integer insertExpenseType(ExpenseType expenseType) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_EXPENSE_TYPE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, expenseType.getName());
			statement.setString(2, expenseType.getCode());
			statement.setBoolean(3, expenseType.getIsAdmin());
			statement.setInt(4, expenseType.getTaxId());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the expense type " + expenseType.getName() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the expense type " + expenseType.getName() + " failed, no rows affected.");
				}
			}
		}
	}

	@Override
	public void updateExpenseType(ExpenseType expenseType) {
		getJdbcTemplate().update(UPDATE_EXPENSE_TYPE, expenseType.getName(), expenseType.getIsAdmin(), expenseType.getTaxId(), expenseType.getId());		
	}

	@Override
	public boolean checkIfExpenseTypeExists(ExpenseType expenseType) {
		Integer exists = null;
		if (expenseType.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_EXPENSE_TYPE_EXISTS, Integer.class, expenseType.getCode());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_EXPENSE_TYPE_EXISTS + BY_NO_ID_CLAUSE, Integer.class, expenseType.getCode(), expenseType.getId());
		}
			
		return exists != null && exists > 0 ;
	}

	@Override
	public DataReport selectExpenseTotalAndCountByStore(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_EXPENSE_TOTAL_AND_COUNT_QUERY + STORE_ID_CRITERIA,
				getDataReportDataRowMapper(), startDateTime, endDateTime, storeId);
	}

	@Override
	public DataReport selectExpenseTotalAndCountWithoutRefundByStore(Long startDateTime, Long endDateTime,
			Integer storeId) {
		return getJdbcTemplate().queryForObject(
				GET_EXPENSE_TOTAL_AND_COUNT_QUERY + NOT_REFUND_CRITERIA + STORE_ID_CRITERIA,
				getDataReportDataRowMapper(), startDateTime, endDateTime, storeId);
	}

	@Override
	public DataReport selectCollectionTotalAndCountByStore(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_COLLECTION_TOTAL_AND_COUNT_QUERY, getDataReportDataRowMapper(),
				startDateTime, endDateTime, storeId);
	}
	
	@Override
	public List<ExpensePriceByType> selectExpensePriceGroupByType(Long startDateTime, Long endDateTime,
			Integer storeId) {
		return getJdbcTemplate().query(GET_EXPENSE_PRICE_BY_TYPE, getExpensePriceByTypeRowMapper(), storeId,
				startDateTime, endDateTime);
	}

}
