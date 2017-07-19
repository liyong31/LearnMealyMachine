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
// only complete automaton is supported
public abstract class StateBase implements State {
	
	protected final int index;
	protected final Machine machine;
	protected final Transition[] trans;
	
	public StateBase(Machine machine, int index) {
		this.machine = machine;
		this.index = index;
		this.trans = new Transition[machine.getInAPs().size()];
	}
	
	@Override
	public Machine getMachine() {
		return machine;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public void addTransition(int letter, int state, int out) {
		assert letter < trans.length;
		// now allow to change the transition
		Transition tr = machine.makeTransition(state, out);
//		if(tr == null) {
//			tr = machine.makeTransition(state, out);
//		}
		trans[letter] = tr;
	}
	
	@Override
	public void addTransition(int letter, int state) {
		addTransition(letter, state, -1);
	}

	
	@Override
	public int getSuccessor(int letter) {
		Transition tr = trans[letter];
		assert tr != null;
		return tr.getSuccessor();
	}


}
