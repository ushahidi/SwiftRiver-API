package com.ushahidi.swiftriver.dao.hibernate;

import java.util.Map;
import java.util.Set;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ushahidi.swiftriver.dao.BucketDAO;
import com.ushahidi.swiftriver.model.Bucket;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.User;

public class HibernateBucketDAO extends HibernateDaoSupport implements
		BucketDAO {

	/**
	 * @see BucketDAO#create(Bucket)
	 */
	@Override
	public void create(Bucket bucket) {
		getHibernateTemplate().save(bucket);
	}

	/**
	 * @see BucketDAO#update(Bucket)
	 */
	@Override
	public void update(Bucket bucket) {
		getHibernateTemplate().update(bucket);
	}
	
	/**
	 * @see BucketDAO#delete(Bucket)
	 */
	@Override
	public void delete(Bucket bucket) {
		getHibernateTemplate().delete(bucket);
	}

	/**
	 * @see BucketDAO#getBucket(long)
	 */
	@Override
	public Bucket getBucket(long id) {
		return (Bucket) getHibernateTemplate().get(Bucket.class, id);
	}

	/**
	 * @see BucketDAO#getDrops(long, Map...)
	 */
	@Override
	public Set<Drop> getDrops(long id, Map<Object, Object>... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDrop(Bucket bucket, Drop drop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDrops(Bucket bucket, Set<Drop> drops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDrop(Bucket bucket, Drop drop) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDrops(Bucket bucket, Set<Drop> drops) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCollabotator(Bucket bucket, User user, boolean readOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<User> getCollaborators(Bucket bucket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeCollaborator(Bucket bucket, User user) {
		// TODO Auto-generated method stub

	}

}
