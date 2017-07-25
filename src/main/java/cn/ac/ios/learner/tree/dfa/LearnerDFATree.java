package cn.ac.ios.learner.tree.dfa;

import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.learner.tree.LearnerTree;

import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class LearnerDFATree extends LearnerTree {

	public LearnerDFATree(Alphabet inAps, MembershipOracle<HashableValue> membershipOracle) {
		super(inAps, membershipOracle);
	}

	@Override
	public LearnerType getLearnerType() {
		return LearnerType.DFA_TREE;
	}


}
