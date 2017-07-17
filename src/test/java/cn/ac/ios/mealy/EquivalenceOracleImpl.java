/* Written by Yong Li, Depeng Liu                                       */
/* Copyright (c) 2016                  	                               */
/* This program is free software: you can redistribute it and/or modify */
/* it under the terms of the GNU General Public License as published by */
/* the Free Software Foundation, either version 3 of the License, or    */
/* (at your option) any later version.                                  */

/* This program is distributed in the hope that it will be useful,      */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of       */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        */
/* GNU General Public License for more details.                         */

/* You should have received a copy of the GNU General Public License    */
/* along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package cn.ac.ios.mealy;

import ac.ac.ios.oracle.EquivalenceOracle;

public class EquivalenceOracleImpl implements EquivalenceOracle<MealyMachine, Boolean>{

	@Override
	public Boolean answerEquivalenceQuery(MealyMachine automaton) {
		
		if(automaton != null) {
			System.out.println("Is following automaton the unknown automaton: 1/0?");
			System.out.println(automaton.toString());
		}else {
			System.out.println("Is above automaton the unknown automaton: 1/0?");
		}
		boolean answer = InputHelper.getInputAnswer();
		if(! answer) {
			System.out.println("Please input a counterexample:");
		}
		return answer;
	}

}
