package ngd.utility;

import ngd.model.status.EUserProfile.EPersonality;

public class StringProcessor {
	
	public static String convertToLocalPersonalityNames(String personalityTypes) {
		
		String localNames = "" ;
		if(personalityTypes == null) return null;
		final String[] types = personalityTypes.substring(1,personalityTypes.length()-1).split(", ");
		for(String t : types)
			localNames = localNames + EPersonality.getLocalString(Integer.parseInt(t)) + ", ";
		return localNames.substring(0, localNames.length()-2);
	}

}
