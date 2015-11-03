package com.c2point.tms.entity.access;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.Organisation;
import com.c2point.tms.entity.SimplePojo;

@Entity
public class SecurityGroup extends SimplePojo {
	
	private static Logger logger = LogManager.getLogger( SecurityGroup.class.getName());
	
	@ManyToOne
	private Organisation organisation;
	
	private String	code;
	private String	defName;
	
	@OneToMany( mappedBy = "ownerGroup", 
		cascade = { CascadeType.ALL },
		fetch=FetchType.LAZY )
	@MapKey( name = "function" )
	private Map< SupportedFunctionType, AccessRight > mapOfAR = 
			new HashMap< SupportedFunctionType, AccessRight >( SupportedFunctionType.size());

	
	protected SecurityGroup() {
		super();
		
//		normalize();
	}
	public SecurityGroup( Organisation org, String code, String defName ) {
		this();
		
		this.organisation = org;
		this.code = code;
		this.defName = defName;
	}
	
	public Organisation getOrganisation() { return organisation; }
	public void setOrganisation( Organisation organisation ) { this.organisation = organisation; }

	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }

	public String getDefName() { return defName; }
	public void setDefName(String defName) { this.defName = defName; }

	public void setRights( AccessRight ar ) {
		setRights( ar.getFunction(), ar.isRead(), ar.isWrite(), ar.isAdd(), ar.isDel());
	}
	public boolean setRights( SupportedFunctionType function, boolean read, boolean write, boolean add, boolean del ) {
    	boolean bRes = normalize( function );
    	
    	AccessRight ar = mapOfAR.get( function );
    	ar.setRead( read );
    	ar.setWrite( write );
    	ar.setAdd( add );
    	ar.setDel( del );
    	
    	return bRes;
	}
	
	public AccessRight getRights( SupportedFunctionType function ) {
		return mapOfAR.get( function );
	}
	
	public boolean isRead( SupportedFunctionType function ) {
		return mapOfAR.get( function ).isRead();
	}
	public boolean isWrite( SupportedFunctionType function ) {
		return mapOfAR.get( function ).isWrite();
	}
	public boolean isAdd( SupportedFunctionType function ) {
		return mapOfAR.get( function ).isAdd();
	}
	public boolean isDel( SupportedFunctionType function ) {
		return mapOfAR.get( function ).isDel();
	}
	
	public void enableRead( SupportedFunctionType function ) { mapOfAR.get( function ).setRead(); }
	public void enableWrite( SupportedFunctionType function ) { mapOfAR.get( function ).setWrite(); }
	public void enableAdd( SupportedFunctionType function ) { mapOfAR.get( function ).setAdd(); }
	public void enableDel( SupportedFunctionType function ) { mapOfAR.get( function ).setDel(); }
	
	public void disableRead( SupportedFunctionType function ) { mapOfAR.get( function ).clearRead(); }
	public void disableWrite( SupportedFunctionType function ) { mapOfAR.get( function ).clearWrite(); }
	public void disableAdd( SupportedFunctionType function ) { mapOfAR.get( function ).clearAdd(); }
	public void disableDel( SupportedFunctionType function ) { mapOfAR.get( function ).clearDel(); }
	
	
	// Create AccessRights recordS if necessary for this security group
	// Return TRUE if one or more records have been created
	public boolean normalize() {
		boolean bRes = false;
        for( SupportedFunctionType function : SupportedFunctionType.values())   {
        	bRes = ( normalize( function ) || bRes );
        }
		
        return bRes;
	}

	// Create One AccessRights record for specified function
	// Return TRUE if new record has been created
	private boolean normalize( SupportedFunctionType function ) {
		boolean bRes = false;
        if ( !mapOfAR.containsKey( function )) {
        	if ( function != null ) {
            	mapOfAR.put( function, new AccessRight( this, function ));
            	bRes = true;
        	} else {
        		logger.error( "SupportedFunctionType cannot be null!" );
        	}
        	
        }
        
        return bRes;
	}

	public String toString() {
		
		return "Security Group [code,defName]: '" + code + "', '" + defName + "'" ; 
	}

	public static SecurityGroup getInstanceForTesting( Organisation org, String code, String defName ) { 
		
		SecurityGroup sg = new SecurityGroup( org, code, defName );
		sg.normalize();
		
		return sg;
	}
	
	
}
