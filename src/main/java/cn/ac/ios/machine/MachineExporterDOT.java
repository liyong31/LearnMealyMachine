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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.machine.mealy.MealyMachine;
import cn.ac.ios.words.APList;

public class MachineExporterDOT {
	
    public static void export(Machine machine, OutputStream stream) {

        PrintStream out = new PrintStream(stream);

        if(machine instanceof MealyMachine) out.println("// Mealy Machine");
        else out.println("// DFA");
        out.println("digraph {");
        int startNode = machine.getStateSize();
        for (int node = 0; node < machine.getStateSize(); node++) {
            out.print("  " + node + " [label=\"" + node + "\"");
            boolean dc = false;
            if((machine instanceof DFA)){
            	DFA dfa = (DFA)machine;
            	if(dfa.getAcceptance().isFinal(node)) {
            		dc = true;
            	}
            }
            if(dc) out.print(", shape = doublecircle ]; \n");
            else out.print(", shape = circle ]; \n");
        }	
        out.println("  " + startNode + " [label=\"\", shape = plaintext];");
        out.println();
        APList inAps = machine.getInAPs();
        for (int node = 0; node < machine.getStateSize(); node++) {
        	if(node == machine.getInitialState()) {
        		out.println("  " + startNode + " -> " + node + " [label=\"\"];");
        	}
        	for(int letter = 0; letter < inAps.size(); letter ++) {
        		int succ = machine.getSuccessor(node, letter);
        		String outStr = "";
        		if(machine instanceof MealyMachine) {
        			int outNr = machine.getState(node).getOutput(letter);
        			outStr += "|" + machine.getOutAPs().get(outNr);
        		}
        		out.println("  " + node + " -> " + succ
        				+ " [label=\"" + inAps.get(letter) + outStr + "\"];");
        	}
        }

        out.println("}");
    }
    
    public static String toString(Machine machine) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            export(machine, out);
            return out.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

}
