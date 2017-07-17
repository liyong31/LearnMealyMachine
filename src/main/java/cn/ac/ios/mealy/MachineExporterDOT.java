package cn.ac.ios.mealy;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;


import cn.ac.ios.words.APList;

public class MachineExporterDOT {
	
    public static void export(MealyMachine automata, OutputStream stream) {

        PrintStream out = new PrintStream(stream);

        out.println("// Mealy Machine");
        out.println("digraph {");
        int startNode = automata.getStateSize();
        for (int node = 0; node < automata.getStateSize(); node++) {
            out.print("  " + node + " [label=\"" + node + "\"");
            out.print(", shape = circle ]; \n");
        }	
        out.println("  " + startNode + " [label=\"\", shape = plaintext];");
        out.println();
        APList inAps = automata.getInAPs();
        APList outAps = automata.getOutAPs();
        for (int node = 0; node < automata.getStateSize(); node++) {
        	if(node == automata.getInitialState()) {
        		out.println("  " + startNode + " -> " + node + " [label=\"\"];");
        	}
        	for(int letter = 0; letter < inAps.size(); letter ++) {
        		int succ = automata.getSuccessor(node, letter);
        		int outNr = automata.getState(node).getOutput(letter);
        		out.println("  " + node + " -> " + succ
        				+ " [label=\"" + inAps.get(letter) + "|" + outAps.get(outNr) + "\"];");
        	}
        }

        out.println("}");
    }
    
    public static String toString(MealyMachine automata) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            export(automata, out);
            return out.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

}
