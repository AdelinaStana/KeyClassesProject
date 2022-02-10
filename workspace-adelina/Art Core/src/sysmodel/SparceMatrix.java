package sysmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

public final class SparceMatrix<E> {
	private final int rows;
	private final int columns;
	
	private final Collection<E> elements;
	private  Map<Integer, Map<Integer, E>> map;
	private  Map<Integer, Map<Integer, E>> map2;

	public SparceMatrix(int rows, int columns) {
		if (rows <= 0 || columns <= 0)
			throw new NegativeArraySizeException();
		this.columns = columns;
		this.rows = rows;
		map = new HashMap<Integer, Map<Integer, E>>();
		map2 = new HashMap<Integer, Map<Integer, E>>();
		elements = new ArrayList<E>();
	}
	
	public void initEmptyMatrix() {
		map = new HashMap<Integer, Map<Integer, E>>();
		map2 = new HashMap<Integer, Map<Integer, E>>();
		elements.clear();
	}

	public void putElement(int i, int j, E element) {
		if (i < 0 || j < 0 || i >= rows || j >= columns)
			throw new IndexOutOfBoundsException();
                
                // adaugat ptr eliminat elementele nule
                //TODO:: maybe we should fix this externally, as a Float casted to an Integer will crash everything (in the case of Hillclimbing)
                if (Integer.class.isInstance(element) && (Integer) element==0) {
//                    System.out.println("Eliminat zero intre "+i+" si "+j);
                    return;
                //TODO:: temporary fix
                }  else if(Float.class.isInstance(element) && (Float) element == 0.0f) {
                    return;
                }
                
		Integer ii = Integer.valueOf(i);
		Integer jj = Integer.valueOf(j);
		Map<Integer, E> m = map.get(ii);
		if (m == null) {
			m = new HashMap<Integer, E>();
			map.put(ii, m);
		}
		m.put(jj, element);

		m = map2.get(jj);
		if (m == null) {
			m = new HashMap<Integer, E>();
			map2.put(jj, m);
		}
		m.put(ii, element);

		elements.add(element);
	}

	public E getElement(int i, int j) {
		if (i < 0 || j < 0 || i >= rows || j >= columns) {
			// throw new IndexOutOfBoundsException();
			return null;
		}
		Map<Integer, E> m = map.get(Integer.valueOf(i));
		if (m == null)
			return null;
		return m.get(Integer.valueOf(j));
	}

        
        public void mergeIntoContainer(int i, int j) {
            // i = contained, j=container
            // a[j][x] += a[i][x] row j will add row i
            // a[i][x]=0
            // a[x][j] += a[x][i]
            // a[x][i]=0
            int x;
           // System.out.println("merge into container "+i+"  "+j);
            for (x=0; x<columns; x++) { 
                if ((x!=j)&&(x!=i)&&(getElement(i,x))!=null) {
                    Integer suma=0;
                    if (getElement(j,x)!=null)
                        suma=(Integer)getElement(j,x);
                    suma=suma+(Integer)getElement(i,x);
                    //System.out.println("putelement "+j+" "+x+" "+suma);
                    putElement(j,x, (E)suma);
                }
            }
            
            for (x=0; x<columns; x++) { 
                if ((x!=j)&&(x!=i)&&(getElement(x,i)!=null)) {
                    Integer suma=0;
                    if (getElement(x,j)!=null)
                        suma=(Integer)getElement(x,j);
                    suma=suma+(Integer)getElement(x,i);
                    putElement(x,j, (E)suma);
                      //  System.out.println("putelement "+x+" "+j+" "+suma);
                }
            }
            
//            System.out.println("> "+this.toString());
            
             for (x=0; x<rows; x++) { 
                if ((getElement(x,i))!=null) {
                    map.get(x).remove(i);
                }
             }
             for (x=0; x<columns; x++) { 
                if ((getElement(i,x))!=null) {
                    map2.get(x).remove(i);
                }
             }
             
            map.put(i, null);
  //           System.out.println(">"+ this.toString());
            map2.put(i,null);
   //          System.out.println(">"+this.toString());
        }
        
        public void deleteNode(int i) {
        	 int x;
        	 for (x=0; x<rows; x++) { 
                 if ((getElement(x,i))!=null) {
                     map.get(x).remove(i);
                 }
              }
              for (x=0; x<columns; x++) { 
                 if ((getElement(i,x))!=null) {
                     map2.get(x).remove(i);
                 }
              }
              
             map.put(i, null);
   //           System.out.println(">"+ this.toString());
             map2.put(i,null);
        }
        
