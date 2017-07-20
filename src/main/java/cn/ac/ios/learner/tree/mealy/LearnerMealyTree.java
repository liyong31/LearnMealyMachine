package cn.ac.ios.learner.tree.mealy;

import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.learner.tree.ValueNode;
import cn.ac.ios.learner.tree.dfa.LearnerDFATree;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.machine.mealy.MealyMachine;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class LearnerMealyTree extends LearnerDFATree {

	private final Alphabet outAps;
	public LearnerMealyTree(Alphabet inAps, Alphabet outAps, MembershipOracle<HashableValue> membershipOracle) {
		super(inAps, membershipOracle);
		this.outAps = outAps;
	}
	
	@Override
	public LearnerType getLearnerType() {
		return LearnerType.MEALY_TREE;
	}

	protected void constructHypothesis() {

//		// construct machine according to KV tree
		if (nodeToSplit != null) {
			updatePredecessors();
		}
		
		Machine machine = new MealyMachine(inAps.getAPs(), outAps.getAPs());
		
		for(int i = 0; i < states.size(); i ++) {
			machine.createState();
		}
		
		for(ValueNode state : states) {
			State s = machine.getState(state.id);
			for(int letter = 0; letter < inAps.getAPSize(); letter ++) {
				// get output
				int out = processMembershipQuery(state.label.append(letter), inAps.getEmptyWord()).get();
				s.addTransition(letter, state.getSuccessor(letter), out);
			}

			if(state.label.isEmpty()) {
				machine.setInitial(state.id);
			}
		}
		
		this.machine = machine;

	}
}
