/* Copyright (c) 2016, 2017                                               */
/*       Institute of Software, Chinese Academy of Sciences               */
/* This file is part of ROLL, a Regular Omega Language Learning library.  */
/* ROLL is free software: you can redistribute it and/or modify           */
/* it under the terms of the GNU General Public License as published by   */
/* the Free Software Foundation, either version 3 of the License, or      */
/* (at your option) any later version.                                    */

/* This program is distributed in the hope that it will be useful,        */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of         */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          */
/* GNU General Public License for more details.                           */

/* You should have received a copy of the GNU General Public License      */
/* along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

package cn.ac.ios.mealy;

import cn.ac.ios.learner.table.mealy.LearnerMealyTable;
import cn.ac.ios.machine.Machine;
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
			
			Machine model = learner.getHypothesis();
//			System.out.println("automaton\n" + model.toString());
			
			Query<HashableValue> ceQuery = learner.makeTableConsistent();
			
			if(ceQuery != null) {
				learner.refineHypothesis(ceQuery);
				continue;
			}
			
			EquivalenceOracle<Machine, Boolean> equivalenceOracle = new EquivalenceOracleImpl();
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

