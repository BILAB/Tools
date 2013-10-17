package org.bilab.gpi.util;

import java.io.*;


public class SeqLoadFile_txt implements FileLoader {
	
	private String[] fastaheader(String header){
		int stop = 0;
		String[] re = new String[2];
		
		if(header.indexOf(" ")>=0){
			stop = header.indexOf(" ");
		}else{
			if(header.indexOf("|")>=0){
				stop = header.indexOf("|");
			}else{
				stop = header.length();
			}
		}
		re[0]=header.substring(1,stop);
		if(stop<header.length()){
			re[1]=header.substring(stop+1);
		}else{
			re[1]="";
		}
		return re;
	}
	
	private String Check_seq(String seq){
		String st = "BAD";
		String seqRegExp = "^[A-IK-NP-Z]+$";
		
		if(seq.matches(seqRegExp)){
			return "OK";
		}
		return st;
	}
	@Override
	public void filereader(String filename, SeqsQueue sq){
		// TODO Auto-generated method stub
		
		String line = null; //not declared within while loop
	    String id = null;
	    String desp = null;
	    String seq = null;
	    String[] hd = new String[2];
		try {
			BufferedReader input = new BufferedReader(new FileReader(filename));
			try {	       
		        try {
					while (( line = input.readLine()) != null){
					  // read fasta contents
						if(line.startsWith(">")){
							if(seq != null){
								// try to trim blank space and * 
								seq.replace("*$", "");
								seq.replace("^\\s+|\\s+|\\s+$","");
								Protein p = new Protein();
								p.ID = id;
								p.Description = desp;
								p.Seq = seq;
								p.length = seq.length();
								p.SeqStat = Check_seq(seq);
								sq.add(p);
								//clear temp variables
								id = null;
								desp = null;
								seq = null;
							}
							hd = fastaheader(line);
							id=hd[0];
							desp=hd[1];
						}else{
							if(seq == null){
								seq = line.toUpperCase();
							}else{
								seq += line.toUpperCase();
							}
						}
					}
					// Store last one
					seq.replace("*$", "");
					seq.replace("^\\s+|\\s+|\\s+$","");
					Protein p = new Protein();
					p.ID = id;
					p.Description = desp;
					p.Seq = seq;
					p.length = seq.length();
					p.SeqStat = Check_seq(seq);
					sq.add(p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		      finally {
		        try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
