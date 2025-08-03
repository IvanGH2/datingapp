package ngd.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import ngd.controller.converter.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${ngd.ext-resource.location}")
	private String extResource;
	
	@Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new BodyTypeConverter());
        registry.addConverter(new EduLevelConverter());
        registry.addConverter(new PersonalityConverter());
        registry.addConverter(new RelStatusConverter());
        registry.addConverter(new EmploymentConverter());
        registry.addConverter(new ChildrenConverter());
        registry.addConverter(new ProfileVisibilityConverter());
    }
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		
		final String[] RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/", "classpath:/resources/",
				"classpath:/static/", "classpath:/static/user/images/" };
		registry.addResourceHandler("/**")
		.addResourceLocations(RESOURCE_LOCATIONS)	
		.addResourceLocations(extResource)
		.setCachePeriod(60 * 60)
		.resourceChain(true);
	}	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(localeChangeInterceptor());
	}
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver lr = new SessionLocaleResolver();
		lr.setDefaultLocale(Locale.forLanguageTag("en"));
		return lr;
	}
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:static/lang/message");
		messageSource.setDefaultEncoding("UTF-8");
		//messageSource.setCacheSeconds(60 * 60);
		return messageSource;
	}
}
