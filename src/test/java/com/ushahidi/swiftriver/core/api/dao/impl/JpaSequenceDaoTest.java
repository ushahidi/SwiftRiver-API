package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.SequenceDao;
import com.ushahidi.swiftriver.core.model.Sequence;
import com.ushahidi.swiftriver.core.support.AbstractIntegrationTest;

public class JpaSequenceDaoTest extends AbstractIntegrationTest {
	
	@Autowired
	SequenceDao sequenceDao;
	
	@PersistenceContext
	protected EntityManager em;

	@Test
	public void getIds() {
		Sequence seq = sequenceDao.findById("droplets");
		
		assertEquals(11, sequenceDao.getIds(seq, 2));
		em.flush();
		
		String sql = "SELECT id FROM seq WHERE name = 'droplets'";
		assertEquals(12L, this.jdbcTemplate.queryForLong(sql));
	}
}
