package com.hhub.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private DataSource dataSource;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//            .anyRequest()
//            .permitAll()
//            .and().csrf().disable();
    	
    	
    	
    	http.
        authorizeRequests()
        .antMatchers("/add_blog_post/**").hasAuthority("EDITOR")
        .antMatchers("/login").permitAll()
        .antMatchers("/registration").permitAll()
        .antMatchers("/add_user/**","/user_list/**")
        .hasAuthority("ADMIN").anyRequest()
        .authenticated().and().csrf().disable().formLogin()
        .loginPage("/login").failureUrl("/login?error=true")
        .defaultSuccessUrl("/")
        .usernameParameter("email")
        .passwordParameter("password")
        .and().logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/login").and().exceptionHandling()
        .accessDeniedPage("/access-denied");
   
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
		    web
		    .ignoring()
		    .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/images/**", "/webjars/**");
		    
		    
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.jdbcAuthentication()
	        .usersByUsernameQuery(usersQuery)
	        .authoritiesByUsernameQuery(rolesQuery)
	        .dataSource(dataSource)
	        .passwordEncoder(bCryptPasswordEncoder);
    }
}
