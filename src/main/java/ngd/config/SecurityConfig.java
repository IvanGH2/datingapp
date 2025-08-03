package ngd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import ngd.signin.LoginHandler;
import ngd.signin.LogoutHandler;
import ngd.signin.LogoutSuccessHandler;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserInfoService userInfoService;

	@Override
	protected void configure(HttpSecurity pHttp) throws Exception {
		
		pHttp
			.authorizeRequests()
				.antMatchers("/webjars/**").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/images/**").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/register/**").permitAll()
				.antMatchers("/processRegisterInfo/**").permitAll()
				.antMatchers("/entry/**").permitAll()
				.antMatchers("/displayProfile/**").permitAll()
				//.antMatchers("/ping").permitAll()
				.antMatchers("/letsclick/**").permitAll()
				.anyRequest().fullyAuthenticated()
				
			.and()
				.formLogin()
					.loginPage("/letsclick")					
					.permitAll()
					.successHandler(getLoginHandler())
					//.failureUrl("/loginFailed")
			.and()
				.logout()
					.addLogoutHandler(getLogoutHandler())
					.logoutSuccessHandler(getLogoutSuccessHandler())
			.and()
				.sessionManagement()
					.maximumSessions(1)
			.and().and()
				.logout()
					.invalidateHttpSession(true)
					.logoutSuccessUrl("/")
					
			;

	}
	@Bean
	org.springframework.security.web.authentication.logout.LogoutHandler getLogoutHandler() {
		return new LogoutHandler();
	}
	@Bean
	LogoutSuccessHandler getLogoutSuccessHandler() {
		return new LogoutSuccessHandler();
	}
	@Bean
	LoginHandler getLoginHandler() {
		return new LoginHandler();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		auth
			.userDetailsService(this.userInfoService)
			.passwordEncoder(passwordEncoder());
		
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() throws Exception {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HttpFirewall httpFirewall() {
		StrictHttpFirewall sfw = new StrictHttpFirewall();
		sfw.setAllowSemicolon(true);

		return sfw;
	}

}



