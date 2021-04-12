
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over integers.
 * 
 * 
 */
public class FibonacciHeap {

	static int numOfTotalLinks = 0;
	static int numOfTotalCuts = 0;
	private int numOfTrees;
	int numOfMarkedNodes;
	public HeapNode trees;
	private HeapNode minTree;
	private int numOfElements;

	public FibonacciHeap() {
		this.trees = null;
		this.minTree = null;
		this.numOfElements = 0;
		this.numOfMarkedNodes = 0;
		this.numOfTrees = 0;
	}

	/**
	 * public void fixTreeStructure()
	 * 
	 * fix the Structure of the tree after deleteMin() , Arrange the roots in
	 * Buckets and linked Equal rank , the result is a list of nodes sorted by ranks
	 * only one tree with each rank
	 */
	public void fixTreeStructure() {

		if (this.isTreeEmpty()) {
			this.minTree = null;
			return;
		}
		if (this.numOfTrees == 1) {
			this.minTree = this.trees;
			return;
		}
		int maxNumOfTrees = 2 + (int) Math.ceil(log2(this.numOfElements));

		// create the buckets
		HeapNode[] newTrees = new HeapNode[maxNumOfTrees];
		HeapNode pointer = this.trees;
		HeapNode prev;

		do {
			int targetCell = pointer.rank;
			HeapNode combined = pointer;
			prev = pointer;
			pointer = pointer.getRightSibling();
			removeFromSib(combined);
			if (combined.isMarked) {
				combined.isMarked = false;
				this.numOfMarkedNodes--;
			}
			while (newTrees[targetCell] != null) {
				// link trees with the same rank
				combined = combined.linkTo(newTrees[targetCell]);
				newTrees[targetCell] = null;
				targetCell++;
			}

			assert combined.rank == targetCell;

			newTrees[targetCell] = combined;

		} while (prev != pointer);
		this.trees = null;
		this.minTree = null;
		this.numOfTrees = 0;
		// create the new HeapNode from newTrees
		for (HeapNode tree : newTrees) {

			if (tree != null) {
				setSibiling(tree);
				this.numOfTrees += 1;
				// add all node to this.trees at the end
				this.add(tree, false);
				if (this.minTree == null || tree.key < this.minTree.key) {
					this.minTree = tree;
				}

			}
		}

	}

	/**
	 * private void setSibiling(HeapNode tree)
	 * 
	 * set all sibling of tree to tree
	 */
	private void setSibiling(HeapNode tree) {
		tree.setRightSibling(tree);
		tree.setLeftSibling(tree);

	}

	/**
	 * private static double log2(int x)
	 *
	 * return log x, (in base 2)
	 */
	private static double log2(int x) {
		return Math.log(x) / Math.log(2);
	}

