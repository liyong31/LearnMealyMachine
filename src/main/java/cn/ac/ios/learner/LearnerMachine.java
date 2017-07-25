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

package cn.ac.ios.learner;

import cn.ac.ios.machine.Machine;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.ExprValue;
import cn.ac.ios.table.ExprValueWord;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.HashableValueBoolean;

import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;

public abstract class LearnerMachine extends LearnerBase<Machine> {
	
	protected final Alphabet inAps;
	protected final MembershipOracle<HashableValue> membershipOracle;
	private boolean alreadyStarted = false;
	protected Machine machine;
	
	public LearnerMachine(Alphabet inAps, MembershipOracle<HashableValue> membershipOracle) {
		assert inAps != null && membershipOracle != null;
		this.inAps = inAps;
		this.membershipOracle = membershipOracle;
	}
	
	@Override
	public void startLearning() {
		if(alreadyStarted)
			try {
				throw new Exception("Learner should not be started twice");
			} catch (Exception e) {
				e.printStackTrace();
			}
		alreadyStarted = true;
		initialize();
	}
	
	protected abstract void initialize();
	
	protected ExprValue getCounterExampleWord(Query<HashableValue> query) {
		assert query != null;
		Word word = query.getQueriedWord();
		assert word != null;
		return new ExprValueWord(word);
	}
	
	protected ExprValue getExprValueWord(Word word) {
		return new ExprValueWord(word);
	}
	
	protected HashableValue getHashableValueBoolean(boolean result) {
		return new HashableValueBoolean(result);
	}

	@Override
	public Machine getHypothesis() {
		return machine;
	}
	
	public abstract Word getStateLabel(int state);
	
	protected abstract CeAnalyzer getCeAnalyzerInstance(ExprValue exprValue, HashableValue result);
	
	protected HashableValue processMembershipQuery(Word prefix, Word suffix) {
		Query<HashableValue> query = new QuerySimple<>(null, prefix, suffix, -1);
		return membershipOracle.answerMembershipQuery(query);
	}
	
	protected HashableValue processMembershipQuery(Query<HashableValue> query) {
		return membershipOracle.answerMembershipQuery(query);
	}
	
	// counter example analysis
	protected abstract class CeAnalyzer {
		
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
				
				Word sLabel = getStateLabel(s);
				Word tLabel = getStateLabel(t);
									
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
}
