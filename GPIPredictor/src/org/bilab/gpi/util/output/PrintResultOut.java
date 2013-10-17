package org.bilab.gpi.util.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.bilab.gpi.util.Protein;
import org.bilab.gpi.util.SeqsQueue;

public class PrintResultOut {
	/*	private static String Checkfields(String s){
		String[] members = {"ID","organism","SeqStat","GPI","GPI_Cterm","GPI_Cterm_prob","GPI_Nterm","GPI_Nterm_votes_Ratio"};
		String[] revals = {"ID","organism","SeqStat","GPI","Cterm","Cterm_prob","Nterm","Nterm_votes_Ratio"};
		for(int i=0; i<members.length; i++){
			if(members[i].equals(s)){
				return revals[i];
			}
		}
		return null;
		}*/
	private static void print_xml(OutputStream o,SeqsQueue sq){
		int numOfseqs = sq.Size();		
		String ENCODING = "ISO-8859-1";
		String header = "<?xml version=\"1.0\" encoding=\""+ENCODING+"\"?><xml>";
		String end = "</xml>\n";
		
		PrintWriter  pw  =  null;  
		if  (o !=  null)  
			   pw  =  new  PrintWriter(o);
		pw.write(header);
		
		for(int i=0; i<numOfseqs; i++){
			String n = Integer.toString(i+1);
			Protein pr = sq.getByNum(i);
			Field[] fields = pr.getClass().getDeclaredFields();
			pw.write("<Prediction num=\""+n+"\">");
			for(int j=0; j<fields.length; j++){
				String fieldName = fields[j].getName();
				String xmlStartTag="<"+fieldName+">";
				String xmlEndTag="</"+fieldName+">";
				String fieldValue = null;
				try {
					try {
						fieldValue = pr.getClass().getField(fieldName).get(pr).toString();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pw.write(xmlStartTag+fieldValue+xmlEndTag);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pw.write("</Prediction>");
		}
		pw.write(end);
		if(pw!=null){
			pw.flush();
		}
	}
	private static void print_html(OutputStream o, SeqsQueue sq){
		int numOfseqs = sq.Size();
		PrintWriter  pw  =  null;  
		if  (o !=  null)  
			   pw  =  new  PrintWriter(o);
		
		String htmlpage = "<html><head>"+
			"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"+
			"<title>GPI-anchored protein prediction result</title>"+
			"<style type=\"text/css\">"+CSStemplate.cssTable+"</style>"+
			"</head><body><table width=\"80%\" border=\"1\"><tbody>";
		// print table header
		pw.write(htmlpage);
		pw.write("<tr>");
		pw.write("<th>Num</th><th>ID</th>"+
				"<th>organism</th>"+
				"<th>SeqStat</th>"+
				"<th>length</th>"+
				"<th>GPI</th>"+
				"<th>Cterm</th>"+
				"<th>Cterm_prob</th>"+
				"<th>Nterm</th>"+
				"<th>Nterm_votes_Ratio</th>"
				);
		pw.write("</tr>");
		
		for(int i=0; i<numOfseqs; i++){
			String ttStartTag="<td>";
			String ttEndTag="</td>";
			String n = Integer.toString(i+1);
			Protein pr = sq.getByNum(i);
			pw.write("<tr>");		
			pw.write(ttStartTag+n+ttEndTag+
					ttStartTag+pr.ID+ttEndTag+
					ttStartTag+pr.organism+ttEndTag+
					ttStartTag+pr.SeqStat+ttEndTag+
					ttStartTag+pr.length+ttEndTag+
					ttStartTag+pr.GPI+ttEndTag+
					ttStartTag+pr.GPI_Cterm+ttEndTag+
					ttStartTag+pr.GPI_Cterm_prob+ttEndTag+
					ttStartTag+pr.GPI_Nterm+ttEndTag+
					ttStartTag+pr.GPI_Nterm_votes_Ratio+ttEndTag
					);
			pw.write("</tr>");
		}				
		
		pw.write("</tbody></table></body></html>");
		if(pw!=null){
			pw.flush();
		}   
	}
	private static void print_csv(OutputStream o, SeqsQueue sq){
		String blockSeparator = ",";
		int numOfseqs = sq.Size();
		PrintWriter  pw  =  null;  
		if  (o !=  null)  
		   pw  =  new  PrintWriter(o);  
		pw.write(
				"ID"+blockSeparator+
				"Organism"+blockSeparator+
				"Status of Sequence"+blockSeparator+
				"length"+blockSeparator+
				"GPI-anchored"+blockSeparator+
				"Cterm"+blockSeparator+
				"Cterm_prob"+blockSeparator+
				"Nterm"+blockSeparator+
				"Nterm_votes_ratio"+"\n");
		for(int i=0; i<numOfseqs; i++){
			Protein pr = sq.getByNum(i);
			pw.write(
					pr.ID+blockSeparator+
					sq.OtAbbr2All(pr.organism)+blockSeparator+
					pr.SeqStat+blockSeparator+
					pr.Seq.length()+blockSeparator+
					pr.GPI+blockSeparator+
					pr.GPI_Cterm+blockSeparator+
					pr.GPI_Cterm_prob+blockSeparator+
					pr.GPI_Nterm+blockSeparator+
					pr.GPI_Nterm_votes_Ratio+"\n");
		}
		if(pw!=null){
			pw.flush();
		}
		
	}
	
	public static void Printout(OutputStream o,SeqsQueue sq,int format){
		if(format==1){// CSV format 
			print_csv(o,sq);
		}else if(format == 2){ // html table
			print_html(o, sq);
		}else{ //xml
			print_xml(o, sq);
		}
	}
}
