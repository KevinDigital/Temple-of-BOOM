package student;
/* Time spent on a6:  4 hours and 30 minutes.

 * Name(s): Kevin Li, Darius Davis
 * Netid(s): kl553, dvd9
 * What I thought about this assignment: 
 * This assignment helped me to understand the concept of heaps and priority queues.
 * In this way, this assignment was really beneficial for my understanding of the
 * material.
 */

import java.util.*;

/** An instance is a min-heap of distinct elements of type E.
 *  Priorities are double values. Since it's a min-heap, the value
 *  with the smallest priority is at the root of the heap. */
public class Heap<E> {

	private int size; // number of elements in the heap

	/** The heap invariant is given below. Note that / denotes int division.
	 * 
	 *  b[0..size-1] is viewed as a min-heap, i.e. 
	 *  0. The values in b[0..size-1] are all different.
	 *  1. Each array element in b[0..size-1] contains a value of the heap.
	 *  2. The children of each b[i] are b[2i+1] and b[2i+2].
	 *  3. The parent of each b[i] is b[(i-1)/2].
	 *  4. Each b[i]'s priority is >= its parent's priority.
	 *  5. Priorities for the b[i] used for the comparison in point 4
	 *     are given in map. Field map contains one entry for each element of
	 *     the heap, so map and b have the same size.
	 *     For each element e in the heap, its map entry contains in the Prindex
	 *     (for PRIority and INDEX) object the priority of e and its index in b.
	 */
	private ArrayList<E> b;
	private HashMap<E, Prindex> map= new HashMap<E, Prindex>();

	/** Constructor: an empty heap. */
	public Heap() {
		b= new ArrayList<E>();
	}

	/** Return a string that gives this heap, in the format:
	 * [item0:priority0, item1:priority1, ..., item(N-1):priority(N-1)]
	 * Thus, the list is delimited by '['  and ']' and ", " (i.e. a
	 * comma and a space char) separate adjacent items. */
	@Override public String toString() {
		String s= "[";
		for (E t : b) {
			if (s.length() > 1) {
				s = s + ", ";
			}
			s = s + t + ":" + map.get(t).priority;
		}
		return s + "]";
	}

	/** Return a string that gives the priorities in this heap,
	 * in the format: [priority0, priority1, ..., priority(N-1)]
	 * Thus, the list is delimited by '['  and ']' and ", " (i.e. a
	 * comma and a space char) separate adjacent items. */
	public String toStringPriorities() {
		String s= "[";
		for (E t : b) {
			if (s.length() > 1) {
				s = s + ", ";
			}
			s = s + map.get(t).priority;
		}
		return s + "]";
	}

	/** Return the number of elements in this heap.
	 * This operation takes constant time. */
	public int size() {
		return size;
	}

	/** Add e with priority p to the heap.
	 *  Throw an illegalArgumentException if e is already in the heap.
	 *  The expected time is logarithmic and the worst-case time is linear
	 *  in the size of the queue. */ 
	public void add(E e, double p) throws IllegalArgumentException {
		//TODO 1: Do add and bubbleUp together.

		if (map.containsKey(e)) {
			throw new IllegalArgumentException();
		}

		b.add(e);

		Prindex heapElt = new Prindex(size, p);
		map.put(e, heapElt);

		size = size + 1;

		bubbleUp(size - 1);

	}

	/** Return the element of this heap with lowest priority, without
	 *  changing the heap. This operation takes constant time.
	 *  Throw a HeapException if the heap is empty. */
	public E peek() {
		// TODO 2: Do peek.

		if (size == 0) {
			throw new HeapException();
		}

		return b.get(0);
	}

