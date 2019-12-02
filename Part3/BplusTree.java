import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
public class BplusTree <K extends Comparable<K>, V> implements Serializable{
 
	protected BplusNode<K, V> root;
 
	protected int order;
 
	protected BplusNode<K, V> head;
 
	protected int height = 0;
	
	public BplusNode<K, V> getHead() {
		return head;
	}
 
	public void setHead(BplusNode<K, V> head) {
		this.head = head;
	}
 
	public BplusNode<K, V> getRoot() {
		return root;
	}
 
	public void setRoot(BplusNode<K, V> root) {
		this.root = root;
	}
 
	public int getOrder() {
		return order;
	}
 
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return height;
	}
	
	public V get(K key) {
		return root.get(key);
	}
	public V getLessOrMore(K key,K termination,int mode) {
		return root.getLessOrMore(key,termination, mode);
	}
	public void insertOrUpdate(K key, V value) {
		root.insertOrUpdate(key, value, this);
	}
 
	public BplusTree(int order) {
		if (order < 3) {
			System.err.print("order must be greater than 2");
			System.exit(0);
		}
		this.order = order;
		root = new BplusNode<K, V>(true, true);
		head = root;
	}
 
}



