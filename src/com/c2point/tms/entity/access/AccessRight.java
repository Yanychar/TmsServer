package com.c2point.tms.entity.access;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.SimplePojo;
import com.c2point.tms.entity.TmsUser;


@Entity
@Table(name="accessrights")
/*
@NamedQueries({
	@NamedQuery(name = "findAccessRightsByPerson", 
					query = "SELECT record FROM AccessRight record WHERE " +
							"record.user = :user AND " +
							"record.deleted = false"),
})
*/
public class AccessRight extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AccessRight.class.getName());

	// Owner can be Security Group as default values
	// or TmsUser for Customized Access Rights
	@ManyToOne
	private SecurityGroup	ownerGroup;
	
	@ManyToOne
	private TmsUser			ownerUser;
	
	@Column(name="function" )
    @Enumerated(EnumType.ORDINAL)
	private SupportedFunctionType function;
	
	private boolean read;
	private boolean write;
	private boolean add;
	private boolean del;
	
	protected AccessRight() {
		this( null, null, null, false, false, false, false );
	}
	
	public AccessRight( TmsUser ownerUser, SupportedFunctionType function ) {
		this( function, ownerUser, null, false, false, false, false );
	}

	public AccessRight( SecurityGroup ownerGroup, SupportedFunctionType function ) {
		this( function, null, ownerGroup, false, false, false, false );
	}

	private AccessRight( SupportedFunctionType function, 
						 TmsUser ownerUser, SecurityGroup ownerGroup, 
						 boolean read, boolean write, boolean add, boolean del ) {
		this.setOwner( ownerUser );
		this.setOwner( ownerGroup );
		setFunction( function );
		this.setRead( read );
		this.setWrite( write );
		this.setAdd( add );
		this.setDel( del );
	}

	public AccessRight update( AccessRight source ) {
		
		this.setRead( source.isRead());
		this.setWrite( source.isWrite());
		this.setAdd( source.isAdd());
		this.setDel( source.isDel());
	
		return this;
	}

	// Protected methods used by JPA only
	protected SecurityGroup getOwnerGroup() { return ownerGroup; }
	protected void setOwnerGroup( SecurityGroup ownerGroup ) { this.ownerGroup = ownerGroup; }
	protected TmsUser getOwnerUser() { return ownerUser; }
	protected void setOwnerUser( TmsUser ownerUser ) { this.ownerUser = ownerUser; }

	
	
	public SupportedFunctionType getFunction() { return function; }
	public void setFunction( SupportedFunctionType function ) { this.function = function; }

	public boolean isRead() {
		return read;
	}


	public void clearRead() { setRead( false ); }
	public void setRead() { setRead( true ); }
	public void setRead( boolean read ) {
		this.read = read;
	}

	public boolean isWrite() {
		return write;
	}

	public void clearWrite() { setWrite( false ); }
	public void setWrite() { setWrite( true ); }
	public void setWrite(boolean write) {
		this.write = write;
	}

	public boolean isAdd() {
		return add;
	}

	public void clearAdd() { setAdd( false ); }
	public void setAdd() { setAdd( true ); }
	public void setAdd(boolean add) {
		this.add = add;
	}

	public boolean isDel() {
		return del;
	}

	public void clearDel() { setDel( false ); }
	public void setDel() { setDel( true ); }
	public void setDel(boolean del) {
		this.del = del;
	}

	public SimplePojo getOwner() { 
		if ( ownerUser != null ) {
			return ownerUser;
		}
		
		return ownerGroup;
	}
	
	public void setOwner( TmsUser owner ) {
		if ( owner instanceof TmsUser ) {
			this.ownerUser = owner;
			this.ownerGroup = null;
		}
	}

	public void setOwner( SecurityGroup owner ) {
		if ( owner instanceof SecurityGroup ) {
			this.ownerUser = null;
			this.ownerGroup = owner;
		}
	}
	
	
	
	@Override
	public String toString() {
		return "AccessRight [ "
				+ ( function != null ? "'" + function + "', " : "'', ")
				+ ( read ? "R" : "" ) + ( write ? "W" : "" ) 
				+ ( add ? "A" : "" ) + ( del ? "D" : "" )
				+ " ]";
	}
	
	public static AccessRight getInstanceForTesting( SupportedFunctionType function, 
			 TmsUser ownerUser, SecurityGroup ownerGroup, 
			 boolean read, boolean write, boolean add, boolean del ) {
		
		return new AccessRight( function, ownerUser, ownerGroup, read, write, add, del );
		
	}
	
	
}
