package cn.ac.ios.learner.table.dfa;

import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.learner.table.LearnerTable;
import cn.ac.ios.oracle.MembershipOracle;

import cn.ac.ios.table.HashableValue;
//import cn.ac.ios.table.HashableValueBoolean;
import cn.ac.ios.table.ObservationTableAbstract;
import cn.ac.ios.words.Alphabet;


public class LearnerDFATable extends LearnerTable {
	
	public LearnerDFATable(Alphabet aps, MembershipOracle<HashableValue> membershipOracle) {
		super(aps, membershipOracle);
	}

	@Override
	public LearnerType getLearnerType() {
		return LearnerType.DFA_TABLE;
	}


	@Override
	protected ObservationTableAbstract getTableInstance() {
		return new ObservationTableDFA();
	}
}
