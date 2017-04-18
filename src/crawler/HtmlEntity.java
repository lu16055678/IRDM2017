package crawler;


import java.util.LinkedList;
import java.util.List;

public class HtmlEntity {
	private String title = null;
	private String url = null;
	private List<String> links = new LinkedList<String>();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getLinks() {
		return links;
	}
	public void setLinks(List<String> links) {
		this.links = links;
	}
}
