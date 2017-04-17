package search;

public class ContentFile {
	private double score = 0;
	private String content = null;
	private int count = 0;
	private int wordCount = 0;
	
	public void setScore(double s){
		score = s;	
	}
	public void setContent(String c){
		content = c;
	}
	public void setCount(int c){
		count = c;
	}
	public void setWordCount(int w){
		wordCount = w;
	}
	public double getScore(){
		return score;
	}
	public String getContent(){
		return content;
	}
	public int getCount(){
		return count;
	}
	public int getWordCount(){
		return wordCount;
	}
}
