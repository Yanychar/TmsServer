package com.c2point.tms.entity.stub.projectsandtasks;

//import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tms.entity.TmsUser;

@Embeddable
@XmlType(propOrder = { "code", "name" })
public class ProjectManagerStub {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProjectManagerStub.class.getName());

  @Column(name="mgrcode")
	private String code;
  @Column(name="mgrname")
	private String name;

	/**
	 * 
	 */
	public ProjectManagerStub() {
		this( "", "" );
	}
	/**
	 * @param code
	 * @param name
	 */
	public ProjectManagerStub( String code, String name ) {
		super();
		this.code = code;
		this.name = name;
	}
	
	public ProjectManagerStub( TmsUser projectManager ) {
		this( 
				projectManager != null ? projectManager.getCode() : "", 
				projectManager != null ? projectManager.getFirstAndLastNames() : ""
		);
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectManager [" + (code != null ? "code=" + code + ", " : "")
				+ (name != null ? "name=" + name : "") + "]";
	}

}
