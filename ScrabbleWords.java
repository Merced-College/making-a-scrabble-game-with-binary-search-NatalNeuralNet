package scrabbleGame;

public class ScrabbleWords {
	
	private String word;
	
	public ScrabbleWords(){
		
	};
	
	public ScrabbleWords(String word) {
	this.word = word;
	}
	public String getWord(){
		return word;
	}
	void setWord(String word) {
		this.word=word;
	}
	
	@Override
	public String toString() {
		return "Word:" + word; 
	}


}
