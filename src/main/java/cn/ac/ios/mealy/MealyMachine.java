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

import java.util.ArrayList;
import java.util.List;

import cn.ac.ios.words.APList;
import cn.ac.ios.words.Word;

public class MealyMachine {
	
	private int initState;
	private final APList iApList;
	private final APList oApList;
	private final List<MealyState> states;
	
	public MealyMachine(APList iAps, APList oAps) {
		this.iApList = iAps;
		this.oApList = oAps;
		this.states = new ArrayList<>();
	}
	
	public APList getInAPs() {
		return iApList;
	}
	
	public APList getOutAPs() {
		return oApList;
	}
	
	public int getStateSize() {
		return states.size();
	}
	
	public void setInitial(int state) {
		initState = state;
	}
	
	public MealyState createState() {
		MealyState state = new MealyState(this, states.size());
		states.add(state);
		return state;
	}
	
	public MealyState getState(int state) {
		assert state < states.size();
		return states.get(state);
	}
	
	public int getInitialState() {
		return initState;
	}
	
	public int run(Word word) {
		int stateNr = getSuccessor(initState, word.getPrefix(word.length() - 1));
		return getState(stateNr).getOutput(word.getLastLetter());
	}
	
	public int getSuccessor(int state, int letter) {
		assert state < states.size() && letter < iApList.size();
		return getState(state).getSuccessor(letter);
	}
	
	public int getSuccessor(Word word) {
		return getSuccessor(initState, word);
	}
	
	public int getSuccessor(int state, Word word) {
		int s = state;
		for(int letterNr = 0; letterNr < word.length(); letterNr ++) {
			s = getSuccessor(s, word.getLetter(letterNr));
		}
		return s;
	}
	
	public String toString() {
		return MachineExporterDOT.toString(this);
	}
	
	

}
