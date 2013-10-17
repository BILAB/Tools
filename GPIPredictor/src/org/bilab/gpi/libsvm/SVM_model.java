/**
 * 
 */
package org.bilab.gpi.libsvm;

/**
 * @author davecao
 *
 */
public class SVM_model implements java.io.Serializable{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -3950979604700345173L;
	
	SVM_parameter param;    // parameter
     int nr_class;           // number of classes, = 2 in regression/one class svm
     int l;                  // total #SV
     SVM_node[][] SV;        // SVs (SV[l])
     double[][] sv_coef;     // coefficients for SVs in decision functions (sv_coef[k-1][l])
     double[] rho;           // constants in decision functions (rho[k*(k-1)/2])
     double[] probA;         // pariwise probability information
     double[] probB;

     // for classification only

     int[] label;            // label of each class (label[k])
     int[] nSV;              // number of SVs for each class (nSV[k])
                             // nSV[0] + nSV[1] + ... + nSV[k-1] = l

}

