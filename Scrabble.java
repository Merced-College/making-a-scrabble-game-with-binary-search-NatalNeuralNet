/*Arnold Rocha 
 *CPSC 39
 *10/18/24
 */
package scrabbleGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;



/*
 * REQUIREMENTS
1. Create a Word object class and a ScrabbleGame class to read 
	in the Word objects into a sorted ArrayList of Words 
	- Made the readin() method to do this

2. Have your game then choose 4 random characters, and output these to the user.
	- Made the randomLetterStart() method for this
	= Although to make this more like Scrabble I increased this to 7 characters

3. Ask the user for a word made form those 4 letters.
	- Made the playerTurn() method
	- Instead of asking a user to make a word they are given action options
	- Users can pass make a word or exchange words
	
4. Use binary search to search for that word in the Words ArrayList to see if
	the word is valid. Output it if it is a valid word.
	- I made a binarySearch() method to preform binary search 
	- I made a biCheck() method which uses the binary search to check the records of word objects
	
	
*Improvments
1. Added exchange functionality so that a player can swap all or some of their tiles
2. I did slight error handling as we validate that the word guessed is from the tiles given
3. Introduced continuity, wrong or right a player will continue with their tiles
4. If a word is guessed correctly tiles used will be removed and replaced with new ones
	
*Future iterations
1. More error handling as wrong inputs can easily crash the program
2. Scoring functionality 
3. Turn functionality for multiplayer (Began implementing)
	
	*/

public class Scrabble {
	
	// List to store words read from file
	public static ArrayList<ScrabbleWords> records = new ArrayList<ScrabbleWords>();
	
	// List to store the random letters for the user
	public static ArrayList<Character> userLetters = new ArrayList<Character>();
	
	// Global Scanner for user input
	public static Scanner scnr = new Scanner(System.in);
	
	//main
	public static void main(String[] args) {
		
		boolean play = true;
		
		// Read the words from the file
		readIn();
		
		// Sort the list of words to prepare for binary search
		records.sort((a, b) -> a.getWord().compareToIgnoreCase(b.getWord()));
		
		// Generate 7 random letters to start
		randomLetterStart();
		
		
		// Game loop
		while(play) {
			 // Handle player actions (guess, pass, exchange
			playerTurn();
			
			// Ask the user if they want to continue
			System.out.println("cont? y/n");
			String input = scnr.next();
			char response = Character.toUpperCase(input.charAt(0));
			
			if(response=='Y') {
				
				// Display the user's available letters again
				System.out.println("Letters: " + userLetters);
				continue;
			}else {
				// End game if the user selects 'n'
				play = false;
			}
		}
	}//End main
		
	/*
	METHODS
	readIn: Reads in file and words as ScrabbleWords objects
	randomLetterStart: Begins game with 7 random tiles
	randomLetter: Generates single random tiles
	userWord: Takes in user guess
	validGuess: Validates if guess is in tiles
	biCheck: Checks for guess in records using binary search
	playerTurn: Player turn action Guess, Pass, Exchange
	binarySearch: Algorithm for binary search
	
	*/
	
	public static void readIn() {
		
		Scanner input= null;
		
		try {input = new Scanner (new File("CollinsScrabbleWords_2019.txt"));
			while(input.hasNext()) {
				// Read each word, trim whitespace, and add it as a ScrabbleWords object
				String record = input.nextLine().trim();
				records.add(new ScrabbleWords(record));
			}
			
			input.close();
			
		}catch (FileNotFoundException e) {
			//file not found
				System.out.println("file not found");
				e.printStackTrace();
			}
	}//End readIn
	
	
	public static void randomLetterStart() {
		Random random = new Random();
		userLetters.clear();
		
		// Generate 7 random letters and store them
		for(int i=0;i<8;i++) {
			char randomLetter = (char)('A' + random.nextInt(26));
			userLetters.add(randomLetter);
		}
		
		// Display the letters to the user
		System.out.println("Letters: " + userLetters);
	}//randomLetterStart
	
