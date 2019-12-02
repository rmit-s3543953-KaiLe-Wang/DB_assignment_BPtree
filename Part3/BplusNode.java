import java.util.AbstractMap.SimpleEntry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class BplusNode<K extends Comparable<K>, V> implements Serializable {

	protected boolean isLeaf;

	protected boolean isRoot;
	
	protected boolean traversed=false;
	
	protected BplusNode<K, V> parent;

	protected BplusNode<K, V> previous;

	protected BplusNode<K, V> next;

	protected List<Entry<K, V>> entries;

	protected List<BplusNode<K, V>> children;

	public BplusNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		entries = new ArrayList<Entry<K, V>>();

		if (!isLeaf) {
			children = new ArrayList<BplusNode<K, V>>();
		}
	}

	public BplusNode(boolean isLeaf, boolean isRoot) {
		this(isLeaf);
		this.isRoot = isRoot;
	}
	
	public V getLessOrMore(K key, K termination, int mode) {
		// mode =1 less than
		// mode =2 more than
		String result = new String();
		System.out.println("isLeaf=" + isLeaf);
		if (isLeaf) {
			int low = 0, high = entries.size() - 1, mid;
			int comp;
			while (low <= high) {
				mid = (low + high) / 2;
				comp = entries.get(mid).getKey().compareTo(key);
				if (comp == 0) {
					// find mid value which is equal,
					// then search anything that's less than mid, until null given.
					String temp = entries.get(mid).getValue().toString();
					while (temp != null) {
						try {
							// case: mode =1, find records less than the search key
							result += temp + ",";
							if (mode == 1) {
								temp = entries.get(mid--).getValue().toString();
							}
							// case: mode =2, find records more than the search key
							else if (mode == 2) {
								temp = entries.get(mid++).getValue().toString();
							}
							// System.out.println("range query, temp:" + temp);
						} catch (ArrayIndexOutOfBoundsException e) {
							break;
						} catch (IndexOutOfBoundsException e) {
							break;
						} catch (NullPointerException e) {
							break;
						}
					}
					System.out.println("leaf loop, result:" + result);
					return (V) result;
				} else if (comp < 0) {
					low = mid + 1;
					if(mode==1)
						result +=","+entries.get(mid).getValue().toString();
				} else {
					high = mid - 1;
					if(mode==2)
						result +=","+entries.get(mid).getValue().toString();
				}
			}
			return (V) result;
		}

		// if it is not leaf
		// then, search for the children that is less/greater than the result
		// return the target child.
		if (key.toString().compareTo((String) entries.get(0).getKey()) < 0) {
			return children.get(0).getLessOrMore(key, termination, mode);
		}
		// else, if the key is greater than the key on the most RHS key, move down to
		// next level from the largest key.
		else if (key.toString().compareTo((String) entries.get(entries.size() - 1).getKey()) >= 0) {
			return children.get(children.size() - 1).getLessOrMore(key, termination, mode);
		}

		// if the node is in between, binary search the key and return.
		else {
			int low = 0, high = entries.size() - 1, mid = 0;
			int comp;
			while (low <= high) {
				mid = (low + high) / 2;
				comp = entries.get(mid).getKey().toString().compareTo(key.toString());
				if (comp == 0) {
					String temp = new String();
					// System.out.println("range query, temp:" + temp);
					boolean done = false;
					while (mid >= 0 && mid < entries.size() - 1 && !done) {
						try {
							// case: mode =1, find records less than the search key
							result += temp + ",";
							System.out.println("In children search loop1.");
							if (mode == 1) {
								// get the entry in the children, otherwise will get to this section again.
								for (int i = 0; i < children.get(mid + 1).entries.size(); i++) {
									String val = children.get(mid + 1).entries.get(i).getValue().toString();
									System.out.println("val:" + val + "t:" + get(termination));
									if (val.equals(get(termination))) {
										done = true;
										break;
									}
									temp += "," + children.get(mid + 1).entries.get(i).getValue().toString();
								}
								System.out.println("in bpnode class, mode=" + mode + ", mid=" + mid + " records_count:"
										+ temp.length());
								mid--;
							}
							// case: mode =2, find records more than the search key
							else if (mode == 2) {
								for (int i = 0; i < children.get(mid + 1).entries.size(); i++) {
									String val = children.get(mid + 1).entries.get(i).getValue().toString();
									System.out.println("val:" + val + "t:" + get(termination));
									if (val.equals(get(termination))) {
										done = true;
										break;
									}
									temp += "," + children.get(mid + 1).entries.get(i).getValue().toString();
								}
								System.out.println("in bpnode class, mode=" + mode + ", mid=" + mid + " records_count:"
										+ temp.length());
								mid++;
							}
							// System.out.println("range query, temp:" + temp);
						} catch (ArrayIndexOutOfBoundsException e) {
							break;
						} catch (IndexOutOfBoundsException e) {
							break;
						} catch (NullPointerException e) {
							break;
						}
					}
					return (V) result;
				} else if (comp < 0) {
					low = mid + 1;
				} else {
					high = mid - 1;
				}

			}
			
			return children.get(low).getLessOrMore(key, termination, mode);
		}
	}
	public V get(K key) {

		if (isLeaf) {
			int low = 0, high = entries.size() - 1, mid;
			int comp;
			while (low <= high) {
				mid = (low + high) / 2;
				comp = entries.get(mid).getKey().compareTo(key);
				if (comp == 0) {
					return entries.get(mid).getValue();
				} else if (comp < 0) {
					low = mid + 1;
				} else {
					high = mid - 1;
				}
			}
			return null;
		}
		// if it is not leaf
		// if key is less than the most LHS key, move down to next level from the first
		// node (the smallest key)
		if (key.toString().compareTo((String) entries.get(0).getKey()) < 0) {
			return children.get(0).get(key);
		}
		// else, if the key is greater than the key on the most RHS key, move down to
		// next level from the largest key.
		else if (key.toString().compareTo((String) entries.get(entries.size() - 1).getKey()) >= 0) {
			return children.get(children.size() - 1).get(key);
		}
		// if the node is between them, binary search the key and return.
		else {
			int low = 0, high = entries.size() - 1, mid = 0;
			int comp;
			while (low <= high) {
				mid = (low + high) / 2;
				comp = entries.get(mid).getKey().toString().compareTo(key.toString());
				if (comp == 0) {
					return children.get(mid + 1).get(key);
				} else if (comp < 0) {
					low = mid + 1;
				} else {
					high = mid - 1;
				}
			}
			return children.get(low).get(key);
		}
	}

	public void insertOrUpdate(K key, V value, BplusTree<K, V> tree) {
		// if it is leaf, then no need to split, simply insert it.
		if (isLeaf) {
			if (contains(key) != -1 || entries.size() < tree.getOrder()) {
				insertOrUpdate(key, value);
				if (tree.getHeight() == 0) {
					tree.setHeight(1);
				}
				return;
			}
			// needs split
			BplusNode<K, V> left = new BplusNode<K, V>(true);
			BplusNode<K, V> right = new BplusNode<K, V>(true);
			// new connection
			if (previous != null) {
				previous.next = left;
				left.previous = previous;
			}
			if (next != null) {
				next.previous = right;
				right.next = next;
			}
			if (previous == null) {
				tree.setHead(left);
			}

			left.next = right;
			right.previous = left;
			previous = null;
			next = null;

			//copy the original node info to the new node.
			copy2Nodes(key, value, left, right, tree);

			// if it is not the root
			if (parent != null) {
				//adjust the parent children relation
				int index = parent.children.indexOf(this);
				parent.children.remove(this);
				left.parent = parent;
				right.parent = parent;
				parent.children.add(index, left);
				parent.children.add(index + 1, right);
				parent.entries.add(index, right.entries.get(0));
				entries = null; 
				children = null; 

				// parent update.
				parent.updateInsert(tree);
				parent = null; // remove parent
				//if it is the root.
			} else {
				isRoot = false;
				BplusNode<K, V> parent = new BplusNode<K, V>(false, true);
				tree.setRoot(parent);
				left.parent = parent;
				right.parent = parent;
				parent.children.add(left);
				parent.children.add(right);
				parent.entries.add(right.entries.get(0));
				entries = null;
				children = null;
			}
			return;

		}
		// if it is not leaf, then search the most left-hand side or right-hand side
		if (key.compareTo(entries.get(0).getKey()) < 0) {
			children.get(0).insertOrUpdate(key, value, tree);
		} else if (key.compareTo(entries.get(entries.size() - 1).getKey()) >= 0) {
			children.get(children.size() - 1).insertOrUpdate(key, value, tree);
		} else {
			int low = 0, high = entries.size() - 1, mid = 0;
			int comp;
			while (low <= high) {
				mid = (low + high) / 2;
				comp = entries.get(mid).getKey().compareTo(key);
				if (comp == 0) {
					children.get(mid + 1).insertOrUpdate(key, value, tree);
					break;
				} else if (comp < 0) {
					low = mid + 1;
				} else {
					high = mid - 1;
				}
			}
			if (low > high) {
				children.get(low).insertOrUpdate(key, value, tree);
			}
		}
	}

	private void copy2Nodes(K key, V value, BplusNode<K, V> left, BplusNode<K, V> right, BplusTree<K, V> tree) {
		//length of left + right hand side of the tree.
		int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
		boolean b = false;// has the new key been inserted?
		for (int i = 0; i < entries.size(); i++) {
			if (leftSize != 0) {
				leftSize--;
				if (!b && entries.get(i).getKey().compareTo(key) > 0) {
					left.entries.add(new SimpleEntry<K, V>(key, value));
					b = true;
					i--;
				} else {
					left.entries.add(entries.get(i));
				}
			} else {
				if (!b && entries.get(i).getKey().compareTo(key) > 0) {
					right.entries.add(new SimpleEntry<K, V>(key, value));
					b = true;
					i--;
				} else {
					right.entries.add(entries.get(i));
				}
			}
		}
		if (!b) {
			right.entries.add(new SimpleEntry<K, V>(key, value));
		}
	}

	// update after when the node has been inserted
	protected void updateInsert(BplusTree<K, V> tree) {

		// if the number of children node is greater than the order
		// do "push up" or "split" operation
		if (children.size() > tree.getOrder()) {
			// first, split 2 nodes
			BplusNode<K, V> left = new BplusNode<K, V>(false);
			BplusNode<K, V> right = new BplusNode<K, V>(false);
			// left/right nodes length
			int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
			int rightSize = (tree.getOrder() + 1) / 2;
			// copy to the new node, and update the entry
			for (int i = 0; i < leftSize; i++) {
				left.children.add(children.get(i));
				children.get(i).parent = left;
			}
			for (int i = 0; i < rightSize; i++) {
				right.children.add(children.get(leftSize + i));
				children.get(leftSize + i).parent = right;
			}
			for (int i = 0; i < leftSize - 1; i++) {
				left.entries.add(entries.get(i));
			}
			for (int i = 0; i < rightSize - 1; i++) {
				right.entries.add(entries.get(leftSize + i));
			}

			// if the parent is not null, meaning it is not root.
			if (parent != null) {
				// modify the parent and children relation
				int index = parent.children.indexOf(this);
				parent.children.remove(this);
				left.parent = parent;
				right.parent = parent;
				parent.children.add(index, left);
				parent.children.add(index + 1, right);
				parent.entries.add(index, entries.get(leftSize - 1));
				entries = null;
				children = null;

				// after insertion, update the parent node.
				parent.updateInsert(tree);
				// delete the parent node
				parent = null;
				// if it is root
			} else {
				// no longer become the root.
				isRoot = false;
				BplusNode<K, V> parent = new BplusNode<K, V>(false, true);
				tree.setRoot(parent);
				tree.setHeight(tree.getHeight() + 1);
				left.parent = parent;
				right.parent = parent;
				parent.children.add(left);
				parent.children.add(right);
				parent.entries.add(entries.get(leftSize - 1));
				entries = null;
				children = null;
			}
		}
	}

	// helper method for checking the location of the key.
	protected int contains(K key) {
		int low = 0, high = entries.size() - 1, mid;
		int comp;
		while (low <= high) {
			mid = (low + high) / 2;
			comp = entries.get(mid).getKey().compareTo(key);
			if (comp == 0) {
				return mid;
			} else if (comp < 0) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		return -1;
	}

	protected void insertOrUpdate(K key, V value) {
		int low = 0, high = entries.size() - 1, mid;
		int comp;
		while (low <= high) {
			mid = (low + high) / 2;
			comp = entries.get(mid).getKey().compareTo(key);
			if (comp == 0) {
				String temp = entries.get(mid).getValue().toString();
				entries.get(mid).setValue((V) (temp + "," + value.toString()));
				break;
			} else if (comp < 0) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		if (low > high) {
			entries.add(low, new SimpleEntry<K, V>(key, value));
		}
	}

}
