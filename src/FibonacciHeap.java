/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode minimalRoot, firstRoot;
	private int size, markedCount, treeCount;
	private static int totalCuts, totalLinks;
	
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    * Complexity O(1).  
    */
    public boolean isEmpty() 
    {
    	return size == 0;
    }
		
    /* Helper function for insert().
     * The function creates a node with the given key and given special child.
     * The function inserts the node into the heap.
     * The function returns the newly created node. 
     * Complexity O(1).
     */
    private HeapNode insertHelper(int key, HeapNode specialChild) {
    	HeapNode node = new HeapNode(key);
    	node.setSpecialChild(specialChild);
    	if (this.isEmpty()) { // Inserting node to an empty heap.
    		this.firstRoot = node;
    		this.minimalRoot = node;
    		node.setNext(node); // Node should point to itself.
    		node.setPrev(node);
    	}
    	else { // Inserting node at the start of a non-empty heap - "left side".
    		HeapNode lastRoot = this.firstRoot.prev; // Find the last tree in the heap - "most right tree".
    		node.setNext(this.firstRoot);
    		node.setPrev(lastRoot);
    		lastRoot.setNext(node);
    		this.firstRoot.setPrev(node);
    		if (this.minimalRoot.getKey() > node.getKey()) { // Updating the minimal heap-node field.
    			this.minimalRoot = node;
    		}
    	}	
    	this.size++;
    	this.treeCount++;
    	return node;	
    }
    
    /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    * Complexity O(1).
    */
    public HeapNode insert(int key)
    {   
    	return this.insertHelper(key, null);
    }

    /*
     * Helper function for link()
     * The function receives the root nodes root1 and root2, and links them.
     * The function assumes root1.key < root2.key.
     * The function returns root1
     * Complexity O(1). 
     */
    private HeapNode linkHelper(HeapNode root1, HeapNode root2) {
    	HeapNode prev2 = root2.getPrev(); // Get the bigger-key roots previous node. Could be itself.
		HeapNode next2 = root2.getNext(); // Get same roots next node. Could be itself.
		prev2.setNext(next2);
		next2.setPrev(prev2); // These two lines remove tree with root2 from the list of trees.
		if (this.firstRoot.getKey() == root2.getKey()) { // root2 was the heaps first root.
			this.firstRoot = next2;
		}
		if (root1.getChild() == null) { // root1 is a single node with no children.
			root2.setNext(root2);
			root2.setPrev(root2);
		}
		else { // Most frequent case - linking two trees with at least one child.
			HeapNode child1 = root1.getChild();
			child1.getPrev().setNext(root2);
			root2.setPrev(child1.getPrev());
			child1.setPrev(root2);
			root2.setNext(child1);
		}
		root2.setParent(root1); // Fixing pointers of root and its new child
		root1.setChild(root2);
		root1.setRank(root1.getRank()+1);
		this.treeCount--;
		return root1;
    }
    
    /*
     *  Helper function for consolidate().
     *  The function receives two root nodes of the same rank.
     *  The function links the two nodes according to the algorithm we saw in class.
     *  The function validates the fields of the nodes and of the heap.
     *  The function returns the root of the new linked tree.
     *  Complexity O(1).
     */
    private HeapNode link(HeapNode root1, HeapNode root2) {
    	FibonacciHeap.totalLinks++;
    	if (root1.getKey() < root2.getKey()) { // Need to make root2 the child of root 1.
    		return linkHelper(root1, root2);
    	}
    	return linkHelper(root2, root1); // Else - root1 is the child of root2.
    }
    
    /*
     * Helper function for deleteMin().
     * The function takes the heap and performs a consolidation of it, after deletion of the min. root.
     * A.C. Complexity O(logn).
     * W.C. Complexity O(n). 
     */
    private void consolidate() {
    	int length = 3 * (int)Math.floor(Math.log(this.size) / Math.log(2)) + 1; // Large size just in case
    	HeapNode[] cups = new HeapNode[length];
    	HeapNode curr = this.firstRoot;
    	do { // Iterate over roots and either insert to empty cell or link with root in cell and move up
    		while (cups[curr.getRank()] != null) {
    			curr = this.link(curr, cups[curr.getRank()]);
    			cups[curr.getRank()-1] = null;
    		}
    		cups[curr.getRank()] = curr;
    		curr = curr.getNext();
    	} while (curr.getKey() != this.firstRoot.getKey());
    	int newLength = 0; // Creating "thinner" array of the consolidated roots
    	for (int i = 0; i < length; i++) {
    		if (cups[i] != null) {
    			newLength++;
    		}
    	}
    	HeapNode[] newCups = new HeapNode[newLength];
    	int index = 0;
    	for (int i = length-1; i > -1; i--) {
    		if (cups[i] != null) {
    			newCups[index] = cups[i];
    			index++;
    		}
    	}
    	this.firstRoot = newCups[0]; // Setting roots to be consolidated roots in decreasing order of ranks
    	this.minimalRoot = newCups[0];
    	int min = this.minimalRoot.getKey();
    	for (int i = 0; i < newLength-1; i++) {
    		newCups[i].setNext(newCups[i+1]);
    		newCups[i+1].setParent(newCups[i]);
    		if (min > newCups[i].getKey()) {
    			this.minimalRoot = newCups[i];
    			min = newCups[i].getKey();
    		}
    	}
    	newCups[newLength-1].setNext(newCups[0]);
    	newCups[0].setPrev(newCups[newLength-1]);
    	if (min > newCups[newLength-1].getKey()) {
    		this.minimalRoot = newCups[newLength-1];
			min = newCups[newLength-1].getKey();
    	}
    	this.treeCount = newLength;
    }
    
    /*
     * Helper function for deleteMin().
     * The function resets the heap to be an empty one, fixing relevant fields.
     * Complexity O(1).
     */
    private void clear() {
		this.size = 0;
		this.markedCount = 0;
		this.treeCount = 0;
		this.minimalRoot = null;
		this.firstRoot = null;
    }
    
    /**
    * public void deleteMin()
    *
    * The function deletes the node containing the minimum key.
    * A.C. Complexity O(logn).
    * W.C. Complexity O(n).
    */
    public void deleteMin()
    {
    	if (this.isEmpty()) { // Heap is empty
    		return;
    	}
    	if (this.size == 1) { // Heap will become empty 
    		this.clear();
    		return;
    	}
    	HeapNode nextNode = this.minimalRoot.getNext();
    	HeapNode prevNode = this.minimalRoot.getPrev();
    	HeapNode firstChild = this.minimalRoot.getChild();
    	this.size--;
    	if (nextNode.getKey() == this.minimalRoot.getKey()) { // Minimal has no siblings, has children
    		int firstKey = firstChild.getKey();
    		this.firstRoot = firstChild;
    		this.minimalRoot.setChild(null);
    		do {
    			firstChild.setParent(null);
    			firstChild.unmark();
    			this.markedCount--;
    			firstChild = firstChild.getNext();
    		} while (firstChild.getKey() != firstKey);
    	}
    	else if (firstChild == null) { // Minimal has no children, has siblings
    		prevNode.setNext(nextNode);
    		nextNode.setPrev(prevNode);
    		if (this.minimalRoot.getKey() == this.firstRoot.getKey()) { // Minimal was first
    			this.firstRoot = nextNode;
    		}
    	}
    	else { // Minimal has children and siblings
    		int firstKey = firstChild.getKey();
    		HeapNode lastChild = firstChild.getPrev();
    		this.minimalRoot.setChild(null);
    		prevNode.setNext(firstChild);
    		firstChild.setPrev(prevNode);
    		nextNode.setPrev(lastChild);
    		lastChild.setNext(nextNode);
    		if (this.minimalRoot.getKey() == this.firstRoot.getKey()) { // Minimal was first
    			this.firstRoot = firstChild;
    		}
    		do {
    			firstChild.setParent(null);
    			firstChild.unmark();
    			this.markedCount--;
    			firstChild = firstChild.getNext();
    		} while (firstChild.getKey() != firstKey);    		
    	}
    	consolidate();     	
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    * Complexity O(1).
    */
    public HeapNode findMin() // Complexity O(1).
    {
    	return this.minimalRoot; // Get the value of minimalRoot field.
    }     
    
    /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Complexity O(1)
    */
    public void meld(FibonacciHeap heap2) // Complexity O(1).
    {
    	if (!heap2.isEmpty()) { // Melding current heap with a non-empty heap.
    		if (this.isEmpty()) { // Current heap is empty - make heap2 the current heap.
    			this.firstRoot = heap2.firstRoot;
    			this.minimalRoot = heap2.minimalRoot;
    		}
    		else { // Current heap is NOT empty - need to add heap2 at the end of current heap - "most right heap".
    			HeapNode lastRoot = this.firstRoot.prev; // Get current heaps last tree.
    			HeapNode newLastRoot = heap2.firstRoot.prev; // Get heap2's last tree. Should be the last one after meld.
    			this.firstRoot.setPrev(newLastRoot);
    			newLastRoot.setNext(this.firstRoot); // These two lines insert the last tree of heap2 to current heap.
    			lastRoot.setNext(heap2.firstRoot);
    			heap2.firstRoot.setPrev(lastRoot); // These two lines insert the first tree of heap2 to current heap.
    			if (this.minimalRoot.getKey() < heap2.minimalRoot.getKey()) { // Check if minimal node update is required.
    				this.minimalRoot = heap2.minimalRoot;
    			}
    		}
    		this.size = this.size + heap2.size;
    		this.treeCount = this.treeCount + heap2.treeCount;
    	}
    }

   /**
    * public int size()
    *
    * The function returns the number of elements in the heap.
    * Complexity O(1).   
    */
    public int size() // Complexity O(1).
    {
    	return this.size; // Get the value of size field.
    }
    	
    /**
    * public int[] countersRep()
    *
    * The function returns an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * Complexity O(n). 
    */
    public int[] countersRep() // Complexity O(n).
    {
    	if (this.isEmpty()) { // Heap is empty - return an empty array.
    		return new int[] {};
    	} // Else...:
    	int length = 3 * (int)Math.floor(Math.log(this.size) / Math.log(2)) + 1; // Large size just in case
    	int[] temp = new int[length];
    	HeapNode root = this.firstRoot;
    	for (int i = 0; i < this.treeCount; i++) { // Count occurrences of ranks of all the trees
    		temp[root.getRank()]++;
    		root = root.getNext();
    	}
    	int actualLength = 0;
    	for (int i = temp.length-1; i > -1; i--) { // Start from last index and go down
    		if (temp[i] > 0) { // Need to find the first index, starting from the end of the list, which is not 0.
    			actualLength = i + 1;
    			break;
    		}
    	}
    	int[] res = new int[actualLength]; // Initialize a new array with a proper size.
    	for (int i = 0; i < res.length; i++) { // Copy the counts from temp.
    		res[i] = temp[i];
    	}
    	return res;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
	* The function makes sure we maintain the FibonacciHeap invariants after its operation.
    * A.C. Complexity O(logn).
    * W.C. Complexity O(n).
    */
    public void delete(HeapNode x) // Complexity O(logn).
    {   
    	if (x.getParent() != null) { // Make sure the node becomes a root
    		cascadingCut(x);
    	}
    	this.minimalRoot = x; // Set the node to be the minimal root so it would be deleted.
    	this.deleteMin(); // Delete the minimal root, "catching" the true minimal root in the process
    }

    /*
     *  Helper function for cascadingCuts().
     *  The function receives a non-root node.
     *  The function cuts the node from its parent and adds it as a root.
     *  Complexity O(1).
     */
    private void cut(HeapNode node) {
    	HeapNode parent = node.getParent();
    	node.setParent(null);
    	node.unmark();
    	this.markedCount--;
    	parent.setRank(parent.getRank()-1);
    	if (node.getNext().getKey() == node.getKey()) { // If node is an only child
    		parent.setChild(null);
    	}
    	else {
    		if (parent.getChild().getKey() == node.getKey()) { // If node is the first child and isn't single 
        		parent.setChild(node.getNext());
    		}
    		node.getNext().setPrev(node.getPrev());
    		node.getPrev().setNext(node.getNext());
    	}
    	this.firstRoot.getPrev().setNext(node); // Fixing siblings of the node
    	node.setPrev(this.firstRoot.getPrev());
    	this.firstRoot.setPrev(node);
    	node.setNext(this.firstRoot);
    	if (this.minimalRoot.getKey() > node.getKey()) { // Validating minimalRoot
    		this.minimalRoot = node;
    	}
    	FibonacciHeap.totalCuts++;
    	this.treeCount++;
    }
    
    /*
     *  Helper function for decreaseKey()
     *  The function receives a node and begins the cascading cut process.
     *  A.C. Complexity O(1).
     *  W.C. Complexity O(logn).
     */
    private void cascadingCut(HeapNode node) {
    	HeapNode parent = node.getParent();
    	cut(node);
    	if (parent.getParent() != null) { // Check if parent isn't a root
    		if (!parent.getMarked()) { // If the parent isn't marked
    			parent.mark();
    		}
    		else { // If the parent is marked
    			cascadingCut(parent);
    		}
    	}	
    }
    
    /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * A.C. complexity O(1).
    * W.C. complexity O(logn).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey() - delta); // Decrease the node's key
    	if (x.getParent() == null) {
    		return;
    	}
    	if (x.getKey() < x.getParent().getKey()) { // Cascade if necessary
    		cascadingCut(x);
    	}
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    * Complexity O(1). 
    */
    public int potential()
    {    
    	return this.treeCount + 2 * this.markedCount; // Calculates using the values of the respected fields.
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    * Complexity O(1).
    */
    public static int totalLinks()
    {    
    	return totalLinks; // Get the value of totalLinks field.
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    * Complexity O(1). 
    */
    public static int totalCuts() 
    {    
    	return totalCuts; // Get the value of totalCuts field.
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H.
    * Complexity O(k*deg(h)). 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {   
    	if (H.isEmpty()) {
    		return new int[0];
    	}
    	FibonacciHeap helperHeap = new FibonacciHeap(); // Init. a helper heap
    	int[] arr = new int[k]; // Init a k-length int array
    	// Insert minimal root to helper heap, record its child in H
    	helperHeap.insertHelper(H.minimalRoot.getKey(), H.minimalRoot.getChild());
    	for (int i = 0; i < k; i++) { // Delete k minimums from the helper heap, inserting its children in its place
    		arr[i] = helperHeap.minimalRoot.getKey(); // The minimal key will be inserted into arr
    		if (helperHeap.minimalRoot.getSpecialChild() != null) {
    			HeapNode child = helperHeap.minimalRoot.getSpecialChild();
    			int first = child.getKey();
    			do {
    				helperHeap.insertHelper(child.getKey(), child.getChild());
    				child = child.getNext();
    			} while (child.getKey() != first);
    		}	
    		helperHeap.deleteMin();
    	}
    	return arr;
    }

	/**
	 * Helper function.
	 * The function returns the first root.
	 * Complexity O(1).
	 */
	public HeapNode getFirst() {
		return this.firstRoot;
	}
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
    	private boolean mark;
    	private int rank;
    	private HeapNode parent, prev, next, child, specialChild;
    		
    	/*
    	 * Constructor for HeapNode.
    	 * The function receives a key. 
    	 * The function sets the key field to the key inserted.
    	 * Complexity O(1).
    	 */
    	public HeapNode(int key) {
    		this.key = key;
    	}
    	
    	public int getKey() { // Returns the key of the node. Complexity O(1).
    		return this.key;
    	}
    	
    	public void setKey(int k) { // Sets the key of the node. Complexity O(1).
    		this.key = k;
    	}

		public boolean getMarked() { // Returns the mark of the node. Complexity O(1).
    		return this.mark;
    	}

		public void mark() { // Marks the node AKA turns its mark field value to true. Complexity O(1).
    		this.mark = true; // Means a child was deleted from this node.
    	}

		public void unmark() { // Unmark the node AKA turns its mark field value to false. Complexity O(1).
    		this.mark = false; // Means we deleted the node from its parent and made it a root.
    	}

		public int getRank() { // Returns the number of children this node has. Complexity O(1).
    		return this.rank;
    	}
    	
    	public void setRank(int r) { // Sets the number of children this node has. Complexity O(1).
    		this.rank = r; // r represents the number of children.
    	}
    	
    	public HeapNode getParent() { // Returns the parent node of this node. Complexity O(1).
    		return this.parent; // parent could be null if node is root.
    	}
    	
    	public void setParent(HeapNode p) { // Sets the parent node of this node. Complexity O(1).
    		this.parent = p; // p is a node representing the happy newly parent to be.
    	}
    	
    	public HeapNode getPrev() { // Returns the previous node of this node, AKA its left brother. Complexity O(1).
    		return this.prev;
    	}
    	
    	public void setPrev(HeapNode p) { // Sets this node's previous brother - its left node. Complexity O(1).
    		this.prev = p; // p is a node representing the left brother.
    	}
    	
    	public HeapNode getNext() { // Returns the next node of this node, AKA its right brother. Complexity O(1).
    		return this.next;
    	}
    	
    	public void setNext(HeapNode n) { // Sets this node's next brother - its right node. Complexity O(1).
    		this.next = n;
    	}
    	
    	public HeapNode getChild() { // Returns the leftmost child of this node. Complexity O(1).
    		return this.child;
    	}
    	
    	public void setChild(HeapNode c) { // Sets the leftmost child of this node. Complexity O(1).
    		this.child = c; // c is a node representing the leftmost child of this node.
    	}
    	
    	private HeapNode getSpecialChild() { // Returns the specialChild field of the node. Used only in kMin(). Complexity O(1).
    		return this.specialChild;
    	}
    	
    	private void setSpecialChild(HeapNode c) { // Sets the specialChild field of the node. Used only in kMin(). Complexity O(1).
    		this.specialChild = c;
    	}
    }
}
