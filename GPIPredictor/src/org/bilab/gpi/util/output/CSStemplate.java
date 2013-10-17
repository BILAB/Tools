/**
 * 
 */
package org.bilab.gpi.util.output;

/**
 * @author davecao
 *
 */
public class CSStemplate {
	public static final String cssTable = 
		"table,td,th{"+
			"border:1px solid #000;"+
			"border-collapse:collapse;"+
			"margin:0;"+
			"padding:0;"+
		"}"+
		"td,th{"+
			"padding:.2em .5em;"+
			"vertical-align:top;"+
			"font-weight:normal;"+
			"text-align:center;"+
			"}"+
		"thead th{"+
			"text-transform:uppercase;"+
			"background:#666;"+
			"color:#fff;"+
		"}"+
		"tbody td{"+
			"background:#ccc;"+
		"}"+
		"tbody th{"+
			"background:#999;"+
		"}"+
		"tbody tr.odd td{"+
			"background:#eee;"+
		"}"+
		"tbody tr.odd th{"+
			"background:#ccc;"+
		"}"+
		"caption{"+
			"text-align:left;"+
			"font-size:140%;"+
			"text-transform:uppercase;"+
			"letter-spacing:-1px;"+
		"}"+
		"table th a:link{"+
			"color:#030;"+
		"}"+
		"table th a:visited{"+
			"color:#003;"+
		"}"+
		"table td a:link{"+
			"color:#369;"+
		"}"+
		"table td a:visited{"+
			"color:#000;"+
		"}"+
		"table a:hover{"+
			"text-decoration:none;"+
		"}"+
		"table a:active{"+
			"color:#000;"+
		"}";

}
