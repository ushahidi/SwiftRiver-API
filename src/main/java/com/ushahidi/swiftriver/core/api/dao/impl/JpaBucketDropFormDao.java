package com.ushahidi.swiftriver.core.api.dao.impl;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketDropFormDao;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.BucketDropFormField;

@Repository
public class JpaBucketDropFormDao extends AbstractJpaDao<BucketDropForm>
		implements BucketDropFormDao {

	
	/* 
	 * Override to persist field values when the form is created.
	 * 
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftbucket.core.api.dao.impl.AbstractJpaDao#create(java.lang.Object)
	 */
	@Override
	public BucketDropForm create(BucketDropForm dropForm) {
		dropForm = super.create(dropForm);
		if (dropForm.getValues() != null) {
			for (BucketDropFormField fieldValue : dropForm.getValues()) {
				fieldValue.setDropForm(dropForm);
				em.persist(fieldValue);
			}
		}

		return dropForm;
	}
	
	
	/* 
	 * Override to replace field values when the form is modified.
	 * 
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftbucket.core.api.dao.impl.AbstractJpaDao#update(java.lang.Object)
	 */
	@Override
	public BucketDropForm update(BucketDropForm dropForm) {
		String query = "DELETE FROM BucketDropFormField f WHERE f.dropForm = :dropForm";
		em.createQuery(query).setParameter("dropForm", dropForm).executeUpdate();
		
		if (dropForm.getValues() != null) {
			for (BucketDropFormField fieldValue : dropForm.getValues()) {
				fieldValue.setDropForm(dropForm);
				em.persist(fieldValue);
			}
		}
		em.refresh(dropForm);
		
		return dropForm;
	}

}
