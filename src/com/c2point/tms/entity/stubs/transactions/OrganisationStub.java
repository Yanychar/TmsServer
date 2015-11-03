package com.c2point.tms.entity.stubs.transactions;

import javax.xml.bind.annotation.XmlType;

import com.c2point.tms.entity.Organisation;

@XmlType(propOrder = { "code", "name" })
public class OrganisationStub {
	String code;
	String name;
	/**
	 * @param id
	 * @param name
	 */
	public OrganisationStub( String code, String name ) {
		super();
		this.code = code;
		this.name = name;
	}
	public OrganisationStub() {
		this( "", "" );
	}
	public OrganisationStub( Organisation organisation ) {
		this( organisation.getCode(), organisation.getName());
	}

	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
