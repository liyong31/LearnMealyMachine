package cn.ac.ios.learner.tree.mealy;

import java.util.BitSet;

import cn.ac.ios.learner.LearnerType;
import cn.ac.ios.learner.tree.TreeImpl;
import cn.ac.ios.learner.tree.ValueNode;
import cn.ac.ios.learner.tree.dfa.LearnerDFATree;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.State;

import cn.ac.ios.machine.mealy.MealyMachine;
import cn.ac.ios.oracle.MembershipOracle;
import cn.ac.ios.table.ExprValue;
import cn.ac.ios.table.HashableValue;
import cn.ac.ios.tree.Node;
import cn.ac.ios.tree.TreePrinterBoolean;
import cn.ac.ios.words.Alphabet;
import cn.ac.ios.words.Word;
import gnu.trove.iterator.TIntObjectIterator;

// TODO align with makeTableClosed
public class LearnerMealyTree extends LearnerDFATree {

	private final Alphabet outAps;
	public LearnerMealyTree(Alphabet inAps, Alphabet outAps, MembershipOracle<HashableValue> membershipOracle) {
		super(inAps, membershipOracle);
		this.outAps = outAps;
	}
	
	@Override
	public LearnerType getLearnerType() {
		return LearnerType.MEALY_TREE;
	}

	protected void constructHypothesis() {

//		// construct machine according to KV tree
		if (nodeToSplit != null) {
			updatePredecessors();
		}
		
		Machine machine = new MealyMachine(inAps.getAPs(), outAps.getAPs());
		
		for(int i = 0; i < states.size(); i ++) {
			machine.createState();
		}
		
		for(ValueNode state : states) {
			for(int letter = 0; letter < inAps.getAPSize(); letter ++) {
				BitSet preds = state.predecessors.get(letter);
				if(preds == null) continue;
				for(int predNr = preds.nextSetBit(0)
						; predNr >= 0
						; predNr = preds.nextSetBit(predNr + 1)) {
					State s = machine.getState(predNr);
					// get output
					int out = processMembershipQuery(state.label.append(letter), inAps.getEmptyWord()).get();
					s.addTransition(letter, state.id, out);
				}
			}

			if(state.label.isEmpty()) {
				machine.setInitial(state.id);
			}
		}
		
		this.machine = machine;

	}
	
	// word will never be empty word
	protected Node<ValueNode> updateTree(ExprValue exprValue, HashableValue result) { 
		
		CeAnalyzerMealyTree analyzer = getCeAnalyzerInstance(exprValue, result);
		analyzer.analyze();
		
		// replace nodePrev with new experiment node nodeExpr 
		// and two child r[1..length-1] and nodePrev
		
		ExprValue wordExpr = analyzer.getNodeExpr();
		Node<ValueNode> nodePrev = analyzer.getNodeToSplit();
		Node<ValueNode> parent = nodePrev.getParent();
		
		// new experiment word
		Node<ValueNode> nodeExpr = getValueNode(parent, nodePrev.fromBranch(), wordExpr); // replace nodePrev
		if(parent != null) {
			parent.addChild(nodePrev.fromBranch(), nodeExpr);
		}else { // became root node
			tree = new TreeImpl(nodeExpr);
		}
		
		// state for r[1..length-1]
		HashableValue branchNodeLeaf = analyzer.getLeafBranch();
		HashableValue branchNodePrev = analyzer.getNodeSplitBranch();
		Node<ValueNode> nodeLeaf = getValueNode(nodeExpr, branchNodeLeaf, analyzer.getNodeLeaf());
		ValueNode stateLeaf =  new ValueNode(states.size(), analyzer.getNodeLeaf().get());
		stateLeaf.node = nodeLeaf;
		nodeLeaf.setValue(stateLeaf);
		states.add(stateLeaf); // add new state
		
		Node<ValueNode> nodePrevNew = getValueNode(nodeExpr, branchNodePrev, nodePrev.getLabel());
		nodePrevNew.setValue(nodePrev.getValue()); // To update
		nodePrevNew.getValue().node = nodePrevNew; // update node
		
		nodeExpr.addChild(branchNodeLeaf, nodeLeaf);
		nodeExpr.addChild(branchNodePrev, nodePrevNew);
		
		// update outgoing transitions for nodeLeaf
		updateSuccessors(stateLeaf.id, 0, inAps.getAPSize() - 1);
		
		Word wordNodePrev = nodePrevNew.getLabel().get();
		if(wordNodePrev.isEmpty()) {
			tree.setLamdaLeaf(nodePrevNew);
		}
		
		return nodePrevNew;
	}
	
