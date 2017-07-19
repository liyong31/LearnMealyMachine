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

package cn.ac.ios.learner.tree;

import java.util.BitSet;


import cn.ac.ios.tree.Node;
import cn.ac.ios.words.Word;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Value in node 
 * */
public class ValueNode {
	
	public final int id;
	public Node<ValueNode> node;
	public final Word label;
	public TIntObjectMap<BitSet> predecessors; // use more efficient way to store
	public TIntIntMap successors;              // may be deleted later
	
	public ValueNode(int id, Word label) {
		this.id = id;
		this.label = label;
		predecessors = new TIntObjectHashMap<>();
		successors = new TIntIntHashMap();
	}
	
	public void addPredecessor(int source, int letter) {
		if(predecessors.containsKey(letter)) {
			BitSet states = predecessors.get(letter);
			states.set(source);
		}else {
			BitSet states = new BitSet();
			states.set(source);
			predecessors.put(letter, states);
		}
	}
	
	public void addSuccessor(int letter, int target) {
		successors.put(letter, target);
	}
	
	public int getSuccessor(int letter) {
		if(successors.containsKey(letter)) 
		  return successors.get(letter);
		assert false : "no such letter key";
		return -1;
	}
	
	public String toString() {
		return id + " : " + label.toStringWithAlphabet();
	}

}
