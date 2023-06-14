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
		HeapItem item = new HeapItem();
		item.key = key; item.info = info;
		HeapNode node = new HeapNode();
		AssignNode(item, node);
		if (numberOfTrees == 1) 
		{
			min = last = node;
			return item;
		}
		if (key < min.Key())
			min = node;
		HeapNode minTree = last.next;
		HeapNode prev = last;
		prev.next = node;
		node.next = minTree;
		while (minTree != node && minTree.rank == node.rank)
		{
			HeapNode next = minTree.next;
			node = Link(node, minTree);
			prev.next = node;
			node.next = next;
			minTree = next;
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
		size -= 1;
		if (min.rank == 0)
		{
			last.next = min.next;
			min.next = null;
			numberOfTrees--;
			if (numberOfTrees == 0)
				last = null;
		}
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
		if (item.key < min.item.key) 
			min = item.node;
		while (null != item.node.parent && item.key < item.node.parent.Key())
		{
			HeapNode childN = item.node, parentN = item.node.parent;
			HeapItem parent = parentN.item;
			AssignNode(item, parentN);
			AssignNode(parent, childN);
		}
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
		HeapNode minA = last.next, minB = heap2.last.next, carry = null, prev = null, first = null, remainder = null;
		while (true)
		{
			if (null == carry)
			{
				if (minA.rank == minB.rank)
				{
					int prevRank = minA.rank;
					HeapNode nA = minA.next, nB = minB.next; 
					carry = Link(minA, minB);
					minA = nA; minB = nB;
					if (minA.rank <= prevRank)
					{
						if (prevRank < minB.rank)
							remainder = minB;
						break;
					}
					else if (minB.rank <= prevRank)
					{
						remainder = minA;
						break;
					}
				}
				if (minA.rank < minB.rank)
				{
					int prevRank = minA.rank;
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
					if (minA.rank <= prevRank)
					{
						remainder = minB;
						break;
					}
				}
				else //minB.rank < minA.rank
				{
					int prevRank = minB.rank;
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
					if (minB.rank <= prevRank)
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
					HeapNode nA = minA.next;
					carry = Link(minA, carry);
					minA = nA;
				}
				else if (minB.rank < minA.rank)
				{ //necessarily, minB.rank == carry.rank
					HeapNode nB = minB.next;
					carry = Link(minB, carry);
					minA = nB;
				}
				else 
				{ //the three nodes share a rank
					HeapNode nA = minA.next, nB = minB.next; 
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
					minA = nA; minB = nB;
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
					prev.next = carry;
					prev = carry;
					carry = null;
					break;
				}
				else 
				{
					HeapNode nR = remainder.next;
					int prevRank = remainder.rank;
					carry = Link(remainder, carry);
					remainder = nR;
					if (remainder.rank <= prevRank)
					{
						remainder = null;
						break;
					}
				}
			}
		}
		if (null == remainder)
		{
			prev.next = carry;
			prev = carry;
			carry = null;
		}
		else //carry is null, remainder isn't 
		{ 
			prev.next = remainder;
			while (prev.rank <= prev.next.rank)
			{
				prev = prev.next;
			}
		}
		prev.next = first;
		last = prev;
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
		return numberOfTrees; // should be replaced by student code
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
		B.next = A.child.next;
		A.child.next = B;
		B.parent = A;
		A.child = B;
		A.rank++;
		numberOfTrees--;
		return A;
	}
	//#endregion
}
