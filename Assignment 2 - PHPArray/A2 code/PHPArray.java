import java.util.*;

public class PHPArray <V> implements Iterable<V> {
	private static final int INIT_CAPACITY = 4;

	private int N; //number of key-value pairs in the symbol table
	private int M; //size of linear probing table
	private Node<V> [] entries; //the hash table
	private Node<V> head; //head of linked list
	private Node<V> tail; //tail of linked list
  private int resetCount; //reset counter used in each() method
  private Node<V> current = head; //node used in each() method

	// create an empty hash table - use 16 as default size
 	public PHPArray() {
    	this(INIT_CAPACITY);
  	}

  // create a PHPArray of given capacity
  public PHPArray(int capacity) {
    M = capacity;
    @SuppressWarnings("unchecked")
    Node<V>[] temp = (Node<V>[]) new Node[M];
    entries = temp;
    head = tail = null;
    N = 0;
    resetCount = 0;
  }

  //put method if user inputs an int key instead of a string key
  public void put(int key, V val) {
    String newKey = String.valueOf(key);

  	put(newKey, val);
  }

	// insert the key-value pair into the symbol table
  public void put(String key, V val) {
    if (val == null) unset(key);

    // double table size if 50% full
    if (N >= M/2) resize(2*M);

    // linear probing
    int i;
    for (i = hash(key); entries[i] != null; i = (i + 1) % M) {
     		// update the value if key already exists
     		if (entries[i].key.equals(key)) {
       		entries[i].value = val; return;
     		}
    }
  	// found an empty entry
    entries[i] = new Node<V>(key, val);
    //insert the node into the linked list
    if(head == null) {
     	head = tail = entries[i];
    }
    else {
      tail.next = entries[i];
      entries[i].prev = tail;
      tail = tail.next;
    }

    N++;
  }  	

  //get method if input is an int key instead of a string key
  public V get(int key) {
    String newKey = String.valueOf(key);

    return get(newKey);
  }

  public V get(String key) {
    for (int i = hash(key); entries[i] != null; i = (i + 1) % M)
      if (entries[i].key.equals(key))
        return entries[i].value;
    return null;
  }

  public Iterator<V> iterator() {
    return new MyIterator();
  }

  public class MyIterator implements Iterator<V> {
    private Node<V> current;

    public MyIterator() {
      current = head;
    }

    public boolean hasNext() {
      return current != null;
    }

    public V next() {
      V result = current.value;
      current = current.next;
      return result;
    }
  }

  public Pair<V> each() {
    Pair<V> currentPair;  //create a new Pair

    if(resetCount == 0) {  //reset the iterator if resetCount = 0
      reset(); 
    }

    if(current != null) {
      String key = current.key;
      V val = current.value;
      currentPair = new Pair<V>(key,val);    //create new pair with key and value from current node
      current = current.next;                //iterate to next node
      resetCount++;
      return currentPair;
    }

    return null;
  }

  public void reset() {
   current = head;       //sets current node back to the head 
   resetCount = 0;     
  }

  public ArrayList<String> keys() {
    ArrayList<String> keys = new ArrayList<String>();

    Node curr = head;

    while(curr != null) {
      keys.add(curr.key);       //moves through linked list and adds keys to the arrayList
      curr = curr.next;
    }

    return keys;
  }


  public ArrayList<V> values() {
    ArrayList<V> vals = new ArrayList<V>();

    Node<V> curr = head;

    while(curr != null) {
      vals.add(curr.value);      //moves through linked list and adds values to the arrayList
      curr = curr.next;
    }

    return vals;
  }

  public int length() {
    return N;
  }

  public void unset(int key) {
    String newKey = String.valueOf(key);  //if unset is called with an int, call unset with a string value

    unset(newKey);
  }

  // delete the key (and associated value) from the symbol table
  public void unset(String key) {
    if (get(key) == null) return;

    // find position i of key
    int i = hash(key);
    while (!key.equals(entries[i].key)) {
      i = (i + 1) % M;
    }

    // delete node from hash table
    Node<V> toDelete = entries[i];
    entries[i] = null;
    //delete the node from the linked list in O(1)
    if(toDelete == head && toDelete == tail) {
      head = tail = null;
    }
    else if(toDelete == head) {
      toDelete.next.prev = null;
      head = head.next;
    }
    else if(toDelete == tail) {
      toDelete.prev.next = null;
      tail = tail.prev;
    }
    else {
      toDelete.prev.next = toDelete.next;
      toDelete.next.prev = toDelete.prev;
    }


    // rehash all keys in same cluster
    i = (i + 1) % M;
    while (entries[i] != null) {
      // delete and reinsert
      Node<V> nodeToRehash = entries[i];
      entries[i] = null;
      rehash(nodeToRehash);
      i = (i + 1) % M;
    }

    N--;

    // halves size of array if it's 12.5% full or less
    if (N > 0 && N <= M/8) resize(M/2);
  }

