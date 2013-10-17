/**
 * 
 */
package org.bilab.gpi.libsvm;

import java.io.Serializable;

/**
 * @author davecao
 *
 */
public class SVM_problem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6869656175929469349L;
	
	public int l;
    public double[] y;
    public SVM_node[][] x;

}
