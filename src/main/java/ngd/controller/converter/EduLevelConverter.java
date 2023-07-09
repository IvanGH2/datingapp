package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.EEducationLevel;

public class EduLevelConverter implements Converter<String, EEducationLevel>{

	@Override
    public EEducationLevel convert(String val) {
        return EEducationLevel.getObject(Integer.parseInt(val));
    }
}