  public void showTable(){
    String key;
    V val;

    System.out.println("\tRaw Hash Table Contents: ");  

    for(int i = 0; i < entries.length; i++) {
      if(entries[i] == null) {
        System.out.println(i + ": null");  //if entry at this index is null
      }
      else {
        System.out.println(i + ": Key: " + entries[i].key +  " Value: " + (V)entries[i].value);
      }
    }

  }

  @SuppressWarnings("unchecked")
  public void sort() {
    ArrayList<V> values = new ArrayList<V>();  //arraylist to hold the values
    ArrayList<Comparable> values1 = new ArrayList<Comparable>();  //arraylist to hold the values that are casted to Comparable - this allows us to use collections.sort() on the values

    if(!(head.value instanceof Comparable)) {
      throw new ClassCastException("PHPArray values are not comparable -- cannot be sorted");  //if the value type does not implement comparable throw an exception
    }

    for(int i = 0; i < entries.length; i++) {
      if(entries[i] != null) {
        values.add(entries[i].value);   //add non null values to arraylist
      }
    }

    for(int i = 0; i < values.size(); i++){
      values1.add((Comparable) values.get(i)); //add values to comparable arraylist - cast every value to comparable
    }

    Collections.sort(values1); //sort the values

    for(int i = 0; i < values1.size(); i++) {
      values.set(i, (V) values1.get(i));   //set the values in the original arraylist to the sorted values - cast back to type V
    } 

    Node<V>[] temp = (Node<V>[]) new Node[M];
    entries = temp;
    head = tail = null;                     //reset symbol table
    N = 0;
    resetCount = 0;

    for(int i = 0; i < values.size(); i ++) {
      put(i, values.get(i));              //add values in sorted order
    }

  }

  @SuppressWarnings("unchecked")
  public void asort() {
    ArrayList<Pair<V>> pairs = new ArrayList<Pair<V>> (); //arraylist to hold Pairs - Pair class implements comparable so collections.sort can be called on it

    if(!(head.value instanceof Comparable)) {
      throw new ClassCastException("PHPArray values are not comparable -- cannot be sorted");  //if the value type does not implement comparable throw an exception
    }

    for(int i = 0; i < entries.length; i++) {
      if(entries[i] != null) {                       
        pairs.add(new Pair<V>(entries[i].key, entries[i].value));   //add non null values to arraylist
      }
    }

    Collections.sort(pairs);   //sort the values

    Node<V>[] temp = (Node<V>[]) new Node[M];
    entries = temp;
    head = tail = null;                   //reset symbol table
    N = 0;
    resetCount = 0;

    for(int i = 0; i < pairs.size(); i ++) {
      put(pairs.get(i).key, pairs.get(i).value);  //add new pairs in sorted order
    }

  }

  public PHPArray<String> array_flip() {

    if(!(head.value instanceof String)) {
      throw new ClassCastException("Array cannot be flipped - value of original array is not string"); //if the value is not of type string - throw exception 
    }

    PHPArray<String> flippedArray = new PHPArray<String>(M); //create a new PHPArray of the size of this PHPArray

    Node curr = head; 

    while(curr != null) {
      flippedArray.put((String) curr.value, curr.key); //iterate through current PHPArray and add the (swapped) keys and values 
      curr = curr.next;
    }

    return flippedArray;
  }

  // resize the hash table to the given capacity by re-hashing all of the keys
  private void resize(int capacity) {
    System.out.println("\t\tSize: " + N + " -- resizing array from " + M + " to " + capacity);

    PHPArray<V> temp = new PHPArray<V>(capacity);

    //rehash the entries in the order of insertion
    Node<V> current = head;
    while(current != null){
        temp.put(current.key, current.value);
        current = current.next;
    }
    entries = temp.entries;
    head    = temp.head;
    tail    = temp.tail;
    M       = temp.M;
  }


  // rehash a node while keeping it in place in the linked list
  private void rehash(Node<V> node){
    System.out.println("\t\tKey " + node.key + " rehashed...\n");

    int i;
    for(i = hash(node.key); entries[i] != null; i = (i + 1) % M ){

    }
    entries[i] = node;
  }


    // hash function for keys - returns value between 0 and M-1
  private int hash(String key) {
    return (key.hashCode() & 0x7fffffff) % M;
  }


  	//An inner class to store nodes of a doubly-linked list
    //Each node contains a (key, value) pair
  private class Node<V> {
    private String key;
    private V value;
    private Node<V> next;
    private Node<V> prev;

    Node(String key, V value){
      this(key, value, null, null);
    }

    Node(String key, V value, Node<V> next, Node<V> prev){
      this.key = key;
      this.value = value;
      this.next = next;
      this.prev = prev;
    }
  }
  	// A public static inner class which allows it to be nested within PHPArray but it can still be publicly accessed
  @SuppressWarnings("unchecked")
	public static class Pair<V> implements Comparable<Pair<V>> {
		public String key;
		public V value;

		public Pair() {
			this.key = null;
			this.value = null;
		}

		public Pair(String key, V value) {
			this.key = key;
			this.value = value;
		}

    public int compareTo(Pair<V> pair) {
      return ((Comparable)(this.value)).compareTo((Comparable) pair.value); //compareTo method so Pairs can be compared to each other
    }
	}		
}