	/** Remove and return the element of this heap with lowest priority.
	 *  The expected time is logarithmic and the worst-case time is linear
	 *  in the size of the heap.
	 *  Throw a HeapException if the heap is empty. */
	public E poll() {
		// TODO 3: Do poll and bubbleDown together.
		// Do NOT create new map entries
		// Look in the specification at the required time bounds

		if (size == 0) {
			throw new HeapException();
		}

		if (size == 1) {
			size = 0;
			return b.remove(0);
		}

		// At least 2 elements in queue
		E val = b.get(0);
		size = size - 1;
		b.set(0, b.get(size));
		b.remove(size);

		map.get(b.get(0)).index = 0;

		bubbleDown(0);

		return val;
	}

	/** Change the priority of element e to p.
	 *  The expected time is logarithmic and the worst-case time is linear
	 *  in the size of the heap.
	 *  Throw an illegalArgumentException if e is not in the heap. */
	public void changePriority(E e, double p) {
		// TODO 4: Do updatePriority.
		// Do NOT create new map entries

		if (!map.containsKey(e)) {
			throw new IllegalArgumentException();
		}

		double oldPrior = map.get(e).priority;

		map.get(e).priority = p;

		if (oldPrior > p) {
			bubbleUp(map.get(e).index);
		}

		else if (oldPrior < p) {
			bubbleDown(map.get(e).index);
		}
	}

	/** Bubble b[k] up in heap to its right place.
	 *  Precondition: Each b[i]'s priority >= parent's priority 
	 *                except perhaps for b[k] */
	private void bubbleUp(int k) {
		// TODO1 Do add and bubbleUp together.
		// Do not use recursion; use iteration.
		// Do NOT create new map entries

		while (k > 0) {
			int p= (k-1) / 2;  //p is k's parent

			double priorK = map.get(b.get(k)).priority;
			double priorP = map.get(b.get(p)).priority;
			int indexK = map.get(b.get(k)).index;
			int indexP = map.get(b.get(p)).index;

			if (priorK >= priorP) return;

			// Swap index

			int tempI= indexP; 
			map.get(b.get(p)).index= indexK; 
			map.get(b.get(k)).index= tempI;

			E temp1 = b.get(k);
			E temp2 = b.get(p);
			b.set(indexP, temp1);
			b.set(indexK, temp2);

			k= indexP;
		}

	}

	/** Bubble b[k] down in heap until it finds the right place.
	 *  Precondition: Each b[i]'s priority <= childrens' priorities 
	 *                except perhaps for b[k] */
	private void bubbleDown(int k) {
		// TODO 3:  Do poll and bubbleDown together.
		// Do not use recursion; use iteration.
		// Do NOT create new map entries



		while (2*k+1 < size) {
			int c= smallerChildOf(k);

			double priorK = map.get(b.get(k)).priority;
			double priorC = map.get(b.get(c)).priority;
			int indexK = map.get(b.get(k)).index;
			int indexC = map.get(b.get(c)).index;

			if (priorK <= priorC) return;

			// Swap b[k] and b[c]
					int tempI= indexC; 
			map.get(b.get(c)).index= indexK; 
			map.get(b.get(k)).index= tempI;

			E temp1 = b.get(k);
			E temp2 = b.get(c);
			b.set(indexC, temp1);
			b.set(indexK, temp2);

			k= c;
		}

	}

	/** Return the index of the smaller child of b[n]
	 *  If the two children have the same priority, choose the right one.
	 *  Precondition: left child exists: 2n+1 < size of heap */
	private int smallerChildOf(int n) {
		// You do not have to implement this method. We found it useful.
		// Implement it if you want.
		// Change its specification if you want.
		int c= 2*n + 1;
		if (c + 1  >=  size || map.get(b.get(c)).priority < map.get(b.get(c+1)).priority) return c;
		return c+1;
	}

	/** An instance contains the priority and index an element of the heap. */
	private static class Prindex {
		private double priority; // priority of this element
		private int index;  // index of this element in heap b

		/** Constructor: an instance in b[i] with priority p. */
		private Prindex(int i, double p) {
			index= i;
			priority= p;
		}
	}
}