package com.c2point.tms.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class TaskAdapter  extends 
							XmlAdapter< TaskAdapter.TasksList, Map<String, Task> > {

	public static class TasksList {

		@XmlElement(name="task")
		private List<Task> list;
		
		protected TasksList() {
			list = new ArrayList<Task>();
		}

		protected TasksList( Map<String, Task> map ) {
			list = new ArrayList<Task>( map.values());
		}
		
	}

	@Override
	public TasksList marshal( Map<String, Task> map ) throws Exception {
		return new TasksList( map );
	}

	@Override
	public Map<String, Task> unmarshal( TasksList list ) throws Exception {
		Map<String, Task> map = new HashMap<String, Task>();
		
		for ( Task task : list.list ) {
			map.put( task.getCode(), task );
		}
		
		return map; 
	}

}
