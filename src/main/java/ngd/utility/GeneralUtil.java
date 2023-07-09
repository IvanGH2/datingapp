package ngd.utility;

import java.util.List;

public class GeneralUtil {
	
		
	public static String concatFromList(List<String> items, String separator) {
		
		String result="";
		for(String item : items)
			result = result + separator + item;
		
		return result;
	}

}
