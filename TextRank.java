package test;

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

//get keywords

public class TextRank
{
	//阻尼系数
    static final float d = 0.85f;
  
    //最大迭代次数
    
    static final int max_iter = 200;
    
    //收敛极限
    static final float min_diff = 0.001f;

    public TextRank()
    
    {
    	
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        
    }
    
    

    public String getKeyword(String address)
    
    {   
    	
    	//import stopword (should be excluded
    	stopword sp = new stopword();
    	
    	//get string list
        List<String> wordList = new ArrayList<String>();
    	
		try {
		
			//file reader path
			FileReader fr = new FileReader(address);
			
			//
			BufferedReader br = new BufferedReader(fr);
			
			
			
			while(true){
				
				String line = br.readLine();
				if(line==null){
					break;
				}
				
				//去掉标点
				line = line.replaceAll("['!@,%/.#?()]", "");
		        
				sp.deleteWord(line);
		        
		        String[] str = line.split(" ");
		        
		        //去标点以后的单词们存成list
		        int str_length = str.length;
		        
		        for(int i = 0; i<str_length; i++){
		        	String ss = str[i];
		        	wordList.add(ss);
		        }

				
			}
			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
        
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
            result += entryList.get(i).getKey() + '#';
        }
        return result;
    }

    public static void main(String[] args)
    {
    	
    	List<String> addresses = new ArrayList<String>();
    	
    	addresses.add("/Users/dupingyuan/Desktop/test1.txt");
    	addresses.add("/Users/dupingyuan/Desktop/test2.txt");
    	addresses.add("/Users/dupingyuan/Desktop/test3.txt");
    	addresses.add("/Users/dupingyuan/Desktop/test4.txt");

        List<Integer> ranks = new ArrayList<Integer>();
    	for(int i_address = 0; i_address<addresses.size(); i_address++)
    	{
    		String address = addresses.get(i_address);
    		String rank_word = new TextRank().getKeyword(address);
        	System.out.println(address);
        	String[] rank_words = rank_word.split("#");
        	
        	int rank_words_length = rank_words.length;
        	
        	HashMap<String, Integer> work_rank = new HashMap<String, Integer>();
        	
        	for(int i = 0; i<rank_words_length; i++)
        	
        	{
        		
        		work_rank.put(rank_words[i], i+1);
        	}
    		
        	File file = new File(address);
        	StringBuilder result = new StringBuilder();
        	try{
        		BufferedReader br = new BufferedReader(new FileReader(file));
        		String s = null;
        		while((s=br.readLine())!=null){
        			result.append(System.lineSeparator()+s);
        		}
        		br.close();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	String content = result.toString();
        	int count = 0;
            Pattern words = Pattern.compile("my");
            Matcher match = words.matcher(content);
           
            if(match.find()){
            	ranks.add(work_rank.get(words.toString()));
         	
            	
        					    			

            
            	
            }

        	
    	}
    	

	    for (int m=0; m< ranks.size()-1; m++ ) {
	    	for(int n=1; n<ranks.size();n++){
    	
    	if (ranks.get(n)>ranks.get(m)){
    		int a1 = ranks.get(n);
        	int a2 = ranks.get(m);
    		ranks.add(n,a2);
    		ranks.remove(m);
    		ranks.add(m,a1);
    		ranks.remove(n+1);
    			}
	    	}
	    }
	    
	    System.out.println(ranks);
	    

    }
    

}