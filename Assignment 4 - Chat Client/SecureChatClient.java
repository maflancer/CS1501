import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	  Socket connection;
    String cipherName;
    SymCipher cipher;
    BigInteger E;
    BigInteger N;

    public SecureChatClient ()
    {
      try {

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr = 
                InetAddress.getByName(serverName);

        connection = new Socket(addr, PORT);   // Connect to server with new
                                               // Socket
        myWriter = new ObjectOutputStream(connection.getOutputStream()); // creates an ObjectOutputStream on the socket (for writing) 
        myWriter.flush();                                                // and immediately calls the flush() method

        myReader = new ObjectInputStream(connection.getInputStream()); //creates on ObjectInputStream on the socket

        E = (BigInteger) myReader.readObject(); //receives the server's public key, E, as a BigInteger object
        System.out.println("Key E: " + E + "\n");
        N = (BigInteger) myReader.readObject(); //receives the server's public mod value, N, as a BigInteger object
        System.out.println("Key N: " + N + "\n");

        cipherName = (String) myReader.readObject(); //receives the server's preferred symmetric cipher (either "Sub" or "Add"), as a String object.

        if(cipherName.equals("Sub")) {
          cipher = new Substitute();
        }
        else if(cipherName.equals("Add")) {       // creates either a Substitute object or anAdd128 object, 
          cipher = new Add128();                  // storing the resulting object in a SymCipher variable
        }
        else {
          System.out.println("Invalid cipher name"); 
        }

        System.out.println("Type of encryption: " + cipherName + "\n"); //prints type of symmetric encryption

        byte [] symmetricKey = cipher.getKey();
        System.out.print("Symmetric key: ");
        printByteArray(symmetricKey);              //print symmetric key to conssole

        BigInteger key = new BigInteger(1, symmetricKey); //converts the result from cipher.getKey() into a BigInteger object

        myWriter.writeObject(key.modPow(E, N)); //RSA-encrypts the BigInteger version of the key using E and N, 
        myWriter.flush();                       //and sends the resulting BigInteger to the server

        this.setTitle(myName);      // Set title to identify chatter

        myWriter.writeObject(cipher.encode(myName)); // encrypts the user's name using the cipher and send it to the server
        myWriter.flush();

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

	      addWindowListener(
              new WindowAdapter()
              {
                  public void windowClosing(WindowEvent e)
                  { 
                    try { 
                      byte [] exitMsg = cipher.encode("CLIENT CLOSING");
                      myWriter.writeObject(exitMsg);
                      System.exit(0);
                    }
                    catch (Exception ex)
                    {
                      System.out.println("Problem closing client!");
                      System.exit(0);
                    }
                    finally {
                      System.exit(0);
                    }
                  } 
              }
          );

        setSize(500, 200);
        setVisible(true);

      }
      catch (Exception e)
      {
        System.out.println("Problem starting client!");
      }
    }

    public void run()
    {
        while (true)
        {
             try {  
                byte[] msg = (byte[]) myReader.readObject(); //receives message from server

                System.out.println("-------------- [ DECRYPTING MESSAGE ] --------------\n");

                System.out.println("Array of bytes received: ");
                printByteArray(msg);                         //and output to the console

                String currMsg = cipher.decode(msg);         //decodes the message

                System.out.println("Decrypted array of bytes: ");
                printByteArray(currMsg.getBytes());                //outputs decrypted array of bytes
			         
                System.out.println("Decrypted message: " + currMsg + "\n");

                outputArea.append(currMsg+"\n");       
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
      try {
        System.out.println("-------------- [ ENCRYPTING MESSAGE ] --------------\n");

        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");

        String msg = myName + ":" + currMsg;    
        System.out.println("Original string message: " + msg + "\n");   //output to console the original string message

        System.out.print("Original message (array of bytes): ");
        printByteArray(msg.getBytes());                           //output to console the corresponding array of bytes

        byte [] encodedMsg = cipher.encode(msg);             //encodes message and outputs to console the encrypted array of bytes
        System.out.print("The encrypted array of bytes: ");
        printByteArray(encodedMsg);

        myWriter.writeObject(encodedMsg);
        myWriter.flush();
      }
      catch (Exception ex)
      {
        System.out.println("Error sending message to server.");
      }
    }

    public void printByteArray(byte [] byteArray) {
      for(int i = 0; i < byteArray.length; i++) {
        System.out.print(byteArray[i] + " ");
      }
      System.out.println("\n");
    }                                               

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
