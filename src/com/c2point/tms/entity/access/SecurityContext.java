package com.c2point.tms.entity.access;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;

@Embeddable
public class SecurityContext {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( SecurityContext.class.getName());

	@ManyToOne
//	@Column( name = "secgroup_id" )
	@OneToOne( cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY )
	private SecurityGroup	secGroup;

	// Access Rights
	@OneToMany( mappedBy = "ownerUser",
			cascade = { CascadeType.ALL },
			fetch=FetchType.LAZY )
	@MapKey( name = "function" )
	private Map< SupportedFunctionType, AccessRight > mapOfAR = 
			new HashMap< SupportedFunctionType, AccessRight >( SupportedFunctionType.size());


	public SecurityGroup getSecGroup() {
		return secGroup;
	}
	public void setSecGroup(SecurityGroup secGroup) {
		this.secGroup = secGroup;
	}
	public Map<SupportedFunctionType, AccessRight> getMapOfAR() {
		return mapOfAR;
	}
	public void setMapOfAR(Map<SupportedFunctionType, AccessRight> mapOfAR) {
		this.mapOfAR = mapOfAR;
	}
	
	// Business methods

	public void setAccessRights( AccessRight ar ) {
		if ( ar != null && ar.getFunction() != null ) {
			mapOfAR.put( ar.getFunction(), ar );
		}
	}
	
	public boolean isRead( SupportedFunctionType function ) { return getAR( function ).isRead(); }
	public boolean isWrite( SupportedFunctionType function ) { return getAR( function ).isWrite(); }
	public boolean isAdd( SupportedFunctionType function ) { return getAR( function ).isAdd(); }
	public boolean isDel( SupportedFunctionType function ) { return getAR( function ).isDel(); }

	public boolean isAccessible( SupportedFunctionType function ) { return isRead( function ) || isWrite( function ); }
	
	private AccessRight getAR( SupportedFunctionType function ) {
		AccessRight ar = null;
		
		// Read customized AR for user if exists
		if ( mapOfAR != null && mapOfAR.size() > 0 ) {
			ar = mapOfAR.get( function );
		}
		
		// If AR was not customized than read from Security Group
		if ( ar == null && secGroup != null ) {
			ar = secGroup.getRights( function );
		}
		
		// If no Security Group has been assigned than read default value that equal to FALSE for all functions 
		if ( ar == null ) {
			ar = new AccessRight( ( TmsUser )null, function );
			if ( secGroup != null && secGroup.getCode().compareTo( "008" ) == 0 ) {
				ar.setRead();
				ar.setWrite();
			}
		}
		
		return ar;
	}
	@Override
	public String toString() {
		String str = "";
		if ( mapOfAR != null && mapOfAR.size() > 0 ) {
			str = "Customised rights";
		} else {
			str = secGroup.toString();
		}
		return str;
	}

	
}
