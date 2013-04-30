package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ActivityDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountActivity;
import com.ushahidi.swiftriver.core.model.Activity;
import com.ushahidi.swiftriver.core.model.BucketCollaboratorActivity;
import com.ushahidi.swiftriver.core.model.FormActivity;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverActivity;
import com.ushahidi.swiftriver.core.model.RiverCollaboratorActivity;

public class JpaActivityDaoTest extends AbstractJpaDaoTest {

	@Autowired
	private ActivityDao activityDao;

	@Autowired
	private RiverDao riverDao;

	@Autowired
	private AccountDao accountDao;

	@Test
	public void create() {
		River river = riverDao.findById(1L);
		Account account = accountDao.findById(3L);
		RiverActivity activity = new RiverActivity();
		activity.setAccount(account);
		activity.setAction("create");
		activity.setActionDateAdd(new Date());
		activity.setActionOnObj(river);

		activityDao.create(activity);

		String sql = "SELECT account_id, action, action_on, action_on_id, action_date_add FROM account_actions WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				activity.getId());

		assertEquals(3L, ((Number) results.get("account_id")).longValue());
		assertEquals("create", results.get("action"));
		assertEquals("river", results.get("action_on"));
		assertEquals(1L, ((Number) results.get("action_on_id")).longValue());
		assertEquals(activity.getActionDateAdd().getTime(),
				((Date) results.get("action_date_add")).getTime());
	}

	@Test
	public void findForSpecificAccount() {
		List<Activity> activities = activityDao.find(3L, 100, Long.MAX_VALUE,
				false, false);

		assertEquals(6, activities.size());

		// There is a bucket activity
		Activity activity = activities.get(0);
		assertTrue(activity instanceof BucketCollaboratorActivity);
		assertEquals(8L, activity.getId());
		assertEquals(2L, ((BucketCollaboratorActivity) activity).getActionOnObj().getId());

		// There is a bucket activity
		activity = activities.get(1);
		assertTrue(activity instanceof RiverCollaboratorActivity);
		assertEquals(7L, activity.getId());
		assertEquals(2L, (long)((RiverCollaboratorActivity) activity).getActionOnObj().getId());

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
	public void findForSpecificAccountNewerThanId() {
		List<Activity> activities = activityDao.find(3L, 100, 7L, true, false);

		assertEquals(1, activities.size());
		assertEquals(8L, activities.get(0).getId());
	}
	
	@Test
	public void findForFollowingAccounts() {
		List<Activity> activities = activityDao.find(3L, 100, Long.MAX_VALUE, false, true);
		
		assertEquals(2, activities.size());
		assertEquals(6L, activities.get(0).getId());
		assertEquals(5L, activities.get(1).getId());
	}
	
	@Test
	public void findForFollowingAccountsNewerThanId() {
		List<Activity> activities = activityDao.find(3L, 100, 5L, true, true);
		
		assertEquals(1, activities.size());
		assertEquals(6L, activities.get(0).getId());
	}

}
