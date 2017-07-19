package cn.ac.ios.learner.table;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ios.learner.Learner;

import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.ExprValue;
import cn.ac.ios.table.ExprValueWord;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.ObservationRow;
import cn.ac.ios.table.ObservationTableAbstract;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;

public abstract class LearnerTable implements Learner<Machine, HashableValue> {

	protected final Alphabet inAps;
	protected ObservationTableAbstract observationTable;
	protected final MembershipOracle<HashableValue> membershipOracle;
	private boolean alreadyStarted = false;
	protected Machine machine;
	
	public LearnerTable(Alphabet inAps
			, MembershipOracle<HashableValue> membershipOracle) {
		assert inAps != null && membershipOracle != null;
		this.inAps = inAps;
		this.membershipOracle = membershipOracle;
		this.observationTable = getTableInstance();
	}
	
	@Override
	public void startLearning() {
		if(alreadyStarted )
			try {
				throw new Exception("Learner should not be started twice");
			} catch (Exception e) {
				e.printStackTrace();
			}
		alreadyStarted = true;
		initializeTable();
	}
	
	protected ExprValue getCounterExampleWord(Query<HashableValue> query) {
		assert query != null;
		Word word = query.getQueriedWord();
		assert word != null;
		return new ExprValueWord(word);
	}
		
	protected Query<HashableValue> processMembershipQuery(ObservationRow row, int offset, ExprValue valueExpr) {
		Query<HashableValue> query = new QuerySimple<>(row, row.getWord(), valueExpr.get(), offset);
		HashableValue result = membershipOracle.answerMembershipQuery(query);
		Query<HashableValue> queryResult = new QuerySimple<>(row, row.getWord(), valueExpr.get(), offset);
		queryResult.answerQuery(result);
		return queryResult;
	}
	
	protected HashableValue processMembershipQuery(Word prefix, Word suffix) {
		Query<HashableValue> query = new QuerySimple<>(null, prefix, suffix, -1);
		return membershipOracle.answerMembershipQuery(query);
	}
	
	protected void initializeTable() {
		
		Word wordEmpty = inAps.getEmptyWord();
		observationTable.addUpperRow(wordEmpty);
		ExprValue exprValue = getExprValueWord(wordEmpty);
		
		// add empty word column
		observationTable.addColumn(exprValue);
		// add every alphabet
		for(int letterNr = 0; letterNr < inAps.getAPs().size(); letterNr ++) {
			observationTable.addLowerRow(inAps.getLetterWord(letterNr));
		}
		
		// ask initial queries for upper table
		processMembershipQueries(observationTable.getUpperTable()
				, 0, observationTable.getColumns().size());
		// ask initial queries for lower table
		processMembershipQueries(observationTable.getLowerTable()
				, 0, observationTable.getColumns().size());
		
		makeTableClosed();
		
	}
	
	protected void processMembershipQueries(List<ObservationRow> rows
			, int colOffset, int length) {
		List<Query<HashableValue>> results = new ArrayList<>();
		List<ExprValue> columns = observationTable.getColumns();
		int endNr = length + colOffset;
		for(ObservationRow row : rows) {
			for(int colNr = colOffset; colNr < endNr; colNr ++) {
				results.add(processMembershipQuery(row, colNr, columns.get(colNr)));
			}
		}
		putQueryAnswers(results);
	}
		
	protected void putQueryAnswers(List<Query<HashableValue>> queries) {
		for(Query<HashableValue> query : queries) {
			putQueryAnswers(query);
		}
	}
	
	protected void putQueryAnswers(Query<HashableValue> query) {
		ObservationRow row = query.getPrefixRow();
		HashableValue result = query.getQueryAnswer();
		assert result != null;
		row.set(query.getSuffixColumn(), result);
	}
	
	protected void makeTableClosed() {
		ObservationRow lowerRow = observationTable.getUnclosedLowerRow();
		
		while(lowerRow != null) {
			// 1. move to upper table
			observationTable.moveRowFromLowerToUpper(lowerRow);
			// 2. add one letter to lower table
			List<ObservationRow> newLowerRows = new ArrayList<>();
			for(int letterNr = 0; letterNr < inAps.getAPs().size(); letterNr ++) {
				Word newWord = lowerRow.getWord().append(letterNr);
				ObservationRow row = observationTable.getTableRow(newWord); // already existing
				if(row != null) continue;
				ObservationRow newRow = observationTable.addLowerRow(newWord);
				newLowerRows.add(newRow);
			}
			// 3. process membership queries
			processMembershipQueries(newLowerRows, 0, observationTable.getColumns().size());
			lowerRow = observationTable.getUnclosedLowerRow();
		}
		
		constructHypothesis();
	}
	
