import java.io.*;
import java.util.*;

public class CrosswordB
{
	private DictInterface D;
	private char [][] theBoard;
	private StringBuilder [] colStr;
	private StringBuilder [] rowStr;
	private String [] highestScoreSolution;
	private int highestScore;
	private int solutions;
	private int size;

	//command line arguments
	private static String dictType;  //either DLB to use DLB.java or any other character to use MyDictionary.java
	private static String dictName;  //dict8.txt
	private static String testFile;  //example- test3a.txt
	//COMMAND LINE EXAMPLE: java CrosswordB DLB dict8.txt test3a.txt
	
	public static void main(String [] args) throws IOException
	{
		dictType = args[0]; 
		dictName = args[1];
		testFile = args[2];
		new CrosswordB();
	}

	public CrosswordB() throws IOException{
		//Read the dictionary
		Scanner fileScan = new Scanner(new FileInputStream(dictName));
		String st;

		if(dictType.equals("DLB")){ 
			D = new DLB();	
		}
		else {
			D = new MyDictionary();
		}

		while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.add(st);
		}
		fileScan.close();

		//Read the test file
		Scanner fReader = new Scanner(new FileInputStream(testFile));

		size = fReader.nextInt();

		theBoard = new char[size][size];

		String nothing = fReader.nextLine(); //first line returns an empty string - ""

		for (int i = 0; i < size; i++)
		{
			String rowString = fReader.nextLine();
			for (int j = 0; j < rowString.length(); j++)
			{	
				theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
			}
		}
		fReader.close();