	public static String userWord(){
		
		System.out.print("User Word: ");
		String userWord = scnr.next().toUpperCase();
		
		return userWord;
	}//End userWord
	
	public static boolean validGuess(String userWord) {
		
		// Copy the available letters to check if the word can be formed
		ArrayList<Character> userWordList = new ArrayList<>(userLetters);
		ArrayList<Integer> replaceIndices = new ArrayList<>();
		
		// Check if the word can be made from the available letters
		for (char c : userWord.toCharArray()) {
			
			if(userWordList.contains(c)) {
				int index = userLetters.indexOf(c);
				replaceIndices.add(index); // Track indices for replacement
				userWordList.remove((Character) c);  // Remove letter once used
			}else {
				// Invalid guess if word cannot be made from available letters
				System.out.println("\t!!!\nUse the letters provided\n" + "\t" + userLetters +"\n\t!!!" );
				return false;
			}
		
		}
		
		// Check if the word exists in the valid Scrabble words
		if(biCheck(userWord)) {
			// If valid, replace the used letters with new random ones
			for(int index : replaceIndices) {
				char newLetter = randomLetter();
				userLetters.set(index, newLetter);
			}
		System.out.println(userWord +" found");
	
		return true;
		
		}else {
			
			System.out.println("Word " + userWord + " not a valid word");
			return false;
		}
	}//End validGuess
	
	public static boolean biCheck(String userWord) {
		
		String[] wordArray = records.stream().map(ScrabbleWords::getWord).toArray(String[]::new);
		
		return binarySearch(wordArray, userWord);
	}//end biCheck
	
	
	public static char randomLetter() {
		
		Random random = new Random();
		char randomLetter = (char)('A' + random.nextInt(26));
		
		return randomLetter;
	}//End randomLetter
	
	public static void playerTurn() {
		
		int turn = 0;
		
		char response;
	
		System.out.println("Player Actions: G - Guess, X - Exchange, P - Pass ");
		String input = scnr.next();
		response = Character.toUpperCase(input.charAt(0));
		
		// User's action "Guess"
		if (response == 'G') {
			
			String userWord = userWord();
			validGuess(userWord);
			turn++;

		//// User action "Pass"
		}else if(response == 'P') {
			
			turn++;
		//// User action "Exchange"
		}else if (response == 'X') {
			
			System.out.println("Exchange All(A) or Some(S)?");
			input = scnr.next();
			response = Character.toUpperCase(input.charAt(0));
			
			 // Exchange all tiles
			if (response == 'A') {
				
				// Reused randomLetterStart same functionality needed
				randomLetterStart();
				turn++;
				
			}else if (response == 'S') {
				 // Exchange specific tiles
				System.out.println("Which letters would you like to Exchange?");
				for(int i=0;i<userLetters.size();i++) {
					System.out.print(i+1+ ": "+userLetters.get(i)+"\t");
				}
				scnr.nextLine();  // Clear buffer
				input = scnr.nextLine();
				String[] posArray = input.split("[ ]+");
				
				// Replace selected tiles with new random letters
				for(String pos : posArray) {
						int index = Integer.parseInt(pos.trim())-1;
						char randomLetter = randomLetter();
						userLetters.set(index, randomLetter);
					
				}		
				System.out.print("New Letters: " + userLetters);
				turn++;
			}
		}
	}//End playerTurn
	
	public static boolean binarySearch(String [] array, String key) {
		
		int low =0;
		int high = array.length -1;
		
		// Perform binary search
		while(high>= low) {
			int mid = (low +high) / 2;
			int comparisonResult = key.compareToIgnoreCase(array[mid]);
			
			if(comparisonResult <0) {
				high = mid - 1;
			}else if(comparisonResult ==0) {
				return true;  // Word found
			}
			else {
				low = mid + 1;
			}
		}
		return false; // Word not found
	}//end binarySearch
}//end main
