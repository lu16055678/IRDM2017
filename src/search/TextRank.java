package search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import bestirdm.TextRank;

/**
 * TextRank关键词提取
 * @author hankcs
 */
public class TextRank
{
    static final float d = 0.85f;
  
    static final int max_iter = 200;
    static final float min_diff = 0.001f;

    public TextRank()
    {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }
    
    

    public String getKeyword(String address)
    {   
    	
    	stopword sp = new stopword();
    	
        List<String> wordList = new ArrayList<String>();
    	
		try {
		
			
			FileReader fr = new FileReader(address);
			
			BufferedReader br = new BufferedReader(fr);
			
			
			
			while(true){
				
				String line = br.readLine();
				if(line==null){
					break;
				}
				line = line.replaceAll("['!@,%/.#?()]", "");
		        
//				line = deleteWord(line);
				
				sp.deleteWord(line);
		        
		        String[] str = line.split(" ");
		        
		        int str_length = str.length;
		        
		        for(int i = 0; i<str_length; i++){
		        	String ss = str[i];
		        	wordList.add(ss);
		        }				
			}
			
//			bw.flush();
//			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

//        List<String> wordList = new ArrayList<String>();
    	
//        String s = title + content;
//        s = s.replaceAll("['!@,%/.#?()]", "");
//        
//        s = deleteWord(s);
//        
//        String[] str = s.split(" ");
//        
//        int str_length = str.length;
//        
//        for(int i = 0; i<str_length; i++){
//        	String ss = str[i];
//        	wordList.add(ss);
//        }
        
        
        
        
        Map<String, Set<String>> words = new HashMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList)
        {
            if (!words.containsKey(w))
            {
                words.put(w, new HashSet<String>());
            }
            que.offer(w);
            if (que.size() > 5)
            {
                que.poll();
            }

            for (String w1 : que)
            {
                for (String w2 : que)
                {
                    if (w1.equals(w2))
                    {
                        continue;
                    }

                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                }
            }
        }
        
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i)
        {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet())
            {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String other : value)
                {
                    int size = words.get(other).size();
                    if (key.equals(other) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
            }
        });

        String result = "";
        int entryListlength = entryList.size();
        for (int i = 0; i < entryListlength; ++i)
        {
            result += entryList.get(i).getKey()+"#";
        }
        return result;
    }

        

}