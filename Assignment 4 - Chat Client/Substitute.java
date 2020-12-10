import java.util.ArrayList;
import java.util.Collections;

class Substitute implements SymCipher 
{
	private final byte[] key;

	Substitute() { //create a random 256-byte array which is a permutation of the 256 possible byte values and will serve as a map from bytes to their substitution values
		ArrayList<Integer> byteList = new ArrayList<Integer> ();
		key = new byte[256];

		for(int i = 0; i < 256; i++) {
			byteList.add(i);
		}

		Collections.shuffle(byteList);

		for (int i = 0; i < 256; i++) {
			key[i] = byteList.get(i).byteValue();
		}
	}

	Substitute(byte[] givenKey ) { // use the byte array parameter as its key
		key = new byte[256];

		System.arraycopy(givenKey, 0, key, 0, 256);
	}

	public byte [] getKey() {
		return key;
	}

	public byte[] encode(String s) {
		byte[] byteArray = s.getBytes(); //convert the String parameter to an array of bytes
		byte[] encodedArray = new byte[byteArray.length];

		for(int i = 0; i < byteArray.length; i++) { //iterate through all of the bytes, substituting the appropriate bytes from the key
			encodedArray[i] = key[Byte.toUnsignedInt(byteArray[i])];
		}

		return encodedArray;
	}

	public String decode(byte [] bytes) {
		byte [] byteArray = new byte[bytes.length];

		for(int i = 0; i < bytes.length; i++) {
			boolean check = false;

			for(int j = 0; j < key.length; j++) {   // reverse the substitution (using the decoded byte array)
				if(!check) {
					if(bytes[i] == key[j]) {
						check = true;
						byteArray[i] = (byte) j;           
					}
				}
			}
		}

		return new String(byteArray); // convert the resulting bytes back to a String
	}

	//public static void main(String [] args) {
	//	String word = "Hello Matt!";

	//	Substitute test = new Substitute();     //TEST

	//	System.out.println(test.decode(test.encode(word)));
	//}
}