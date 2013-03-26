package com.ushahidi.swiftriver.core.api.dao.impl;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketCollaboratorDao;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;

@Repository
public class JpaBucketCollaboratorDao extends
		AbstractJpaDao<BucketCollaborator> implements BucketCollaboratorDao {

}
