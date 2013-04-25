package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.SequenceDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

public class JpaDropDaoTest extends AbstractJpaDaoTest {

	@Autowired
	DropDao dropDao;
	
	@Autowired
	SequenceDao sequenceDao;

	@Test
	public void createDrops() {
		List<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setChannel("test channel");
		drop.setTitle("test title");
		drop.setContent("test content");
		drop.setDatePublished(new Date(1362724400000L));
		drop.setOriginalId("test original id");

		Identity identity = new Identity();
		identity.setOriginId("test identity original id");
		drop.setIdentity(identity);
		drops.add(drop);
		
		List<Long> riverIds = new ArrayList<Long>();
		riverIds.add(1L);
		drop.setRiverIds(riverIds);
		
		List<Long> channelIds = new ArrayList<Long>();
		channelIds.add(3L);
		drop.setChannelIds(channelIds);

		// Destination buckets
		List<Long> bucketIds = new ArrayList<Long>();
		bucketIds.add(1L);
		drop.setBucketIds(bucketIds);
		
		Tag tag = new Tag();
		tag.setTag(" Test tag ");
		tag.setType(" Just a test ");
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(tag);
		drop.setTags(tags);

		List<Place> places = new ArrayList<Place>();
		Place place = new Place();
		place.setPlaceName(" Neverland ");
		place.setLongitude(-35.2033f);
		place.setLatitude(31.9216f);
		places.add(place);
		drop.setPlaces(places);

		List<Media> media = new ArrayList<Media>();
		Media newMedia = new Media();
		newMedia.setUrl("http://example.com/new ");
		media.add(newMedia);
		
		// Get the current channel drop count
		String channelDropCountSQL = "SELECT drop_count " +
				"FROM river_channels WHERE id = ? AND river_id = ?";
		int channelDropCount = this.jdbcTemplate.queryForInt(channelDropCountSQL, 3L, 1L);
		
		// Get the current drop count for the destination bucket
		String bucketDropCountSQL = "SELECT drop_count FROM buckets WHERE id = ?";
		int bucketDropCount = this.jdbcTemplate.queryForInt(bucketDropCountSQL, 1L);

		dropDao.createDrops(drops);

		String sql = "SELECT id, channel, droplet_hash, "
				+ "droplet_orig_id, droplet_title, "
				+ "droplet_content, droplet_date_pub, "
				+ "droplet_date_add, identity_id FROM droplets WHERE "
				+ "id =  ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				drop.getId());

		assertEquals(11L, ((Number) results.get("id")).longValue());
		assertEquals("test channel", results.get("channel"));
		assertEquals("476029d4b6f84664ac56d93e9f6fd27a",
				results.get("droplet_hash"));
		assertEquals("test title", results.get("droplet_title"));
		assertEquals("test content", results.get("droplet_content"));
		assertEquals("test original id", results.get("droplet_orig_id"));
		assertNotNull(results.get("droplet_date_pub"));
		assertNotNull(results.get("droplet_date_add"));
		assertEquals(3L, ((Number) results.get("identity_id")).longValue());
		assertNotNull(tag.getId());
		assertNotNull(place.getId());
		assertNotNull(newMedia.getId());

		sql = "SELECT id, river_id, droplet_date_pub, river_channel_id FROM rivers_droplets WHERE droplet_id = ?";

		results = this.jdbcTemplate.queryForMap(sql, drop.getId());
		assertEquals(6L, ((Number) results.get("id")).longValue());
		assertEquals(1L, ((Number) results.get("river_id")).longValue());
		assertEquals(drop.getDatePublished().getTime(), ((Date)results.get("droplet_date_pub")).getTime());
		assertEquals(3L, ((Number) results.get("river_channel_id")).longValue());
		
		// Ensure rivers_droplets sequence was updated correctly
		assertEquals(6, sequenceDao.findById("rivers_droplets").getId());
		
		sql = "SELECT max_drop_id, drop_count FROM 	rivers WHERE id = ?";
		results = this.jdbcTemplate.queryForMap(sql, 1L);
		assertEquals(6L, results.get("max_drop_id"));
		assertEquals(7, results.get("drop_count"));
		
		// Verify that the channel drop count has been updated
		int updatedChannelDropCount = this.jdbcTemplate.queryForInt(channelDropCountSQL, 3L, 1L);
		assertEquals(channelDropCount + 1, updatedChannelDropCount);
		
		// Verify that the drop count for the bucket was incremented
		int updatedBucketDropCount = this.jdbcTemplate.queryForInt(bucketDropCountSQL, 1L);
		assertEquals(bucketDropCount + 1, updatedBucketDropCount);
	}	
}