		// Show user the board
		System.out.println("EMPTY BOARD: ");
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				System.out.print(theBoard[i][j] + " ");
			}
			System.out.println();
		}

		//initialize stringbuilder arrays
		colStr = new StringBuilder[size];
		rowStr = new StringBuilder[size];

		for(int i = 0; i < size; i++) {
			colStr[i] = new StringBuilder("");
			rowStr[i] = new StringBuilder("");
		}	

		//initialize highestScore and solutions
		highestScore = 0;
		solutions = 0;
		highestScoreSolution = new String [size]; 

		Solve(0, 0);

		if(highestScore == 0){
			System.out.println("\nNO SOLUTION FOUND");
		}
		else{
			System.out.println("\nSolutions found: " + solutions);
			System.out.println("\nHighest Score: " + highestScore);

			System.out.println("\nHIGHEST SCORE BOARD: ");
			for(int i = 0; i < size; i++) {
				System.out.println(highestScoreSolution[i].toString());   //print the board
			}
		}
	}

	private void Solve(int row, int col) {
		if(theBoard[row][col] != '-') { //if the square is a + or letter
			for(char c = 'a'; c <= 'z'; c++) { //loops through every character in the alphabet
				if(isValid(row, col, c)){  //if the character is valid
					rowStr[row].append(c); 
					colStr[col].append(c);	//add character to rowStr and colStr				

					if(col < theBoard.length - 1) {    //if not at an end column
						Solve(row, col + 1);           //move right to the next square 
					}
					if((col == theBoard.length - 1) && (row < theBoard.length - 1)){  //if at end of column
						Solve(row + 1, 0);			   //move down to the next row and start at the leftmost character 
					}
					if((col == theBoard.length - 1) && (row == theBoard.length - 1)){ //if the board is solved
						if(dictType.equals("DLB")) {  
							if(Score() > highestScore) { //if the current solution's score is higher than the current highest score
								highestScore = Score();  //set new high score
								for(int i = 0; i < size; i++) {
									highestScoreSolution[i] = rowStr[i].toString();  //set new highest score board 
								}
							}

							solutions++;  

							rowStr[row].deleteCharAt(rowStr[row].length() - 1);  //delete last character/backtrack
							colStr[col].deleteCharAt(colStr[col].length() - 1);
						}
						else { //if using MyDictionary
							System.out.println("\nSOLVED BOARD: "); 
							for(int i = 0; i < size; i++) {
								System.out.println(rowStr[i].toString());   //print the board
							}

							System.out.println("Score: " + Score());     //print the score 
							System.exit(0); 
						}
					}

				}						
			}

			if((row == 0 && col == 0) && (theBoard[row][col] >= 'a' && theBoard[row][col] <= 'z'))
			{
				//DO NOTHING  - this stops a null pointer exception that occured when the first letter was a constant letter and it tried deleting an empty square
			}
			else {
				if(rowStr[row].length() != 0) {   //if the current row contains any character
					rowStr[row].deleteCharAt(rowStr[row].length() - 1); //delete last character added to rowStr
				}
				else {                            //if the current row is empty, remove the last character from the previous row
					if(row - 1 >= 0) { //checks if row is in bounds 
						rowStr[row - 1].deleteCharAt(rowStr[row - 1].length() - 1); 
					}
				}
					
				if(col == 0) {     
					if(colStr[size - 1].length() - 1 >= 0){  //checks if col is in bounds             
						colStr[size - 1].deleteCharAt(colStr[size - 1].length() - 1); //if current col is 0 then wrap around and remove the character from the last col
					}
				}
				else{
					colStr[col - 1].deleteCharAt(colStr[col - 1].length() - 1);  //remove character from previous colStr
				}
			}
		}
		else if(theBoard[row][col] == '-') { //character is a minus
			rowStr[row].append('-');
			colStr[col].append('-');	//add minus to rowStr and colStr

			if(col < theBoard.length - 1) {    //if not at an end column
				Solve(row, col + 1);           //move right to the next square 
			}
			if((col == theBoard.length - 1) && (row < theBoard.length - 1)){  //if at end of column
				Solve(row + 1, 0);			   //move down to the next row and start at the leftmost character 
			}
			if((col == theBoard.length - 1) && (row == theBoard.length - 1)){ //if the board is solved
				if(dictType.equals("DLB")) {
					if(Score() > highestScore) { //if the current solution's score is higher than the current highest score
						highestScore = Score();  //set new high score
						for(int i = 0; i < size; i++) {
							highestScoreSolution[i] = rowStr[i].toString();   //set new highest score board 
						}
					}

					solutions++;

					rowStr[row].deleteCharAt(rowStr[row].length() - 1);    //delete last character/backtrack
					colStr[col].deleteCharAt(colStr[col].length() - 1);
				}
				else {
					System.out.println("\nSOLVED BOARD: ");
					for(int i = 0; i < size; i++) {
						System.out.println(rowStr[i].toString());   //print the board
					}

					System.out.println("Score: " + Score());     //print the score 
					System.exit(0); 	}
			}

			if((row == 0 && col == 0) && (theBoard[row][col] >= 'a' && theBoard[row][col] <= 'z'))
			{
				//DO NOTHING  - this stops a null pointer exception that occured when the first letter was a constant letter and it tried deleting an empty square
			}
			else {
				if(rowStr[row].length() != 0) {   //if the current row contains any character
					rowStr[row].deleteCharAt(rowStr[row].length() - 1);
				}
				else {                            //if the current row is empty, remove the last character from the previous row
					rowStr[row - 1].deleteCharAt(rowStr[row - 1].length() - 1);
				}
					
				if(col == 0) {                    
					colStr[size - 1].deleteCharAt(colStr[size - 1].length() - 1); //if current col is 0 then wrap around and remove the character from the last col
				}
				else{
					colStr[col - 1].deleteCharAt(colStr[col - 1].length() - 1);  //remove character from previous colStr
				}
			}

		}
		else{
			//System.out.println("What?");  program should never get to else statement, as every square should be a +, -. or letter 
		}
	}

	private boolean isValid(int i, int j, char c) {	

		int rowStart;
		int colStart;

		if(rowStr[i].toString().contains("-")) {   //if the current row contains a -
			rowStart = findMinus(rowStr[i], j);    //find start index 
		}
		else {
			rowStart = 0;						   //if no -, then the starting position will be 0
		}

		if(colStr[j].toString().contains("-")) {   //if the current col contains a -
			colStart = findMinus(colStr[j], i);    //find start index 
		}
		else {
			colStart = 0;						   //if no -, then the starting position will be 0
		}

		if(theBoard[i][j] >= 'a' && theBoard[i][j] <= 'z') { //if the current position is a constant letter
			if(theBoard[i][j] != c) {   //if the character is not equal to the constant letter the character is not valid
				return false;
			}
		}

		if(j == theBoard.length - 1 || theBoard[i][j + 1] == '-') {  //If j is an end index, then rowStr[i] + the letter must be a valid word in the dictionary 
			int res = D.searchPrefix(rowStr[i].append(c), rowStart, rowStr[i].length() - 1);  //or if the next character is a - then the rowstr[i] + letter must be a word
			rowStr[i].deleteCharAt(rowStr[i].length() - 1);
			if(res != 2 && res != 3) {  //if not a word
				return false;
			}
		}

		if(j < theBoard.length - 1 && theBoard[i][j + 1] != '-') {  //If j is not an end index and the next char is not a -, then rowStr[i] + the letter a must be a valid prefix in the dictionary
			int res = D.searchPrefix(rowStr[i].append(c), rowStart, rowStr[i].length() - 1);
			rowStr[i].deleteCharAt(rowStr[i].length() - 1);
			if(res != 1 && res !=3) {  //if not a prefix  
				return false;
			}
		}

		if(i == theBoard.length - 1 || theBoard[i + 1][j] == '-') {   //If i is an end index, then colStr[j] + the letter must be a valid word in the dictionary 
			int res = D.searchPrefix(colStr[j].append(c), colStart, colStr[j].length() - 1);  //also if the next character is a - then the colstr[j] + letter must be a word
			colStr[j].deleteCharAt(colStr[j].length() - 1);
			if(res != 2 && res != 3) { //if not a word
				return false;
			}
		}

		if(i < theBoard.length - 1 && theBoard[i + 1][j] != '-'){  		//If i is not an end index and the next char is not a -, then colStr[j] + the letter must be a valid prefix in the dictionary 
			int res = D.searchPrefix(colStr[j].append(c), colStart, colStr[j].length() - 1);
			colStr[j].deleteCharAt(colStr[j].length() - 1);
			if(res != 1 && res != 3) {  //if not a prefix
				return false;
			}  

		}

		return true;
	}

	private int findMinus(StringBuilder curr, int index) {  //finds starting index that will be used in searchPrefix
		int start = 0;

		for(int i = 0; i < index; i++){   //index will be the current col if checking rowStr or current row if checking colStr
			if(curr.toString().charAt(i) == '-'){ //loops through rowStr, setting the start index to the rightmost - before the current character
				start = i + 1; //adds 1 to start index to check starting the character after the - 
			}
		}

		return start;
	}

	private int Score() {  //computes score of solved crossword puzzle
		int score = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				score += letterPoints(rowStr[i].charAt(j));
			}
		}
		return score;
	}

	private int letterPoints(char c) { //calculates how many points a  letter is worth
		int point = 0;
		switch(c) {
			case 'a':
				point = 1;
				break;
			case 'b':
				point = 3;
				break;
			case 'c':
				point = 3;
				break;
			case 'd':
				point = 2;
				break;
			case 'e':
				point = 1;
				break;
			case 'f':
				point = 4;
				break;
			case 'g':
				point = 2;
				break;
			case 'h':
				point = 4;
				break;
			case 'i':
				point = 1;
				break;
			case 'j':
				point = 8;
				break;
			case 'k':
				point = 5;
				break;
			case 'l':
				point = 1;
				break;
			case 'm':
				point = 3;
				break;
			case 'n':
				point = 1;
				break;
			case 'o':
				point = 1;
				break;
			case 'p':
				point = 3;
				break;
			case 'q':
				point = 10;
				break;
			case 'r':
				point = 1;
				break;
			case 's':
				point = 1;
				break;
			case 't':
				point = 1;
				break;
			case 'u':
				point = 1;
				break;
			case 'v':
				point = 4;
				break;
			case 'w':
				point = 4;
				break;
			case 'x':
				point = 8;
				break;
			case 'y':
				point = 4;
				break;
			case 'z':
				point = 10;
				break;
			case '-':
				point = 0;
				break;
			default:
				point = 0;
		}
		return point;
	}
}