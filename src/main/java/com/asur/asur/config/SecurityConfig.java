package com.asur.asur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import com.asur.asur.models.User;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    private static final String[] PUBLIC_MATCHERS = {
        "/webjars/**",
        "/css/**",
        "/js/**",
        "/img/**"
};


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     /*    http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE)
                .hasRole("ADMIN")
                .antMatchers("/admin")
                .hasAnyRole("ADMIN")
                .antMatchers("/user")
                .hasAnyRole("USER", "ADMIN")
                .antMatchers("/login")
                .anonymous()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();*/



        http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers(PUBLIC_MATCHERS).permitAll()
                .requestMatchers("/robot").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/diagnostics").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/index").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/put-file").hasAnyRole("USER", "ADMIN")
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
                .defaultSuccessUrl("/index", true)
				.permitAll()
			)
			.logout((logout) -> logout
				.logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout"))
				.logoutSuccessUrl("/login")
			);
            //exceptionHandling().accessDeniedHandler(accessDeniedHandler);
		return http.build();
    }

   /*   @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(securityDebug)
                .ignoring()
                .antMatchers("/static/**","/resources/**","/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico", 
                "/bootstrap/js/**",  "/bootstrap/css/**");
    }
*/
}
