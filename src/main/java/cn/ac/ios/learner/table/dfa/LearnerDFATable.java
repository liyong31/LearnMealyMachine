package cn.ac.ios.learner.table.dfa;

import cn.ac.ios.learner.LearnerBase;
import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class LearnerDFATable  extends LearnerBase<DFA> {
	
	protected final Alphabet inAps;
	protected final MembershipOracle<HashableValue> membershipOracle;
	
	public LearnerDFATable(Alphabet aps, MembershipOracle<HashableValue> membershipOracle) {
		this.inAps = aps;
		this.membershipOracle = membershipOracle; 
	}
	

	@Override
	public LearnerType getLearnerType() {
		// TODO Auto-generated method stub
		return LearnerType.DFA;
	}

	@Override
	public void startLearning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DFA getHypothesis() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refineHypothesis(Query<HashableValue> query) {
		// TODO Auto-generated method stub
		
	}

}
