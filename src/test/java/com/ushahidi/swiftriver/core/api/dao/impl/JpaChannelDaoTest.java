package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.River;

public class JpaChannelDaoTest extends AbstractJpaDaoTest {
	
	final Logger logger = LoggerFactory.getLogger(JpaChannelDaoTest.class);
	
	@Autowired
	RiverDao riverDao;

	@Autowired
	ChannelDao channelDao;

	@Test
	public void testCreateChannel() {
		River river = riverDao.findById(2L);

		Channel channel = new Channel();
		channel.setChannel("test channel");
		channel.setParameters("test parameters");
		channel.setActive(true);
		channel.setRiver(river);
		channelDao.create(channel);

		assertNotNull(channel.getId());
		String sql = "SELECT river_id, channel, active, parameters FROM river_channels WHERE id = ?";

		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql,
				channel.getId());
		assertEquals(2L, ((Number)r.get("river_id")).longValue());
		assertEquals("test channel", r.get("channel"));
		assertEquals(true, r.get("active"));
		assertEquals("test parameters", r.get("parameters"));
		
	}
	
	@Test(expected=IncorrectResultSizeDataAccessException .class)
	public void testDelete() {
		Channel channel = channelDao.findById(3L);
		channelDao.delete(channel);
		em.flush();
		
		String sql = "SELECT * FROM river_channels WHERE id = ?";
		
		this.jdbcTemplate.queryForMap(sql, 3);
	}
	
	@Test
	public void updateChannel()
	{
		Channel channel = channelDao.findById(1L);
		channel.setChannel("updated-channel");
		channel.setParameters("updated-parameters");
		channelDao.update(channel);
		em.flush();
		
		String sql = "SELECT channel, parameters FROM river_channels WHERE id = ?";
		
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, 1);
		assertEquals("updated-channel", (String)results.get("channel"));
		assertEquals("updated-parameters", (String)results.get("parameters"));
	}
}
