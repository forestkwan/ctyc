package org.ctyc.mgt.summercamp;

import java.util.Comparator;

import org.ctyc.mgt.model.summercamp.DineTableGroup;

public class DineTableNumberComparator implements Comparator<DineTableGroup>{

	@Override
	public int compare(DineTableGroup left, DineTableGroup right) {
		
		return left.getTableNumber() - right.getTableNumber();
	}
}