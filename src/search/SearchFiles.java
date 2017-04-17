package search;

/**
 * This code was based on the source code on tutorials of Apache Lucene website.
 * It was modified by @author mingminlu.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import Rank.TFIDFCalculator;

public class SearchFiles {
	private static final String INDEXPATH = "index";
	private static final String DOCPATH = "pageText";
	private String field = "contents";
	private int hitsPerpage = 10;
	private static List<ContentFile> docList = new LinkedList<ContentFile>();
	private static int filecount = 0;
	private static int allfile = 0;
	public void Search(String queryString) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEXPATH)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		BufferedReader in = null;
		in = Files.newBufferedReader(Paths.get(DOCPATH), StandardCharsets.UTF_8);
		QueryParser parser = new QueryParser(field, analyzer);
		queryString = queryString.trim();
		Query query = parser.parse(queryString);
		while (true) {
			System.out.println("Searching for: " + query.toString(field));
			doPagingSearch(in, searcher, query, hitsPerpage, DOCPATH == null && queryString == null);
			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}

	public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage,
			boolean interactive) throws IOException {
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		filecount = numTotalHits;
		System.out.println(numTotalHits + " total matching documents");
		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);
		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length + " of " + numTotalHits
						+ " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {

				Document doc = searcher.doc(hits[i].doc);

				String path = doc.get("path");
				if (path != null) {

					File file = new File(path);
					allfile = new File(DOCPATH).list().length;
					String fileContent = txt2String(file).toString();
					// FileOutputStream fos = new FileOutputStream("hh.txt");
					// fos.write(fileContent.getBytes());
					ContentFile content = new ContentFile();
					content.setContent(fileContent);
					docList.add(i, content);

					System.out.println((i + 1) + ". " + path);
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i + 1) + ". " + "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit)
					break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}

	public static StringBuilder txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * Implementation of TF-IDF and sort them;
	 * @param keyword
	 */
	private static void Tfidf(String keyword) {
		keyword = keyword.toLowerCase();
		int wholeCount = 0;
		//System.out.println(keyword.toLowerCase());
		for (int i = 0; i < docList.size(); i++) {
			int articlecount = 0;
			int wordCount = 0;
			String content = docList.get(i).getContent().toLowerCase();
			
			Pattern pattern = Pattern.compile(keyword);
			Pattern word = Pattern.compile("[//s//p{Zs}]");
			Matcher found = pattern.matcher(content);
			Matcher match = word.matcher(content);
			while(match.find()){
				wordCount++;
			}
			docList.get(i).setWordCount(wordCount);
			System.out.println("the document has "+docList.get(i).getWordCount()+" words");
			// Find all matches
			while (found.find()) {
				// Get the matching string
				articlecount++;
				wholeCount++;
				//String digitNumList = found.group();				
			}
			docList.get(i).setCount(articlecount);
			System.out.println("for document "+(i+1)+"it has keyword: "+articlecount);			
		}
		for (int i = 0; i < docList.size(); i++){
			double tf = docList.get(i).getCount()/docList.get(i).getWordCount();
			double idf = Math.log10(docList.size()/allfile);
			double tfidf = tf*idf;
			docList.get(i).setScore(tfidf);
		}
		//在下面这个for循环对docList进行排序，docList.get(i)就是搜索到的文件。
		//用docList.get(i).getScore()来比较，大的排前面。
		for(int i = 0; i < docList.size(); i++){
			
			
		}
		System.out.println("for the whole documents it has keyword: "+wholeCount+" there are "+allfile);

	}

	public static void main(String args[]) throws IOException, ParseException {
		SearchFiles search = new SearchFiles();
		System.out.println("Enter a word to search:");
		String input = new Scanner(System.in).nextLine();
		search.Search(input);
		System.out.println(docList.get(0).getContent());
		Tfidf(input);

	}
}
