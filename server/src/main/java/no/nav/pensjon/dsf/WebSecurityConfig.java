package no.nav.pensjon.dsf;

import no.nav.pensjon.dsf.auth.abac.AbacSecurityExpressionHandler;
import no.nav.pensjon.dsf.auth.jwt.JwtAuthenticationProcessingFilter;
import no.nav.pensjon.dsf.auth.jwt.JwtAuthenticationProvider;
import no.nav.pensjon.dsf.auth.ldap.LdapAuthenticationProcessingFilter;
import no.nav.pensjon.dsf.auth.ldap.LdapAuthenticationSuccessHandler;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private ActiveDirectoryLdapAuthenticationProvider ldapProvider;

    private JwtAuthenticationProvider jwtProvider;

    private LdapAuthenticationSuccessHandler ldapSuccessHandler;

    private CounterService counterService;

    private AbacSecurityExpressionHandler expressionHandler;

    public WebSecurityConfig(ActiveDirectoryLdapAuthenticationProvider ldapProvider, JwtAuthenticationProvider jwtProvider,
                             LdapAuthenticationSuccessHandler ldapSuccessHandler, AbacSecurityExpressionHandler expressionHandler,
                             CounterService counterService) {
        this.ldapProvider = ldapProvider;
        this.jwtProvider = jwtProvider;
        this.ldapSuccessHandler = ldapSuccessHandler;
        this.expressionHandler = expressionHandler;
        this.counterService = counterService;
    }

    private LdapAuthenticationProcessingFilter ldapAuthenticationProcessingFilter() throws Exception {
        LdapAuthenticationProcessingFilter filter = new LdapAuthenticationProcessingFilter(
                new AntPathRequestMatcher("/api/login", "POST"),
                counterService
        );

        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(ldapSuccessHandler);

        return filter;
    }

    private JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() throws Exception {
        JwtAuthenticationProcessingFilter filter = new JwtAuthenticationProcessingFilter(
                new AntPathRequestMatcher("/api/**"),
                counterService
        );

        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                    .expressionHandler(expressionHandler)
                    .antMatchers("/api/person/{fnr}").access("harTilgangTilPerson(#fnr)")
                    .anyRequest().denyAll().and()
                .addFilterBefore(ldapAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapProvider);
        auth.authenticationProvider(jwtProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/", "public/**", "/api/internal/**", "/metrics");

    }
}
