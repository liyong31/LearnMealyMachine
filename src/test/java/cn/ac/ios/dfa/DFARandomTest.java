package cn.ac.ios.dfa;

import java.util.Random;
import java.util.TreeMap;

import cn.ac.ios.learner.table.dfa.LearnerDFATable;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.mealy.EquivalenceOracleImpl;
import cn.ac.ios.mealy.InputHelper;
import cn.ac.ios.oracle.EquivalenceOracle;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.query.Query;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.words.Alphabet;

public class DFARandomTest {
	
	public static void main(String[] args) {
		
		Alphabet input = new Alphabet(String.class);
		input.addLetter("a");
		input.addLetter("b");
		input.addLetter("c");
		
		int numCases = 5;
		int numOK = 0;
		
		for(int i = 0; i < numCases; i ++) {
			Machine machine = getRandomAutomaton(input, 6);
			System.out.println("Case " + i );
			System.out.println(machine.toString());
			if(testLearnerDFA(machine, input)) {
				numOK ++;
			}
		}
		
		System.out.println("Tested " + numCases + " cases and " + numOK + " cases passed !");
		
	}
	
	private static boolean testLearnerDFA(Machine machine, Alphabet alphabet) {
		TeacherDK teacher = new TeacherDK(machine, alphabet);
		LearnerDFATable learner = new LearnerDFATable(alphabet, teacher);
		System.out.println("starting learning");
		learner.startLearning();

		while(true) {
			System.out.println("Table is both closed and consistent\n" + learner.toString());
			Machine model = learner.getHypothesis();
			// along with ce
			Query<HashableValue> ceQuery = teacher.answerEquivalenceQuery(model);
			boolean isEq = ceQuery.getQueryAnswer().get();
			if(isEq) {
				System.out.println(model.toString());
				break;
			}
			HashableValue val = teacher.answerMembershipQuery(ceQuery);
			ceQuery.answerQuery(val);
			learner.refineHypothesis(ceQuery);
		}
		
		return true;
	}
	
	public static Machine getRandomAutomaton(Alphabet alphabet, int numState) {
    	
    	Machine result = new DFA(alphabet.getAPs());

		Random r = new Random();
		
		for(int i = 0; i < numState; i ++) {
			result.createState();
		}
		
		// add self loops for those transitions
		for(int i = 0; i < numState; i ++) {
			State state = result.getState(i);
			for(int k=0 ; k < alphabet.getAPs().size(); k++){
				state.addTransition(k, i);
			}
		}
		
		result.setInitial(0);
		
		// final states
		int numF = r.nextInt(numState-1);
		boolean hasF = false;
		numF = numF > 0 ? numF : 1;
		for(int n = 0; n < numF ; n ++) {
			int f = r.nextInt(numF);
			if(f != 0) {
				result.getAcceptance().setFinal(f);
				hasF = true;
			}
		}
		
		if(! hasF) {
			result.getAcceptance().setFinal(numF);
		}
		
		int numTrans = r.nextInt(numState * alphabet.getAPs().size());
		
		// transitions
		for(int k=0 ; k < alphabet.getAPs().size(); k++){
			for(int n = 0; n < numTrans; n++ ){
				int i=r.nextInt(numState);
				int j=r.nextInt(numState);
				result.getState(i).addTransition(k, j);
			}
		}
				
		return result;
	}

}
