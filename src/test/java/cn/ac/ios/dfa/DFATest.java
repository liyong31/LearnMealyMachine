package cn.ac.ios.dfa;

import cn.ac.ios.learner.Learner;
import cn.ac.ios.learner.table.dfa.LearnerDFATable;
import cn.ac.ios.learner.tree.dfa.LearnerDFATree;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.mealy.EquivalenceOracleImpl;
import cn.ac.ios.mealy.InputHelper;
import cn.ac.ios.oracle.EquivalenceOracle;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class DFATest {
	
	public static void main(String[] args) {
		

		if(args.length < 1) {
			System.out.println("Usage: <PROP> <table|tree>");
			System.exit(0);
		}
		
		Alphabet input = new Alphabet(String.class);
		input.addLetter("a");
		input.addLetter("b");
        
		MembershipOracle<HashableValue> membershipOracle = new MembershipOracleImpl();

		Learner<Machine, HashableValue> learner = null;
		
		if(args[0].equals("table")) learner = new LearnerDFATable(input, membershipOracle);
		else learner = new LearnerDFATree(input, membershipOracle);
		
		System.out.println("starting learning");
		learner.startLearning();
		boolean result = false;
		while(true) {
			System.out.println("Table is both closed and consistent\n" + learner.toString());
			
			Machine model = learner.getHypothesis();
			EquivalenceOracle<Machine, Boolean> equivalenceOracle = new EquivalenceOracleImpl();
			result = equivalenceOracle.answerEquivalenceQuery(model);
			if(result) {
				break;
			}
			Query<HashableValue> ceQuery = InputHelper.getCeWordStr(input);
			HashableValue val = membershipOracle.answerMembershipQuery(ceQuery);
			ceQuery.answerQuery(val);
			learner.refineHypothesis(ceQuery);
		}
		
	}
}

