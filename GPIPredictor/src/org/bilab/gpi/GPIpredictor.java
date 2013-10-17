/**
 * 
 */
package org.bilab.gpi;

import java.io.BufferedWriter;

import java.io.File;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bilab.gpi.KD.*;
import org.bilab.gpi.libsvm.*;
import org.bilab.gpi.util.*;
import org.bilab.gpi.util.output.PrintResultOut;
/**
 * @author davecao
 *
 */
public class GPIpredictor {
	// Load Config file via a system environment named GPISVM_WEB_CONFIG
	ConfigReader ConfParams = new ConfigReader();
	String orgType = "euk";
	String input_file_name = null;
	String output_file_name = null;
	int output_format = 1;
	
	SeqsQueue querys = new SeqsQueue();
	
	private void print_help(){
		System.out.print(
				"Usage: GPIpredictor [options] -i fasta file\n"
				+"options:\n"
				+"-t: set type of orgnism (default euk)\n"
				+"	euk -- Eukaryote\n"
				+"	gram+ -- Gram positives\n"
				+"	gram- -- Gram negatives\n"
				+"-f: output format (default 1)\n"
				+"	1 -- csv format\n"
				+"	2 -- html table\n"
				+"	3 -- xml format\n"
				+"-o: output file name\n"
				);
				System.exit(1);
	}
	
	private void process_cmd(String argv[]){
		// parse options
		int i;
		
		for(i=0;i<argv.length;i++){
			if(argv[i].charAt(0) != '-') break;
			
			if(++i>=argv.length)print_help();
			
			switch(argv[i-1].charAt(1)){
				case 't':
					if(!argv[i].matches("euk|gram\\+|gram-")){
						System.out.println("WRONG options: -t ");
						print_help();
						System.exit(1);
					}else{
						orgType = argv[i];
					}
					break;
				case 'o':
					output_file_name = argv[i];
					break;
				case 'i':
					input_file_name = argv[i];
					break;
				case 'f':
					if(argv[i].matches("1|2|3")){
						output_format = Integer.parseInt(argv[i]);
					}else{
						System.out.println("WRONG ptions: -f");
						print_help();
						System.exit(1);
					}
					break;
				default:
					System.err.print("Wrong options: see help \n");
					print_help();
			}
		}
			
		if(input_file_name == null){
			System.err.print("Without specifying the input file\n");
			print_help();
			System.exit(1);
		}
	}
	private double[] GPISVM(String id,String s){
		int WinSize = 9;
		int seqLength = s.length();
		int RequiredLen = 60;
		
		double sum = 0.0;
		String propName = "KYTJ820101";
		int nr_class = SVM.svm_get_nr_class(ConfParams.svm_model);
		double[] prob_estimates = new double[nr_class];
		double re[] = new double[nr_class];
		//initialize re[]
		re[0]=999;re[1]=999;

		ArrayList<Double> digitSeq = null;
		SVM_node[] svm_node = new SVM_node[RequiredLen-WinSize+1];
		// check the sequence length
		if (seqLength < RequiredLen){
			System.out.println("GPISVM ignores "+id+" (<60 residues).");
			//System.out.println("The minumium length of an input sequence length is 60 residues.");
		}else{
			String tSeq = s.substring(seqLength-RequiredLen);
			digitSeq = AAproperties.get(propName,tSeq);
			if (!(digitSeq == null) && !(digitSeq.size()==0)){
				for(int i=0; i< RequiredLen-WinSize+1; i++){
					for(int j=i; j< i+WinSize; j++){
						sum += digitSeq.get(j).doubleValue();
					}
					svm_node[i] = new SVM_node();
					svm_node[i].index = i+1;
					svm_node[i].value = sum/WinSize;
					sum = 0.0;
				}
			}
			//Prediction
			//double val = SVM.svm_predict(gpiWebParams.svm_model, svm_node);
			//re = (val>0)?"Y":"N";//Double.toString(val);
			
			
			SVM.svm_predict_probability(ConfParams.svm_model, svm_node, prob_estimates);
			if(prob_estimates[0]>prob_estimates[1]){
				re[0]=1;
				re[1]=prob_estimates[0];
			}else{
				re[0]=-1;
				re[1]=prob_estimates[1];
			}
			//System.out.print("Y");
		}
		return re;

	}
	private String SignalPVote_Pred(String name,String ot,String s){
		String re="";
		int reVal = 0;
		File tempfile = null;
        try {
        	// Create temporary file.
        	tempfile = File.createTempFile("gpisvmTemp", ".seq");
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(tempfile));
			out.write(">"+name+"\n");
	        out.write(s);
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String cmd = ConfParams.signalP_cmd + " -t " + ot +" "+tempfile.getPath();
		//System.out.println(cmd);
		try{					 
			 Runtime rt = Runtime.getRuntime();
			 Process proc = rt.exec(cmd);
			 
			 // Error message
			 SystemCall errorSignalP = new SystemCall(proc.getErrorStream(),"ERROR");
			 // Output message
			 SystemCall outputSignalP = new SystemCall(proc.getInputStream(),"OUTPUT");
			 
			 // kick them off
			 errorSignalP.start();
			 outputSignalP.start();
			 // any error?
			 int exitVal = proc.waitFor();
			 //System.out.println("Process exitValue: " + exitVal);
			 if(exitVal == 0){
				 //normal
				 if(outputSignalP.output==null || outputSignalP.output.isEmpty()){
					System.out.println(name+" got nothings");
					re="Error";
				 }else{
					 reVal = SignalPVote_Parser(outputSignalP.output);
					 re = Integer.toString(reVal);
				 }
			 }else{
				 re = "Error";
			 }
		}catch (Throwable t){
			 t.printStackTrace();
		}
		tempfile.delete();
		
		return re;
	}
	
