package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.SequenceDao;
import com.ushahidi.swiftriver.core.model.Sequence;

public class JpaSequenceDaoTest extends AbstractJpaDaoTest {
	
	@Autowired
	SequenceDao sequenceDao;
	
	@Test
	public void getIds() {
		Sequence seq = sequenceDao.findById("droplets");
		
		assertEquals(11, sequenceDao.getIds(seq, 2));
		em.flush();
		
		String sql = "SELECT id FROM seq WHERE name = 'droplets'";
		assertEquals(12L, this.jdbcTemplate.queryForLong(sql));
	}
}
