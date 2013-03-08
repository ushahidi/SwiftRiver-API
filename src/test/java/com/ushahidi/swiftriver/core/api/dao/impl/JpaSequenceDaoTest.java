package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.model.Sequence;

public class JpaSequenceDaoTest extends AbstractDaoTest {
	
	@Autowired
	JpaSequenceDao sequenceDao;
	
	@PersistenceContext
	protected EntityManager em;

	@Test
	public void getIds() {
		Sequence seq = sequenceDao.findById("droplets");
		
		assertEquals(11, sequenceDao.getIds(seq, 2));
		em.flush();
		
		String sql = "SELECT `id` FROM `seq` WHERE `name` = 'droplets'";
		assertEquals(12L, this.jdbcTemplate.queryForLong(sql));
	}
}
