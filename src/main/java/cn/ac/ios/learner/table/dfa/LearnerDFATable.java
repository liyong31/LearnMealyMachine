package cn.ac.ios.learner.table.dfa;

import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.learner.table.LearnerTable;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.ExprValue;
import cn.ac.ios.table.HashableValue;
//import cn.ac.ios.table.HashableValueBoolean;
import cn.ac.ios.table.ObservationRow;
import cn.ac.ios.table.ObservationTableAbstract;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;

public class LearnerDFATable  extends LearnerTable {
	
	public LearnerDFATable(Alphabet aps, MembershipOracle<HashableValue> membershipOracle) {
		super(aps, membershipOracle);
	}
	

	@Override
	public LearnerType getLearnerType() {
		return LearnerType.DFA;
	}

	@Override
	protected Query<HashableValue> processMembershipQuery(ObservationRow row, int offset, ExprValue valueExpr) {
		Query<HashableValue> query = new QuerySimple<>(row, row.getWord(), valueExpr.get(), offset);
		HashableValue result = membershipOracle.answerMembershipQuery(query);
		Query<HashableValue> queryResult = new QuerySimple<>(row, row.getWord(), valueExpr.get(), offset);
		queryResult.answerQuery(result);
		return queryResult;
	}
	
//	private HashableValue getHashableValueBoolean(boolean result) {
//		return new HashableValueBoolean(result);
//	}
	
	private boolean processMembershipQuery(Word prefix, Word suffix) {
		Query<HashableValue> query = new QuerySimple<>(null, prefix, suffix, -1);
		return membershipOracle.answerMembershipQuery(query).get();
	}


	@Override
	protected CeAnalyzer getCeAnalyzerInstance(ExprValue exprValue, HashableValue result) {
		return new CeAnalyzerDFA(exprValue, result);
	}


	@Override
	protected ObservationTableAbstract getTableInstance() {
		return new ObservationTableDFA();
	}
	
	private class CeAnalyzerDFA extends CeAnalyzer {

		public CeAnalyzerDFA(ExprValue exprValue, HashableValue result) {
			super(exprValue, result);
		}

		@Override
		public void analyze() {
			boolean memCE = result.get();
			Word wordCE = exprValue.get();
			int low = 0, high = wordCE.length() - 1;
			while(low <= high) {
				
				int mid = (low + high) / 2;
				
				assert mid < wordCE.length();

				int s = machine.getSuccessor(wordCE.getPrefix(mid));
				int t = machine.getSuccessor(s, wordCE.getLetter(mid));
				
				Word sLabel = observationTable.getUpperTable().get(s).getWord();
				Word tLabel = observationTable.getUpperTable().get(t).getWord();
									
				boolean memS = processMembershipQuery(sLabel, wordCE.getSuffix(mid));
				boolean memT = processMembershipQuery(tLabel, wordCE.getSuffix(mid + 1));
				
				if (memS != memT) {
					column = getExprValueWord(wordCE.getSuffix(mid + 1));
					break;
				}

				if (memS == memCE) {
					low = mid + 1;
				} else {
					high = mid;
				}
			}
			
		}
		
	}

}
