package org.ctyc.mgt.summercamp;

import java.util.Comparator;

import org.ctyc.mgt.model.summercamp.DineTableGroup;

public class DineTableGenderBalanceComparator implements Comparator<DineTableGroup>{

	@Override
	public int compare(DineTableGroup left, DineTableGroup right) {
		
		return Math.abs(right.getNetGenderBalance()) - Math.abs(left.getNetGenderBalance());
	}
}
