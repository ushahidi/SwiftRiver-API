package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.ActivityDao;
import com.ushahidi.swiftriver.core.model.AccountActivity;
import com.ushahidi.swiftriver.core.model.Activity;
import com.ushahidi.swiftriver.core.model.BucketActivity;
import com.ushahidi.swiftriver.core.model.FormActivity;
import com.ushahidi.swiftriver.core.model.RiverActivity;

public class JpaActivityDaoTest extends AbstractJpaDaoTest {

	@Autowired
	private ActivityDao activityDao;

	@Test
	public void find() {
		List<Activity> activities = activityDao.find(3L, 100, Long.MAX_VALUE,
				false);

		assertEquals(6, activities.size());

		// There is a bucket activity
		Activity activity = activities.get(0);
		assertTrue(activity instanceof BucketActivity);
		assertEquals(8L, activity.getId());
		assertEquals(2L, ((BucketActivity) activity).getActionOnObj().getId());

		// River activity
		activity = activities.get(1);
		assertTrue(activity instanceof RiverActivity);
		assertEquals(7L, activity.getId());
		assertEquals(2L, (long) ((RiverActivity) activity).getActionOnObj()
				.getId());

		// Account activity
		activity = activities.get(2);
		assertTrue(activity instanceof AccountActivity);
		assertEquals(4L, activity.getId());
		assertEquals(4L, ((AccountActivity) activity).getActionOnObj().getId());

		// Form activity
		activity = activities.get(3);
		assertTrue(activity instanceof FormActivity);
		assertEquals(3L, activity.getId());
		assertEquals(1L, (long) ((FormActivity) activity).getActionOnObj()
				.getId());
	}

	@Test
	public void findNewerThanId() {
		List<Activity> activities = activityDao.find(3L, 100, 7L, true);
		
		assertEquals(1, activities.size());
		assertEquals(8L, activities.get(0).getId());
	}

}
