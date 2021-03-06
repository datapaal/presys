package no.nav.autoconfigure.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.util.StringUtils.hasText;

/**
 * Creates a LdapAuthenticationProvider bean compatible with Active Directory
 * and other LDAP compliant directory servers.
 * <p>
 * Filter-based searches are depending on a service username and password, and
 * will only be enabled if they are provided.
 */
@Configuration
@EnableConfigurationProperties(LdapProviderProperties.class)
public class LdapProviderAutoConfiguration {

    private final LdapProviderProperties providerProperties;
    private final NavLdapProperties ldapProperties;

    public LdapProviderAutoConfiguration(LdapProviderProperties providerProperties, NavLdapProperties ldapProperties) {
        this.providerProperties = providerProperties;
        this.ldapProperties = ldapProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper();
    }

    /**
     * Exposes the LdapTemplate so that the LdapHealthIndicator (Spring Actuator) is auto-configured
     */
    @Bean
    public LdapOperations ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public LdapAuthenticationProvider ldapAuthenticationProvider(UserDetailsContextMapper userDetailsContextMapper,
                                                                 LdapAuthenticator authenticator,
                                                                 LdapAuthoritiesPopulator populator) {
        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(authenticator, populator);
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsContextMapper(userDetailsContextMapper);
        return provider;
    }

    @Bean
    public LdapAuthenticator ldapAuthenticator(ContextSource contextSource) {
        LdapContextSource ldapContextSource = (LdapContextSource) contextSource;
        BindAuthenticator authenticator = new BindAuthenticator(ldapContextSource);
        String userDnPattern = providerProperties.getUserDnPattern();

        if (hasText(userDnPattern)) {
            authenticator.setUserDnPatterns(new String[]{userDnPattern});
        }

        if (credentialsSpecified()) {
            authenticator.setUserSearch(newFilterBasedLdapUserSearch(ldapContextSource));
        }

        return authenticator;
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator(ContextSource contextSource) {
        return credentialsSpecified()
                ? newLdapAuthoritiesPopulator(contextSource)
                : newMemberOfAttributeAuthoritiesPopulator();
    }

    private boolean credentialsSpecified() {
        return hasText(ldapProperties.getUsername()) && hasText(ldapProperties.getPassword());
    }

    private FilterBasedLdapUserSearch newFilterBasedLdapUserSearch(LdapContextSource contextSource) {
        return new FilterBasedLdapUserSearch(
                providerProperties.getUserSearchBase(),
                providerProperties.getUserSearchFilter(),
                contextSource);
    }

    private LdapAuthoritiesPopulator newLdapAuthoritiesPopulator(ContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator populator = new DefaultLdapAuthoritiesPopulator(
                contextSource, providerProperties.getGroupSearchBase());

        populator.setGroupSearchFilter(providerProperties.getGroupSearchFilter());
        populator.setSearchSubtree(true);
        populator.setRolePrefix(providerProperties.getRolePrefix());
        populator.setConvertToUpperCase(providerProperties.isConvertRoleToUpperCase());
        return populator;
    }

    private MemberOfAttributeAuthoritiesPopulator newMemberOfAttributeAuthoritiesPopulator() {
        return new MemberOfAttributeAuthoritiesPopulator(
                providerProperties.getRolePrefix(),
                providerProperties.isConvertRoleToUpperCase());
    }

    private static class MemberOfAttributeAuthoritiesPopulator implements LdapAuthoritiesPopulator {

        private static final Log LOG = LogFactory.getLog(MemberOfAttributeAuthoritiesPopulator.class);
        private final String rolePrefix;
        private final boolean convertToUpperCase;

        MemberOfAttributeAuthoritiesPopulator(String rolePrefix, boolean convertToUpperCase) {
            this.rolePrefix = rolePrefix;
            this.convertToUpperCase = convertToUpperCase;
        }

        @Override
        public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
            String[] groups = userData.getStringAttributes("memberOf");

            if (groups == null) {
                LOG.debug("No values for 'memberOf' attribute.");
                return AuthorityUtils.NO_AUTHORITIES;
            }

            LOG.debug("'memberOf' attribute values: " + Arrays.asList(groups));
            ArrayList<GrantedAuthority> authorities = new ArrayList<>(groups.length);

            for (String group : groups) {
                authorities.add(newSimpleGrantedAuthority(group));
            }

            return authorities;
        }

        private SimpleGrantedAuthority newSimpleGrantedAuthority(String group) {
            LdapName name = LdapUtils.newLdapName(group);
            String role = name.getRdn(name.size() - 1).getValue().toString();

            if (convertToUpperCase) {
                role = role.toUpperCase();
            }

            return new SimpleGrantedAuthority(rolePrefix + role);
        }
    }
}
