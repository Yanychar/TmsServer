package com.c2point.tms.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;


/**
 * Base class for all entities
 * 
 * @param <ID>
 *            Type for primary key.
 */
@MappedSuperclass
public abstract class AbstractPojo {

	@Column(nullable = false)
	@Version
	protected Long consistencyVersion;

	@XmlTransient
	public abstract long getId();

	public abstract void setId( long id );

	@XmlTransient
	public Long getConsistencyVersion() {
		return consistencyVersion;
	}

	public void setConsistencyVersion(Long consistencyVersion) {
		this.consistencyVersion = consistencyVersion;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof AbstractPojo) {
			if (((AbstractPojo) obj).getId() <= 0 && this.getId() <= 0 && obj == this) {
				return true;
			} else if (((AbstractPojo) obj).getId() > 0 && (((AbstractPojo) obj).getId() == getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Long.toString( getId()).hashCode();
	}

}
