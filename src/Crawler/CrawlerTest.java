package Crawler;

/**
 * Cited From Stephen, 2014, which is available at
 * http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/
 */
public class CrawlerTest {

	/**
	 * This is our test. It creates a spider (which creates spider legs) and
	 * crawls the web.
	 * 
	 * @param args
	 *            - not used
	 */
	public static void main(String[] args) {
		Crawler spider = new Crawler();
		spider.search(
				"http://www.cs.ucl.ac.uk/home/");
	}

}
