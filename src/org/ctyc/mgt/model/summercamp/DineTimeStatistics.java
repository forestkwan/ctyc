package org.ctyc.mgt.model.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DineTimeStatistics {

	private String timeOfDay;	
	private Collection<DineTableStatistics> dineTableStatisticsList;
	
	public DineTimeStatistics(String timeOfDay) {
		super();
		this.timeOfDay = timeOfDay;
	}
	
	public String getTimeOfDay() {
		return timeOfDay;
	}

	public Collection<DineTableStatistics> getDineTableStatisticsList() {
		if (this.dineTableStatisticsList == null){
			this.dineTableStatisticsList = new ArrayList<DineTableStatistics>();
		}
		return dineTableStatisticsList;
	}
	
	public void setDineTableStatistics(
			int tableNumber,
			int numberOfDay,
			int participantCount,
			CampName campName){
		
		DineTableStatistics tempDineTableStatistics = null;
		
		// Search the target dine table statistics object
		for (DineTableStatistics dineTableStatistics : this.getDineTableStatisticsList()){
			if (dineTableStatistics.getTableNumber() == tableNumber){
				tempDineTableStatistics = dineTableStatistics;
				break;
			}
		}
		
		// If not found, create a new one and add to the list
		if (tempDineTableStatistics == null){
			tempDineTableStatistics = new DineTableStatistics(tableNumber, campName);
			this.getDineTableStatisticsList().add(tempDineTableStatistics);
		}
		

		if (numberOfDay == 1){
			tempDineTableStatistics.setDay1Count(participantCount);
		}
		
		if (numberOfDay == 2){
			tempDineTableStatistics.setDay2Count(participantCount);
		}
		
		if (numberOfDay == 3){
			tempDineTableStatistics.setDay3Count(participantCount);
		}
		
		if (numberOfDay == 4){
			tempDineTableStatistics.setDay4Count(participantCount);
		}
	}

}
