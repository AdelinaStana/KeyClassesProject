package sysmodel;

//import sysmodel.algorithm.Algorithm;


public class StringBinSearch {//implements Algorithm{
	public int indexOf(final String[] where ,final String x) {
		 if (x==null)
			 return -1;
		 int low = 0;
	     int high = where.length - 1;
	     int mid;
	     while( low <= high )
	     {
	            //mid = ( low + high ) / 2;
	    	 	mid = ( low + high ) >>> 1;
	            if( where[ mid ].compareTo( x ) < 0 )
	                low = mid + 1;
	            else if( where[ mid ].compareTo( x ) > 0 )
	                high = mid - 1;
	            else
	                return mid;
	     }
		return -1;
	}
	public StringBinSearch(){}
}
