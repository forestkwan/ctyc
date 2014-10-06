package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;

public class DineTimeSlot implements Serializable{
	
	public static enum TimeOfDay{
		MORNING, NOON, NIGHT
	}
	
	private static final long serialVersionUID = -1690690971738005984L;
	private int numberOfDay;
	private TimeOfDay timeOfDay;
	
	public DineTimeSlot(int numberOfDay, TimeOfDay timeOfDay) {
		super();
		this.numberOfDay = numberOfDay;
		this.timeOfDay = timeOfDay;
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
		if (!(obj instanceof DineTimeSlot)) {
			return false;
		}
		DineTimeSlot other = (DineTimeSlot) obj;
		if (numberOfDay != other.numberOfDay) {
			return false;
		}
		if (timeOfDay != other.timeOfDay) {
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
	public TimeOfDay getTimeOfDay() {
		return timeOfDay;
	}
	public void setTimeOfDay(TimeOfDay timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

}
