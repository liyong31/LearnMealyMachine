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

package cn.ac.ios.dfa;

import java.util.Random;

import cn.ac.ios.learner.Learner;
import cn.ac.ios.learner.table.dfa.LearnerDFATable;
import cn.ac.ios.learner.tree.dfa.LearnerDFATree;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class DFARandomTest {
	
	public static void main(String[] args) {
		
		if(args.length < 3) {
			System.out.println("Usage: <PROGRAM> <table|tree> <NUM_OF_CASES> <NUM_OF_STATES_FOR_CASE>");
			System.exit(0);
		}
		
		boolean table = true;
		if(args[0].equals("tree")) table = false;
		
		Alphabet input = new Alphabet(String.class);
		input.addLetter("a");
		input.addLetter("b");
		input.addLetter("c");
		
		int numCases = Integer.parseInt(args[1]);
		int numStates = Integer.parseInt(args[2]);
		int numOK = 0;
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < numCases; i ++) {
			Machine machine = getRandomAutomaton(input, numStates);
			System.out.println("Case " + i );
			System.out.println(machine.toString());
			if(testLearnerDFA(machine, input, table)) {
				numOK ++;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Tested " + numCases + " cases and " + numOK + " cases passed in "
						+ ((end-start) / 1000) + " secs !");
		
	}
	
	private static boolean testLearnerDFA(Machine machine, Alphabet alphabet, boolean table) {
		TeacherDK teacher = new TeacherDK(machine, alphabet);
		Learner<Machine, HashableValue> learner = null;
		if(table) learner = new LearnerDFATable(alphabet, teacher);
		else learner = new LearnerDFATree(alphabet, teacher);
		System.out.println("starting learning");
		learner.startLearning();

		while(true) {
			System.out.println("Table is both closed and consistent\n" + learner.toString());
			Machine model = learner.getHypothesis();
			// along with ce
			Query<HashableValue> ceQuery = teacher.answerEquivalenceQuery(model);
			boolean isEq = ceQuery.getQueryAnswer().get();
			if(isEq) {
				System.out.println(model.toString());
				break;
			}
//			HashableValue val = teacher.answerMembershipQuery(ceQuery);
//			ceQuery.answerQuery(val);
			learner.refineHypothesis(ceQuery);
		}
		
		return true;
	}
	
	public static Machine getRandomAutomaton(Alphabet alphabet, int numState) {
    	
    	Machine result = new DFA(alphabet.getAPs());

		Random r = new Random(System.currentTimeMillis());
		
		for(int i = 0; i < numState; i ++) {
			result.createState();
		}
		
		// add self loops for those transitions
		for(int i = 0; i < numState; i ++) {
			State state = result.getState(i);
			for(int k=0 ; k < alphabet.getAPs().size(); k++){
				state.addTransition(k, i);
			}
		}
		
		result.setInitial(0);
		
		// final states
		int numF = r.nextInt(numState-1);
		boolean hasF = false;
		numF = numF > 0 ? numF : 1;
		for(int n = 0; n < numF ; n ++) {
			int f = r.nextInt(numF);
			if(f != 0) {
				result.getAcceptance().setFinal(f);
				hasF = true;
			}
		}
		
		if(! hasF) {
			result.getAcceptance().setFinal(numF);
		}
		
		int numTrans = r.nextInt(numState * alphabet.getAPs().size());
		
		// transitions
		for(int k=0 ; k < alphabet.getAPs().size(); k++){
			for(int n = 0; n < numTrans; n++ ){
				int i=r.nextInt(numState);
				int j=r.nextInt(numState);
				result.getState(i).addTransition(k, j);
			}
		}
				
		return result;
	}

}
