package org.bilab.gpi.util;

import java.util.ArrayList;

public class SeqsQueue{
	private  int size;
	private ArrayList<String> SeqsIDs;
	private ArrayList<String> repSeqsIDs;
	private ArrayList<String> badSeqsIDs;
	private ArrayList<Protein> sequences;
	
	public SeqsQueue(){
		size = 0;
		SeqsIDs = new ArrayList<String>();
		repSeqsIDs = new ArrayList<String>();
		sequences = new ArrayList<Protein>();
	}
	
	public String OtAbbr2All(String ot){
		if(ot.equalsIgnoreCase("euk")){
			return "Eukaryotes";
		}else if(ot.equalsIgnoreCase("gram+")){
			return "Gram-positive bacteria";
		}else if(ot.equalsIgnoreCase("gram-")){
			return "Gram-negative bacteria";
		}else{
			return "unknown";
		}
	}	
	public void add(Protein p){
		// Search protein id from the existing list
		if(SeqsIDs.contains(p.ID)){
			repSeqsIDs.add(p.ID);
		}else{
			if(p.SeqStat.equalsIgnoreCase("OK")){
				SeqsIDs.add(p.ID);
				sequences.add(p);
				size = sequences.size();
			}else{
				badSeqsIDs.add(p.ID);
			}
		}
	}
	
	public Protein getByID(String ID){
		Protein p = null;
		if(SeqsIDs.contains(ID))
			p = sequences.get(SeqsIDs.indexOf(ID));
		return p;
	}
	
	public Protein getByNum(int i){
		return sequences.get(i);
	}
	
	public int Size(){
		return size;
	}
}
