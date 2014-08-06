package model;


import java.text.DecimalFormat;

/**
 * Utilities
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0.1/13.04.2008
 * 
 */
public class Utilities {
	
	/**
	 * Returns user friendly representation of file size
	 *
	 * @param size size of a file
	 * @return formated size of the file using B, KB, MB, GB
	 */
	public static String fileSizeUnits(long length)
	{	
		if(length<1024)
		{
			return length+" B";
		}
		
		double size = (double) length;		
		DecimalFormat format = new DecimalFormat("####0.00");
		
		if(length<1048576)
		{
			return format.format(size/1024)+" KB";
		}
		else if(length<1073741824)
		{
			return format.format(size/1048576)+" MB";
		}
		else
		{
			return format.format(size/1073741824)+" GB";
		}
	}

}
