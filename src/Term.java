import java.util.Comparator;


class Term implements Comparator<Term>{
	
	String str;
	int docId;
	
	public Term(String str, int docId){
		this.str = str;
		this.docId = docId;
	}
	public Term(){};
	
	public String getTerm() {return this.str;}
	public int getDocNumber() {return this.docId;}
	
	 public int compare(final Term t1, Term t2) {
		   if (t1.getTerm().compareTo(t2.getTerm()) > 0) {
	           return 1;
	       } else if (t1.getTerm().compareTo(t2.getTerm()) < 0) {
	           return -1;
	       } else  if(t1.getDocNumber() > t2.getDocNumber()) {
	    	   return 1;
	       } else if(t1.getDocNumber() < t2.getDocNumber()) return -1;
	       else return 0;
		   
	     }

}
