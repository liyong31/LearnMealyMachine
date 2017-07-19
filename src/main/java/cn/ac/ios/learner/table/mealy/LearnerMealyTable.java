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

package cn.ac.ios.learner.table.mealy;


import java.util.List;

import cn.ac.ios.words.Word;

import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.learner.table.dfa.LearnerDFATable;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.mealy.MealyMachine;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.ExprValue;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.HashableValueInt;
import cn.ac.ios.table.ObservationRow;
import cn.ac.ios.table.ObservationTableAbstract;
import cn.ac.ios.words.Alphabet;

//TODO later we should support totally output alphabets
public class LearnerMealyTable extends LearnerDFATable {
	private final Alphabet outAps;

	public LearnerMealyTable(Alphabet inAps, Alphabet outAps, MembershipOracle<HashableValue> membershipOracle) {
		super(inAps, membershipOracle);
		this.outAps = outAps;
	}
	
	@Override
	public LearnerType getLearnerType() {
		return LearnerType.MEALY_TABLE;
	}
	
	
	protected void initialize() {
		// first upper table empty word
		observationTable.addUpperRow(inAps.getEmptyWord());
		// then lower table
		for(int letterNr = 0; letterNr < inAps.getAPSize(); letterNr ++) {
			observationTable.addLowerRow(inAps.getLetterWord(letterNr));
		}
		makeTableClosed();
	}

	
	private HashableValue getOutput(Word word) {
		HashableValue answer = membershipOracle.answerMembershipQuery(new QuerySimple<>(word));
		assert answer instanceof HashableValueInt;
		return answer;
	}
	
	protected HashableValueInt getHashableValueInt(int value) {
		return new HashableValueInt(value);
	}
	
	public Query<HashableValue> makeTableConsistent() {

		// check whether it is consistent with observation table
		List<ObservationRow> upperTable = observationTable.getUpperTable();
		List<ExprValue> columns = observationTable.getColumns();
		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			for(int colNr = 0; colNr < columns.size(); colNr ++) {
				Word suffix = columns.get(colNr).get();
				Word word = upperTable.get(rowNr).getWord().concat(suffix);
				int val = upperTable.get(rowNr).getValues().get(colNr).get();
				boolean equal = machine.runMealy(word) == val;
				if(! equal) {
					Query<HashableValue> query = new QuerySimple<>(word);
					query.answerQuery(getHashableValueInt(val));
					return query;
				}
			}
		}
		return null;
	}
	
	protected void constructHypothesis() {
		MealyMachine machine = new MealyMachine(inAps.getAPs(), outAps.getAPs());
		List<ObservationRow> upperTable = observationTable.getUpperTable();

		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			machine.createState();
		}

		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			State state = machine.getState(rowNr);
			for(int letter = 0; letter < inAps.getAPSize(); letter ++) {
				int succ = getSuccessorRow(rowNr, letter);
				//TODO reuse the result from this.machine
				// unless membership query is expensive otherwise I will not
				// try to improve this part
				int out = getOutput(upperTable.get(rowNr).getWord().append(letter)).get();
				state.addTransition(letter, succ, out);
			}
		}
		this.machine = machine;
	}

	@Override
	protected ObservationTableAbstract getTableInstance() {
		return new ObservationTableMealy();
	}


}
