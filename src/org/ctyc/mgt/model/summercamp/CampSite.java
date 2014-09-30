package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class CampSite implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Collection<CanteenTable> canteenTables;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Collection<CanteenTable> getCanteenTables() {
		if (this.canteenTables == null){
			this.canteenTables = new ArrayList<CanteenTable>();
		}
		return canteenTables;
	}
}
