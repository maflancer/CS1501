/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - r < input.txt   (compress with reset)
 *  Execution:    java LZWmod + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
    private static final int R = 256;        // number of input chars
    private static final int MINBITS = 9;
    private static final int MAXBITS = 16;
    private static int W = 9;                // codeword width
    private static int L = 512;              // number of codewords = 2^W
    private static char resetMode = 'n';     // default is no reset

    public static void compress() { 
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        BinaryStdOut.write((byte) resetMode); //writes either a 'r' or 'n' at the start of the compressed file

        //initialize the current string
        StringBuilder current = new StringBuilder();
        //read and append the first char
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) {

            codeword = st.get(current);
            //read and append the next char to current
            c = BinaryStdIn.readChar();
            current.append(c);

            if(!st.contains(current)){
              BinaryStdOut.write(codeword, W);

              if(code < L) {    // Add to symbol table if not full
                st.put(current, code++);
              }
              else if(code >= L){ //if codebook is full
                if(W < MAXBITS) { //if codebook can still be expanded
                    W++;
                    L = (int) Math.pow(2, W); //increment W and set L = 2^W
                    st.put(current, code++);
                }
                else if(W == MAXBITS && resetMode == 'r') { 
                    st = new TSTmod<Integer>();
                    for (int i = 0; i < R; i++)                         //reset dictionary
                        st.put(new StringBuilder("" + (char) i), i);

                    code = R + 1;  // R is codeword for EOF
                    W = MINBITS; //set W to 9 
                    L = (int) Math.pow(2, W); //set L = 2^9
                }
              }
              //reset current
              current = new StringBuilder();
              current.append(c);
            }
        }

        //Write the codeword of whatever remains
        //in current
        BinaryStdOut.write(st.get(current), W);

        BinaryStdOut.write(R, W); //Write EOF
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[(int) Math.pow(2,16)]; //2^MAXBITS
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        resetMode = BinaryStdIn.readChar(); //checks the first char (either 'r' or 'n') to determine resetMode 

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);

            if(i >= L){ //codeword value is outside the range of codebook
                if(W < MAXBITS){ //if the codebook can still be expanded
                    W++;
                    L = (int) Math.pow(2, W); 
                }
                else if(W == MAXBITS && resetMode == 'r') { 
                    st = new String[(int) Math.pow(2, 16)]; //2^MAXBITS

                    // initialize symbol table with all 1-character strings
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";                        // (unused) lookahead for EOF 

                    i = R; //set i to R instead of R + 1 because expand is one step behind compress

                    W = MINBITS;                 //set W to 9 and L = 2^9
                    L = (int) Math.pow(2, W);  
                }
            } 

            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if(args.length == 2){   //length will be 2 for compression: - for compression as well as a 'r' or 'n' for reset mode
            if      (args[1].equals("r")) resetMode = 'r';
            else if (args[1].equals("n")) resetMode = 'n';  //set reset mode
        }

        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
