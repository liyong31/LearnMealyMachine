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

import cn.ac.ios.machine.Machine;
import cn.ac.ios.oracle.EquivalenceOracle;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.HashableValueBoolean;
import cn.ac.ios.util.UtilMachine;
import cn.ac.ios.words.APList;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;
import dk.brics.automaton.Automaton;

public class TeacherDK implements MembershipOracle<HashableValue>, EquivalenceOracle<Machine, Query<HashableValue>> {

	private final Automaton automaton;
	private final Machine machine;
	private final Alphabet alphabet;
	
	public TeacherDK(Machine machine, Alphabet alphabet) {
		this.automaton = UtilMachine.dfaToDkAutomaton(machine);
		this.machine = machine;
		this.alphabet = alphabet;
	}
	
	private Word parseString(String counterexample) {
		APList aps = machine.getInAPs();
		String[] wordStr = counterexample.split("");
		int[] wordArr = new int[wordStr.length];
		
		for(int letterNr = 0; letterNr < wordStr.length; letterNr ++) {
			wordArr[letterNr] = aps.indexOf(wordStr[letterNr]);
		}
		
		return alphabet.getArrayWord(wordArr);
	}
	
	@Override
	public Query<HashableValue> answerEquivalenceQuery(Machine machine) {
		
		Automaton conjecture = UtilMachine.dfaToDkAutomaton(machine);
		Automaton result = automaton.clone().minus(conjecture.clone());
		String counterexample = result.getShortestExample(true);
		Word wordCE = alphabet.getEmptyWord();
		boolean isEq = true;
		
		if(counterexample == null) {
			result = conjecture.clone().minus(automaton.clone());
			counterexample = result.getShortestExample(true);
		}
		
		if(counterexample != null) {
			wordCE = parseString(counterexample);
			isEq = false;
		}
		
		
		Query<HashableValue> ceQuery = new QuerySimple<>(wordCE);
		ceQuery.answerQuery(new HashableValueBoolean(isEq));
		return ceQuery;
	}

	@Override
	public HashableValue answerMembershipQuery(Query<HashableValue> query) {
		Word word = query.getQueriedWord();
		boolean result = machine.runDFA(word);
		return new HashableValueBoolean(result);
	}
	

}
