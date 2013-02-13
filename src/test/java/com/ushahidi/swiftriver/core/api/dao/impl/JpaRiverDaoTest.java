package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.MediaThumbnail;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.util.DateUtil;

public class JpaRiverDaoTest extends AbstractDaoTest {
	
	@Autowired
	RiverDao riverDao;
	
	@Autowired
	AccountDao accountDao;

	@Test
	public void findById() {
		River r = riverDao.findById(1);
		
		assertEquals("Public River 1", r.getRiverName());
	}
	
	@Test
	public void getDrops() {
		Account account = accountDao.findById(1);
		List<Drop> drops = riverDao.getDrops(1L, Long.MAX_VALUE, 10, account);
		
		assertEquals(5, drops.size());
		
		Drop drop = drops.get(0);
		assertEquals(5, drop.getId());
		assertEquals("rss", drop.getChannel());
		assertEquals("droplet_5_title", drop.getTitle());
		assertEquals("droplet_5_content", drop.getContent());
		assertEquals("5", drop.getOriginalId());
		assertEquals(30, drop.getCommentCount());
		assertEquals(1, drop.getIdentity().getId());
		assertEquals("identity1_name", drop.getIdentity().getName());
		assertEquals("identity1_avatar", drop.getIdentity().getAvatar());
		assertNotNull(drop.getOriginalUrl());
		assertEquals(3, drop.getOriginalUrl().getId());
		assertEquals("http://www.bbc.co.uk/nature/20273855", drop.getOriginalUrl().getUrl());
		
		
		
		assertEquals(2, drop.getTags().size());
		Tag tag = drop.getTags().get(0);
		assertEquals(1, tag.getId());
		assertEquals("Jeremy Hunt", tag.getTag());
		assertEquals("person", tag.getType());
		
		assertEquals(2, drop.getLinks().size());
		Link link = drop.getLinks().get(0);
		assertEquals(10, link.getId());
		assertEquals("http://www.bbc.co.uk/sport/0/football/20319573", link.getUrl());
		
		assertEquals(2, drop.getMedia().size());
		Media media = drop.getMedia().get(0);
		assertEquals(1, media.getId());
		assertEquals("http://gigaom2.files.wordpress.com/2012/10/datacapspercentage.jpeg", media.getUrl());
		assertEquals("image", media.getType());
		
		assertEquals(2, media.getThumbnails().size());
		MediaThumbnail thumbnail = media.getThumbnails().get(0);
		assertEquals("https://2bcbd22fbb0a02d76141-1680e9dfed1be27cdc47787ec5d4ef89.ssl.cf1.rackcdn.com/625dd7cb656d258b4effb325253e880631699d80345016e9e755b4a04341cda1.peg", thumbnail.getUrl());
		assertEquals(80, thumbnail.getSize());
		
		assertEquals(2, drop.getPlaces().size());
		Place place = drop.getPlaces().get(0);
		assertEquals(1, place.getId());
		assertEquals("Wales", place.getPlaceName());
		assertEquals(new Float(146.11), place.getLongitude());
		assertEquals(new Float(-33), place.getLatitude());
		
	}
	
	@Test
	@Transactional
	public void testCreateRiver() {
		River river = new River();
		Account account = accountDao.findByUsername("user1");

		river.setRiverName("Test river");
		river.setAccount(account);
		river.setRiverPublic(false);

		riverDao.save(river);
		
		assertNotNull(river.getId());
		String sql = "SELECT river_name, account_id FROM `rivers` WHERE `id` = ?";
		
		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql, river.getId());
		assertEquals("Test river", (String)r.get("river_name"));
		assertEquals(BigInteger.valueOf(3L), (BigInteger)r.get("account_id"));
		
	}

	@Test
	@Transactional
	public void testAddCollaborator() {
		long riverId = 1;

		River river = riverDao.findById(riverId);
		int collaboratorCount = river.getCollaborators().size();

		Account account = accountDao.findByUsername("user3");		

		riverDao.addCollaborator(river, account, true);
		assertEquals(collaboratorCount+1, river.getCollaborators().size());
	}
}
