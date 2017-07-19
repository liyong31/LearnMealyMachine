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

package cn.ac.ios.dfa;

import cn.ac.ios.machine.Machine;
import cn.ac.ios.oracle.EquivalenceOracle;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import dk.brics.automaton.Automaton;

public class TeacherDK implements MembershipOracle<HashableValue>, EquivalenceOracle<Machine, Query<HashableValue>> {

	private Automaton automaton;
	
	public TeacherDK(Machine machine) {
		
	}
	
	@Override
	public Query<HashableValue> answerEquivalenceQuery(Machine automaton) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashableValue answerMembershipQuery(Query<HashableValue> query) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
