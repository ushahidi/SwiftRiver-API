package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.River;

public class JpaChannelDaoTest extends AbstractDaoTest {

	@Autowired
	RiverDao riverDao;

	@Autowired
	ChannelDao channelDao;

	@Test
	public void testCreateChannel() {
		River river = riverDao.findById(2);

		Channel channel = new Channel();
		channel.setChannel("test channel");
		channel.setParameters("test parameters");
		channel.setActive(true);
		channel.setRiver(river);
		channelDao.save(channel);

		assertNotNull(channel.getId());
		String sql = "SELECT `river_id`, `channel`, `active`, `parameters` FROM `river_channels` WHERE `id` = ?";

		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql,
				channel.getId());
		assertEquals(2L, r.get("river_id"));
		assertEquals("test channel", r.get("channel"));
		assertEquals(1, r.get("active"));
		assertEquals("test parameters", r.get("parameters"));
		
	}
}
