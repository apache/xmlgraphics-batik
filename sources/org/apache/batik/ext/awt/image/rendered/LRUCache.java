package org.apache.batik.ext.awt.image.rendered;

public class LRUCache {
	public interface LRUObj {
		public  void    lruSet(LRUNode nde);
		public  LRUNode lruGet();
		public  void    lruRemove();
	}

	public class LRUNode {
		private LRUNode next = null;
		private LRUNode prev = null;
		private LRUObj  obj  = null;
			
		public LRUNode getNext() { return next; }
		public LRUNode getPrev() { return prev; }
		public LRUObj  getObj()  { return obj; }
						
		protected void setNext(LRUNode newNext) { next = newNext; }
		protected void setPrev(LRUNode newPrev) { prev = newPrev; }
		protected void setObj (LRUObj  newObj)  { 
			if (obj != null) obj.lruRemove();

			obj = newObj;
			if (obj != null) obj.lruSet(this);
		}

						
		protected void unlink() {
			// Unlink this node from it's current pos...
			if (getNext() != null)
				getNext().setPrev(getPrev());
			if (getPrev() != null)
				getPrev().setNext(getNext());
			
			setNext(null);
			setPrev(null);
		}
						
		protected void insertBefore(LRUNode nde) {
			// Already here...
			if (this == nde) return;
			
			unlink();
			
			// Actually insert this node...
			if (nde == null) {
				// empty lst...
				setNext(this);
				setPrev(this);
			} else {
				setNext(nde);
				setPrev(nde.getPrev());
				getNext().setPrev(this);
				getPrev().setNext(this);
			}
		}
	}

	public class LRUList {

		private LRUNode head = null;
		private int     size = 0;
			
		public LRUList() {}
			
		public synchronized int getSize() { return size; }
			
		public synchronized void empty() {
			while(size > 0) pop();
		}
			
		public LRUNode getHead() { return head; }
		public LRUNode getTail() { return head.getPrev(); }
			
		public synchronized void touch(LRUNode nde) {
			if (nde == null) return;
			nde.insertBefore(head);
			head = nde;
		}

		public synchronized void add(LRUNode nde) {
			touch(nde);
			size++;
		}
			
		public synchronized void remove(LRUNode nde) {
			if (nde == null) return;
			if (nde == head) head = nde.getNext();
			nde.unlink();
			size--;
		}
			
		public synchronized LRUNode pop() {
			if (head == null) return null;
			
			LRUNode nde = head;
			
			if (head.getNext() == head) head = null;  // Last node...
			else                        head = head.getNext();
			
			nde.unlink();
			size--;
			return nde;
		}
	}

	private LRUList free    = null;
	private LRUList used    = null;
	private int     maxSize = 0;
		
	public LRUCache(int size) {
		if (size <= 0) size=1;
		maxSize = size;
		
		free = new LRUList();
		used = new LRUList();
		
		while (size > 0) {
			free.add(new LRUNode());
			size--;
		}
	}

	public int getUsed() {
		return used.getSize();
	}

	public void setSize(int newSz) {

		if (maxSize < newSz) {  // list grew...

			for (int i=maxSize; i<newSz; i++)
				free.add(new LRUNode());

		} else if (maxSize > newSz) {

			for (int i=used.getSize(); i>newSz; i--) {
				LRUNode nde = used.getTail();
				used.remove(nde);
				nde.setObj(null);
			}
		}

		maxSize = newSz;
	}

	public void flush() {
		while (used.getSize() > 0) {
			LRUNode nde = used.pop();
			nde.setObj(null);
			free.add(nde);
		}
	}

	public void remove(LRUObj obj) {
		LRUNode nde = obj.lruGet();
		if (nde == null) return;
		used.remove(nde);
		nde.setObj(null);
		free.add(nde);
	}

	public void touch(LRUObj obj) {
		LRUNode nde = obj.lruGet();
		if (nde == null) return;
		used.touch(nde);
	}

	public void add(LRUObj obj) {
		LRUNode nde = obj.lruGet();

		// already linked in...
		if (nde != null) {
			used.touch(nde);
			return;
		}

		if (free.getSize() > 0) {
			nde = free.pop();
			nde.setObj(obj);
			used.add(nde);
		} else {
			nde = used.getTail();
			nde.setObj(obj);
			used.touch(nde);
		}
	}

	protected void print() {
		System.out.println("In Use: " + used.getSize() +
						   " Free: " + free.getSize());
		LRUNode cur = used.getHead();
		do {
			System.out.println(cur.getObj());
			cur = cur.getNext();
		} while (cur != used.getHead());
	}

}
