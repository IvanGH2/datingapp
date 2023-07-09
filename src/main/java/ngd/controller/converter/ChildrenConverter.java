package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.EChildrenStatus;



public class ChildrenConverter implements Converter<String, EChildrenStatus>{

	@Override
    public EChildrenStatus convert(String val) {
        return EChildrenStatus.getObject(Integer.parseInt(val));
    }
}
