package ngd.controller.converter;



import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.EPersonality;;

public class PersonalityConverter implements Converter<String, EPersonality>{

	@Override
    public EPersonality convert(String val) {
        return EPersonality.getObject(Integer.parseInt(val));
    }

}
