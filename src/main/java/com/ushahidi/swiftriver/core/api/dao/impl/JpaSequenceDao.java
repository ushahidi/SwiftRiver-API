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
