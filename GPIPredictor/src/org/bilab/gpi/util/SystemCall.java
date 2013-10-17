/**
 * 
 */
package org.bilab.gpi.util;

/**
 * @author davecao
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author davecao
 *
 */
public class SystemCall extends Thread {
	InputStream is;
	String type;
	public String output;
	String SEPARATOR = "\n";
	public SystemCall(InputStream is, String type){
		this.is = is;
		this.type = type;
		this.output = null;
	}
	
	public void run(){
		try{
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line= null;
            while ( (line = br.readLine()) != null){
            	if(output == null){
            		output = line+SEPARATOR;
            	}else{
                  	output = output + line + SEPARATOR;
            	}
            }
                //System.out.println(type + ">" + line);    
         } catch (IOException ioe){
                ioe.printStackTrace();  
         }

	}
}