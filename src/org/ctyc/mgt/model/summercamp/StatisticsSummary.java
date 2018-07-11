package org.ctyc.mgt.model.summercamp;

import java.util.HashMap;
import java.util.Map;

public class StatisticsSummary {

	private Map<String, CampDineStatistics> campDineStatisticsMap;

	public Map<String, CampDineStatistics> getCampDineStatisticsMap() {
		
		if (this.campDineStatisticsMap == null){
			this.campDineStatisticsMap = new HashMap<>();
		}
		return campDineStatisticsMap;
	}
}
