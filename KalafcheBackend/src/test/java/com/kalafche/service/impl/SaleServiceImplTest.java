package com.kalafche.service.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.kalafche.dao.ItemDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.Item;
import com.kalafche.service.impl.ItemServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class SaleServiceImplTest {
	
	//@InjectMocks
	private SaleServiceImpl saleServiceImpl;
	
	@Test
	public void testSearchSalesForPastPeriodsByStores() throws Exception {
		saleServiceImpl = new SaleServiceImpl();
		//saleServiceImpl.searchSalesForPastPeriodsByStores(7);
	}

}
