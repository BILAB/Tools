/**
 * 
 */
package org.bilab.gpi.util;
import java.io.File;
import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.bilab.gpi.libsvm.SVM;
import org.bilab.gpi.libsvm.SVM_model;

/**
 * @author davecao
 *
 */
public class ConfigReader {
	public  SVM_model svm_model;
	public  String svm_model_path;
	public  String signalP_path;
	public  String signalP_ver;
	//public static String signalP_params_organism;
	public  String signalP_params_method;
	public  String signalP_params_format;
	public  String signalP_params_euk_thnn;
	public  String signalP_params_gramPos_thnn;
	public  String signalP_params_gramNeg_thnn;
	public  String signalP_params_trunc;
	public  String ConfigFile;
	public  String signalP_cmd;
	
	public	String rpsblast_path;
	public	String rpsblast_params_CDD;
	public	String rpsblast_params_QueryType;// if T is proteins
	public	String rpsblast_params_Eval;
	public	String rpsblast_params_oFormat;
	public	String rpsblast_params_Filter;
	public	String rpsblast_params_nCPUs;
	
	public  String rpsblast_cmd;
	
	public ConfigReader(){
		// Read System variable: GPI_WEB_CONFIG in xml format
		LoadParams();
	}
	private void LoadParams(){
		// Find the configure file 
		ConfigFile = System.getenv("GPISVM_WEB_CONFIG");
		//ConfigFile = "/home/davecao/config.xml";
		if(ConfigFile == null){
			System.out.println("You must set environment variable GPISVM_WEB_CONFIG");
			//Runtime.getRuntime().exit(0);
		}
	    
		// Load configure params
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
	          DocumentBuilder bulider = factory.newDocumentBuilder();
	          Document doc = bulider.parse(new File(ConfigFile));
	          if (doc == null) {
	               System.err.println("can't find get file name!");
	                return;
	          }
	          Element rootElement = doc.getDocumentElement();
	          Node node = null;
	          String nodeValue = "";
	          
	          //<gpimodel></gpimodel>
	          Element GPImodelElement = (Element)rootElement.getElementsByTagName("gpimodel").item(0);
	          NodeList nodes = GPImodelElement.getChildNodes();
	          for (int i = 0; i < nodes.getLength(); i++) {
	        	  node = nodes.item(i);
	              nodeValue = "";
	              if (node.getNodeType() == 1) {
	                    Node nodeChild = node.getChildNodes().item(0);
	                    if (nodeChild != null)
	                        nodeValue = nodeChild.getNodeValue();
	              }
	              if(node.getNodeName().equals("#text"))continue;
	              if (node.getNodeName().equals("path")) {
	              		svm_model_path = nodeValue;
	              }
	          }
	          //<rpsblast></rpsblast>
	          Element rpsblastElement = (Element)rootElement.getElementsByTagName("rpsblast").item(0);
	          nodes = rpsblastElement.getChildNodes();
	          for (int i = 0; i < nodes.getLength(); i++) {
	        	  node = nodes.item(i);
	              nodeValue = "";
	              if (node.getNodeType() == 1) {
	                    Node nodeChild = node.getChildNodes().item(0);
	                    if (nodeChild != null)
	                        nodeValue = nodeChild.getNodeValue();
	              }
	              if(node.getNodeName().equals("#text"))continue;
	              if (node.getNodeName().equals("path")) {
	            	  rpsblast_path = nodeValue;
	              }else if(node.getNodeName().equals("cdd")) {
	            	  rpsblast_params_CDD = nodeValue;
	              }else if(node.getNodeName().equals("protein")) {
	            	  rpsblast_params_QueryType = nodeValue;
	              }else if(node.getNodeName().equals("expectation")) {
	            	  rpsblast_params_Eval = nodeValue;
	              }else if(node.getNodeName().equals("oformat")) {
	            	  rpsblast_params_oFormat = nodeValue;
	              }else if(node.getNodeName().equals("filter")) {
	            	  rpsblast_params_Filter = nodeValue;
	              }else if(node.getNodeName().equals("cpunodes")) {
	            	  rpsblast_params_nCPUs = nodeValue;
	              }else{
	            	  
	              }
	          }
	          Element signalpElement = (Element)rootElement.getElementsByTagName("signalp").item(0);
	          nodes = signalpElement.getChildNodes();
	          for (int i = 0; i < nodes.getLength(); i++) {
	        	  node = nodes.item(i);
	              nodeValue = "";
	              if (node.getNodeType() == 1) {
	                    Node nodeChild = node.getChildNodes().item(0);
	                    if (nodeChild != null)
	                        nodeValue = nodeChild.getNodeValue();
	              }
	              if(node.getNodeName().equals("#text"))continue;
	              if (node.getNodeName().equals("version")) {
	              			signalP_ver = nodeValue;
	              }else if(node.getNodeName().equals("path")){
	              			signalP_path = nodeValue;
	              }else if(node.getNodeName().equals("euk")){
	              			signalP_params_euk_thnn = nodeValue;
	              }else if(node.getNodeName().equals("gramPositive")){
	            	  		signalP_params_gramPos_thnn = nodeValue;
	              }else if(node.getNodeName().equals("gramNegative")){
	              			signalP_params_gramNeg_thnn = nodeValue;
	              }else if(node.getNodeName().equals("method")){
	              			signalP_params_method = nodeValue;
	              }else if(node.getNodeName().equals("format")){
	              			signalP_params_format = nodeValue;
	              }else if(node.getNodeName().equals("trunc")){
	              			signalP_params_trunc = nodeValue;
	              }else {
                  }
	          }
	      }catch (ParserConfigurationException e) {
	              System.err.println("Configure File Error!" + e);
	      }catch (SAXException e) {
	              System.err.println("Configure File Error!" + e);
	      }catch (IOException e) {
	              System.err.println("Configure File Error!" + e);
	     }
	      // Load gpi svm model
	     try {
				svm_model = SVM.svm_load_model(svm_model_path);
	     }catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
		}
	    // create signalP_cmd
	     signalP_cmd = signalP_path + 
	     	   " -m "+signalP_params_method+
	     	   " -f "+signalP_params_format+
	     	   " -trunc "+signalP_params_trunc;
	     // create rpsblast  -p T -d 
	     rpsblast_cmd = rpsblast_path + 
	           " -p "+rpsblast_params_QueryType+
	           " -d "+rpsblast_params_CDD+
	           " -m "+rpsblast_params_oFormat+
	           " -F "+rpsblast_params_Filter+
	           " -a "+rpsblast_params_nCPUs;
	}
}