	private Partition findNodePartition(Word word) {
		return findNodePartition(word, tree.getRoot());
	}
	
	private Partition findNodePartition(Word word, Node<ValueNode> nodeCurr) {
		while(! nodeCurr.isLeaf()) {
			Word wordExpr = nodeCurr.getLabel().get();
			HashableValue result = processMembershipQuery(word, wordExpr);
			Node<ValueNode> node = nodeCurr.getChild(result);
			if(node == null) {
				return new Partition(false, nodeCurr, result); // new leaf node
			}
			nodeCurr = node; // possibly we can not find that node
		}
		return new Partition(true, nodeCurr, null);
	}
	
	//TODO be carefull with accepting states
	// should I rewrite updateTree function?
	//TODO make code more clear
	
	@Override
	protected void updatePredecessors() {
		
		TIntObjectIterator<BitSet> iterator = nodeToSplit.getValue().predecessors.iterator();
		Node<ValueNode> parent = nodeToSplit.getParent();
		BitSet letterDeleted = new BitSet();
		while(iterator.hasNext()) {
			iterator.advance();
			int letter = iterator.key();
			BitSet statePrevs = (BitSet)iterator.value().clone(); 
			BitSet stateRemoved = new BitSet(statePrevs.size());
			// when addNode is called, statePrevs will not add states, 
			// but iterator.value() may add new states
			// since we do not care new added states, they are correct
			for(int stateNr = statePrevs.nextSetBit(0)
					; stateNr >= 0
					; stateNr = statePrevs.nextSetBit(stateNr + 1)) {
				ValueNode statePrev = states.get(stateNr);
				Partition partition = findNodePartition(statePrev.label.append(letter), parent);
				if(partition.found) {
					if (partition.node != nodeToSplit) {
						updateTransition(stateNr, letter, partition.node.getValue().id);
						stateRemoved.set(stateNr); // remove this predecessor
					} // change to other leaf node
				}else {
					Node<ValueNode> node = addNode(partition.node, partition.branch
							, getExprValueWord(statePrev.label.append(letter)));
					updateTransition(stateNr, letter, node.getValue().id);
					stateRemoved.set(stateNr);  // remove this predecessor
				}
			}
			BitSet temp = (BitSet)iterator.value().clone(); 
			temp.andNot(stateRemoved);
			if(temp.isEmpty()) {
				letterDeleted.set(letter);
			}else {
				iterator.setValue(temp);
			}
		}
		
		for(int letter = letterDeleted.nextSetBit(0)
				; letter >= 0
				; letter = letterDeleted.nextSetBit(letter + 1)) {
			nodeToSplit.getValue().predecessors.remove(letter);
		}
		
	}
	
	public String toString() {
		return TreePrinterBoolean.toString(tree);
	}
	
	private class Partition {
		boolean found ;
		Node<ValueNode> node;
		HashableValue branch;
		
		public Partition(boolean f, Node<ValueNode> n, HashableValue v) {
			this.found = f;
			this.node = n;
			this.branch = v;
		}
	}
	
	private Node<ValueNode> addNode(Node<ValueNode> parent, HashableValue branch, ExprValue nodeLabel) {
		Node<ValueNode> nodeLeaf = getValueNode(parent, branch, nodeLabel);
		ValueNode stateLeaf =  new ValueNode(states.size(), nodeLabel.get());
		stateLeaf.node = nodeLeaf;
		nodeLeaf.setValue(stateLeaf);
		states.add(stateLeaf); // add new state
		
		parent.addChild(branch, nodeLeaf);
		// test whether this node is accepting
		Word period = nodeLabel.get();
		HashableValue result = processMembershipQuery(period, inAps.getEmptyWord());
//		Boolean isAccepting = result.getLeft();
		if(result.isAccepting()) nodeLeaf.setAcceting();
		
		updateSuccessors(stateLeaf.id, 0, inAps.getAPSize() - 1);
		return nodeLeaf;
	}
	
