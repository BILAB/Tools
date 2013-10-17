/**
 * 
 */
package org.bilab.gpi.util;


/**
 * @author davecao
 *
 */
public class SeqLoadFactory {
	
	private static String getSuffix(String filename){
		int i=filename.lastIndexOf(".");
		if (i != -1 && i != filename.length() - 1)
			return filename.substring(i+1);
		else
			return null;
	}
	private static FileLoader FileClassLoader(String classname){
		Class<?> c;
		try{
			c = Class.forName(classname);
			return (FileLoader)c.newInstance();
		}catch(Exception e){
			e.printStackTrace(); 
		}
		return null;
	}
	//Dynamically load class
	public static int Load(String filename,SeqsQueue sq){
		String packageName = "org.bilab.gpi.util.";
		String className = "SeqLoadFile_";
		String input_file_suffix = null;
		FileLoader fl;
		
		input_file_suffix = getSuffix(filename);
		
		if(input_file_suffix == null || 
		   input_file_suffix.matches("txt|fa|mpfa|fna|fsa|fas|fasta"))
		{
			fl=FileClassLoader(packageName+className+"txt");
			if(fl==null){
				System.out.println("Could not load file reader.");
			}else{
				fl.filereader(filename, sq);
				return 1;
			}
			
		}
		return 0;
	}
}
