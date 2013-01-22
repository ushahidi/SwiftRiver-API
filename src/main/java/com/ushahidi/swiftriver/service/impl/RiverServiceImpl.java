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
package com.ushahidi.swiftriver.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ushahidi.swiftriver.dao.RiverDAO;
import com.ushahidi.swiftriver.dao.SwiftRiverDAO;
import com.ushahidi.swiftriver.model.Channel;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.River;
import com.ushahidi.swiftriver.model.User;
import com.ushahidi.swiftriver.service.RiverService;

/**
 * Service class for rivers
 * @author ekala
 *
 */
@Service
public class RiverServiceImpl extends AbstractServiceImpl<River, Long> implements RiverService {

	@Autowired
	private RiverDAO riverDAO;

	public void setRiverDAO(RiverDAO riverDAO) {
		this.riverDAO = riverDAO;
	}

	public SwiftRiverDAO<River, Long> getServiceDAO() {
		return riverDAO;
	}

	/**
	 * @see RiverServiceImpl#getRiver(Long)
	 */
	public Map<String, Object> getRiver(Long id) {
		// Fetch the river from the database
		River river = riverDAO.findById(id);
		
		// Verify that the river exists
		if (river == null) {
			logger.debug("Could not find river with id " + id);
			return null;
		}

		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		
		Map<String, Object> riverData = gson.fromJson(river.toString(), type);
		
		return riverData;
	}

	/**
	 * @see RiverService#getDrops(long, Object...)
	 */
	public ArrayList<String> getDrops(long id, Object... params) {
		ArrayList<String> dropsArray = new ArrayList<String>();
		for (Drop drop: riverDAO.getDrops(id, params)) {
			dropsArray.add(drop.toString());
		}
		return dropsArray;
	}

	/**
	 * @see RiverService#getCollaborators(River)
	 */
	public ArrayList<String> getCollaborators(River river) {
		ArrayList<String> collaboratorsList = new ArrayList<String>();
		for(User user: riverDAO.getCollaborators(river)) {
			collaboratorsList.add(user.toString());
		}
		
		return collaboratorsList;
	}

	@Override
	public void addCollaborator(long riverId, User user, boolean readOnly) {
		riverDAO.addCollaborator(riverId, user, readOnly);
	}

	@Override
	public void removeCollaborator(long riverId, User user) {
		riverDAO.removeCollaborator(riverId, user);
	}

	@Override
	public void removeDrop(long riverId, Drop drop) {
		riverDAO.removeDrop(riverId, drop);
	}

	@Override
	public void addDrop(long riverId, Drop drop) {
		riverDAO.addDrop(riverId, drop);
	}

	@Override
	public void addDrops(long riverId, Collection<Drop> drops) {
		riverDAO.addDrops(riverId, drops);
	}

	@Override
	public void addChannel(long riverId, Channel channel) {
		riverDAO.addChannel(riverId, channel);
	}

	@Override
	public List<Channel> getChannels(long riverId) {
		return riverDAO.getChannels(riverId);
	}

	@Override
	public void removeChannel(long riverId, Channel channel) {
		riverDAO.removeChannel(riverId, channel);
	}

}
