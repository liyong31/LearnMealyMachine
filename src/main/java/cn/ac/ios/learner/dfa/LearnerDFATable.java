package cn.ac.ios.learner.dfa;

import cn.ac.ios.learner.LearnerBase;
import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;

public class LearnerDFATable  extends LearnerBase<DFA> {

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
