package org.ctyc.mgt.model.summercamp;

public class TableAndParticipant {

	private DineTableGroup dineTableGroup;
	private Participant participant;
	
	public TableAndParticipant(DineTableGroup dineTableGroup, Participant participant){
		this.dineTableGroup = dineTableGroup;
		this.participant = participant;
	}
	
	public DineTableGroup getDineTableGroup() {
		return dineTableGroup;
	}
	
	public Participant getParticipant() {
		return participant;
	}
	
}
