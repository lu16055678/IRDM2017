package Crawler;

/**
 * Cited From Stephen, 2014, which is available at http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/
 */
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
	private static final int MAX_PAGES_TO_SEARCH = 10;
	private Set<String> pageVisited = new HashSet<String>();
	private List<String> pageToVisit = new LinkedList<String>();
	private List<String> pageFound = new LinkedList<String>();
	private static final Pattern FILTERS = Pattern.compile(".*(\\.(xml|pdf|css|js|bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private int pagecount = 0;

	public void search(String url) {
		System.out.println(this.pageVisited.size());
		Matcher match = FILTERS.matcher(url);
		if (!match.matches()) {
			while (this.pageVisited.size() < MAX_PAGES_TO_SEARCH) {
				String currentUrl;
				CrawlerLeg leg = new CrawlerLeg();
				pagecount++;
				System.out.println("Page Count: " + pagecount);
				boolean success = false;
				if (this.pageToVisit.isEmpty()) {
					currentUrl = url;
					this.pageVisited.add(url);
				} else {
					currentUrl = this.nextUrl();
				}
				boolean receive = leg.crawl(currentUrl);
				if (receive) {
					pageFound.add(0, currentUrl);
					// success = leg.searchForWord(searchWord);
					System.out.println();
				}
				/**
				 * if (success) { System.out.println(String.format("**Success**
				 * Word %s found at %s", searchWord, currentUrl));
				 * pageFound.add(0, currentUrl); // break; }
				 **/
				if (leg.getLinks().isEmpty()) {
					break;
				}
				this.pageToVisit.addAll(leg.getLinks());
			}
			System.out.println("\n**Done** Visited " + this.pageVisited.size() + " web page(s)");
		}
	}

	private String nextUrl() {
		String nextUrl;
		do {
			nextUrl = this.pageToVisit.remove(0);
		} while (this.pageVisited.contains(nextUrl));
		this.pageVisited.add(nextUrl);
		return nextUrl;
	}
}
