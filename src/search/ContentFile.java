package search;

public class ContentFile {
	private double score = 0;
	private String content = null;
	private int count = 0;
	private int wordCount = 0;
	private String path = null;
	private int rankScore = 0;
	private String fileName = null;
	private double pr = 0;
	
	public double getPr() {
		return pr;
	}
	public void setPr(double pr) {
		this.pr = pr;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
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
	public void setPath(String t){
		path = t;
	}
	public void setRankScore(int r){
		rankScore = r;
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
	public String getPath(){
		return path;
	}
	public int getRankScore(){
		return rankScore;
	}
}
