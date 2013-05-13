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
package com.ushahidi.swiftriver.core.api.dao.impl;

import javax.persistence.LockModeType;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.SequenceDao;
import com.ushahidi.swiftriver.core.model.Sequence;

@Repository
public class JpaSequenceDao extends AbstractJpaDao<Sequence> implements
		SequenceDao {
	
	/**
	 * Override to obtain a pessimistic lock on the entity.
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Sequence findById(Object id) {
		return (Sequence) em.find(Sequence.class, id, LockModeType.PESSIMISTIC_WRITE);
	}
	
	/**
	 * Obtain a range of IDs from the sequence
	 * 
	 * @param num
	 * @return
	 */
	public long getIds(Sequence seq, int num)
	{
		long start = seq.getId();
		
		seq.setId(seq.getId() + num);
		em.merge(seq);
		
		return start+1;
	}

}
