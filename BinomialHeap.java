/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap
{
	public int size;
	public HeapNode last;
	public HeapNode min;
	
	private int numberOfTrees;

	public BinomialHeap() { }

	private BinomialHeap(HeapNode Last, int Size) 
	{
		last = Last;
		size = Size;
		FindMinAndCountTrees();
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 *
	 */
	public HeapItem insert(int key, String info) 
	{    
		numberOfTrees++;
		size++;
		HeapItem item = new HeapItem();
		item.key = key; item.info = info;
		HeapNode node = new HeapNode();
		AssignNode(item, node);
		if (numberOfTrees == 1) 
		{
			min = last = node.next = node;
			return item;
		}
		if (key < min.Key())
			min = node;
		HeapNode minTree = last.next;
		last.next = node;
		node.next = minTree;
		while (minTree != node && minTree.rank == node.rank)
		{
			HeapNode next = minTree.next;
			if (next == node || next == minTree)
			{
				node = Link(node, minTree);
				node.next = node;
				last = node;
				break;
			}
			else 
			{
				node = Link(node, minTree);
				node.next = next;
				last.next = node;
				minTree = next;
			}
		}
		Validate_HareTortoise();
		return item;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		if (min.rank == 0)
		{
			last.next = min.next;
			min.next = null;
			numberOfTrees--;
			size--;
			if (numberOfTrees == 0)
			{
				last = null;
				min = null;
			}
			else 
			{
				min = last;
				HeapNode temp = last.next;
				while (temp != last)
				{
					if (temp.Key() < min.Key())
						min = temp;
					temp = temp.next;
				}
			}
			return;
		}
		HeapNode minChild = min.child;
		minChild.parent = null;
		minChild = minChild.next;
		while (minChild != min.child)
		{
			minChild.parent = null;
			minChild = minChild.next;
		}
		min.child = null;
		HeapNode afterMin = min.next;
		int treeSize = (int)Math.pow(2, min.rank);
		if (afterMin == min)
		{
			min = null;
			last = null;
		}
		else 
		{
			HeapNode temp = afterMin;
			last = afterMin;
			while (temp.next != min)
			{
				temp = temp.next;
				if (last.rank < temp.rank)
					last = temp;
			}
			temp.next = afterMin; 
			min.next = null;
		}
		numberOfTrees--;
		size -= treeSize;
		BinomialHeap temp = new BinomialHeap(minChild, treeSize - 1);
		meld(temp);
	}

	/**
	 * 
	 * Return the minimal HeapItem
	 *
	 */
	public HeapItem findMin()
	{
		return min.item;
	} 

	/**
	 * 
	 * pre: 0<diff<item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{    
		item.key -= diff;
		while (null != item.node.parent && item.key < item.node.parent.Key())
		{
			HeapNode childN = item.node, parentN = item.node.parent;
			HeapItem parent = parentN.item;
			AssignNode(item, parentN);
			AssignNode(parent, childN);
		}
		if (item.key < min.item.key) 
			min = item.node;
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{    
		decreaseKey(item, Integer.MIN_VALUE + item.key);
		deleteMin();
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		if (heap2.empty()) return;
		if (empty()) 
		{
			size = heap2.size;
			last = heap2.last;
			min = heap2.min;
			numberOfTrees = heap2.numberOfTrees;
			heap2.size = 0;
			heap2.last = null;
			heap2.min = null;
			heap2.numberOfTrees = 0;
			return;
		}	
		size += heap2.size;
		HeapNode minA = last.next, minB = heap2.last.next, carry = null, prev = null, first = null, remainder = null;
		HeapNode youngestA = minA, youngestB = minB;
		while (true)
		{
			if (null == carry)
			{
				if (minA.rank == minB.rank)
				{
					HeapNode nextA = minA.next, nextB = minB.next; 
					carry = Link(minA, minB);
					if (nextA == youngestA)
					{
						if (nextB != youngestB)
							remainder = nextB;
						break;
					}
					else if (nextB == youngestB)
					{
						remainder = nextA;
						break;
					}
					minA = nextA; minB = nextB;
				}
				else if (minA.rank < minB.rank)
				{
					if (prev == null)
					{
						minA = (first = prev = minA).next;
					}
					else 
					{
						prev.next = minA;
						prev = minA;
						minA = minA.next;
					}
					if (minA == youngestA)
					{
						remainder = minB;
						break;
					}
				}
				else //minB.rank < minA.rank
				{
					if (prev == null)
					{
						minB = (first = prev = minB).next;
					}
					else 
					{
						prev.next = minB;
						prev = minB;
						minB = minB.next;
					}
					if (minB == youngestB)
					{
						remainder = minA;
						break;
					}
				}
			}
			else if (carry.rank < minA.rank && carry.rank < minB.rank)
			{
				if (null == first)
				{
					first = prev = carry;
				}
				else 
				{
					prev.next = carry;
					prev = carry;
				}
				carry = null;
			}
			else //carry isn't null but not the strict minimum
			{
				if (minA.rank < minB.rank)
				{ //necessarily, minA.rank == carry.rank
					HeapNode nextA = minA.next;
					carry = Link(minA, carry);
					if (minA == youngestA)
					{
						remainder = minB;
						break;
					}
					minA = nextA; 
				}
				else if (minB.rank < minA.rank)
				{ //necessarily, minB.rank == carry.rank
					HeapNode nextB = minB.next;
					carry = Link(minB, carry);
					if (minB == youngestB)
					{
						remainder = minA;
						break;
					}
					minB = nextB;
				}
				else 
				{ //the three nodes share a rank
					HeapNode nextA = minA.next, nextB = minB.next; 
					if (null == first)
					{
						first = prev = carry;
					}
					else 
					{
						prev.next = carry;
						prev = carry;
					}
					carry = Link(minA, minB);
					if (nextA == youngestA)
					{
						if (nextB != youngestB)
							remainder = nextB;
						break;
					}
					else if (nextB == youngestB)
					{
						remainder = nextA;
						break;
					}
					minA = nextA; minB = nextB;
				}
			}
		}
		if (null == carry && null == remainder)
		{
			System.out.println("Both remainder and carry are null; bugs incoming");
		}
		if (null != remainder && null != carry)
		{
			while (true) 
			{
				if (carry.rank < remainder.rank)
				{
					if (null == prev)
					{
						prev = first = carry;
					}
					else 
					{
						prev.next = carry;
						prev = carry;
					}
					carry = null;
					break;
				}
				else 
				{
					HeapNode remainderNext = remainder.next;
					carry = Link(remainder, carry);
					remainder = remainderNext;
					if (remainder == youngestA || remainder == youngestB)
					{
						remainder = null;
						break;
					}
				}
			}
		}
		if (null == remainder)
		{
			if (null == prev)
			{
				first = prev = carry; 
			}
			else 
			{
				prev.next = carry;
				prev = carry;
			}
			carry = null;
		}
		else //carry is null, remainder isn't 
		{ 
			prev.next = remainder;
			while (prev.rank < prev.next.rank)
			{
				prev = prev.next;
			}
		}
		prev.next = first;
		last = prev;
		Validate_HareTortoise();
		FindMinAndCountTrees();
	}

	private void Validate_HareTortoise()
	{
		if (last == null)
		{
			if (size != 0)
				System.out.println("ERROR");
			return;
		}
		HeapNode hare = last, tort = last;
		do
		{
			tort = tort.next;
			hare = hare.next.next;
		}
		while (hare != tort);
		if (hare != last)
		{
			System.out.println("ERROR");
		}
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return size; 
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return size == 0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		return numberOfTrees;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 */
	public class HeapNode
	{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;

		public int Key()
		{
			return item.key;
		}
		public String Info()
		{
			return item.info;
		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 */
	public class HeapItem
	{
		public HeapNode node;
		public int key;
		public String info;
	}

	//#region helpers
	private void AssignNode(HeapItem Item, HeapNode Node)
	{
		Item.node = Node;
		Node.item = Item;
	}
	private HeapNode Link(HeapNode A, HeapNode B)
	{
		assert A.rank == B.rank: "Incorrect ranks given to Link";
		if (A.Key() > B.Key()) //swap so A is smaller
		{
			HeapNode temp = B;
			B = A;
			A = temp;
		}
		if (A.rank == 0)
		{
			B.next = B;
		}
		else 
		{
			B.next = A.child.next;
			A.child.next = B;
		}		
		B.parent = A;
		A.child = B;
		A.rank++;
		numberOfTrees--;
		return A;
	}

	private void FindMinAndCountTrees()
	{
		HeapNode temp = last.next;
		numberOfTrees = 1;
		min = last;
		while (temp != last)
		{
			numberOfTrees++;
			if (temp.Key() < min.Key())
				min = temp;
			temp = temp.next;
		}
	}

	public void Print() {
		System.out.println("Binomial Heap:");
		System.out.println("Size: " + size);

		if (min != null) {
			System.out.println("Minimum Node: " + min.item.key);
		} else {
			System.out.println("No minimum node.");
		}

		System.out.println("Heap Nodes:");
		if (last != null) {
			java.util.Set<HeapNode> visited = new java.util.HashSet<>();
			PrintHeapNode(last.next, 0, visited);
		} else {
			System.out.println("No heap nodes.");
		}
	}

	private void PrintHeapNode(HeapNode node, int indentLevel, java.util.Set<HeapNode> visited) {
		StringBuilder indent = new StringBuilder();
		for (int i = 0; i < indentLevel; i++) {
			indent.append("    ");
		}

		System.out.println(indent + "K:" + node.Key() + " R: " + node.rank + " N: " + node.next.Key());

		visited.add(node);

		if (node.child != null && !visited.contains(node.child)) {
			System.out.println(indent + "Child:");
			PrintHeapNode(node.child.next, indentLevel + 1, visited);
		}

		if (node.next != null && !visited.contains(node.next)) {
			System.out.println(indent + "Sibling:");
			PrintHeapNode(node.next, indentLevel, visited);
		}
	}
	//#endregion
}
