/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.JpaDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.util.DateUtil;

/**
 * Service class for rivers
 * @author ekala
 *
 */
@Service
public class RiverService extends AbstractServiceImpl<River, Long> {

	@Autowired
	private RiverDao riverDao;
	
	/* Logger */
	private static Logger logger = LoggerFactory.getLogger(RiverService.class);

	public void setRiverDAO(RiverDao riverDao) {
		this.riverDao = riverDao;
	}

	public JpaDao<River, Long> getServiceDao() {
		return riverDao;
	}

	/**
	 * @see RiverService#getRiver(Long)
	 */
	public Map<String, Object> getRiver(Long id) {
		// Fetch the river from the database
		River river = riverDao.findById(id);
		
		// Verify that the river exists
		if (river == null) {
			logger.debug("Could not find river with id " + id);
			return null;
		}
		
		// TODO Convert the river data to a map
		return new HashMap<String, Object>();
	}

	/**
	 * @see RiverService#getDropsSinceId(Long, Long, int)
	 */
	@Transactional
	public ArrayList<Map<String, Object>> getDropsSinceId(Long id, Long sinceId, int dropCount) {
		ArrayList<Map<String, Object>> dropsArray = new ArrayList<Map<String,Object>>();
		riverDao.getDrops(id, sinceId, dropCount);
		return dropsArray;
	}

	/**
	 * @see RiverService#getCollaborators(River)
	 */
	public ArrayList<Map<String, Object>> getCollaborators(River river) {
		ArrayList<Map<String, Object>> collaboratorsList = new ArrayList<Map<String,Object>>();

		return collaboratorsList;
	}

	public void addCollaborator(long riverId, User user, boolean readOnly) {
		riverDao.addCollaborator(riverId, user, readOnly);
	}

	public void removeCollaborator(long riverId, User user) {
		riverDao.removeCollaborator(riverId, user);
	}

	public void removeDrop(long riverId, Drop drop) {
		riverDao.removeDrop(riverId, drop);
	}

	public void addDrop(long riverId, Drop drop) {
		riverDao.addDrop(riverId, drop);
	}

	public void addDrops(long riverId, Collection<Drop> drops) {
		riverDao.addDrops(riverId, drops);
	}

	public void addChannel(long riverId, Channel channel) {
		riverDao.addChannel(riverId, channel);
	}

	public ArrayList<Map<String, Object>> getChannels(long riverId) {
		riverDao.getChannels(riverId);
		// TODO: Implement this
		return null;
	}

	public void removeChannel(long riverId, Channel channel) {
		riverDao.removeChannel(riverId, channel);
	}

	public static Map<String, Object> getRiverMap(River river, Account queryingAccount)
	{		
		Object[][] riverData = { { "id", river.getId() },
				{"name", river.getRiverName()},
				{"description", null},
				{"category", null},
				{"follower_count", river.getFollowers().size()},
				{"is_collaborating", river.getCollaborators().contains(queryingAccount)},
				{"is_following", river.getFollowers().contains(queryingAccount)},
				{"public", river.isRiverPublic()},
				{"active", river.isActive()},
				{"drop_count", river.getDropCount()},
				{"drop_quota", river.getDropQuota()},
				{"full", river.isFull()},
				{"date_added", DateUtil.formatRFC822(river.getDateAdded())},
				{"expiry_date", DateUtil.formatRFC822(river.getExpiryDate())},
				{"extension_count", river.getExtensionCount()}};
		
		return ArrayUtils.toMap(riverData);
	}
}
