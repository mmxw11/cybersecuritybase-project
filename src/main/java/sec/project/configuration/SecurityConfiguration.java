package sec.project.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // TODO: test data
    // xxs (message),
    // injection (login),
    // access controler (other accounts, adminpanel),
    // Security Misconfiguration (admin, admin account),
    // Sensitive Data Exposure passwords plaintext
    // Broken Authentication session ids
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity sec) throws Exception {
        // H2-console.
        sec.csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();
        // sec.csrf().disable();
        sec.authorizeRequests()
                // H2-console
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                // Resources.
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()
                // Sign up.
                /**  .antMatchers(HttpMethod.GET, "/login").permitAll()
                  .antMatchers(HttpMethod.GET, "/sign-up").permitAll()*/
                // Only uses who are NOT authenticated can access the processing endpoints.
                /**  .antMatchers(HttpMethod.POST, "/api/auth/sign-up").hasRole("ANONYMOUS")
                  .antMatchers(HttpMethod.POST, "/api/auth/login").hasRole("ANONYMOUS")*/
                // Require login for everything else.
                .anyRequest().authenticated()
                .and()
                // Login.
                .formLogin()/**
                            .loginPage("/login")
                            .failureUrl("/login?fail=true")
                            .loginProcessingUrl("/api/auth/login")*/
                .defaultSuccessUrl("/", true)
                // Logout.
                .and()
                .logout().permitAll()
        /**.logoutUrl("/api/auth/logout")
        .logoutSuccessUrl("/login")*/
        ;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
