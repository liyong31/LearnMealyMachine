package cn.ac.ios.dfa;

import cn.ac.ios.learner.Learner;
import cn.ac.ios.learner.table.dfa.LearnerDFATable;
import cn.ac.ios.learner.tree.dfa.LearnerDFATree;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class DFALearnCompare {
	
	private static int numMemTable = 0;
	private static int numMemTree = 0;
	private static int numEqTable = 0;
	private static int numEqTree = 0;
	
	private static long testLearnerDFA(Machine machine, Alphabet alphabet, boolean table) {
		TeacherDK teacher = new TeacherDK(machine, alphabet);
		Learner<Machine, HashableValue> learner = null;
		if(table) learner = new LearnerDFATable(alphabet, teacher);
		else learner = new LearnerDFATree(alphabet, teacher);
		
		long time = System.currentTimeMillis();
		System.out.println("starting learning");
		learner.startLearning();

		while(true) {
//			System.out.println("Table is both closed and consistent\n" + learner.toString());
			Machine model = learner.getHypothesis();
			// along with ce
			Query<HashableValue> ceQuery = teacher.answerEquivalenceQuery(model);
			boolean isEq = ceQuery.getQueryAnswer().get();
			if(isEq) {
//				System.out.println(model.toString());
				break;
			}
//			HashableValue val = teacher.answerMembershipQuery(ceQuery);
//			ceQuery.answerQuery(val);
			learner.refineHypothesis(ceQuery);
		}
		
		time = System.currentTimeMillis() - time;
		if(table) {
			numMemTable += teacher.getNumMembership();
			numEqTable += teacher.getNumEquivalence();
		}else {
			numMemTree += teacher.getNumMembership();
			numEqTree += teacher.getNumEquivalence();
		}
		return time;
	}
	
	public static void main(String[] args) {
		
		int numCases = Integer.parseInt(args[0]);
		int numStates = Integer.parseInt(args[1]);
		
		final int apSize = 100;
		long timeTable = 0;
		long timeTree = 0;
		
		int n = 0;
		for(int k = apSize - 1; k <= apSize; k ++) {
			for(int i = 0; i < numCases; i ++) {
				n ++;
				Alphabet alphabet = new Alphabet(String.class);
				for(int c = 0; c < apSize; c ++) {
					alphabet.addLetter("" + ((char)c));
				}
				Machine machine = DFAGen.getRandomAutomaton(alphabet, numStates);
				timeTable += testLearnerDFA(machine, alphabet, true);
				timeTree += testLearnerDFA(machine, alphabet, false);
				System.out.println("Done for case " + n);
			}
			
		}
		System.out.println("numCases = " + n);
		System.out.println("table=" + (timeTable / 1000) + " tree=" + (timeTree / 1000));
		System.out.println("table MQ=" + numMemTable + " tree MQ=" + numMemTree);
		System.out.println("table EQ=" + numEqTable + " tree EQ=" + numEqTree);
		
	}
	
	

}
