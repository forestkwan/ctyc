package org.ctyc.mgt.utils;

import java.util.List;

import org.ctyc.mgt.model.summercamp.CampName;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.springframework.util.CollectionUtils;

public class DineTableGroupUtils {

	public static int findLastTableIndex(List<DineTableGroup> dineTableGroups, CampName campName){
		
		if (CollectionUtils.isEmpty(dineTableGroups)){
			return -1;
		}
		
		int index = -1;
		
		for (int i = 0; i < dineTableGroups.size(); i++){
			if (dineTableGroups.get(i).getCampName() == campName){
				index = i;
			}
		}
		
		return index;
	}
}
