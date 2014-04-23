Introduction
=================================
The files in this directory belong to the "GPIPredictor" package
for the prediction of glycosylphosphatidylinositol(GPI) lipid 
modification of a protein. 


Notes    
=================================
GPIPredictor uses an optimized support vector machine (SVM) classifier
to recognize the C-terminal sequence pattern and uses a voting system
based on SignalP version 3.0 to determine the presence or absence of the
N-terminal signal of a typical GPI-anchored protein. 



 Compile
=================================
Prerequisite:
  Java 1.5 SDK or later
  Apache Ant version 1.7.1 

Compile source files to a jar
>cd GPIPredictor
>ant

The GPIPredictor.jar will be created and stored in the "jars" directory 

Settings
====================================

1) Modify the config.xml in the "resources" directory
   Change YOUR_PATH to the absolute path of the SVM model file named 
GPImodel.model which also located in the "resources" directory.
And also specify the absolute path of the execute file of "SignalP v3.0".

SignalP v3.0 can be obtained from http://www.cbs.dtu.dk/cgi-bin/nph-sw_request?signalp

  
--------- Start ----------------------   
<?xml version="1.0" encoding="UTF-8"?>
<gpisvm-config>
<gpimodel>
 <path>/YOUR_PATH/GPImodel.model</path>
</gpimodel>
<signalp>
  <version>3.0</version>
  <path>/YOUR_PATH/signalp-3.0/signalp</path>
  <euk>0.43</euk>
  <gramPositive>0.45</gramPositive>
  <gramNegative>0.44</gramNegative>  
  <method>nn+hmm</method>
  <format>short</format>
  <trunc>70</trunc>
</signalp>
<rpsblast></rpsblast>
</gpisvm-config>  
-------- End -------------------------

2) Set an enviroment variable, "GPISVM_WEB_CONFIG", to point to config.xml

BASH:
   export GPISVM_WEB_CONFIG=/YOURPATH/config.xml

Execute
=======================================

>java -jar /PATH/GPIPredictor.jar [options] fasta file

Usage: CMD [options] fasta file
options:
-t: set type of orgnism (default euk)
	euk -- Eukaryote
	gram+ -- Gram positives
	gram- -- Gram negatives
-f: output format (default 1)
	1 -- csv format
	2 -- html table
	3 -- xml format
-o: output file name
-i: input protein sequences in FASTA format

Explanation of results
=======================================
The results looks like,

  
------------ Result------------------------------------------------------------------------
Num	ID		organism	SeqStat	length	GPI	Cterm	Cterm_prob	Nterm	Nterm_votes_Ratio
1	CCW12_YEAST	euk		OK	133	Y	Y	99.39		Y	85.71
2	CCW14_YEAST	euk		OK	238	Y	Y	99.14		Y	100.0
3	CRH1_YEAST	euk		OK	507	Y	Y	94.69		Y	85.71
...
----------------------------------------------------------------------------------------------

Num     : the sequential number of input sequences

ID      : the identifer of the protein sequence

organism: the protein belongs to eukaryote, gram+ or gram-. which is needed for siganlp

SeqStat : the sequence status show whether the protein has unknown amino acid.

length  : the sequence length of the input protein

Cterm   : indicate whether the protein contains a C-terminal signal of a typical GPI-anchored protein,
	  where "Y" means it is and "N" means it is not.

Cterm_prob: the probability that the input protein contains or does not contains a C-terminal signal.

Nterm   : similar to the "Cterm" item.

Nterm_votes_ratio : the vote ratio for determing whether the protein contains or does not contain a N-terminal signal.
                    To this end, GPIPredictor uses a voting system named SignalP-Vote which consists of seven binary indica-
                   tors obtained from SignalP version 3.0.

GPI     : indicate whether the input protein is a GPI-anchored protein or not,
	  where "Y" means it is and "N" means it is not. 
         *NOTE*
           GPI is "Y" only if both the "Cterm" item and the "Nterm" are  "Y".



Contact & Cite information
=================================
Developer:  Dr. Wei CAO
davecao@bi.a.u-tokyo.ac.jp

Bioinformation Engineering Laboratory
Graduate School of Agricultural and Life Science
University of Tokyo.
No.1-1-1 Yayoi Bukyo-ku, Tokyo, Japan. ZIP:113-8657

Papers (peer reviewed) :
1: Cao W, Maruyama J, Kitamoto K, Sumikoshi K, Terada T, Nakamura S, Shimizu K.
Using a new GPI-anchored-protein identification system to mine the protein
databases of Aspergillus fumigatus, Aspergillus nidulans, and Aspergillus oryzae.
J Gen Appl Microbiol. 2009 Oct;55(5):381-93. 
PubMed PMID: 19940384.

2: Wei Cao, Sumikoshi K, Terada T, Nakamura S, Kitamoto K, Shimizu K, 
Computational Protocol for Screening GPI-anchored Proteins, 
Bioinformatics and Computational Biology Lecture Notes in Computer Science 2009 Jun; 5462, 164-175. 
doi:10.1007/978-3-642-00727-9_17
