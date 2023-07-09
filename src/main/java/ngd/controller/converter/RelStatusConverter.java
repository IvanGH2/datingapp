package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;

import ngd.model.status.EUserProfile.ERelationshipStatus;;

public class RelStatusConverter implements Converter<String, ERelationshipStatus>{

	@Override
    public ERelationshipStatus convert(String val) {
        return ERelationshipStatus.getObject(Integer.parseInt(val));
    }
}
