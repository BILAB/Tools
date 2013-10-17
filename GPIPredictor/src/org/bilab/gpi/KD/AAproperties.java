/**
 *  a:97  A  alanine	    
 *  b:98  B  aspartic acid or asparagine
 *  c:99  C  cysteine
 *  d:100 D  aspartic acid
 *  e:101 E  glutamic acid
 *  f:102 F  phenylalanine
 *  g:103 G  glycine
 *  h:104 H  histidine
 *  i:105 I  isoleucine
 *  j:106 J  not used 
 *  k:107 K  lysine
 *  l:108 L  leucine
 *  m:109 M  methionine
 *  n:110 N  asparagine
 *  o:111 O  not used 
 *  p:112 P  proline
 *  q:113 Q  glutamine
 *  r:114 R  arginine
 *  s:115 S  serine
 *  t:116 T  threonine
 *  u:117 U  not used 
 *  v:118 V  valine
 *  w:119 W  tryptophan
 *  x:120 X  any amino acid
 *  y:121 Y  tyrosine
 *  z:122 Z	glutamic acid or glutamine
 */
package org.bilab.gpi.KD;

/**
 * @author davecao
 *
 */
import java.util.ArrayList;
import java.util.Arrays;

public class AAproperties {
	//ABCDEFGHIJKLMNOPQRSTUVWXYZ
	private final static double[][] Properties = 
	    {
			// Kyte J., Doolittle R.F. J. Mol. Biol. 157:105-132(1982).     
			{1.800,   // A  alanine	    
	         0.000,   // B  aspartic acid or asparagine
             2.500,   // C  cysteine
            -3.500,   // D  aspartic acid
            -3.500,   // E  glutamic acid
             2.800,   // F  phenylalanine
            -0.400,   // G  glycine
            -3.200,   // H  histidine
            4.500,    // I  isoleucine
            0.000,    // J  not used 
            -3.900,   // K  lysine
            3.800,    // L  leucine
            1.900,    // M  methionine
            -3.500,   // N  asparagine
            0.000,    // O  not used 
            -1.600,   // P  proline
            -3.500,   // Q  glutamine
            -4.500,   // R  arginine
            -0.800,   // S  serine
            -0.700,   // T  threonine
            0.000,    // U  not used 
            4.200,    // V  valine
            -0.900,   // W  tryptophan
            0.000,    // X  any amino acid
            -1.300,   // Y  tyrosine
            0.000     // Z	glutamic acid or glutamine
			},
			// Bulkness: Zimmerman J.M., Eliezer N., Simha R. J. Theor. Biol. 21:170-201(1968).
			{
				11.500,   // A  alanine
		        0.000,    // B  aspartic acid or asparagine
		        13.460,   // C  cysteine
		        11.680,   // D  aspartic acid
		        13.570,	  // E  glutamic acid
		        19.800,	  // F  phenylalanine
		        3.400,    // G  glycine
		        13.690,   // H  histidine
		        21.400,   // I  isoleucine
		        0.000,    // J  not used 
		        15.710,   // K  lysine
		        21.400,   // L  leucine
		        16.250,   // M  methionine
		        12.820,   // N  asparagine
		        0.000,    // O  not used 
		        17.430,   // P  proline
		        14.450,   // Q  glutamine
		        14.280,   // R  arginine
		        9.470,    // S  serine
		        15.770,   // T  threonine
		        0.000,    //U  not used 
		        21.570,   // V  valine
		        21.670,   // W  tryptophan
		        0.000,    // X  any amino acid
		        18.030,   // Y  tyrosine
		        0.000,    // Z  glutamic acid or glutamine 
			}
	    };
	
	private final static String[] PropNameList  = {"KYTJ820101","ZIMJ680102"};
	
	public static ArrayList<Double> get(String propName,String aa){
		int aaindex = 0;
		int inx = 0;

		String temp = aa.toLowerCase();
		ArrayList<Double> re = new ArrayList<Double>();
		
		aaindex = Arrays.binarySearch(AAproperties.PropNameList, propName);
		if (aaindex == -1){
			// Jump out of this function
			System.out.println("Amino acid property NOT found");
		}else{
			for(int i = 0; i<aa.length(); i++){
				inx = (int)(temp.charAt(i))- 97;
				re.add(Double.valueOf(Properties[aaindex][inx]));
			}
		}
		
		return re;
	}	
	
}
