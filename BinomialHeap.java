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
		numberOfTrees++; //Link decreases this as necessary
		size++; //increase size by 1, to make sure it always matches the number of nodes in the tree; this is because insert adds only a single node.
		HeapItem item = new HeapItem();
		item.key = key; item.info = info;
		HeapNode node = new HeapNode();
		AssignNode(item, node);
		if (numberOfTrees == 1) 
		{
			min = last = node.next = node;
			return item;
		}
		if (key < min.item.key)
			min = node;
		HeapNode minTree = last.next;
		last.next = node;
		node.next = minTree;
		while (minTree != node && minTree.rank == node.rank)
		{//minTree and its sibling need Linking()
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
		return item;
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		if (null == min)
			return;
		if (min.rank == 0)
		{ //simple case where min is a B0
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
					if (temp.item.key < min.item.key)
						min = temp;
					temp = temp.next;
				}
			}
			return;
		}
		/*-*/ SumRanksOfDeletions += min.rank;
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
		return null == min ? null : min.item;
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
		while (null != item.node.parent && item.key < item.node.parent.item.key)
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
		//integer overflow trick is intended; this make key - diff equal min_value 
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
		HeapNode minA = last.next, minB = heap2.last.next, carry = null, tail = null, head = null, remainder = null;
		last.next = heap2.last.next = null;
		while (true) 
		{
			if (null == carry)
			{
				if (minA.rank < minB.rank)
				{ //add minA to linked root list 
					if (null == tail) 
						tail = head = minA;
					else 
					{
						tail.next = minA;
						tail = minA;
					}
					if (null == minA.next)
					{
						remainder = minB;
						break;
					}
					minA = minA.next;
				}
				else if (minB.rank < minA.rank)
				{//add minA to linked root list 
					if (null == tail) 
						tail = head = minB;
					else 
					{
						tail.next = minB;
						tail = minB;
					}
					if (null == minB.next)
					{
						remainder = minA;
						break;
					}
					minB = minB.next;
				}
				else 
				{//minA and minB share a rank, link and carry them
					HeapNode nextA = minA.next, nextB = minB.next;
					carry = Link(minA, minB);
					if (null == nextA)
					{
						remainder = nextB; //whether null or not
						break;
					}
					else if (null == nextB)
					{
						remainder = nextA;
						break;
					}
					minA = nextA; minB = nextB;
				}
			}
			else
			{//carry exists
				if (carry.rank < minA.rank && carry.rank < minB.rank)
				{ //carry has minimal rank among nodes
					if (null == tail) 
						tail = head = carry;
					else 
					{
						tail.next = carry;
						tail = carry;
					}
					carry = null;
					continue;
				}
				if (minA.rank < minB.rank)
				{ //necessarily minA.rank == carry.rank
					HeapNode nextA = minA.next; 
					carry = Link(minA, carry);
					if (null == nextA)
					{
						remainder = minB;
						break;
					}
					minA = nextA;
					continue;
				}
				if (minB.rank < minA.rank)
				{ //necessarily minB.rank == carry.rank
					HeapNode nextB = minB.next; 
					carry = Link(minB, carry);
					if (null == nextB)
					{
						remainder = minA;
						break;
					}
					minB = nextB;
					continue;
				}
				//the three nodes share the same rank
				//add carry to root list
				if (null == tail) 
					tail = head = carry;
				else 
				{
					tail.next = carry;
					tail = carry;
				}
				HeapNode nextA = minA.next, nextB = minB.next;
				//set carry equals link of A and B
				carry = Link(minA, minB);
				//exit if done with one or both lists
				if (null == nextA)
				{
					remainder = nextB; //whether null or not
					break;
				}
				else if (null == nextB)
				{
					remainder = nextA;
					break;
				}
				minA = nextA; minB = nextB;
			}
		}
		if (null != carry && null != remainder)
		{ //behaves like insert
			while (carry.rank == remainder.rank)
			{ //link carry with remainder and advance remainder
				HeapNode nextR = remainder.next;
				carry = Link(carry, remainder);
				if (null == nextR)
				{ //carry is added below, since it is not null
					remainder = null;
					break;
				}
				remainder = nextR;
			}
		}
		if (null != carry) 
		{//add carry to root list
			if (null == tail) 
				tail = head = carry;
			else 
			{
				tail.next = carry;
				tail = carry;
			}
		}
		if (null != remainder)
		{
			tail.next = remainder;
			while (null != tail.next)
				tail = tail.next;
		}
		tail.next = head;
		last = tail;
		FindMinAndCountTrees();
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

	//TEMPORARY
	public int NumberOfLinks;
	public int SumRanksOfDeletions;
	private HeapNode Link(HeapNode A, HeapNode B)
	{
		NumberOfLinks++;
		if (A.item.key > B.item.key || (A.item.key == B.item.key && B == min)) 
		{ //swap so A is smaller, or is minimum to retain min being a root
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
			if (temp.item.key < min.item.key)
				min = temp;
			temp = temp.next;
		}
	}
}
