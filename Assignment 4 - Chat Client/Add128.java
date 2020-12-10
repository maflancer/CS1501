import java.util.Random;

class Add128 implements SymCipher 
{
	private byte[] key;
 
	Add128() { // create a random 128-byte additive key and store it in an array of bytes
		Random randomGenerator = new Random();  
		key = new byte[128];
		randomGenerator.nextBytes(key);
	}

	Add128(byte[] givenKey) { // use the byte array parameter as its key
		key = new byte[128];
		
		System.arraycopy(givenKey, 0, key, 0, 128);
	}

	public byte [] getKey() {
		return key;
	}

	public byte[] encode(String s) {
		byte[] byteArray = s.getBytes(); // convert the String parameter to an array of bytes

		if(byteArray.length < key.length) { //if message is shorter than the key, ignore the remaining bytes in the key
			for(int i = 0; i < byteArray.length; i++) {
				byteArray[i] += key[i];
			}
		} 
		else { //if message is longer than the key, cycle through the key as many times as necessary
			for(int i = 0; i < byteArray.length; i++) {
				byteArray[i] += key[i % key.length];
			}
		}

		return byteArray;

	}

	public String decode(byte [] bytes) {
		if(bytes.length < key.length) { //if message is shorter than the key, ignore the remaining bytes in the key
			for(int i = 0; i < bytes.length; i++) {
				bytes[i] -= key[i];
			}
		}
		else { //if message is longer than the key, cycle through the key as many times as necessary
			for(int i = 0; i < bytes.length; i++) {
				bytes[i] -= key[i % key.length];
			}
		}

		return new String(bytes);
	}

	//public static void main(String [] args) {
	//	String word = "Hello Matt!";

	//	Add128 test = new Add128();           TEST

	//	System.out.println(test.decode(test.encode(word)));
	//}

}