package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.MediaDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.support.AbstractIntegrationTest;

public class JpaMediaDaoTest extends AbstractIntegrationTest {

	@Autowired
	private MediaDao mediaDao;

	@Test
	public void getMedia() {
		List<Media> media = new ArrayList<Media>();
		Media existingMedia = new Media();
		existingMedia
				.setUrl("http://gigaom2.files.wordpress.com/2012/11/percolate.jpg ");
		media.add(existingMedia);
		Media newMedia = new Media();
		newMedia.setUrl("http://example.com/new ");
		newMedia.setType("image");
		media.add(newMedia);

		List<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setId(5);
		drop.setMedia(media);
		drop.setImage(newMedia);
		drops.add(drop);

		mediaDao.getMedia(drops);

		assertEquals(9, existingMedia.getId());

		String sql = "SELECT hash, url " + "FROM media WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				newMedia.getId());

		assertEquals("44b764d6f4dab845f031ba9e52f61d95", results.get("hash"));
		assertEquals("http://example.com/new", results.get("url"));

		sql = "SELECT media_id FROM droplets_media WHERE droplet_id = 5";

		List<Long> dropletMedia = this.jdbcTemplate.queryForList(sql,
				Long.class);
		assertEquals(3, dropletMedia.size());
		assertTrue(dropletMedia.contains(newMedia.getId()));
		
		sql = "SELECT droplet_image FROM droplets WHERE id = 5";
		assertEquals(newMedia.getId(), this.jdbcTemplate.queryForLong(sql));
	}
	
	/**
	 * Drop with null media in list should not cause exception
	 */
	@Test
	public void getMediaWithDropInListMissingMedia() {
		List<Media> media = new ArrayList<Media>();
		Media existingMedia = new Media();
		existingMedia
				.setUrl("http://gigaom2.files.wordpress.com/2012/11/percolate.jpg ");
		media.add(existingMedia);
		Media newMedia = new Media();
		newMedia.setUrl("http://example.com/new ");
		newMedia.setType("image");
		media.add(newMedia);

		List<Drop> drops = new ArrayList<Drop>();
		Drop dropWithoutMedia = new Drop();
		dropWithoutMedia.setId(1);
		drops.add(dropWithoutMedia);
		Drop drop = new Drop();
		drop.setId(5);
		drop.setMedia(media);
		drops.add(drop);

		mediaDao.getMedia(drops);
	}
}