        public String toString() {
		return map.toString();
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public boolean isEmptyRow(int i) {
		if (i < 0 || i >= rows)
			throw new IndexOutOfBoundsException();
		return map.get(Integer.valueOf(i)) == null;
	}

	public Set<Integer> getConnectedToRow(int i) {
		if (i < 0 || i >= rows)
			throw new IndexOutOfBoundsException();
		return getUtil(i, map);
	}

	public Set<Integer> getConnectedToColumns(int i) {
		if (i < 0 || i >= columns)
			throw new IndexOutOfBoundsException();
		return getUtil(i, map2);
	}

	private Set<Integer> getUtil(int i, Map<Integer, Map<Integer, E>> m) {
		Map<Integer, E> connected = m.get(Integer.valueOf(i));
		if (connected == null)
			return null;
		else
			return connected.keySet();
	}

// Added for PageRank
        
        public int getNumberOfNodes(){
        if (rows>columns)return  rows;
        else return columns;
        }
        
        public List<Integer> getAllNodes() {
            List<Integer> l=new ArrayList<Integer>();
            int n=getNumberOfNodes();
            for (int i=0; i<n; i++)
                l.add(i);
            return l;          
        }
        
        public List<Integer> getNodesWithoutOutlinks() {
            List<Integer> l=new ArrayList<Integer>();
            // add to l only nodes without outlinks
            for (int j=0; j<columns; j++) 
                if (getUtil(j, map2)==null)
                    l.add(j);
            //System.out.println("Nodes without outlinks");
            //System.out.println(l.toString());
            return l;
        }

        public Set<Integer> inboundNeighbors(int p) {
        /*    List<Integer> l=new ArrayList<Integer>();
            // add to l only nodes that are inbound neighbors of p
            if (getUtil(p, map)==null)
                return l;
            return new ArrayList(getUtil(p, map)); */
        	Set<Integer> s= getUtil(p,map);
        	if (s==null)
        		return new HashSet<Integer>();
        	else return s;
        }

        
        public List<Integer> outboundNeighbors(int p) {
            List<Integer> l=new ArrayList<Integer>();
            // add to l only nodes that are outbound neighbors of p
            if (getUtil(p, map2)==null)
                return l;
            return new ArrayList(getUtil(p, map2));
        }
        
        public int outDegree(int p) {
        int od=0;
        if (getUtil(p,map2)==null)
            return 0;
         return getUtil(p,map2).size();
        }
        
         public int inDegree(int p) {
        int id=0;
        if (getUtil(p,map)==null)
            return 0;
         return getUtil(p,map).size();
        }
        
        public int outWeight(int p) {
        int od=0;
        E e;
        for (int i=0; i<rows; i++) {
            e=getElement(i,p);
            if (e!=null) 
                od=od+(Integer)e;
        }
        return od;
        }
        
        public int inWeight(int p) {
        int id=0;
        E e;
        for (int i=0; i<columns; i++) {
            e=getElement(p,i);
            if (e!=null) 
                id=id+(Integer)e;
        }
        return id;
        }
        
        public int Weight(int ip, int p) {
            int w=0;
            E e;
            e=getElement(p,ip);
            if (e!=null)
                w=(Integer)e;
            return w;

        }
        
        public SparceMatrix createReverse() {
       // reverse directions of edges 
            SparceMatrix rev=new SparceMatrix(rows, columns);
            for (int i=0; i<rows; i++)
                for (int j=0; j<columns; j++)
                {
                    if (getElement(i,j)!=null)
                        rev.putElement(j, i, getElement(i,j));
                }
            return rev;
        }
        
        
        private E createSum(E e1, E e2, int F) {
            //int F=2;
            Integer n;
            if (e1==null) {
                 n=((Integer)e2)/F;
                 return (E)n;
            }//return e2;
            if (e2==null) {
                n=(1*(Integer)e1);
                return (E)n;
            }//return e1;
            n=(F*(Integer)e1+(Integer)e2)/F;
            return (E)n;
        }
        public SparceMatrix createUndirected( int F) {
       // new[i][j]=old[i,j]+old[j][i] 
            SparceMatrix rev=new SparceMatrix(rows, columns);
            
            for (int i=0; i<rows; i++)
                for (int j=0; j<columns; j++)
                    if ((getElement(i,j)!=null)||(getElement(j,i)!=null)) {
                        E e=createSum(getElement(i,j),getElement(j,i), F);
                    rev.putElement(i, j, e);
                    }
        
            return rev;
        }
        
        }
        
        

