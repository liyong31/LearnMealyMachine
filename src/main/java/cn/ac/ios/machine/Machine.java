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

package cn.ac.ios.machine;

import cn.ac.ios.words.APList;
import cn.ac.ios.words.Word;

// only support for DFA and deterministic Mealy machine
public interface Machine {
	
	Acceptance getAcceptance();
	
	APList getInAPs();
	
	APList getOutAPs();
	
	State getState(int index);
	
	int getStateSize();
	
	State createState();
	
	void setInitial(int state);
	
	default void setInitial(State state) {
		setInitial(state.getIndex());
	}
	
	int getInitialState();
	
	default boolean runDFA(Word word) {
		int stateNr = getSuccessor(getInitialState(), word);
		return getAcceptance().isFinal(stateNr);
	}
	
	default int runMealy(Word word) {
		int stateNr = getSuccessor(getInitialState(), word.getPrefix(word.length() - 1));
		return getState(stateNr).getOutput(word.getLastLetter());
	}
	
	default int getSuccessor(int state, int letter) {
		assert state < getStateSize() && letter < getInAPs().size();
		return getState(state).getSuccessor(letter);
	}
	
	default int getSuccessor(Word word) {
		return getSuccessor(getInitialState(), word);
	}
	
	default int getSuccessor(int state, Word word) {
		int s = state;
		for(int letterNr = 0; letterNr < word.length(); letterNr ++) {
			s = getSuccessor(s, word.getLetter(letterNr));
		}
		return s;
	}
	
	Transition makeTransition(int state, int out);
	State makeState(int index);
	

}
