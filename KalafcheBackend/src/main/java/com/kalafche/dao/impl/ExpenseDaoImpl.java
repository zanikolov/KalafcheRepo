package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.ExpenseDao;
import com.kalafche.model.DailyReportData;
import com.kalafche.model.Expense;
import com.kalafche.model.ExpenseType;

@Service
public class ExpenseDaoImpl extends JdbcDaoSupport implements ExpenseDao {

	private static final String INSERT_EXPENSE = "insert into expense (create_timestamp, employee_id, price, description, store_id, file_id, type_id)"
			+ " values (?, ?, ?, ?, ?, ?, (select id from expense_type where code = ?))";

	private static final String SELECT_EXPENSE_TYPES = "select * from expense_type ";
	private static final String EXPENSE_TYPES_IS_ADMIN_CLAUSE = " where is_admin = false ";
	
	private static final String GET_EXPENSES_QUERY = "select " +
			"e.id, " +
			"e.price, " +
			"e.description, " +
			"et.id as type_id, " +
			"et.name as type_name, " +
			"e.create_timestamp as timestamp, " +
			"e.file_id, " +
			"em.id as employee_id, " +
			"em.name as employee_name, " +
			"ks.id as store_id, " +
			"CONCAT(ks.city, ', ', ks.name) as store_name " +
			"from expense e " +
			"left join expense_type et on e.TYPE_ID = et.id " +
			"join store ks on ks.id = e.store_id " +
			"join employee em on em.id = e.employee_id ";
	
	private static final String PERIOD_CRITERIA_QUERY = " where create_timestamp between ? and ?";
	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
	private static final String ORDER_BY = " order by create_timestamp";
	
	private static final String INSERT_EXPENSE_TYPE = "insert into expense_type (name, code, is_admin) values (?, ?, ?)";
	private static final String UPDATE_EXPENSE_TYPE = "update expense_type set name = ?,  is_admin = ? where id = ?";
	private static final String CHECK_IF_EXPENSE_TYPE_EXISTS = "select count(*) from expense_type where code = ?";
	private static final String BY_NO_ID_CLAUSE = " and id <> ?";

	private static final String GET_EXPENSE_TOTAL_AND_COUNT_QUERY = "select " +
			"count(e.id) as count, " +
			"sum(e.price) as totalAmount " +
			"from expense e " +
			"join expense_type et on e.type_id = et.id " +
			"where e.create_timestamp between ? and ? " +
			"and e.store_id = ? " +
			"and et.is_admin = false; ";
	
	private static final String GET_COLLECTION_TOTAL_AND_COUNT_QUERY = "select " +
			"count(e.id) as count, " +
			"sum(e.price) as totalAmount " +
			"from expense e " +
			"join expense_type et on e.type_id = et.id " +
			"where e.create_timestamp between ? and ? " +
			"and e.store_id = ? " +
			"and et.code = 'COLLECTION'; ";
	
	private BeanPropertyRowMapper<Expense> rowMapper;
	private BeanPropertyRowMapper<ExpenseType> expenseTypeRowMapper;
	
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
	
	@Override
	public void insertExpense(Expense expense) {
		getJdbcTemplate().update(INSERT_EXPENSE, expense.getTimestamp(), expense.getEmployeeId(), expense.getPrice(),
				expense.getDescription(), expense.getStoreId(), expense.getFileId(), expense.getTypeCode());
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
	public List<Expense> searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds) {
		String searchQuery = GET_EXPENSES_QUERY + PERIOD_CRITERIA_QUERY + String.format(STORE_CRITERIA_QUERY, storeIds)
				+ ORDER_BY;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);

		return getJdbcTemplate().query(searchQuery, getRowMapper(), startDateMilliseconds, endDateMilliseconds);
	}

	@Override
	public Integer insertExpenseType(ExpenseType expenseType) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_EXPENSE_TYPE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, expenseType.getName());
			statement.setString(2, expenseType.getCode());
			statement.setBoolean(3, expenseType.getIsAdmin());

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
		getJdbcTemplate().update(UPDATE_EXPENSE_TYPE, expenseType.getName(), expenseType.getIsAdmin(), expenseType.getId());		
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
	public DailyReportData selectExpenseTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_EXPENSE_TOTAL_AND_COUNT_QUERY, DailyReportData.class, startDateTime, endDateTime, storeId);
	}

	@Override
	public DailyReportData selectCollectionTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_COLLECTION_TOTAL_AND_COUNT_QUERY, DailyReportData.class, startDateTime, endDateTime, storeId);
	}

}