	private int SignalPVote_Parser(String re){
		String ans = null;
		HashMap<String, String> result= new HashMap<String, String>();
		String[] line = re.split("\\n");
		for(int i=0;i<line.length;i++){
			if((line[i].charAt(0) == '#') || line[i].isEmpty()) continue;
				String[] re1 = line[i].split("\\s+");
				result.put("id", re1[0]);
				//SignalP-NN
				result.put("NN_Cmax_val",re1[1]);
				result.put("NN_Cmax_pos",re1[2]);
				result.put("NN_Cmax_ans",re1[3]);
				result.put("NN_Ymax_val",re1[4]);
				result.put("NN_Ymax_pos",re1[5]);
				result.put("NN_Ymax_ans",re1[6]);
				result.put("NN_Smax_val",re1[7]);
				result.put("NN_Smax_pos",re1[8]);
				result.put("NN_Smax_ans",re1[9]);
				result.put("NN_Smean_val",re1[10]);
				result.put("NN_Smean_ans",re1[11]);
				result.put("NN_D_val",re1[12]);
				result.put("NN_D_ans",re1[13]);
				//SignalP-HMM
				result.put("HMM_Cmax_aa",re1[15]);
				result.put("HMM_Cmax_val",re1[16]);
				result.put("HMM_Cmax_pos",re1[17]);
				result.put("HMM_Cmax_ans",re1[18]);
				result.put("HMM_Sprob_val",re1[19]);
				result.put("HMM_Sprob_ans",re1[20]);
				ans = re1[3]+re1[6]+re1[9]+re1[11]+re1[13]+re1[18]+re1[20];
				//System.out.println(line);
		}
		int count = 0;
		for(int i=0;i<ans.length();i++){
			if(ans.charAt(i)=='Y')count++;
		} 
		//return (count>3)?"Y":"N";
		return count;
	}
	
	private double formatFloat(double src, int pos){
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] re = new double[2];
		int numOfseqs = 0;
		int file_read_status = 0;
		
		GPIpredictor gpiPred = new GPIpredictor();
		// TODO Auto-generated method stub
		
