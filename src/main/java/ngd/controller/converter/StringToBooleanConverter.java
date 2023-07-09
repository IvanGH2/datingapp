package ngd.controller.converter;

import org.springframework.core.convert.converter.Converter;



public class StringToBooleanConverter  implements Converter<String, Boolean>{

	@Override
    public Boolean convert(String val) {
        return Boolean.valueOf(val);
    }
}
