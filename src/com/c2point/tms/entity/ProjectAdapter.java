package com.c2point.tms.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class ProjectAdapter extends 
					XmlAdapter< ProjectAdapter.ProjectsList, Map<String, Project> > {

	public static class ProjectsList {

		@XmlElement(name="project")
		private List<Project> list;
		
		protected ProjectsList() {
			list = new ArrayList<Project>();
		}

		protected ProjectsList( Map<String, Project> map ) {
			list = new ArrayList<Project>( map.values());
		}
		
	}

	@Override
	public ProjectsList marshal( Map<String, Project> map ) throws Exception {
		return new ProjectsList( map );
	}

	@Override
	public Map<String, Project> unmarshal( ProjectsList list ) throws Exception {
		Map<String, Project> map = new HashMap<String, Project>();
		
		for ( Project project : list.list ) {
			map.put( project.getCode(), project );
		}
		
		return map; 
	}

}
