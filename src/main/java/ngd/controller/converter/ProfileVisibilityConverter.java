package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.EProfileVisibility;


public class ProfileVisibilityConverter implements Converter<String, EProfileVisibility>{

	@Override
    public EProfileVisibility convert(String val) {
        return EProfileVisibility.getObject(Integer.parseInt(val));
    }
}