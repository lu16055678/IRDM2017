package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
/**
 * Cited From Stephen, 2014, which is available at http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerLeg {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a
	// normal web browser.

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
	// private List<String> context = new LinkedList<String>();
	private static final String DIR = "page";
	
	/**
	 * This performs all the work. It makes an HTTP request, checks the
	 * response, and then gathers up all the links on the page. Perform a
	 * searchForWord after the successful crawl
	 * 
	 * @param url
	 *            - The URL to visit
	 * @return whether or not the crawl was successful
	 */
	public boolean crawl(String url) {
		try {
			if (url.indexOf("www.cs.ucl.ac.uk") >= 0 && url != null) {
				Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
				Document htmlDocument = connection.get();
				this.htmlDocument = htmlDocument;
				/**
				 *   200 is the HTTP OK status code indicating that everything is good.
				 */
				if (connection.response().statusCode() == 200)
				{
					//System.out.println("\n**Visiting** Received web page at " + url);
					
					Elements title = htmlDocument.getElementsByTag("title");
					String name = title.text();		
					
					File file = new File(DIR, name);
					
					if(!file.exists()){						
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(htmlDocument.html().getBytes());
						fos.close();
						System.out.println("Saved");
					}				
				}
				if (connection.response().contentType() == null) {
					System.out.println("**Failure** Retrieved Nothing");
					return false;
				}
				if (!connection.response().contentType().contains("text/html")) {
					System.out.println("**Failure** Retrieved something other than HTML");
					return false;
				}

				Elements linksOnPage = htmlDocument.select("a[href]");

				//System.out.println("Found (" + linksOnPage.size() + ") links");
				for (Element link : linksOnPage) {
					this.links.add(link.absUrl("href"));
					//System.out.println(links.get(0));
					
				}
				Elements title = htmlDocument.getElementsByTag("title");
				String name = title.text();		
				//HtmlEntity entity = new HtmlEntity();
				File file = new File("link", name);
				
				if(!file.exists()){						
					PrintWriter writer = new PrintWriter(file);
					double numberOfLinks = (double)getLinks().size();
					writer.print(numberOfLinks);
					writer.close();
					
				}
				
				// context.add(this.htmlDocument.body().text());
				return true;
			} else {
				return false;
			}
		} catch (IOException ioe) {
			// We were not successful in our HTTP request
			return false;
		}

	}

	/**
	 * Performs a search on the body of on the HTML document that is retrieved.
	 * This method should only be called after a successful crawl.
	 * 
	 * @param searchWord
	 *            - The word or string to look for
	 * @return whether or not the word was found
	 */
	/**
	 * public boolean searchForWord(String searchWord) { // Defensive coding.
	 * This method should only be used after a successful // crawl. if
	 * (this.htmlDocument == null) { System.out.println("ERROR! Call crawl()
	 * before performing analysis on the document"); return false; }
	 * System.out.println("Searching for the word " + searchWord + "...");
	 * String bodyText = this.htmlDocument.body().text(); return
	 * bodyText.toLowerCase().contains(searchWord.toLowerCase()); }
	 **/

	public List<String> getLinks() {
		return this.links;
	}
	/**
	 * public List<String> getContext(){ return this.context; }
	 **/

}