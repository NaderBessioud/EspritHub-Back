package tn.esprithub.Security;

import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import tn.esprithub.Entities.User;
import tn.esprithub.Services.UserService;



@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	
	@Autowired
	private UserService service;
	


	@Autowired
	DataSource dataSource;
	
	@Autowired
	private CustomAuthenticationFilter authenticationFilter;
	

	private static final Logger logger = LogManager.getLogger(SecurityConfiguration.class);
	
	
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		
	auth.userDetailsService(getDetailsService()).passwordEncoder(getPasswordEncoder());
	
		
		
	
	}
	@Bean
	public UserDetailsService getDetailsService() {
	return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User user=service.findByUserName(username); 
				System.out.println(user == null);
				Collection<SimpleGrantedAuthority> authorites=new ArrayList<>();
				authorites.add(new SimpleGrantedAuthority(user.getRole().toString()));

				return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorites);

			}
		};
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
	
		return new BCryptPasswordEncoder();	
	}
	
	@Bean
	public JWTAuthenticationEntryPoint getAuthenticationEntryPoint() {
		return new JWTAuthenticationEntryPoint();
	}
	

	
	@Override
	@Bean
	
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().
		authorizeRequests()
		
		
		
		
		
		.antMatchers("/**").permitAll()
		.anyRequest().authenticated()
		.and().exceptionHandling().authenticationEntryPoint(getAuthenticationEntryPoint());
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(authenticationFilter,UsernamePasswordAuthenticationFilter.class);
		
		
		
		}
	
	
	
}