		// Process command line arguments
		gpiPred.process_cmd(args);
		
		// read sequence file
		file_read_status=SeqLoadFactory.Load(gpiPred.input_file_name, gpiPred.querys);
		if(file_read_status==0){
			System.exit(1);
		}
		numOfseqs = gpiPred.querys.Size();
		if(numOfseqs == 0){
			System.out.println("The input file seems to be empty!");
			System.exit(1);
		}
		//CmdProgressBar cmdbar = new CmdProgressBar(numOfseqs);
		//cmdbar.initialize();
		String total = Integer.toString(numOfseqs);
		// GPI prediction
		for(int i=0; i<numOfseqs; i++){
			String s = " [ "+Integer.toString(i+1)+" of "+total+" Completed ] ";
			String id = gpiPred.querys.getByNum(i).ID;
			String seq = gpiPred.querys.getByNum(i).Seq;
			String ot = gpiPred.orgType;
			String sigP = "N.A.";
			
			gpiPred.querys.getByNum(i).organism = ot;
			//GPI-SVM check the Cterm
			re = gpiPred.GPISVM(id,seq);
			if(re[0]!=999){
				gpiPred.querys.getByNum(i).GPI_Cterm=(re[0]==1)?"Y":"N";
				gpiPred.querys.getByNum(i).GPI_Cterm_prob=gpiPred.formatFloat(re[1]*100,2);
			}else{
				gpiPred.querys.getByNum(i).GPI_Cterm="N.A.";
				gpiPred.querys.getByNum(i).GPI_Cterm_prob=Float.NaN;	
			}
			// SignalP-Vote check the Nterm
			if(!gpiPred.querys.getByNum(i).GPI_Cterm.equalsIgnoreCase("N.A.")){
				sigP = gpiPred.SignalPVote_Pred(id,ot,seq);
				if(!sigP.equalsIgnoreCase("Error")){
					double votes = Double.valueOf(sigP); 
					if (votes>3){
						gpiPred.querys.getByNum(i).GPI_Nterm= "Y";
						gpiPred.querys.getByNum(i).GPI_Nterm_votes_Ratio = gpiPred.formatFloat(votes/7*100,2);
						if(gpiPred.querys.getByNum(i).GPI_Cterm.equalsIgnoreCase("Y")){
							gpiPred.querys.getByNum(i).GPI = "Y";
						}else{
							gpiPred.querys.getByNum(i).GPI = "N";
						}
					}else{
						gpiPred.querys.getByNum(i).GPI_Nterm= "N";
						gpiPred.querys.getByNum(i).GPI = "N";
						gpiPred.querys.getByNum(i).GPI_Nterm_votes_Ratio = gpiPred.formatFloat((1-votes/7)*100,2);
					}
				}else{
					gpiPred.querys.getByNum(i).GPI_Nterm= "Error";
					gpiPred.querys.getByNum(i).GPI = "N.A.";
					gpiPred.querys.getByNum(i).GPI_Nterm_votes_Ratio = Float.NaN;
				}
			}else{
				gpiPred.querys.getByNum(i).GPI_Nterm= "N.A.";
				gpiPred.querys.getByNum(i).GPI = "N.A.";
				gpiPred.querys.getByNum(i).GPI_Nterm_votes_Ratio = Float.NaN;
			}
			System.out.printf(s);
			if(i!=numOfseqs){
			  System.out.printf("\r");	
			}else{
				System.out.printf("\n");
			}
			//cmdbar.update(i+1);
		}
		// Print out
		OutputStream o = null;
		if(gpiPred.output_file_name!=null){
			try {
				o = new DataOutputStream(new FileOutputStream(gpiPred.output_file_name));
				//o = new FileOutputStream(gpiPred.output_file_name);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			o = System.out;
		}
		PrintResultOut.Printout(o,gpiPred.querys,gpiPred.output_format);
		try {
			o.close();
			if(gpiPred.output_file_name!=null){
				System.out.println("[----- Finished -------]");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}