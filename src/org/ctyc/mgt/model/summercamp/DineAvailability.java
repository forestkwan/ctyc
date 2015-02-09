package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;

public class DineAvailability implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4719893962694771620L;
	private int numberOfDay;
	private String timeOfDay;
	private boolean isJoin;
	
	public DineAvailability(int numberOfDay, String timeOfDay, boolean isJoin){
		this.numberOfDay = numberOfDay;
		this.timeOfDay = timeOfDay;
		this.isJoin = isJoin;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numberOfDay;
		result = prime * result + ((timeOfDay == null) ? 0 : timeOfDay.hashCode());
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
		if (!(obj instanceof DineAvailability)) {
			return false;
		}
		DineAvailability other = (DineAvailability) obj;
		if (numberOfDay != other.numberOfDay) {
			return false;
		}
		if (timeOfDay == null) {
			if (other.timeOfDay != null) {
				return false;
			}
		} else if (!timeOfDay.equals(other.timeOfDay)) {
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
	public String getTimeOfDay() {
		return timeOfDay;
	}
	public void setTimeOfDay(String timeOfDay) {
		this.timeOfDay = timeOfDay;
	}
	public boolean isJoin() {
		return isJoin;
	}
	public void setJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}
}
