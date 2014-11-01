package cofh.lib.util;

import com.google.common.base.Objects;
import com.google.common.primitives.Ints;

@SuppressWarnings("unchecked")
public class LinkedHashList<E extends Object> { // TODO: implements List<E>, Deque<E>, Cloneable, java.io.Serializable

	private static final class Entry {
		Entry next;
		Entry prev;
		final Object key;
		final int hash;
		Entry nextInBucket;

		Entry(Object key, int keyHash) {

			this.key = key;
			this.hash = keyHash;
		}
	}

	private static int hash(Object n) {

		int h = n.hashCode();
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	Entry head;
	Entry tail;
	private int size;
	private int mask;
	private Entry[] hashTable;

	public LinkedHashList() {

		hashTable = new Entry[8];
	}

	public boolean push(E obj) {

		int hash = hash(obj);
		if (seek(obj, hash) != null)
			return false;

		Entry e;
		insert(e = new Entry(obj, hash));
		rehashIfNecessary();
		e.prev = tail;
		e.next = null;
		if (tail != null)
			tail.next = e;
		else
			head = e;
		tail = e;
		return true;
	}

	public E pop() {

		Entry e = tail;
		if (e != null) {
			delete(e);
			tail = e.prev;
			e.prev = null;
			if (tail != null)
				tail.next = null;
			else
				head = null;
			return (E) e.key;
		}
		return null;
	}

	public E peek() {

		return tail != null ? (E) tail.key : null;
	}

	public E poke() {

		return head != null ? (E) head.key : null;
	}

	public boolean unshift(E obj) {

		int hash = hash(obj);
		if (seek(obj, hash) != null)
			return false;

		Entry e;
		insert(e = new Entry(obj, hash));
		rehashIfNecessary();
		e.next = head;
		e.prev = null;
		if (head != null)
			head.prev = e;
		else
			tail = e;
		head = e;
		return true;
	}

	public E shift() {

		Entry e = head;
		if (e != null) {
			delete(e);
			head = e.next;
			e.next = null;
			if (head != null)
				head.prev = null;
			else
				tail = null;
			return (E) e.key;
		}
		return null;
	}

	public int size() {
		return size;
	}

	public boolean contains(E obj) {

		return seek(obj, hash(obj)) != null;
	}

	private Entry seek(E obj, int hash) {

		for (Entry entry = hashTable[hash & mask];
				entry != null;
				entry = entry.nextInBucket)
			if (hash == entry.hash && Objects.equal(obj, entry.key))
				return entry;

		return null;
	}

	private void insert(Entry entry) {

		int bucket = entry.hash & mask;
		entry.nextInBucket = hashTable[bucket];
		hashTable[bucket] = entry;
		++size;
	}

	private void delete(Entry entry) {

		int bucket = entry.hash & mask;
		Entry prev = null, cur = hashTable[bucket];
		l: {
			if (cur != entry) for (; true; cur = cur.nextInBucket) {
				if (cur == entry) {
					prev.nextInBucket = entry.nextInBucket;
					break l;
				}
				prev = cur;
			}
			hashTable[bucket] = cur.nextInBucket;
		}
		--size;
	}

	private void rehashIfNecessary() {

		Entry[] old = hashTable, newTable;
		if (size > old.length * 2 && old.length < Ints.MAX_POWER_OF_TWO) {
			int newTableSize = old.length * 2, newMask = newTableSize - 1;
			newTable = hashTable = new Entry[newTableSize];
			mask = newMask;

			for (int bucket = old.length; bucket --> 0 ; ) {
				Entry entry = old[bucket];
				while (entry != null) {
					Entry nextEntry = entry.nextInBucket;
					int keyBucket = entry.hash & newMask;
					entry.nextInBucket = newTable[keyBucket];
					newTable[keyBucket] = entry;
					entry = nextEntry;
				}
			}
		}
	}
}