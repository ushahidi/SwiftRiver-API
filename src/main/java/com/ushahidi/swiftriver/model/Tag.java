/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name="tags")
public class Tag implements Serializable{
	
	private static final long serialVersionUID = 4100984822735011886L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name="hash", nullable = false)
	private String hash;
	
	@Column(name="tag", nullable = false)
	private String tag;
	
	@Column(name="tag_canonical", nullable = false)
	private String tagCanonical;
	
	@Column(name="tag_type", nullable = false)
	private String tagType;
	
	public Tag() {
		
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTagCanonical() {
		return tagCanonical;
	}

	public void setTagCanonical(String tagCanonical) {
		this.tagCanonical = tagCanonical;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
