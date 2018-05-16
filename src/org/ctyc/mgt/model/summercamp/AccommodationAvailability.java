package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;

public class AccommodationAvailability implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7922619223291972058L;
	private int numberOfDay;
	private boolean isJoin;

	public AccommodationAvailability(int numberOfDay, boolean isJoin){
		this.numberOfDay = numberOfDay;
		this.isJoin = isJoin;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isJoin ? 1231 : 1237);
		result = prime * result + numberOfDay;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AccommodationAvailability)) {
			return false;
		}
		AccommodationAvailability other = (AccommodationAvailability) obj;
		if (isJoin != other.isJoin) {
			return false;
		}
		if (numberOfDay != other.numberOfDay) {
			return false;
		}
		return true;
	}


	public int getNumberOfDay() {
		return numberOfDay;
	}
	public void setNumberOfDay(int numberOfDay) {
		this.numberOfDay = numberOfDay;
	}
	public boolean isJoin() {
		return isJoin;
	}
	public void setJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}
}
