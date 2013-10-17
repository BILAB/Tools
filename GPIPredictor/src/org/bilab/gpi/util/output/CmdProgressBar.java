/**
 * 
 */
package org.bilab.gpi.util.output;

/**
 * @author davecao
 *
 */
public class CmdProgressBar {
	private int total = 0;
	
	public CmdProgressBar(int TotalSize){
		this.total = TotalSize;
	}
	public void initialize(){
		System.out.println("Start working ...");
	}
	
	public void update(int count){
		String s = " [ "+Integer.toString(count)+" of "+Integer.toString(this.total)+" Completed ] ";
		System.out.printf(s);
		if(count != this.total){
			System.out.printf("\r");// move cursor to the start of the line
		}else{
			System.out.printf("\n");
		}
	}
}
