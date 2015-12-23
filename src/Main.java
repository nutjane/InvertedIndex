import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Main {

	public static HashMap<String, HashMap> globalHash = new HashMap<String, HashMap>();

	public static void main(String[] args) throws IOException {
		
		Scanner sc = new Scanner(System.in);
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command;
        //start reading command
	    while ((command = br.readLine()) != null) {
	    	StringTokenizer st = new StringTokenizer(command);
	    	
			//analyze command
	    	String nextCommand = st.nextToken();
	    	
	    	//create
			if(nextCommand.equals("create")){
				String indexFileName = st.nextToken();
				String buffer = st.nextToken();
				String docName = "";
				while(st.hasMoreTokens()){
					docName += st.nextToken()+" ";
				}
				create(indexFileName, docName);
				
			//save	
			}else if(nextCommand.equals("save")){
				if(st.hasMoreTokens()){
					String indexFileName = st.nextToken();
					if(globalHash.containsKey(indexFileName)){
						save(indexFileName);
					}else System.out.println("Save ERROR: noting to save\n-------------------------");
					
				} else{
					System.out.println("Incorrect Command.");
				}
			}else if(nextCommand.equals("update")){
				String indexFileName = st.nextToken();
				String buffer = st.nextToken();
				String docName = "";
				while(st.hasMoreTokens()){
					docName += st.nextToken()+" ";
				}
				update(indexFileName, docName);
				
			}else if(nextCommand.equals("search")){
				String query = "";
				while(st.hasMoreTokens()){
					query += st.nextToken()+" ";
				}
				if(query.length()>0){
					search(query);
				}else{
					System.out.println("Incorrect Command.");
				}
				
			}else{
				System.out.println("Incorrect Command.");
			}

	    	
	    }

	}
	
	public static void search(String query) throws IOException{
		String indexFileName = "index";
		boolean hasFile = true;
		if(!new File(indexFileName+".txt").isFile()){
			hasFile = false;
			System.out.println("ERROR: Index file not found.");
		}
		if(hasFile){
			
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap = genHashMapFromInvertedIndexFile(indexFileName);
			
			Integer[] listDocOfIndex = listDocOfIndex(hashMap);
			
			//boolean optimization --> postfix
			Stack<String> stack = new Stack<String>();
			query = query.substring(1,query.length()-2);
			query = query.replace("&", " & ").replace("(", " ( ")
					.replace(")", " ) ").replace("|", " | ").replace("~", " ~ ");
			
			String postFixQuery = "";
			StringTokenizer token = new StringTokenizer(query);
			//for one search term
			if(token.countTokens()==1){
				String sTerm = token.nextToken();
				if(hashMap.containsKey(sTerm)){
					String result = hashMap.get(sTerm);
					StringTokenizer tk = new StringTokenizer(result.replace("*", ""),",");
					while(tk.hasMoreTokens()) System.out.println("doc"+tk.nextToken());
				}else System.out.println("ERROR: Your search term is not in the Inverted Index file.");
				
			}
			//more than one search term
			while(token.hasMoreTokens()){
				String tokenValue = token.nextToken();
				if(tokenValue.equals("&") || tokenValue.equals("|") || tokenValue.equals("~")) {
					if(stack.isEmpty()) stack.push(tokenValue);
					else{
						boolean flag = true;
						while(!stack.isEmpty() && flag){
							String poppingValue = stack.pop();
							if(poppingValue.equals("(")){
								stack.push(poppingValue);
								flag = false;
							}else{
								if((poppingValue.equals("~")) /*|| (poppingValue.equals(tokenValue))*/ 
										|| ((poppingValue.equals("|") && (tokenValue.equals("&"))))){
									postFixQuery += poppingValue+" ";
								}else{
									stack.push(poppingValue);
									flag = false;
								}
							}
						}
						stack.push(tokenValue);
					}
					
				}else if(tokenValue.equals("(")){
					stack.push(tokenValue);
					
				}else if(tokenValue.equals(")")){
					boolean flag = true;
					while(!stack.isEmpty() && flag){
						String poppingValue = stack.pop();
						if(!poppingValue.equals("(")){
							postFixQuery += poppingValue+" ";
						}else {
							flag = false;
						}
					}
				}else {
					postFixQuery += tokenValue+" ";
				}
				
				
			}
			while(!stack.isEmpty()){
				postFixQuery += stack.pop()+" ";
			}
			
			
			// done postfix conversion. Next is to evaluate postfix value
			stack = new Stack<String>();
			token = new StringTokenizer(postFixQuery);
			String tokenValue = "";
			if(token.countTokens()!=0){
				
				tokenValue = token.nextToken();
				while(!tokenValue.equals("")){
					if(tokenValue.equals("&")){
						String sTerm = "";
						int loop_round = 1;
						while(tokenValue.equals("&") && !tokenValue.equals("")){
							loop_round++;
							if(token.hasMoreTokens()) {
								tokenValue = token.nextToken();
							}else tokenValue = "";
						}
						while(loop_round>0){
							sTerm += stack.pop()+" ";
							loop_round--;
						}
						String result = heuristic(hashMap, sTerm, '&');
						stack.push(result);
						
					}else if(tokenValue.equals("|")){
						String sTerm = "";
						int loop_round = 1;
						while(tokenValue.equals("|") && !tokenValue.equals("")){
							loop_round++;
							if(token.hasMoreTokens()) {
								tokenValue = token.nextToken();
							}else tokenValue = "";
						}
						while(loop_round>0 && !stack.isEmpty()){
							sTerm += stack.pop()+" ";
							loop_round--;
						}
						String result = heuristic(hashMap, sTerm, '|');
						stack.push(result);
						
					}else if(tokenValue.equals("~")){
						String sTerm = stack.pop();
						String result = "";
						if(sTerm.charAt(0)=='*') {
							result = not_operation(sTerm, listDocOfIndex);
						}else{
							result = not_operation(hashMap.get(sTerm), listDocOfIndex);
						}
						stack.push(result);

						if(token.hasMoreTokens()) {
							tokenValue = token.nextToken();
						}else tokenValue = "";
						
					}else{
						stack.push(tokenValue);
						if(token.hasMoreTokens()) tokenValue = token.nextToken();
					}

				}
				//printinf result
				while(!stack.isEmpty()){
					String result = stack.pop();
					StringTokenizer tk = new StringTokenizer(result.replace("*", ""),",");
					while(tk.hasMoreTokens()) System.out.println("doc"+tk.nextToken());

				}
			
			}
			
			
			
			
		}
		
		
		
	}
	
	public static String not_operation(String listOfDocOfThisTerm, Integer[] listDocOfIndex){
		Integer[] sList = listDocOfIndex.clone();
		StringTokenizer token = new StringTokenizer(listOfDocOfThisTerm.replace("*", ""),",");
		while(token.hasMoreTokens()){
			sList[Integer.parseInt(token.nextToken())-1] = 0;
		}
		String result = "*";
		for(int i=0;i<sList.length;i++){
			if(sList[i]==1) result += (i+1)+",";
		}

		return result.substring(0, result.length()-1);
	}
	
	public static String and_operation(String s1, String s2){
		
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		StringTokenizer token = new StringTokenizer(s1.replace("*", ""),",");
		
		String result ="*";
		while(token.hasMoreTokens()){
			hm.put(token.nextToken(), 0);
		}
		token = new StringTokenizer(s2.replace("*", ""),",");
		while(token.hasMoreTokens()){
			String tk = token.nextToken();
			if(hm.containsKey(tk)){
				result += tk+",";
			}
		}

		return result.substring(0, result.length()-1);
	}
	
	public static String or_operation(String s1, String s2){

		HashMap<String,Integer> hm = new HashMap<String,Integer>();
		StringTokenizer token = new StringTokenizer(s1.replace("*", ""),",");
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		String result ="*";
		while(token.hasMoreTokens()){
			hm.put(token.nextToken(), 0);
		}
		token = new StringTokenizer(s2.replace("*", ""),",");
		while(token.hasMoreTokens()){
			hm.put(token.nextToken(), 0);
		}
		
	    for(HashMap.Entry<String, Integer> entry : hm.entrySet()) {
	    	list.add(Integer.parseInt(entry.getKey()));
 		}
	    Collections.sort(list);
	    for(Integer i : list) result += i+",";
	   
		return result.substring(0, result.length()-1);
	}
	
	public static Integer[] listDocOfIndex(HashMap<String,String> hashMap){
		
		HashMap<Integer, Integer> docOfIndex = new HashMap<Integer, Integer>();
	     for(HashMap.Entry<String, String> entry : hashMap.entrySet()) {
	    	    String key = entry.getKey();
	    	    String value = entry.getValue();
	    	    StringTokenizer token = new StringTokenizer(value,",");
	    	    while(token.hasMoreTokens()){
		    	    docOfIndex.put(Integer.parseInt(token.nextToken()), 0);
	    	    }
	     }
	     Integer[] listDocOfIndex = new Integer[docOfIndex.size()];
	     String result = "";
	     for(HashMap.Entry<Integer, Integer> entry : docOfIndex.entrySet()) {
	    	 listDocOfIndex[entry.getKey()-1] = 1;
	     }

		
		return listDocOfIndex;
	}
	
	public static String heuristic(HashMap<String, String> hashMap, String term, char op){

		
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(term);
		while(token.hasMoreTokens()){
			String tokenValue = token.nextToken();
			if(tokenValue.charAt(0) != '*' ){
				if(hashMap.containsKey(tokenValue)){
					list.add(hashMap.get(tokenValue));
				}else{
					System.out.println("ERROR: Search term '"+tokenValue+"' is not in the Inverted Index file.");
					return "";
				}
			}else list.add(tokenValue);
		}
		
		
		Collections.sort(list, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {	
				int len1;
				int len2;
				if(o1.charAt(0) == '*') len1 = o1.length()-1;
				else len1 = o1.length();
				if(o2.charAt(0) == '*') len2 = o2.length()-1;
				else len2 = o2.length();
				return len1 - len2;
			}
		});
		
		while(list.size()>1){
			String result = "";
			if(op=='&') {
				result = and_operation(list.get(0), list.get(1));
			}else{
				result = or_operation(list.get(0), list.get(1));
			}
			list.remove(0);
			list.remove(0);
			list.add(result);
		}
		return list.get(0);
	}
	
	public static HashMap<String,String> genHashMapFromInvertedIndexFile(String indexFileName) throws IOException{
		HashMap<String, String> sCurrentHashMap = new HashMap<String, String>();
		//then make this inverted file to be a hash
		 BufferedReader br  = new BufferedReader(new FileReader(indexFileName+".txt"));
	     String sCurrentLine;
	     while ((sCurrentLine = br.readLine()) != null) {
	    	StringTokenizer st = new StringTokenizer(sCurrentLine,":");
	 		String sKey = st.nextToken();
	 		String sValue = st.nextToken();
	 		//put the valid value key=word, value=list of doc
	 		sKey = sKey.substring(0,sKey.indexOf(','));
	 		sValue = sValue.substring(1,sValue.length()-1);

	 		sCurrentHashMap.put(sKey, sValue);
	     }
	     

	     return sCurrentHashMap;
	}
	
	public static void save(String indexFileName) throws FileNotFoundException, UnsupportedEncodingException{
		
		//get the correct inverted index file that want to save
		HashMap<String, String> sCurrentHashMap = globalHash.get(indexFileName);
		
		PrintWriter writer = new PrintWriter(indexFileName+".txt", "UTF-8");

		Iterator it = sCurrentHashMap.entrySet().iterator();
		while(it.hasNext()){
			HashMap.Entry<String, String> entry = (HashMap.Entry<String, String>) it.next();
			//if this term appear more than one doc
			if(entry.getValue().length()>1) {
				StringTokenizer stDocID = new StringTokenizer(entry.getValue(),",");
				writer.println(entry.getKey()+","+stDocID.countTokens()+":<"+entry.getValue()+">");
			} else{
				writer.println(entry.getKey()+",1:<"+entry.getValue()+">");
			}
		}
		writer.close();
	}
	
	public static void create(String indexFileName, String listOfDoc){
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		ArrayList<Term> list = extractDoc(listOfDoc);
		 
		 hashMap = addListToHashMap(hashMap, list);		 
		//add to the global hashmap
		globalHash.put(indexFileName, hashMap);

	}
	
	public static void update(String indexFileName, String listOfDoc) throws IOException{
		//check if there's this fileName hashmap in globalHash
		HashMap<String, String> sCurrentHashMap = null;
		boolean hasFile = true;
		
		if(globalHash.containsKey(indexFileName)){
			//ถ้ามีใน global แล้ว เอาออกมาแล้วเอามารวมกับที่เพิ่งอ่านไฟล์ได้เลย
			sCurrentHashMap = globalHash.get(indexFileName);
		
		}else{ 
			//if no hashmap in globalHash, then check if there's this fileName.txt in directory
			
			if(!new File(indexFileName+".txt").isFile()){
					System.out.println("ERROR: The inverted file is not found.");
					hasFile = false;
			}else{
	    	    sCurrentHashMap = genHashMapFromInvertedIndexFile(indexFileName);
			     
			}
		}
		
		if(hasFile){
			ArrayList<Term> newTermList = extractDoc(listOfDoc);
			//if the new term list has value (not doc not found)
			if(newTermList.size()>0){
				
				
				sCurrentHashMap = addListToHashMap(sCurrentHashMap, newTermList);
				
				/*เพิ่มเงื่อนไขกรณี ลบคำออกจากไฟล์ 
				 * 1.ทำ list เป็น hashMap อีกอันไว้เซิชไวไว
				 * loop amount of doc
				 * 2.ไล่ iterative ของ hashMap ว่ามี value อันไหน contain ชื่อไฟล์บ้าง ถ้าcontain ให้เช็คกลับว่า key นั้น มีในแฮชอีกอันหรือเปล่า
				*/
				
				/* // Convert new word (from file) list to HashMap
				HashMap<String, String> newWordHashMap = new HashMap<String, String>();
				for(Term i: newTermList){
					newWordHashMap.put(i.getTerm(), i.getDocNumber()+"");
				}
				
				
				Iterator it = sCurrentHashMap.entrySet().iterator();
				while(it.hasNext()){
					HashMap.Entry<String, String> entry = (HashMap.Entry<String, String>) it.next();
					//if its value contains the doc number, then check if its key is in the newTermHash or note
					// if not --> this term was removed --> remove this key out from sCurrentHashMap
					if(entry.getValue().contains(s)) {
						
					}
				}
				*/
				
				globalHash.put(indexFileName, sCurrentHashMap);

				
			}
			
		}
		
		
		
		
	}
	
	public static HashMap<String, String> addListToHashMap(HashMap<String, String> hashMap, ArrayList<Term> list){
		/*วนลูปใน arrList เพื่อดูว่า ใน คำจาก doc ใหม่นั้น  มีใน hash หรือยัง
		 * ถ้่ามีแล้ว -> เช็คว่า ได้เก็บค่าชื่อของ doc หมายเลขนี้ หรือยัง 
		 * 		ถ้ายัง -> เก็บซพ
		 * 		ถ้าเก็บแล้ว -> Ignore 
		 * ถ้ายังไม่มี -> เติมเข้าไปเลย
		*/
		int position = 0;
		int listSize = list.size();
		while(position < listSize){

			
			String sListTerm = list.get(position).getTerm();
			//if this term is already in hashmap 
			if(hashMap.containsKey(sListTerm)){
				
				//then check if this doc number is already in or not
				String sHashDocList = hashMap.get(sListTerm);
				String sListDocNumber = list.get(position).getDocNumber()+"";

				
				if(!sHashDocList.contains(sListDocNumber)){
					
					//if this doc hasn't already in the hashDocList --> then add it
					StringTokenizer token = new StringTokenizer(sHashDocList,",");
					int[] docListArr = new int[token.countTokens()+1];
					int p=0;
					while(token.hasMoreTokens()){
						docListArr[p++] = Integer.parseInt(token.nextToken());
					}
					docListArr[p] = Integer.parseInt(sListDocNumber);

					Arrays.sort(docListArr);
					String newHashDocList = "";

					for(int element: docListArr){
						newHashDocList += element+",";
					}
					hashMap.put(sListTerm, newHashDocList.substring(0,newHashDocList.length()-1));
				}
				
			}else{
				////if this term is not already in hashmap

				hashMap.put(sListTerm, list.get(position).getDocNumber()+"");
			}
			position++;
		}
		
		return sortHashMapByComparator(hashMap);
	}
	
	public static ArrayList<Term> extractDoc(String listOfDoc){
	
		ArrayList<Term> list = new ArrayList<Term>();
		StringTokenizer st = new StringTokenizer(listOfDoc);
		boolean hasFile = true;
		while(st.hasMoreTokens() && hasFile){ //loop for reading each file. if there's no file --> display error
			if(!new File("IMDB/"+st.nextToken()+".txt").isFile()){
				hasFile = false;
				System.out.println("ERROR: File not found.");
				return new ArrayList<Term>();
			}
		}
		st = new StringTokenizer(listOfDoc);
		while(st.hasMoreTokens()){ //loop for reading each file
			
			try{
		         // open input stream test.txt for reading the file.
				String readingFileName = st.nextToken();
				int fileNumber = Integer.parseInt(readingFileName.substring(3));

				FileReader file = new FileReader("IMDB/"+readingFileName+".txt");
		        BufferedReader br  = new BufferedReader(file);
		        String sCurrentLine;
		         
		         //loop for every line (see the whole file)
		         while ((sCurrentLine = br.readLine()) != null) {
		        	 StringTokenizer stOfLine = new StringTokenizer(sCurrentLine," .,/\\*+:;\"()[]{}<>$%#@!&|?-~");
		        	 //loop for every word in the line
		        	 while(stOfLine.hasMoreTokens()){
		        		 String sCurrentWord = stOfLine.nextToken().toLowerCase();
		        		 
		        		 //check if that word is stop word or not?
		        		 sCurrentWord = stopWordEliminator(sCurrentWord);
		        		 
		        		 //check if it's not stopword and it has ' (apostophee)
		        		 if(!sCurrentWord.equals("") && sCurrentWord.contains("'")){
		        			StringTokenizer st2 = new StringTokenizer(sCurrentWord,"'");
		        			
		        			while(st2.hasMoreTokens()){
		        				list.add(new Term(st2.nextToken(), fileNumber));
		        			}

		        		 //if word is not stopword
		        		 }else if(!sCurrentWord.equals("")){
		        			 list.add(new Term(sCurrentWord, fileNumber));
		        		 }
		        	 }
		         }
		         file.close();
		         br.close();
		         
		      }catch(Exception e){
		         e.printStackTrace();
		      }
			
		}

		Collections.sort(list, new Term());
		return list;
		
	}
	
	public static HashMap<String, String> sortHashMapByComparator(HashMap<String, String> unsortMap) {

		// Convert Map to List
		List<HashMap.Entry<String, String>> list = 
			new LinkedList<HashMap.Entry<String, String>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<HashMap.Entry<String, String>>() {
			public int compare(HashMap.Entry<String, String> o1,
					HashMap.Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		// Convert sorted map back to a Map
		HashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Iterator<HashMap.Entry<String, String>> it = list.iterator(); it.hasNext();) {
			HashMap.Entry<String, String> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
		
	public static String stopWordEliminator(String str) throws IOException{
        BufferedReader br  = new BufferedReader(new FileReader("stopwords.txt"));
        String sCurrentLine;
        
        //loop for every line (see the whole file) && stop when 1st char of stopword list is bigger
        while (((sCurrentLine = br.readLine()) != null )&& (str.charAt(0) >= sCurrentLine.charAt(0))) {
        	if(str.equals(sCurrentLine)) {
        		return "";
        	}
        }
        return str;

	}

}
