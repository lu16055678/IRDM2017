package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class txttest {
    
    public static void txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        //return result.toString();
    }
    
    
}