package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.EEmploymentStatus;

public class EmploymentConverter implements Converter<String, EEmploymentStatus>{

	@Override
    public EEmploymentStatus convert(String val) {
        return EEmploymentStatus.getObject(Integer.parseInt(val));
    }

}