	/**
	 * public boolean isEmpty()
	 *
	 * returns true if and only if the heap is empty.
	 * 
	 */
	public boolean isEmpty() {
		return this.numOfElements == 0;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 */
	public HeapNode insert(int key) {
		HeapNode newNode = new HeapNode(key);

		this.add(newNode, true);

		// correct minTree if needed
		HeapNode min = this.findMin();
		if (min == null || key < min.key) {
			this.minTree = newNode;
		}
		// update num of elements and trees
		this.numOfElements++;
		this.numOfTrees++;

		return newNode;
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin() {

		// disconnect minTree from tree
		HeapNode leftToChild = minTree.getLeftSibling();
		this.removeFromSib(this.minTree);
		int counterOfAddedChild = 0;
		HeapNode child = this.minTree.firstChild;

		// if minTree has children , we need to add them in this.trees
		if (child != null) {

			child.parent = null;
			// for each child remove parent
			counterOfAddedChild = removeParent(child);
			if (this.numOfTrees == 1) {
				this.trees = child;
			} else {
				// add child to trees in the place minTree was
				this.addBewteen(child, leftToChild);
			}

		}
		this.minTree = null;
		this.numOfElements--;
		this.numOfTrees += counterOfAddedChild - 1;
		this.fixTreeStructure();
	}

	/**
	 * private int getNumOfRoots(HeapNode child)
	 *
	 * return number of nodes in child and remove parent for each child
	 */
	private int removeParent(HeapNode child) {

		int key = child.getKey();
		HeapNode pointer = child;
		int count = 0;
		do {
			count++;
			pointer.setParent(null);
			pointer = pointer.rightSibling;
		} while (pointer.getKey() != key);

		return count;
	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal
	 *
	 */
	public HeapNode findMin() {
		if (this.minTree == null) {
			assert this.size() == 0;
			return null;
		}
		return this.minTree;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {
		this.numOfElements += heap2.numOfElements;
		// add heap2 to this.trees
		this.addAll(heap2.trees);
		if (this.minTree == null) {
			this.minTree = heap2.minTree;
		} else if (heap2.minTree == null) {
			// Do nothing - this.minTree should not change.
		} else {
			if (this.minTree.key > heap2.minTree.key) {
				this.minTree = heap2.minTree;
			}
		}

		this.numOfTrees += heap2.getNumOfTrees();
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.numOfElements;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {
		if (this.isEmpty()) {
			int[] res = new int[0];
			return res;
		}
		int maxRank = -1;

		int keyFirst = trees.getKey();
		HeapNode pointer = trees;
		// get the tree with the highest rank
		do {
			maxRank = Math.max(maxRank, pointer.rank);
			pointer = pointer.getRightSibling();
		} while (pointer.getKey() != keyFirst);
		assert maxRank != -1;
		int[] res = new int[maxRank + 1];

		keyFirst = trees.getKey();
		pointer = trees;
		do {
			res[pointer.rank]++;
			pointer = pointer.getRightSibling();
		} while (pointer.getKey() != keyFirst);

		return res;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	public void delete(HeapNode x) {

		assert !this.isEmpty();
		int delta = x.key - this.findMin().key + 1;
		this.decreaseKey(x, delta);
		this.deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {
		x.key -= delta;

		// update minTree
		if (x.key < this.minTree.key) {
			this.minTree = x;
		}

		if (x.parent == null)
			return;
		if (x.parent.key < x.key)
			return;
		cascadingCut(x, x.parent);

	}

	/**
	 * private void cascadingCut(HeapNode x, HeapNode parent)
	 *
	 * cut marked heapNode if needed(after first cut)
	 */
	private void cascadingCut(HeapNode x, HeapNode parent) {
		assert x.parent == parent;
		cut(x, parent);
		if (parent.parent != null) {
			if (!parent.isMarked) {
				parent.isMarked = true;
				this.numOfMarkedNodes++;
			} else {
				cascadingCut(parent, parent.parent);
			}
		}
	}

	/**
	 * private void cut(HeapNode x, HeapNode parent)
	 * 
	 * cut x from parent , and add it to trees (in the first position)
	 */
	private void cut(HeapNode x, HeapNode parent) {
		numOfTotalCuts++;
		assert x.parent == parent;
		x.parent = null;
		if (x.isMarked) {
			x.isMarked = false;
			this.numOfMarkedNodes--;
		}
		parent.rank--;

		// if x has no sibling , so parent has only one child (x)
		if (x.rightSibling == x) {
			parent.firstChild = null;
		} else {
			// update parent first child , if needed
			if (parent.firstChild == x)
				parent.firstChild = x.rightSibling;

			HeapNode left = x.leftSibling;
			HeapNode right = x.rightSibling;
			left.rightSibling = right;
			right.leftSibling = left;
		}
		// add x at the beginning
		this.add(x, true);
		this.numOfTrees++;
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	public int potential() {

		int numOfTrees = this.numOfTrees;
		int numOfMarked = this.numOfMarkedNodes;
		return numOfTrees + 2 * numOfMarked;
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two of the same rank, and generates a tree of rank bigger by one, by
	 * hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	public static int totalLinks() {

		return numOfTotalLinks;
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {

		return numOfTotalCuts;
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * This static function returns the k minimal elements in a binomial tree H. The
	 * function should run in O(k(logk + deg(H)).
	 */
	public static int[] kMin(FibonacciHeap H, int k) {
		FibonacciHeap helper = new FibonacciHeap();
		int[] arr = new int[k];
		int counter = 0;

		// add all nodes in k first levels
		helper.getAllChildTillK(H.minTree, k, 0);

		counter = 0;
		// add to arr k smallest number
		while (counter < k) {
			arr[counter] = helper.minTree.getKey();
			helper.deleteMin();
			counter++;
		}
		return arr;
	}

	/**
	 * private void getAllChildTillK(HeapNode h,int k,int counter)
	 * 
	 * add to this all the nodes in h till k level
	 */
	private void getAllChildTillK(HeapNode h, int k, int counter) {
		if (counter >= k)
			return;
		if (h == null)
			return;
		HeapNode pointer = h;
		int key = pointer.getKey();
		do {
			this.insert(pointer.getKey());

			getAllChildTillK(pointer.getChild(), k, counter + 1);
			pointer = pointer.getRightSibling();
		} while (key != pointer.getKey());

	}

	/**
	 * from list to heap
	 * 
	 * /** public void add(HeapNode newNode)
	 * 
	 * add newNode true -add at the beginning of the list (from the left side to
	 * trees) else at the end
	 */
	public void add(HeapNode newNode, boolean isTreesSetLast) {
		if (trees == null) {
			trees = newNode;
			return;
		}

		// get the last node in the tree
		HeapNode last = trees.getLeftSibling();

		// set newNode sibling
		newNode.setLeftSibling(last);
		newNode.setRightSibling(trees);

		last.setRightSibling(newNode);

		trees.setLeftSibling(newNode);
		// update first node to newNode
		if (isTreesSetLast)
			trees = newNode;

	}

	/**
	 * public void addAll(HeapNode listNode)
	 * 
	 * add all roots in listNode at the end of this.trees
	 */
	public void addAll(HeapNode listNode) {

		HeapNode lastOther = listNode.getLeftSibling();
		HeapNode lastMine = this.trees.getLeftSibling();

		// set listNode at the end of this
		lastMine.setRightSibling(listNode);
		listNode.setLeftSibling(lastMine);

		lastOther.setRightSibling(this.trees);
		this.trees.setLeftSibling(lastOther);

	}

	/**
	 * public void addBewtwen(HeapNode toAdd)
	 *
	 * add chain in between addedTo and it's right sibling
	 */
	public void addBewteen(HeapNode chain, HeapNode addedTo) {

		HeapNode addedToRightSib = addedTo.getRightSibling();
		HeapNode LastInChain = chain.getLeftSibling();

		addedTo.setRightSibling(chain);
		chain.setLeftSibling(addedTo);

		addedToRightSib.setLeftSibling(LastInChain);
		LastInChain.setRightSibling(addedToRightSib);

	}

	/**
	 * public boolean isTreeEmpty
	 * 
	 * @return true if trees ==null , else return false
	 */
	public boolean isTreeEmpty() {
		return this.trees == null;
	}

	/**
	 * public HeapNode getFirst()
	 * 
	 * return this.trees
	 */
	public HeapNode getFirst() {

		return this.trees;

	}

	/**
	 * public void removeFromSib ()
	 * 
	 * disconnect heapNode b from tree
	 */
	public void removeFromSib(HeapNode b) {
		// min is the only heapNode in trees
		if (this.numOfElements == 1) {
			this.trees = null;
			return;
		}
		// if b is minTree
		if (b == this.minTree) {
			// if b is first , update new first
			if (this.minTree == this.trees) {
				if (b.firstChild == null)
					this.trees = this.minTree.getRightSibling();
				else
					this.trees = b.getChild();
			}
		}

		// there is at least 2 trees in tree
		HeapNode Left = b.getLeftSibling();
		HeapNode Right = b.getRightSibling();

		// disconnect minTree from tree
		Left.setRightSibling(Right);
		Right.setLeftSibling(Left);

		b.setLeftSibling(b);
		b.setRightSibling(b);

	}

	/**
	 * public int getNumOfTrees()
	 * 
	 * @return numOfTrees
	 */
	public int getNumOfTrees() {
		return numOfTrees;
	}

	/**
	 * public class HeapNode
	 * 
	 */
	public class HeapNode {

		private boolean isMarked;
		public int key;
		private HeapNode leftSibling;
		private HeapNode rightSibling;
		private HeapNode firstChild;
		private HeapNode parent;
		private int rank;

		/**
		 * HeapNode(int key) constructor
		 */
		public HeapNode(int key) {
			this.key = key;
			this.leftSibling = this;
			this.rightSibling = this;
			this.firstChild = null;
			this.parent = null;
			this.rank = 0;
			this.isMarked = false;
		}

		/**
		 * public int getKey()
		 * 
		 * return key
		 */
		public int getKey() {
			return this.key;
		}

		/**
		 * public void setRightSibling(HeapNode hN)
		 * 
		 * set hN as right sibling
		 */
		public void setRightSibling(HeapNode hN) {
			this.rightSibling = hN;
		}

		/**
		 * public void setLeftSibling(HeapNode hN)
		 * 
		 * set hN as left sibling
		 */
		public void setLeftSibling(HeapNode hN) {
			this.leftSibling = hN;
		}

		/**
		 * public void setParent(HeapNode hN)
		 * 
		 * set hN as parent
		 */
		public void setParent(HeapNode hN) {
			this.parent = hN;
		}

		/**
		 * public void setFirstChild(HeapNode hN)
		 * 
		 * set hN as child
		 */
		public void setFirstChild(HeapNode hN) {
			this.firstChild = hN;
		}

		/**
		 * public HeapNode getRightSibling()
		 * 
		 * return rightSibling
		 */
		public HeapNode getRightSibling() {
			return rightSibling;
		}

		/**
		 * public HeapNode getLeftSibling( )
		 * 
		 * return leftSibling
		 */
		public HeapNode getLeftSibling() {
			return leftSibling;
		}

		/**
		 * private void addChild(HeapNode newChild)
		 * 
		 * add newChild to this as child
		 */
		private void addChild(HeapNode newChild) {
			assert newChild.key > this.key;
			this.rank++;
			newChild.parent = this;
			if (this.firstChild == null) {
				this.firstChild = newChild;
				newChild.rightSibling = newChild;
				newChild.leftSibling = newChild;
			} else {
				// link the new child as right and left sibling
				HeapNode originalChild = this.firstChild;
				HeapNode originalChildLeft = originalChild.leftSibling;
				originalChild.leftSibling = newChild;
				newChild.rightSibling = originalChild;
				originalChildLeft.rightSibling = newChild;
				newChild.leftSibling = originalChildLeft;
				this.firstChild = newChild;
			}
		}

		/**
		 * private HeapNode linkTo(HeapNode other)
		 * 
		 * create new heapNode from this and other
		 */
		private HeapNode linkTo(HeapNode other) {
			numOfTotalLinks++;
			HeapNode res;
			// choose the smallest as the root
			if (other.key < this.key) {
				res = other;
				other.addChild(this);
			} else {
				res = this;
				this.addChild(other);
			}
			return res;
		}

		/**
		 * public int getRank()
		 * 
		 * return rank
		 */
		public int getRank() {
			return rank;
		}

		/**
		 * public HeapNode getChild()
		 * 
		 * return fisrtChild
		 */
		public HeapNode getChild() {
			return firstChild;
		}

		/**
		 * public HeapNode getParent()
		 *
		 * return parent
		 */
		public HeapNode getParent() {
			return parent;
		}
	}

}
