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

import com.kalafche.dao.InvoiceDao;
import com.kalafche.model.invoice.Invoice;
import com.kalafche.model.invoice.InvoiceType;

@Service
public class InvoiceDaoImpl extends JdbcDaoSupport implements InvoiceDao {

	private static final String INSERT_INVOICE = "INSERT INTO invoice " +
			"(STARTDATE_TIMESTAMP,ENDDATE_TIMESTAMP,CREATE_TIMESTAMP,CREATED_BY,ISSUEDATE_TIMESTAMP,ISSUER_COMPANY_ID,RECIPIENT_COMPANY_ID,TYPE_ID,TOTAL_AMOUNT,BASE,DUE_VAT) VALUES " +
			"(?                  ,?                ,?               ,?         ,?                  ,?                ,?                   ,?      ,?           ,?   ,?      ) ";

	private static final String SELECT_INVOICES = "select " +
			"i.id, " +
			"i.startdate_timestamp as startDateTimestamp, " +
			"i.enddate_timestamp as endDateTimestamp, " +
			"i.create_timestamp as create_timestamp, " +
			"i.created_by as createdByEmployeeId, " +
			"i.issuedate_timestamp as issueDate, " +
			"i.issuer_company_id as issuerCompanyId, " +
			"i.recipient_company_id as recipientCompanyId, " +
			"i.type_id as typeId, " +
			"it.name as typeName, " +
			"it.code as typeCode, " +
			"i.total_amount as totalAmount, " +
			"i.base as base, " +
			"i.due_vat as dueVAT " +
			"from invoice i " +
			"join invoice_type it on i.type_id = it.id ";
	private static final String ISSUER_ID_CLAUSE = " where i.issuer_company_id = ?";
	private static final String RECIPIENT_ID_CLAUSE = " and i.recipient_company_id = ? ";
	private static final String START_DATE_CLAUSE = " and i.STARTDATE_TIMESTAMP = ? ";
	private static final String END_DATE_CLAUSE = " and i.ENDDATE_TIMESTAMP = ? ";
	private static final String SELECT_INVOICE_TYPES = "SELECT * FROM invoice_type ";
	private static final String INVOICE_TYPE_CODE_CLAUSE = " where code = ?";
	
	private BeanPropertyRowMapper<Invoice> invoiceRowMapper;
	
	private BeanPropertyRowMapper<InvoiceType> invoiceTypeRowMapper;
	
	@Autowired
	public InvoiceDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Invoice> getInvoiceRowMapper() {
		if (invoiceRowMapper == null) {
			invoiceRowMapper = new BeanPropertyRowMapper<Invoice>(Invoice.class);
			invoiceRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return invoiceRowMapper;
	}
	
	private BeanPropertyRowMapper<InvoiceType> getInvoiceTypeRowMapper() {
		if (invoiceTypeRowMapper == null) {
			invoiceTypeRowMapper = new BeanPropertyRowMapper<InvoiceType>(InvoiceType.class);
			invoiceTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return invoiceTypeRowMapper;
	}
	
	@Override
	public Integer insertInvoice(Invoice invoice) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_INVOICE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, invoice.getStartDateTimestamp());
			statement.setLong(2, invoice.getEndDateTimestamp());
			statement.setLong(3, invoice.getCreateTimestamp());
			statement.setInt(4, invoice.getCreatedByEmployeeId());
			statement.setLong(5, invoice.getIssueDate());
			statement.setInt(6, invoice.getIssuer().getId());
			statement.setInt(7, invoice.getRecipient().getId());
			statement.setInt(8, invoice.getTypeId());
			statement.setBigDecimal(9, invoice.getTotalAmount());
			statement.setBigDecimal(10, invoice.getBase());
			statement.setBigDecimal(11, invoice.getDueVAT());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the invoice " + invoice.getCreateTimestamp() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the invoice " + invoice.getCreateTimestamp() + " failed, no rows affected.");
				}
			}
		}	
	}

	@Override
	public Invoice getInvoice(Long startDate, Long endDate, Integer issuerCompanyId, Integer recipientCompanyId) {
		List<Invoice> result = getJdbcTemplate().query(
				SELECT_INVOICES + ISSUER_ID_CLAUSE + RECIPIENT_ID_CLAUSE + START_DATE_CLAUSE + END_DATE_CLAUSE,
				getInvoiceRowMapper(), issuerCompanyId, recipientCompanyId, startDate, endDate);
		
		return result.size() == 1 ? result.get(0) : null;
	}

	@Override
	public InvoiceType getInvoiceTypeByCode(String code) {
		List<InvoiceType> result = getJdbcTemplate().query(SELECT_INVOICE_TYPES + INVOICE_TYPE_CODE_CLAUSE,
				getInvoiceTypeRowMapper(), code);

		return result.size() == 1 ? result.get(0) : null;
	}
}
