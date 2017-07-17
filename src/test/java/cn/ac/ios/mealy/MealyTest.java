package cn.ac.ios.mealy;

import ac.ac.ios.oracle.EquivalenceOracle;
import ac.ac.ios.oracle.MembershipOracle;
import ac.ac.ios.query.Query;
import ac.ac.ios.table.HashableValue;
import ac.ac.ios.table.HashableValueInt;
import cn.ac.ios.learner.mealy.LearnerMealy;
import cn.ac.ios.words.Alphabet;

public class MealyTest {
	
	public static void main(String[] args) {
		

		Alphabet input = new Alphabet(Integer.class);
		input.addLetter(0);
		input.addLetter(1);
		
		Alphabet output = new Alphabet(Integer.class);
        output.addLetter(0);
        output.addLetter(1);
        output.addLetter(2);
        
		MembershipOracle<HashableValue> membershipOracle = new MembershipOracleImpl(output);

		LearnerMealy learner = new LearnerMealy(input, membershipOracle);
		System.out.println("starting learning");
		learner.startLearning();
		boolean result = false;
		while(true) {
			System.out.println("Table is both closed and consistent\n" + learner.toString());
			
			MealyMachine model = learner.getHypothesis();
			System.out.println("automaton\n" + model.toString());
			
			Query<HashableValue> ceQuery = learner.makeTableConsistent();
			
			if(ceQuery != null) {
				learner.refineHypothesis(ceQuery);
				continue;
			}
			
			EquivalenceOracle<MealyMachine, Boolean> equivalenceOracle = new EquivalenceOracleImpl();
			result = equivalenceOracle.answerEquivalenceQuery(model);
			if(result == true) break;
			ceQuery = InputHelper.getCeWord(input);
			System.out.println("What is the output of " + ceQuery.getQueriedWord().toStringWithAlphabet() + "?");
			int n = InputHelper.getInteger();
			ceQuery.answerQuery(new HashableValueInt(n));
			learner.refineHypothesis(ceQuery);
		}
		
	}
}
