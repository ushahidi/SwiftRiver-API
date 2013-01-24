/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

/**
 * Tests for Drop DAO
 * @author ekala
 *
 */
public class JpaDropDaoTest extends AbstractSwiftRiverTest {

	@Autowired
	private DropDao dropDao;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Before
	public void beforeTest() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @verifies Generation of sequence numbers using the 
	 * NEXTVAL() function in the database
	 */
	@Test
	public void testGetSequenceNumber() {
		// Name of the sequence
		String sequenceName = "droplets";		
		
		// Allocate a set of IDs for the droplets sequence
		Long epochSequenceNo = dropDao.getSequenceNumber(sequenceName, 50);

		// Get the last ID allocated for the sequence
		String sql = "SELECT id FROM seq WHERE name = ?1";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, sequenceName);

		Long currentSequenceNo = new Long(((BigInteger) query.getSingleResult()).toString());
		
		Long expectedSequenceNo = epochSequenceNo + 50;

		assertEquals(expectedSequenceNo, currentSequenceNo);
		
		
	}
}
