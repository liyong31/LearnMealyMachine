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

public class MealyState {
	
	private final int index;
	private final MealyMachine machine;
	private final MealyTransition[] trans;
	
	public MealyState(MealyMachine machine, int index) {
		this.machine = machine;
		this.index = index;
		this.trans = new MealyTransition[machine.getInAPs().size()];
	}
	
	public MealyMachine getMachine() {
		return machine;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void addTransition(int letter, int state, int out) {
		assert letter < trans.length;
		MealyTransition tr = trans[letter];
		if(tr == null) {
			tr = new MealyTransition(state, out);
		}
		trans[letter] = tr;
	}
	
	public void addTransition(int letter, int state) {
		addTransition(letter, state, -1);
	}
	
	public void addOutput(int letter, int output) {
		MealyTransition tr = trans[letter];
		assert tr != null;
		tr.setOutput(output);
	}
	
	public int getSuccessor(int letter) {
		MealyTransition tr = trans[letter];
		assert tr != null;
		return tr.getSuccessor();
	}
	
	public int getOutput(int letter) {
		MealyTransition tr = trans[letter];
		assert tr != null;
		return tr.getOutput();
	}
	

}
