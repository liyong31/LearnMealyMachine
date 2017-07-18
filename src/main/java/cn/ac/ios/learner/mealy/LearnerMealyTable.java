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

package cn.ac.ios.learner.mealy;

import java.util.ArrayList;

import java.util.List;

import cn.ac.ios.words.Word;

import cn.ac.ios.learner.LearnerBase;
import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.mealy.MealyMachine;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.ExprValue;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.HashableValueInt;
import cn.ac.ios.table.ObservationRow;
import cn.ac.ios.words.Alphabet;

//TODO later we should support totally output alphabets
public class LearnerMealyTable extends LearnerBase<MealyMachine>{
	
	private final Alphabet inAps;
	private final Alphabet outAps;
	private final MembershipOracle<HashableValue> membershipOracle;
	private final ObservationTableMealy observationTable;
	private boolean alreadyStarted = false;
	private MealyMachine machine;

	public LearnerMealyTable(Alphabet inAps, Alphabet outAps, MembershipOracle<HashableValue> membershipOracle) {
		this.inAps = inAps;
		this.outAps = outAps;
		this.membershipOracle = membershipOracle;
		this.observationTable = new ObservationTableMealy();
	}
	
	@Override
	public LearnerType getLearnerType() {
		return LearnerType.MEALY;
	}

	@Override
	public void startLearning() {
		// TODO Auto-generated method stub
		if(alreadyStarted)
			try {
				throw new Exception("Learner should not be started twice");
			} catch (Exception e) {
				e.printStackTrace();
			}
		alreadyStarted = true;
		initializeTable();
	}
	
	private void initializeTable() {
		// first upper table empty word
		observationTable.addUpperRow(inAps.getEmptyWord());
		// then lower table
		for(int letterNr = 0; letterNr < inAps.getAPs().size(); letterNr ++) {
			observationTable.addLowerRow(inAps.getLetterWord(letterNr));
		}
	}

	private void processMembershipQueries(List<ObservationRow> rows
			, int colOffset, int length) {
		List<Query<HashableValue>> membershipQueries = new ArrayList<>();
		List<ExprValue> columns = observationTable.getColumns();
		int endNr = length + colOffset;
		for(ObservationRow row : rows) {
			for(int colNr = colOffset; colNr < endNr; colNr ++) {
				membershipQueries.add(new QuerySimple<>(row, row.getWord(), columns.get(colNr).get(), colNr));
			}
		}
		processMembershipQueries(membershipQueries);
	}
	
	private HashableValue getOutput(Word word) {
		HashableValue answer = membershipOracle.answerMembershipQuery(new QuerySimple<>(word));
		assert answer instanceof HashableValueInt;
		return answer;
	}
	
	private void processMembershipQueries(List<Query<HashableValue>> queries) {
		membershipOracle.answerMembershipQueries(queries);
		for(Query<HashableValue> query : queries) {
			HashableValue result = query.getQueryAnswer();
			assert result != null;
			ObservationRow row = query.getPrefixRow();
			row.set(query.getSuffixColumn(), result);
		}
	}
	
	private void makeTableClosed() {
		ObservationRow lowerRow = observationTable.getUnclosedLowerRow();		
		while(lowerRow != null) {
			// 1. move to upper table
			observationTable.moveRowFromLowerToUpper(lowerRow);
			// 2. add one letter to lower table
			List<ObservationRow> newLowerRows = new ArrayList<>();
			for(int letterNr = 0; letterNr < inAps.getAPs().size(); letterNr ++) {
				Word newWord = lowerRow.getWord().append(letterNr);
				newLowerRows.add(observationTable.addLowerRow(newWord));
			}
			// 3. process membership queries
			processMembershipQueries(newLowerRows, 0, observationTable.getColumns().size());
			lowerRow = observationTable.getUnclosedLowerRow();
		}
		
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
					query.answerQuery(observationTable.getHashableValueInt(val));
					return query;
				}
			}
		}
		return null;
	}

    // return counter example for hypothesis
	@Override
	public void refineHypothesis(Query<HashableValue> ceQuery) {
		Word wordCE = ceQuery.getQueriedWord();
		HashableValue output = ceQuery.getQueryAnswer();
		// 
		// binary search, low and high are the lengths of prefix
		Word column = findSuffix(wordCE, output);
		assert column != null;
		
		observationTable.addColumn(column); // add new experiment
		processMembershipQueries(observationTable.getUpperTable(), observationTable.getColumns().size() - 1, 1);
		processMembershipQueries(observationTable.getLowerTable(), observationTable.getColumns().size() - 1, 1);
		
		makeTableClosed();
	}	

	@Override
	public MealyMachine getHypothesis() {
		// we construct the automaton
		MealyMachine machine = new MealyMachine(inAps.getAPs(), outAps.getAPs());
		List<ObservationRow> upperTable = observationTable.getUpperTable();

		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			machine.createState();
		}
		// add states one by one, ordered by the order of occurences
		// and should in a increased order, maybe change this later
		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			State state = machine.getState(rowNr);
			for(int letter = 0; letter < inAps.getAPs().size(); letter ++) {
				int succ = getSuccessorRow(rowNr, letter);
				int out = getOutput(upperTable.get(rowNr).getWord().append(letter)).get();
				state.addTransition(letter, succ, out);
			}
		}
		this.machine = machine;
		return this.machine;
	}
	
	protected int getSuccessorRow(int state, int letter) {
		ObservationRow stateRow = observationTable.getUpperTable().get(state);
		Word succWord = stateRow.getWord().append(letter);

		// search in upper table
		for(int succ = 0; succ < observationTable.getUpperTable().size(); succ ++) {
			ObservationRow succRow = observationTable.getUpperTable().get(succ);
			if(succRow.getWord().equals(succWord)) {
				return succ;
			}
		}
		// search in lower table
		ObservationRow succRow = observationTable.getLowerTableRow(succWord);
		assert succRow != null;
		for(int succ = 0; succ < observationTable.getUpperTable().size(); succ ++) {
			ObservationRow upperRow = observationTable.getUpperTable().get(succ);
			if(succRow.valuesEqual(upperRow)) {
				return succ;
			}
		}
		assert false : "successor values not found";
		return -1;
	}

	
	public String toString() {
		return observationTable.toString();
	}
	
	//find suffix to add 
	private Word findSuffix(Word wordCE, HashableValue output) {
		Word column = null;
		int low = 0, high = wordCE.length() - 1;
		while(low <= high) {
			
			int mid = (low + high) / 2;
			
			assert mid < wordCE.length();

			int s = machine.getSuccessor(wordCE.getPrefix(mid));
			int t = machine.getSuccessor(s, wordCE.getLetter(mid));
			
			Word sLabel = observationTable.getUpperTable().get(s).getWord();
			Word tLabel = observationTable.getUpperTable().get(t).getWord();
								
			HashableValue memS = getOutput(sLabel.concat(wordCE.getSuffix(mid)));
			HashableValue memT = getOutput(tLabel.concat(wordCE.getSuffix(mid + 1)));
			
			
			if (!memS.valueEqual(memT)) {
				column = wordCE.getSuffix(mid + 1);
				break;
			}

			if (memS.valueEqual(output)) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}
		return column;
	}


}
