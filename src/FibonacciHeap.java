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
    *   
    */
    public boolean isEmpty()
    {
    	return size == 0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {   
    	HeapNode node = new HeapNode(key);
    	if (this.isEmpty()) {
    		this.firstRoot = node;
    		this.minimalRoot = node;
    		node.setNext(node);
    		node.setPrev(node);
    	}
    	else {
    		HeapNode lastRoot = this.firstRoot.prev;
    		node.setNext(this.firstRoot);
    		node.setPrev(lastRoot);
    		lastRoot.setNext(node);
    		this.firstRoot.setPrev(node);
    		if (this.minimalRoot.getKey() > node.getKey()) {
    			this.minimalRoot = node;
    		}
    	}	
    	this.size++;
    	this.treeCount++;
    	return node;
    }

    /*
     * Helper function for link()
     * The function receives the root nodes root1 and root2, and links them.
     * The function assumes root1.key < root2.key.
     * The function returns root1
     * Complexity O(1). 
     */
    private HeapNode linkHelper(HeapNode root1, HeapNode root2) {
    	HeapNode prev2 = root2.getPrev();
		HeapNode next2 = root2.getNext();
		prev2.setNext(next2);
		next2.setPrev(prev2);
		if (this.firstRoot.getKey() == root2.getKey()) {
			this.firstRoot = next2;
		}
		if (root1.getChild() == null) {
			root2.setNext(root2);
			root2.setPrev(root2);
		}
		else {
			HeapNode child1 = root1.getChild();
			child1.getPrev().setNext(root2);
			root2.setPrev(child1.getPrev());
			child1.setPrev(root2);
			root2.setNext(child1);
		}
		root2.setParent(root1);
		root1.setChild(root2);
		root1.rank++;
		this.treeCount--;
		return root1;
    }
    
    /*
     *  Helper function for successiveLinking().
     *  The function receives two root nodes of the same rank.
     *  The function links the two nodes according to the algorithm we saw in class.
     *  The function validates the fields of the nodes and of the heap.
     *  The function returns the root of the new linked tree.
     *  Complexity O(1).
     */
    private HeapNode link(HeapNode root1, HeapNode root2) {
    	if (root1.getKey() < root2.getKey()) {
    		return linkHelper(root1, root2);
    	}
    	return linkHelper(root2, root1);
    }
    
    /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	return; // should be replaced by student code
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.minimalRoot;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	if (!heap2.isEmpty()) {
    		if (this.isEmpty()) {
    			this.firstRoot = heap2.firstRoot;
    			this.minimalRoot = heap2.minimalRoot;
    		}
    		else {
    			HeapNode lastRoot = this.firstRoot.prev;
    			HeapNode newLastRoot = heap2.firstRoot.prev;
    			this.firstRoot.setPrev(newLastRoot);
    			newLastRoot.setNext(this.firstRoot);
    			lastRoot.setNext(heap2.firstRoot);
    			heap2.firstRoot.setPrev(lastRoot);
    			if (this.minimalRoot.getKey() < heap2.minimalRoot.getKey()) {
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
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep()
    {
    	if (this.isEmpty()) {
    		return new int[] {};
    	}
    	int length = 2 * (int)Math.floor(Math.log(this.size) / Math.log(2)) + 1;
    	int[] temp = new int[length];
    	HeapNode root = this.firstRoot;
    	for (int i = 0; i < this.treeCount; i++) {
    		temp[root.getRank()]++;
    		root = root.getNext();
    	}
    	int actualLength = 0;
    	for (int i = temp.length-1; i > -1; i++) {
    		if (temp[i] > 0) {
    			actualLength = i + 1;
    			break;
    		}
    	}
    	int[] res = new int[actualLength];
    	for (int i = 0; i < res.length; i++) {
    		res[i] = temp[i];
    	}
    	return res;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, Integer.MAX_VALUE);
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	return; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.treeCount + 2 * this.markedCount;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[100];
        return arr; // should be replaced by student code
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
    	private HeapNode parent, prev, next, child;

    	public HeapNode(int key) {
    		this.key = key;
    	}
    	
    	public int getKey() {
    		return this.key;
    	}
    	
    	private void setKey(int k) {
    		this.key = k;
    	}
    	private boolean getMark() {
    		return this.mark;
    	}
    	
    	private void mark() {
    		this.mark = true;
    	}
    	
    	private void unmark() {
    		this.mark = false;
    	}
    	
    	private int getRank() {
    		return this.rank;
    	}
    	
    	private void setRank(int r) {
    		this.rank = r;
    	}
    	
    	private HeapNode getParent() {
    		return this.parent;
    	}
    	
    	private void setParent(HeapNode p) {
    		this.parent = p;
    	}
    	
    	private HeapNode getPrev() {
    		return this.prev;
    	}
    	
    	private void setPrev(HeapNode p) {
    		this.prev = p;
    	}
    	
    	private HeapNode getNext() {
    		return this.next;
    	}
    	
    	private void setNext(HeapNode n) {
    		this.next = n;
    	}
    	
    	private HeapNode getChild() {
    		return this.child;
    	}
    	
    	private void setChild(HeapNode c) {
    		this.child = c;
    	}
    	
    }
}
