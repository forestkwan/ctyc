package org.ctyc.mgt.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class FamilyGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String familyId;	/* The Believer ID of the family leader */
	private Collection<String> believerIds;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((familyId == null) ? 0 : familyId.hashCode());
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
		if (!(obj instanceof FamilyGroup)) {
			return false;
		}
		FamilyGroup other = (FamilyGroup) obj;
		if (familyId == null) {
			if (other.familyId != null) {
				return false;
			}
		} else if (!familyId.equals(other.familyId)) {
			return false;
		}
		return true;
	}

	public FamilyGroup(String familyId){
		this.familyId = familyId;
		this.believerIds = new HashSet<String>();
	}
	
	public FamilyGroup(String familyId, Collection<String> believerIds){
		this.familyId = familyId;
		this.believerIds = new HashSet<String>();
		this.believerIds.addAll(believerIds);
	}

	public String getFamilyId() {
		return familyId;
	}

	public Collection<String> getBelieverIds() {
		if (this.believerIds == null){
			this.believerIds = new HashSet<String>();
		}
		return believerIds;
	}
}
