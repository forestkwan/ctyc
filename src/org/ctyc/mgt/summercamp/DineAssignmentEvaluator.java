package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.CollectionUtils;

public class DineAssignmentEvaluator {
	
	private Collection<DineAssignmentRule> rules = new ArrayList<DineAssignmentRule>();
	private Collection<DineAssignmentConstraint> constraints = new ArrayList<DineAssignmentConstraint>();
	
	public DineAssignmentEvaluator(Collection<DineAssignmentRule> rules, Collection<DineAssignmentConstraint> constraints){
		
		if (!CollectionUtils.isEmpty(rules)){
			this.rules.addAll(rules);
		}
		
		if (!CollectionUtils.isEmpty(constraints)){
			this.constraints.addAll(constraints);
		}
	}

	public long evaluate(DineAssignmentPlan plan){
		/* TODO
		 * 
		 */
		return 0;
	}
}
