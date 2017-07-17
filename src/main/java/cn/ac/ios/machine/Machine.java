package cn.ac.ios.machine;

import cn.ac.ios.words.APList;

public interface Machine {
	
	Acceptance getAcceptance();
	
	APList getAPs();
	
	State getState(int index);
	
	int getStateSize();
	
	State makeState();
	
	void setInitial(int state);
	
	default void setInitial(State state) {
		setInitial(state.getIndex());
	}

}
