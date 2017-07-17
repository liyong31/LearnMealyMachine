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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ac.ac.ios.oracle.MembershipOracle;
import ac.ac.ios.query.Query;
import ac.ac.ios.table.HashableValue;
import ac.ac.ios.table.HashableValueInt;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;


public class MembershipOracleImpl implements MembershipOracle<HashableValue> {
	
	private final Alphabet outputs;
	
	public MembershipOracleImpl(Alphabet outputs) {
		this.outputs = outputs;
	}

	@Override
	public HashableValue answerMembershipQuery(Query<HashableValue> query) {
		Word queriedWord = query.getQueriedWord();
		System.out.println("What is the output of word " + queriedWord.toStringWithAlphabet() + " in the unknown languge: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		HashableValue answer = null;
		try {
			boolean finished = false;
			while(! finished) {
				String input = reader.readLine();
				Integer n = Integer.parseInt(input);
				if(n != null && outputs.getAPs().indexOf(n) != -1) {
					answer = new HashableValueInt(n);
					finished = true;
				}else {
					System.out.println("Illegal input, try again!");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query.answerQuery(answer);
		return answer;
	}

}
