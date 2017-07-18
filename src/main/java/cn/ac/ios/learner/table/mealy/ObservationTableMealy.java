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

package cn.ac.ios.learner.table.mealy;

import cn.ac.ios.table.ExprValueWord;
import cn.ac.ios.table.HashableValueInt;
import cn.ac.ios.table.ObservationRowBase;
import cn.ac.ios.table.ObservationTableBase;
import cn.ac.ios.words.Word;

public class ObservationTableMealy extends ObservationTableBase {

	@Override
	public ObservationRowBase getRowInstance(Word word) {
		// TODO Auto-generated method stub
		return new ObservationRowBase(word);
	}

	protected HashableValueInt getHashableValueInt(int value) {
		return new HashableValueInt(value);
	}
	
	protected int addColumn(Word word) {
		return addColumn(new ExprValueWord(word));
	}
}
