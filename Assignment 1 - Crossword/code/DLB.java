public class DLB implements DictInterface
{
    private Node root;
    private static final char SENTINEL = '^';

    //Is the key in the symbol table?
    public boolean contains(String key) {
        return get(key) != null;
    }

    //Returns the value associated with the given key
    private Node get(String key) {
        if(key == null) throw new IllegalArgumentException("calls getPrefix() with a null key");
        Node result = get(root, key, 0);
        return result;
    }

    private Node get(Node x, String key, int pos) {
        Node result = null;
        if(x != null){
          if(x.letter == key.charAt(pos)){
            if(pos == key.length()-1){
              result = x;
            } else {
              result = get(x.child, key, pos + 1);
            }
          } else {
            result = get(x.sibling, key, pos);
          }
        }
        return result;
    }

    //Inserts the specified key-value pair into the symbol table
    public boolean add(String key) {
        if (key == null) return false;
        key = key + SENTINEL; //add ^ to signify word 
        root = add(root, key, 0);
        return true;
    }

    private Node add(Node x, String key, int pos) {
        Node result = x;
        if (x == null){
            result = new Node();
            result.letter = key.charAt(pos);
            if(pos < key.length()-1){
              result.child = add(result.child, key, pos + 1);
            } 
        } else if(x.letter == key.charAt(pos)) {
            if(pos < key.length()-1){
             result.child = add(result.child, key, pos + 1);
            } 
        } else {
            result.sibling = add(result.sibling, key, pos);
        }
        return result;
    }

    public int searchPrefix(StringBuilder s) {
    	return searchPrefix(s, 0, s.length() - 1);
    }

	public int searchPrefix(StringBuilder s, int start, int end) {
		boolean isWord = false;
		boolean isPrefix = false;

        String word = s.toString().substring(start, end + 1);

		Node temp = get(word); //gets node in DLB where node.letter is the last character of s before SENTINEL

		if(temp == null) { 
			return 0;  //not word or prefix
		}

		if(temp.child.letter == SENTINEL) {  //if the child is a ^ the string is a word
			isWord = true; //is a word

			if(temp.child.sibling != null) { //if the child node has a sibling, then the word is a prefix
				isPrefix = true;  //is a prefix
			}
		} 
		else {
			isPrefix = true;  //if it is not a word but a prefix
		}

		if(isWord == true && isPrefix == true) { //return 3 if word and prefix
			return 3;
		}
		else if(isWord == true && isPrefix == false) { //return 2 if word but not prefix
			return 2;
		}
		else if(isPrefix == true && isWord == false) { //return 1 if prefix but not word
			return 1;
		}
		else{
			return 0;
		}
	
	}

	private class Node {
        private char letter;
        private Node sibling;
        private Node child;
    }
}