package cn.ac.ios.mealy;

import cn.ac.ios.learner.mealy.LearnerMealyTable;
import cn.ac.ios.machine.mealy.MealyMachine;
import cn.ac.ios.oracle.EquivalenceOracle;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.HashableValueInt;
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

		LearnerMealyTable learner = new LearnerMealyTable(input, output, membershipOracle);
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

