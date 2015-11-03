package com.c2point.tms.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Base class. All entities inherit either
 * SimplePojo
 */
@XmlTransient
@MappedSuperclass
public abstract class SimplePojo extends AbstractPojo {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	//(generator = "partition_aware_seq")
//	@Column(name = "id", nullable = false, insertable = true, updatable = false)
	
//	@GeneratedValue(generator="particular_seq")
//	@SequenceGenerator(name="particular_seq", allocationSize=1)
	@Column(name = "id", nullable = false, insertable = true, updatable = false)
	protected long id;
	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId( long id ) {
		this.id = id;
	}
	@XmlTransient
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted() {
		setDeleted( true );
	}
	
	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}
	
	
}
