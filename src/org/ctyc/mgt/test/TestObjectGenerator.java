package org.ctyc.mgt.test;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.CanteenTable;

public class TestObjectGenerator {

	public static CampSite generateTestCampSite(){
		CampSite campSite = new CampSite();
		campSite.setName("長洲明愛");
		campSite.getCanteenTables().addAll(generateCanteenTables(30, 8));
		
		return campSite;
	}
	
	public static Collection<CanteenTable> generateCanteenTables(int totalNoOfTables, int tableCapacity){
		
		Collection<CanteenTable> canteenTables = new ArrayList<CanteenTable>();
		for (int i=0; i<totalNoOfTables; i++){
			CanteenTable canteenTable = new CanteenTable(i+1, tableCapacity, "");
			canteenTables.add(canteenTable);
		}
		
		return canteenTables;
	}
}