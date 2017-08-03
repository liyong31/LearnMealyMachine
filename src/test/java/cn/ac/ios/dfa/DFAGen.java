package cn.ac.ios.dfa;

import java.util.Random;

import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.State;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.words.Alphabet;

public class DFAGen {
	
	private DFAGen() {
		
	}
	
	public static Machine getRandomAutomaton(int apSize, int numState) {
		
		Alphabet ap = new Alphabet(String.class);
		for(int i = 0; i < apSize; i ++) {
			ap.addLetter("" + ((char)i));
		}
    	
    	Machine result = new DFA(ap.getAPs());

		Random r = new Random(System.currentTimeMillis());
		
		for(int i = 0; i < numState; i ++) {
			result.createState();
		}
		
		// add self loops for those transitions
		for(int i = 0; i < numState; i ++) {
			State state = result.getState(i);
			for(int k=0 ; k < ap.getAPs().size(); k++){
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
		
		int numTrans = r.nextInt(numState * ap.getAPs().size());
		
		// transitions
		for(int k=0 ; k < ap.getAPs().size(); k++){
			for(int n = 0; n < numTrans; n++ ){
				int i=r.nextInt(numState);
				int j=r.nextInt(numState);
				result.getState(i).addTransition(k, j);
			}
		}
				
		return result;
	}

}
