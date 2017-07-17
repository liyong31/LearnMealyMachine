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

import cn.ac.ios.query.Query;
import cn.ac.ios.query.QuerySimple;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;

/** for interactive mode */
public class InputHelper {
	
	public static boolean getInputAnswer() {
		boolean answer = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			boolean finished = false;
			while(! finished) {
				String input = reader.readLine();
				if(input.equals("1")) {
					answer = true;
					finished = true;
				}else if(input.equals("0")) {
					answer = false;
					finished = true;
				}else {
					System.out.println("Illegal input, try again!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	public static Query<HashableValue> getCeWord(Alphabet inputs) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Word word = null;
		try {
			do {
				String input = reader.readLine();
				String[] wordStr = input.split("");
				int[] wordArr = new int[wordStr.length];
				for(int index = 0; index < wordStr.length; index ++) {
					int letter = inputs.getAPs().indexOf(Integer.parseInt(wordStr[index]));
					if(letter == -1) return null;
					wordArr[index] = letter;
				}
				word = inputs.getArrayWord(wordArr);
				if(word == null)	System.out.println("Illegal input, try again!");
			}while(word == null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new QuerySimple<HashableValue>(word);
	}
	
	public static int getInteger() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int numLetters = -1;
		
			do {
				String input = null;
				try {
					input = reader.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					numLetters = Integer.parseInt(input);
				} catch (Exception e) {
					numLetters = -1;
				}
				if(numLetters == -1)	System.out.println("Illegal input, try again!");
			}while(numLetters == -1);
			
		return numLetters;
	}
	
	public static String getLetter() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String letter = null;
		do {
			try {
				letter = reader.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (letter == null)
				System.out.println("Illegal input, try again!");
		} while (letter == null);
			
		return letter;
	}

}
