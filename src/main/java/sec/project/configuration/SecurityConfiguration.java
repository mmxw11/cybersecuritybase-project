package sec.project.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * App security configuration.
 * Code that is commented out is a secure version.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /** @Autowired
    private UserDetailsService userDetailsService;
    */
    @Autowired
    private DbAuthenticationProvider authProvider;

    @Override
    protected void configure(HttpSecurity sec) throws Exception {
        // H2-console.
        sec.csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();
        // sec.csrf().disable();
        sec.authorizeRequests()
                // H2-console This is automatically disabled in production environment by Spring,
                // therefore this is not a vulnerability.
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                // Resources.
                .antMatchers(HttpMethod.GET, "/static/**").permitAll()
                // Login
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                // Only uses who are NOT authenticated can access the processing endpoint.
                .antMatchers(HttpMethod.POST, "/login").hasRole("ANONYMOUS")
                // Require login for everything else.
                .anyRequest().authenticated()
                .and()
                // Login form.
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/login")
                .failureUrl("/login?error")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                // Logout.
                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/login?logout");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
        /** .userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());*/
    }
    /**
     * @Bean 
     * public PasswordEncoder passwordEncoder() { 
     *     return new BCryptPasswordEncoder();
     * }
     */
}
