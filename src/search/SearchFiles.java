package search;

/**
 * This code was based on the source code on tutorials of Apache Lucene website.
 * It was modified by @author mingminlu.
 */
import java.io.BufferedReader;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
//import java.util.Scanner;
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

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class SearchFiles {
	private static final String INDEXPATH = "index";
	private static final String DOCPATH = "pageText";
	private String field = "contents";
	private int hitsPerpage = 100;
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

	private static void CalculateWholeCount() {
		for (int i = 0; i < docList.size(); i++) {
			String content = docList.get(i).getContent().toLowerCase();
			int wordCount = 0;
			Pattern wholeWord = Pattern.compile("[a-zA-Z0-9]+");
			Matcher match = wholeWord.matcher(content);
			while (match.find()) {
				wordCount++;
			}
			docList.get(i).setWordCount(wordCount);
			// System.out.println("the document has " +
			// docList.get(i).getWordCount() + " words");
			// Find all matches
			// System.out.println("for document " + (i + 1) + "it has keyword: "
			// + articlecount);
		}
	}

	private static void CalculateQueryCount(String word) {
		for (int i = 0; i < docList.size(); i++) {
			String content = docList.get(i).getContent().toLowerCase();
			int articlecount = 0;
			Pattern pattern = Pattern.compile(word);
			Matcher found = pattern.matcher(content);
			while (found.find()) {
				// Get the matching string
				articlecount++;
				// wholeCount++;
				// String digitNumList = found.group();
			}
			docList.get(i).setCount(articlecount);
		}
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
			/**
			 * if (end > hits.length) { System.out.println("Only results 1 - " +
			 * hits.length + " of " + numTotalHits + " total matching documents
			 * collected."); System.out.println("Collect more (y/n) ?"); String
			 * line = in.readLine(); if (line.length() == 0 || line.charAt(0) ==
			 * 'n') { break; }
			 * 
			 * hits = searcher.search(query, numTotalHits).scoreDocs; }
			 **/

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
					content.setPath(path);
					docList.add(i, content);
					docList.get(i).setFileName(file.getName());
					//System.out.println(docList.get(i).getFileName());
					//System.out.println((i + 1) + ". " + path);
				} else {
					System.out.println((i + 1) + ". " + "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			/**
			 * if (numTotalHits >= end) { boolean quit = false; while (true) {
			 * System.out.print("Press "); if (start - hitsPerPage >= 0) {
			 * System.out.print("(p)revious page, "); } if (start + hitsPerPage
			 * < numTotalHits) { System.out.print("(n)ext page, "); }
			 * System.out.println("(q)uit or enter number to jump to a page.");
			 * 
			 * String line = in.readLine(); if (line.length() == 0 ||
			 * line.charAt(0) == 'q') { quit = true; break; } if (line.charAt(0)
			 * == 'p') { start = Math.max(0, start - hitsPerPage); break; } else
			 * if (line.charAt(0) == 'n') { if (start + hitsPerPage <
			 * numTotalHits) { start += hitsPerPage; } break; } else { int page
			 * = Integer.parseInt(line); if ((page - 1) * hitsPerPage <
			 * numTotalHits) { start = (page - 1) * hitsPerPage; break; } else {
			 * System.out.println("No such page"); } } } if (quit) break; end =
			 * Math.min(numTotalHits, start + hitsPerPage); }
			 **/
		}
	}

	public static StringBuilder txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while ((s = br.readLine()) != null) {
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
	 * 
	 * @param keyword
	 */
	private static void Tfidf(String keyword) {
		keyword = keyword.toLowerCase();
		CalculateWholeCount();	
		List<String> queryString = new LinkedList<String>();
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
		Matcher match = pattern.matcher(keyword);
		while (match.find()) {
			queryString.add(match.group());
			// System.out.println(match.group());
		}
		// int wholeCount = 0;
		// System.out.println(keyword.toLowerCase());
		double log = (double) allfile / docList.size();
		double idf = Math.log10(log);
		for (int i = 0; i < docList.size(); i++) {
			double tf = 0;
			double temp = 0;
			for (int j = 0; j < queryString.size(); j++) {
				CalculateQueryCount(queryString.get(j));
				temp = (double) docList.get(i).getCount() / docList.get(i).getWordCount();
				tf = tf + temp;		
				//System.out.println("In document "+docList.get(i).getPath()+" For word "+queryString.get(j)+" the score is "+docList.get(i).getCount());
			}
			double tfidf = tf * idf;
			docList.get(i).setScore(tfidf);
		}
		// Sort Doc
		for (int i = 0; i < docList.size() - 1; i++) {
			for (int j = i + 1; j < docList.size(); j++) {
				ContentFile file1;
				ContentFile file2;
				if (docList.get(j).getScore() > docList.get(i).getScore()) {
					file1 = docList.get(j);
					file2 = docList.get(i);
					docList.add(j, file2);
					docList.remove(i);
					docList.add(i, file1);
					docList.remove(j + 1);
				}
			}
		}
		// System.out.println("for the whole documents it has keyword: " +
		// wholeCount + " there are " + allfile);
		System.out.println("TF-IDF");
		for (int k = 0; k < docList.size(); k++) {
			//System.out.println(docList.get(k).getScore());
			System.out.println(docList.get(k).getFileName());
		}
	}

	private static void Pagerank(String keyword){
		keyword = keyword.toLowerCase();
		double alpha = 0.85;
		for(int i=0;i<docList.size();i++){
			double pr = 1;
			String linkName = docList.get(i).getFileName();
			double links = Double.valueOf(txt2String(new File("link",linkName)).toString());
			docList.get(i).setPr(alpha+(1-alpha)*(pr/links));
		}
		for (int i = 0; i < docList.size() - 1; i++) {
			for (int j = i + 1; j < docList.size(); j++) {
				ContentFile file1;
				ContentFile file2;
				if (docList.get(j).getPr() > docList.get(i).getPr()) {
					file1 = docList.get(j);
					file2 = docList.get(i);
					docList.add(j, file2);
					docList.remove(i);
					docList.add(i, file1);
					docList.remove(j + 1);
				}
			}
		}
		System.out.println("Page Rank:");
		for (int k = 0; k < docList.size(); k++) {
			
			System.out.println(docList.get(k).getFileName());
		}
	}
	private static void Bm25(String keyword){
		keyword = keyword.toLowerCase();
		double totalWord = 0;
		CalculateWholeCount();
		List<String> queryString = new LinkedList<String>();
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
		Matcher match = pattern.matcher(keyword);
		while (match.find()) {
			queryString.add(match.group());
			// System.out.println(match.group());
		}
		for(int i=0;i<docList.size();i++){
			totalWord = totalWord+docList.get(i).getWordCount();
		}
		double log = (double) allfile / docList.size();
		double idf = Math.log10(log);
		double k = 2.0;
		double b = 0.75;
		double bm25 = 0;
		double averageWord = totalWord/docList.size();
		for (int i = 0; i < docList.size(); i++) {
			double tf = 0;
			double temp = 0;
			for (int j = 0; j < queryString.size(); j++) {
				CalculateQueryCount(queryString.get(j));
				tf = (double) docList.get(i).getCount() / docList.get(i).getWordCount();				
				temp = (idf*tf*(k+1))/(tf+k+(1-b+b*averageWord));
				bm25 = bm25 + temp;
			}
			docList.get(i).setScore(bm25);
		}
		for (int i = 0; i < docList.size() - 1; i++) {
			for (int j = i + 1; j < docList.size(); j++) {
				ContentFile file1;
				ContentFile file2;
				if (docList.get(j).getScore() > docList.get(i).getScore()) {
					file1 = docList.get(j);
					file2 = docList.get(i);
					docList.add(j, file2);
					docList.remove(i);
					docList.add(i, file1);
					docList.remove(j + 1);
				}
			}
		}
		// System.out.println("for the whole documents it has keyword: " +
		// wholeCount + " there are " + allfile);
		System.out.println("BM25:");
		for (int i = 0; i < docList.size(); i++) {
			//System.out.println(i);
			System.out.println(docList.get(i).getFileName());
			
		}
		
	}

	public static void main(String args[]) throws IOException, ParseException {
		SearchFiles search = new SearchFiles();
		String queryString = "Computer Science";
		search.Search(queryString);
		Tfidf(queryString);
		//Pagerank(queryString);		
		//Bm25(queryString);
	}
}