    // return counter example for hypothesis
	@Override
	public void refineHypothesis(Query<HashableValue> ceQuery) {
		
		ExprValue exprValue = getCounterExampleWord(ceQuery);
		CeAnalyzer analyzer = getCeAnalyzerInstance(exprValue, ceQuery.getQueryAnswer());
		analyzer.analyze();
		observationTable.addColumn(analyzer.getNewColumn()); // add new experiment
		processMembershipQueries(observationTable.getUpperTable(), observationTable.getColumns().size() - 1, 1);
		processMembershipQueries(observationTable.getLowerTable(), observationTable.getColumns().size() - 1, 1);
		
		makeTableClosed();
		
	}
	
	@Override
	public Machine getHypothesis() {
		return machine;
	}
	
	// Default learner for DFA
	protected void constructHypothesis() {
		
		machine = new DFA(inAps.getAPs());
		
		List<ObservationRow> upperTable = observationTable.getUpperTable();
		
		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			machine.createState();
		}
		
		for(int rowNr = 0; rowNr < upperTable.size(); rowNr ++) {
			State state = machine.getState(rowNr);
			for(int letterNr = 0; letterNr < inAps.getAPs().size(); letterNr ++) {
				int succNr = getSuccessorRow(rowNr, letterNr);
				state.addTransition(letterNr, succNr);
			}
			
			if(getStateLabel(rowNr).isEmpty()) {
				machine.setInitial(rowNr);
			}
			
			if(isAccepting(rowNr)) {
				machine.getAcceptance().setFinal(rowNr);
			}
		}
		
	}
	
	// a state is accepting iff it accepts empty language
	private boolean isAccepting(int state) {
		ObservationRow stateRow = observationTable.getUpperTable().get(state);
		int emptyNr = observationTable.getColumnIndex(getExprValueWord(inAps.getEmptyWord()));
		assert emptyNr != -1 : "index -> " + emptyNr;
		return stateRow.getValues().get(emptyNr).isAccepting();
	}
	
	protected ExprValue getExprValueWord(Word word) {
		return new ExprValueWord(word);
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
	
	public Word getStateLabel(int state) {
		return observationTable.getUpperTable().get(state).getWord();
	}
	
	protected CeAnalyzer getCeAnalyzerInstance(ExprValue exprValue, HashableValue result) {
		return new CeAnalyzer(exprValue, result);
	}
	
	// counter example analysis
	protected class CeAnalyzer {
		protected ExprValue column;
		protected final ExprValue exprValue; 
		protected final HashableValue result;
		
		public CeAnalyzer(ExprValue exprValue, HashableValue result) {
			this.exprValue = exprValue;
			this.result = result;
		}
		
		public ExprValue getNewColumn() {
			return column;
		}
		
		
		public void analyze() {
			
			Word wordCE = exprValue.get();
			int low = 0, high = wordCE.length() - 1;
			while(low <= high) {
				
				int mid = (low + high) / 2;
				
				assert mid < wordCE.length();

				int s = machine.getSuccessor(wordCE.getPrefix(mid));
				int t = machine.getSuccessor(s, wordCE.getLetter(mid));
				
				Word sLabel = observationTable.getUpperTable().get(s).getWord();
				Word tLabel = observationTable.getUpperTable().get(t).getWord();
									
				HashableValue memS = processMembershipQuery(sLabel, wordCE.getSuffix(mid));
				HashableValue memT = processMembershipQuery(tLabel, wordCE.getSuffix(mid + 1));
				
				if (! memS.valueEqual(memT)) {
					column = getExprValueWord(wordCE.getSuffix(mid + 1));
					break;
				}

				if (memS.valueEqual(result)) {
					low = mid + 1;
				} else {
					high = mid;
				}
			}
			
		}
	
	}
	
	protected abstract ObservationTableAbstract getTableInstance();

}

