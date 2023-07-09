package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.EBodyType;

public class BodyTypeConverter implements Converter<String, EBodyType>{

	@Override
    public EBodyType convert(String val) {
		
        return EBodyType.getObject(Integer.parseInt(val));
    }
}
