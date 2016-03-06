package acronexpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AcronExpan {
	
	static int d;
	
	private static final String[] lowNames = {
		   "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
		   "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};

	private static final String[] tensNames = {
	   "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

	private static final String[] bigNames = {
	   "thousand", "million", "billion"};
	
	public static void main(String argv[]) {
		
		int idQRY = 0;
		StringBuffer sb = null;
				
		try {
	 
			String[] linePieces;
			
			File fXmlFile = new File("./acron_collection_UTF8.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			doc.getDocumentElement().normalize();
		 		 
			NodeList nList = doc.getElementsByTagName("record");
		 			
			List<List<String>> reclist = new ArrayList<List<String>>();
	
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
				
				List<String> reclines = new ArrayList<String>();				
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
					
					idQRY = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent());
					
					reclines.add(eElement.getElementsByTagName("title").item(0).getTextContent());

					String line = eElement.getElementsByTagName("text").item(0).getTextContent().replaceAll("[?]", ""); 
					
					BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
					iterator.setText(line);
					int start = iterator.first();
					for (int end = iterator.next();
					    end != BreakIterator.DONE;
					    start = end, end = iterator.next()) {
						reclines.add(eElement.getElementsByTagName("text").item(0).getTextContent().substring(start,end));					
					}
					reclist.add(reclines);
				}
			}

			//------------TOKENIZATION---------------------------------
			
			List<String> acronuma = new ArrayList<String>();
			List<String> expan = new ArrayList<String>();
			String[] strarray;
			
			Map<String, String> uniqueacrex = new HashMap<String, String>();
			Map<String, String> acrexall = new HashMap<String, String>();
			
			for (List<String> list : reclist)
			{
				int counter = 0;
			    for (String str : list)
			    {
			    	counter++;
			    				    	
			    	while(counter > 2 && (str.endsWith("\n") || str.endsWith(".")) ) {
			    		str = str.substring(0, str.length()-1);
			    	}
                    String pattern5 =("(^[A-Z]).*[A-Z]([:])([\\t\\n\\r])");
                    str=str.replaceAll(pattern5, "");
                                
			    	str = str.replaceAll("[:][[\t\n\r\f]$]", " ");
			    	str = str.replaceAll("[.][[\t\n\r\f]$]", " ");
			    	str = str.replaceAll("[;][[\t\n\r\f]$]", " ");
			    	str = str.replaceAll("[\t\n\r\f]", " ");
                                
			    	String pattern1 =("([\\(])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])");
			    	String pattern2 =("(\\w+)([\\-])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})"); // pattern gia na bgazei tin - apo 2 token endiamesa
			    	String pattern3 =("([\\(])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})(\\s)([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])");
			    	String pattern4 =("([\\(])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})(\\s)([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})(\\/)([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])");
                               
			    	str = str.replaceAll(pattern1, "$1$2$3"); 
			    	str=str.replaceAll(pattern2, "$1$2$3");
			    	str=str.replaceAll(pattern3, "$1$2$4$5");
			    	str=str.replaceAll(pattern4, "$1$2$4$5$6$7");
			    	
			    	Pattern punctuation = Pattern.compile("([\\(])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})(\\/)([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])|(\\w+)([\\-])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})|([\\(])(\\w+)(\\-)([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])|([\\(])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])|([\\(])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})([\\)])|(\\w+)([\\/])([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})|([a-zA-Z0-9][A-Z0-9][A-Za-z0-9]{0,7})([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})|[a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7}|(\\w+)([a-zA-Z0-9][A-Z][A-Za-z0-9]{0,7})"); //pattern
					Matcher matcher = punctuation.matcher(str);
					
			    	String[] tokens = str.split("[ ,?!:;]+");
			    	
					List<String> tokenlist = Arrays.asList(tokens);
			    						
					int parenthesis;
					
		    		for (int i = 0; i < tokenlist.size(); i++) 
		    		{
		    		    if (punctuation.matcher(tokenlist.get(i)).matches()) {
		    		    	
	    		    		String temp;
	    		    		if(tokenlist.get(i).substring(0, 1).equals("(")) {
	    		    			parenthesis = 1;
	    		    			temp = tokenlist.get(i).replaceAll("[(]", "");
	    		    			temp = temp.replaceAll("[)]", "");
	    		    			acronuma.add(temp);
	    		    		}
	    		    		else {
	    		    			parenthesis = 0;
	    		    			temp = tokenlist.get(i);
	    		    			acronuma.add(tokenlist.get(i));
	    		    		}
		    		    		
	    		    		List<String> tempexpan = new ArrayList<String>();
	    		    		tempexpan = expansion(tokenlist, i, parenthesis);
	    		    		String res = String.join(" ", tempexpan);
	    		    		expan.add(res);

		    		    }
  
		    		}
			    }
			    
			}
			
			for(int i = 0; i< acronuma.size();i++) {
				String key = acronuma.get(i);
				String value = expan.get(i);
				if (!uniqueacrex.containsKey(key)) {
					if(!value.isEmpty()) {
						uniqueacrex.put(key, value);
					}
				}
			}
			
			for(int i = 0; i< acronuma.size();i++) {
				String key = acronuma.get(i);
				String value = expan.get(i);
				if (!uniqueacrex.containsKey(key)) {
						uniqueacrex.put(key, value);
				}
			}
			
			try {
				FileOutputStream fileOut = new FileOutputStream("Acronym-Expansion.xls");
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("ExerciseFinal Worksheet");

				int rowIndex = 0;
				for(int i = 0; i< acronuma.size();i++) {
					int cellIndex = 0;
					Row row = sheet.createRow(rowIndex++);
					String key = acronuma.get(i);
					String value = expan.get(i);

					Cell cell1 = row.createCell(cellIndex++);
					cell1.setCellValue(key);
					Cell cell2 = row.createCell(cellIndex++);
					cell2.setCellValue(value);
				}
				
				for (int index = 0; index < sheet.getRow(0).getPhysicalNumberOfCells(); index++) {
					sheet.autoSizeColumn(index);
				}
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Excel Doc <Acronym-Expansion.xls>: Ready in source folder of the Project!!");
			try {
				FileOutputStream fileOut = new FileOutputStream("Unique-Acro-Expan.xls");
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("ExerciseFinal Worksheet");

				int rowIndex = 0;
				for(Map.Entry<String,String> entry : uniqueacrex.entrySet()) {
					int cellIndex =  0;
					Row row = sheet.createRow(rowIndex++);
					String key = entry.getKey();
					String value = entry.getValue();

					Cell cell1 = row.createCell(cellIndex++);
					cell1.setCellValue(key);
					Cell cell2 = row.createCell(cellIndex++);
					cell2.setCellValue(value);
				}
				
				for (int index = 0; index < sheet.getRow(0).getPhysicalNumberOfCells(); index++) {
					sheet.autoSizeColumn(index);
				}
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}

		    System.out.println("Excel Doc <Unique-Acro-Expan.xls>: Ready in source folder of the Project!!");
	    } 
		catch (Exception e) {
			e.printStackTrace();
	    }
	}
	
	public static List<String> expansion(List<String> tokenlist , int posacro, int parenthesis) {
		
		String numtex;
		int leftex,rightex,leftbound,rightbound,i,j,counter = 0,count = 0;
		boolean parenvalue = false;
		List<String> expanlist = new ArrayList<String>();
		String acronym,expan;
		String[] acroletters,expanletters;

		int size;
		int maxlength = Math.min(tokenlist.get(posacro).length() +5, tokenlist.get(posacro).length() * 2);
		
		if(parenthesis == 1) {
			acronym = tokenlist.get(posacro).replaceAll("[(]", "");
			acronym = acronym.replaceAll("[)]", "");
			acroletters = acronym.split("(?!^)");
			size = acroletters.length + 6;
			ArrayList<Integer> buildacro = new ArrayList<Integer>();
			List<String> misslet = new ArrayList<String>();
			
			if ( posacro > 0) {
				leftex = posacro - 1;
				if (posacro - size < 0) {
					leftbound = 0;
				}
				else {
					leftbound = posacro - size;
				}	
				for(j = acroletters.length -1; j >= 0; j--) {
					counter = 0;
					
					for(i = leftex; i >= leftbound; i--) {
						if(isInteger(acroletters[j].toLowerCase())) {
							numtex = convertNumberToWords(d);
							acroletters[j] = numtex.substring(0, 1).toLowerCase();
						}
						if(!(tokenlist.get(i).equals(""))) {
							
							if(j!= acroletters.length -1 && !(buildacro.isEmpty())) {
								
								if((acroletters[j].toLowerCase()).equals(tokenlist.get(i).substring(0, 1).toLowerCase()) ) {

									if(buildacro.get(buildacro.size() - 1) <= i ) {
									
										for(String mlet : misslet) {
											if(tokenlist.get(i).toLowerCase().contains(mlet.toLowerCase()) ) {
												counter++;
											}
											else
												break;
										}
										if(counter == misslet.size() && (!(buildacro.contains(i))) ) {
											buildacro.remove(buildacro.size() - 1);
											buildacro.add(i);
											misslet.clear();
										}
										
									}
							//		}
									else {
										buildacro.add(i);
										misslet.clear();
										break;
									}
								}
								else {
									if(!(misslet.contains(acroletters[j].toLowerCase()))) {
										misslet.add(acroletters[j].toLowerCase());
									}
								}
							}
							else {
								if((acroletters[j].toLowerCase()).equals(tokenlist.get(i).substring(0, 1).toLowerCase()) ) {
									buildacro.add(i);
									break;
								}
								else {
									if(!(misslet.contains(acroletters[j].toLowerCase()))) {
										misslet.add(acroletters[j].toLowerCase());
									}
								}
							}
						}
					}
					
				}
				if(misslet.isEmpty()) {
					expanlist = tokenlist.subList(buildacro.get(buildacro.size() - 1),buildacro.get(0) + 1);
				}
			}	
		}
		else {
			acroletters = tokenlist.get(posacro).split("(?!^)");
			List<String> misslet = new ArrayList<String>();
			size = acroletters.length + 6;
			if (posacro < (tokenlist.size() - 1)) {
				rightex = posacro + 1;
				if (posacro + size < tokenlist.size() ) {
					rightbound = posacro + size;
				}
				else {
					rightbound = tokenlist.size();
				}
				List<Integer> buildacro = new ArrayList<Integer>();
				for(j = 0; j < acroletters.length; j++) {
					counter = 0;
					for(i = rightex; i < rightbound; i++) {
						if(isInteger(acroletters[j].toLowerCase())) {
							numtex = convertNumberToWords(d);
							acroletters[j] = numtex.substring(0, 1).toLowerCase();
						}
						if(!(tokenlist.get(i).equals("")) && tokenlist.get(i).substring(0, 1).equals("(") && i == posacro + 1) {
							expan = tokenlist.get(i).replaceAll("[(]", "");
							if(!(tokenlist.get(i).equals("")) && (acroletters[j].toLowerCase()).equals(expan.substring(0, 1).toLowerCase()) ) {
								parenvalue = true;
								buildacro.add(i);
							}
							if(!(tokenlist.get(i).equals("")) && tokenlist.get(i).substring(tokenlist.get(i).length() - 1).equals(")") && parenvalue == true) {
								expan = tokenlist.get(i).substring(0, tokenlist.get(i).length()-1);
								if((acroletters[j].toLowerCase()).equals(expan.substring(0, 1).toLowerCase()) ) {
									count++;
								}
								if(count == acroletters.length) {
									parenvalue = false;
								}
							}
							else {
								rightex = rightex + 1;
							}
							break;
						}
						else if(!(tokenlist.get(i).equals("")) && tokenlist.get(i).substring(tokenlist.get(i).length() - 1).equals(")") && parenvalue == true && j == acroletters.length - 1) {
							expan = tokenlist.get(i).substring(0, tokenlist.get(i).length()-1);
							if(expan.contains(acroletters[j].toLowerCase()) ) {
								buildacro.add(i);
								break;
							}
							else {
								parenvalue = false;
								break;
							}
						}
					}
					if(parenvalue == false) {
						break;
					}
				}
				if(!(tokenlist.get(posacro + 1).equals("")) && tokenlist.get(posacro + 1).substring(0, 1).equals("(") && parenvalue == true) {
					expanlist = tokenlist.subList(buildacro.get(0),buildacro.get(buildacro.size() - 1) + 1);
				}
			}
			
		}
		return expanlist;
	}
	
	public static boolean isInteger( String input )
	{
		
		try  
		{  
			d = Integer.parseInt(input);  
			return true;
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;
		}  
	}
	
	public static String convertNumberToWords (int n) {
	   if (n < 0) {
	      return "minus " + convertNumberToWords(-n); }
	   if (n <= 999) {
	      return convert999(n); }
	   String s = null;
	   int t = 0;
	   while (n > 0) {
	      if (n % 1000 != 0) {
	         String s2 = convert999(n % 1000);
	         if (t > 0) {
	            s2 = s2 + " " + bigNames[t-1]; }
	         if (s == null) {
	            s = s2; }
	          else {
	            s = s2 + ", " + s; }}
	      n /= 1000;
	      t++; }
	   return s; 
	}

	// Range 0 to 999.
	private static String convert999 (int n) {
	   String s1 = lowNames[n / 100] + " hundred";
	   String s2 = convert99(n % 100);
	   if (n <= 99) {
	      return s2; }
	    else if (n % 100 == 0) {
	      return s1; }
	    else {
	      return s1 + " " + s2; }
	}

	// Range 0 to 99.
	private static String convert99 (int n) {
	   if (n < 20) {
	      return lowNames[n]; }
	   String s = tensNames[n / 10 - 2];
	   if (n % 10 == 0) {
	      return s; }
	   return s + "-" + lowNames[n % 10]; 
	}
	
}


class ValueComparator implements Comparator<String> {
	 
    Map<String, Integer> map;
 
    public ValueComparator(Map<String, Integer> base) {
        this.map = base;
    }
 
    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}