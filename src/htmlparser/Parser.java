package htmlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;

public class Parser {
	private static final String INPUTDIR = "page";
	private static final String OUTPUTDIR = "pageText";

	public void Parse() throws Exception {
		
		
		File inputPath = new File(INPUTDIR);
		String[] filelist = inputPath.list();
		for (int i = 0; i < inputPath.length(); i++) {			
			BodyContentHandler handler = new BodyContentHandler(1024 * 1024 * 10);
			Metadata metadata = new Metadata();			
			//File readfile = new File(INPUTDIR + "\\" + filelist[i]);
			File inputFile = new File(INPUTDIR,filelist[i]);
			FileInputStream inputstream = new FileInputStream(inputFile);
			ParseContext pcontext = new ParseContext();
			HtmlParser msofficeparser = new HtmlParser();	
			msofficeparser.parse(inputstream, handler, metadata, pcontext);
			FileOutputStream fs = new FileOutputStream(new File(OUTPUTDIR,inputFile.getName()));
			PrintStream p = new PrintStream(fs);
			p.println(handler.toString());
		}
	}
	public static void main(String[] args) throws Exception{
		Parser par = new Parser();
		par.Parse();
	}
	
}