	// may add new leaf node during successor update

	protected void updateSuccessors(int stateNr, int letterFrom, int letterTo) {
		assert stateNr < states.size() 
	    && letterFrom >= 0
	    && letterTo < inAps.getAPSize();
		
		ValueNode state = states.get(stateNr);
		
		Word label = state.label;
		for(int letter = letterFrom; letter <= letterTo; letter ++) {
			Word wordSucc = label.append(letter);
			Partition nodeSucc = findNodePartition(wordSucc);
			if(nodeSucc.found) {
				updateTransition(stateNr, letter, nodeSucc.node.getValue().id);
			}else {
				Node<ValueNode> node = addNode(nodeSucc.node, nodeSucc.branch, getExprValueWord(wordSucc));
				updateTransition(stateNr, letter, node.getValue().id);
			}
			
		}
		
	}
	
	@Override
	protected CeAnalyzerMealyTree getCeAnalyzerInstance(ExprValue exprValue, HashableValue result) {
		return new CeAnalyzerMealyTree(exprValue, result);
	}
		
	// analyze counterexample
	protected class CeAnalyzerMealyTree extends CeAnalyzerTree {
		
		protected Node<ValueNode> nodePrev = tree.getLamdaLeaf();
		protected ExprValue wordExpr;
		protected ExprValue wordLeaf;
		protected HashableValue leafBranch;
		protected HashableValue nodePrevBranch;
		
		public CeAnalyzerMealyTree(ExprValue exprValue, HashableValue result) {
			super(exprValue, result);
		}
		
		// find prefix whose successor needs to be added
		@Override
		public void analyze() {

			this.leafBranch = null;
			this.nodePrevBranch = null;
			
			Word wordCE = this.exprValue.get();
			// get the initial state from automaton
			int letterNr = 0, stateCurr = -1, statePrev = machine.getInitialState();
			
			// binary search, low and high are the lengths of prefix
			int low = 0, high = wordCE.length() - 1;
			while (low <= high) {

				int mid = (low + high) / 2;

				assert mid < wordCE.length();

				int sI = machine.getSuccessor(wordCE.getPrefix(mid));
				int sJ = machine.getSuccessor(sI, wordCE.getLetter(mid));

				Word sILabel = getStateLabel(sI);
				Word sJLabel = getStateLabel(sJ);

				HashableValue memSIAV = processMembershipQuery(sILabel, wordCE.getSuffix(mid));
				HashableValue memSJV = processMembershipQuery(sJLabel, wordCE.getSuffix(mid + 1));

				if (! memSIAV.valueEqual(memSJV)) {
					statePrev = sI;
					letterNr = mid;
					stateCurr = sJ;
					this.leafBranch = memSIAV;
					this.nodePrevBranch = memSJV;
					break;
				}

				if (memSIAV.valueEqual(result)) {
					low = mid + 1;
				} else {
					high = mid;
				}
			}
			
			Word wordPrev = states.get(statePrev).label;         // S(j-1)
			this.wordExpr = getExprValueWord(wordCE.getSuffix(letterNr + 1));  // y[j+1..n]
			this.wordLeaf = getExprValueWord(wordPrev.append(wordCE.getLetter(letterNr))); // S(j-1)y[j]
			this.nodePrev = states.get(stateCurr).node;          // S(j)
		}
		
		public ExprValue getNodeLeaf() {
			return wordLeaf;
		}
		
		public ExprValue getNodeExpr() {
			return wordExpr;
		}
		
		public Node<ValueNode> getNodeToSplit() {
			return nodePrev;
		}
		
		public HashableValue getLeafBranch() {
			return leafBranch;
		}
		
		public HashableValue getNodeSplitBranch() {
			return nodePrevBranch;
		}
		
	}
	
}
