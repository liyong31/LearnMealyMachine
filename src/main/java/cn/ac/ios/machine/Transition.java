package cn.ac.ios.machine;

public interface Transition {
	
	int getSuccessor();
	
	// only for mealy machine
	int getOutput();
	
	void setOutput();
	

}
