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
    public boolean isEmpty() // Complexity O(1).
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
    public HeapNode insert(int key) // Complexity O(1).
    {   
    	HeapNode node = new HeapNode(key);
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
		root2.setParent(root1);
		root1.setChild(root2);
		root1.setRank(root1.getRank()+1);
		//root1.rank++; // TO DO - use setRank()
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
    	if (root1.getKey() < root2.getKey()) { // Need to make root2 the child of root 1.
    		return linkHelper(root1, root2);
    	}
    	return linkHelper(root2, root1); // Else - root1 is the child of root2.
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
    public HeapNode findMin() // Complexity O(1).
    {
    	return this.minimalRoot; // Get the value of minimalRoot field.
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2) // Complexity O(1).
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
    * Returns the number of elements in the heap.
    *   
    */
    public int size() // Complexity O(1).
    {
    	return this.size; // Get the value of size field.
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep() // Complexity O(n).
    {
    	if (this.isEmpty()) { // Heap is empty - return an empty array.
    		return new int[] {};
    	} // Else...:
    	int length = 2 * (int)Math.floor(Math.log(this.size) / Math.log(2)) + 1; // Large size just in case
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
    *
    */
    public void delete(HeapNode x) // Complexity O(logn).
    {    
    	this.decreaseKey(x, Integer.MAX_VALUE); // First decrease the key, so it'd be the new minimal node, O(1) amortized complexity.
    	this.deleteMin(); // Now we can call the original deleteMin function, in O(logn) complexity.
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) // Complexity O(1) amortized.
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
    public int potential() // Complexity O(1).
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
    */
    public static int totalLinks() // Complexity O(1).
    {    
    	return totalLinks; // Get the value of totalLinks field.
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() // Complexity O(1).
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
    	
    	public int getKey() { // Returns the key of the node. Complexity O(1).
    		return this.key;
    	}
    	
    	private void setKey(int k) { // Sets the key of the node. Complexity O(1).
    		this.key = k;
    	}
    	private boolean getMark() { // Returns the mark of the node. Complexity O(1).
    		return this.mark;
    	}
    	
    	private void mark() { // Marks the node AKA turns its mark field value to true. Complexity O(1).
    		this.mark = true; // Means a child was deleted from this node.
    	}
    	
    	private void unmark() { // Unmark the node AKA turns its mark field value to false. Complexity O(1).
    		this.mark = false; // Means we deleted the node from its parent and made it a root.
    	}
    	
    	private int getRank() { // Returns the number of children this node has. Complexity O(1).
    		return this.rank;
    	}
    	
    	private void setRank(int r) { // Sets the number of children this node has. Complexity O(1).
    		this.rank = r; // r represents the number of children.
    	}
    	
    	private HeapNode getParent() { // Returns the parent node of this node. Complexity O(1).
    		return this.parent; // parent could be null if node is root.
    	}
    	
    	private void setParent(HeapNode p) { // Sets the parent node of this node. Complexity O(1).
    		this.parent = p; // p is a node representing the happy newly parent to be.
    	}
    	
    	private HeapNode getPrev() { // Returns the previous node of this node, AKA its left brother. Complexity O(1).
    		return this.prev;
    	}
    	
    	private void setPrev(HeapNode p) { // Sets this node's previous brother - its left node. Complexity O(1).
    		this.prev = p; // p is a node representing the left brother.
    	}
    	
    	private HeapNode getNext() { // Returns the next node of this node, AKA its right brother. Complexity O(1).
    		return this.next;
    	}
    	
    	private void setNext(HeapNode n) { // Sets this node's next brother - its right node. Complexity O(1).
    		this.next = n;
    	}
    	
    	private HeapNode getChild() { // Returns the leftmost child of this node. Complexity O(1).
    		return this.child;
    	}
    	
    	private void setChild(HeapNode c) { // Sets the leftmost child of this node. Complexity O(1).
    		this.child = c; // c is a node representing the leftmost child of this node.
    	}
    	
    }
}
