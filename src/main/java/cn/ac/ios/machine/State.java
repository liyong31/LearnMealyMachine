package cn.ac.ios.machine;

public interface State {
	
	int getIndex();
	
	Machine getMachine();
	
	void addTransition(int letter, int state);
	void addTransition(int letter, int state, int out);
	void addOutput(int letter, int output);

}
