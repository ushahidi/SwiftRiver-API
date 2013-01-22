package com.ushahidi.swiftriver.core.data.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;

import com.ushahidi.swiftriver.core.model.Account;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JpaAccountDaoTest {
	
	@Test
	public void findById()
	{
		EntityManager mockEntityManager = mock(EntityManager.class);
		
		JpaAccountDao accountDao = new JpaAccountDao();
		accountDao.setEm(mockEntityManager);
		accountDao.findById(999);
		
		verify(mockEntityManager).find(Account.class, 999L);
	}
	
	@Test
	public void findByUsername()
	{
		EntityManager mockEntityManager = mock(EntityManager.class);
		Query mockQuery = mock(Query.class);
		Account mockAccount = mock(Account.class);
		when(mockEntityManager.createQuery(anyString())).thenReturn(mockQuery);
		when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
		when(mockQuery.getSingleResult()).thenReturn(mockAccount);
		
		JpaAccountDao accountDao = new JpaAccountDao();
		accountDao.setEm(mockEntityManager);
		Account account = accountDao.findByUsername("admin");
		
		verify(mockEntityManager).createQuery("SELECT a FROM Account a JOIN a.owner o WHERE o.username = :username");
		verify(mockQuery).setParameter("username", "admin");
		assertEquals(mockAccount, account);
	}

}
