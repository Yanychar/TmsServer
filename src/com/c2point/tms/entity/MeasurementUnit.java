package com.c2point.tms.entity;

import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.web.application.TmsApplication;
import com.vaadin.ui.UI;

@Entity
public class MeasurementUnit extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( MeasurementUnit.class.getName());

	private String	defName;
	private String	resourcename;
	private String	description;
	
	protected MeasurementUnit() {
	}

	public MeasurementUnit( String name ) {
		setDefName( name );
	}

	public MeasurementUnit( String name, String resource ) {
		setDefName( name );
		setResourcename( resource );
	}

	public String getDefName() { return defName; }
	public void setDefName( String defName ) { this.defName = defName; }

	public String getResourcename() { return resourcename; }
	public void setResourcename(String resourcename) { this.resourcename = resourcename; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getName() {
		
		String str = (( TmsApplication )UI.getCurrent()).getResourceStr( resourcename );
		
		if ( StringUtils.isBlank( str )) {
			
			str = this.getDefName();
		}
		
		return str;
	}
	
}
