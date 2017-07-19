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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.ac.ios.mealy.InputHelper;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.table.HashableValueBoolean;
import cn.ac.ios.table.HashableValueInt;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;

public class MembershipOracleImpl implements MembershipOracle<HashableValue> {
	
	public MembershipOracleImpl() {
	}

	@Override
	public HashableValue answerMembershipQuery(Query<HashableValue> query) {
		Word queriedWord = query.getQueriedWord();
		System.out.println("Is the word " + queriedWord.toStringWithAlphabet() + " in the unknown languge?: 1/0");
		boolean answer = false;
		answer = InputHelper.getInputAnswer();
		return new HashableValueBoolean(answer);
	}

}
