package no.nav.abac.spring.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.abac.core.annotation.AbacMethodInterceptor;
import no.nav.abac.core.annotation.attribute.AbacAttributeLocator;
import no.nav.abac.core.annotation.attribute.AbacAttributePopulator;
import no.nav.abac.core.annotation.attribute.AbacAttributePopulatorImpl;
import no.nav.abac.core.annotation.context.AbacContext;
import no.nav.abac.core.annotation.context.ThreadLocalAbacContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AbacAnnotationConfig {

    @Autowired(required = false)
    private List<AbacAttributeLocator> abacAttributeLocators = new ArrayList<>();

    @Autowired(required = false)
    @Qualifier("abacDefaultResources")
    private Set<String> defaultResources = new HashSet<>();

    @Autowired(required = false)
    @Qualifier("abacDefaultSubjects")
    private Set<String> defaultSubjects = new HashSet<>();

    @Autowired(required = false)
    @Qualifier("abacDefaultActions")
    private Set<String> defaultActions = new HashSet<>();

    @Autowired(required = false)
    @Qualifier("abacDefaultEnvironment")
    private Set<String> defaultEnvironments = new HashSet<>();

    @Bean
    AbacAttributePopulator abacAttributePopulator() {
        return new AbacAttributePopulatorImpl(abacAttributeLocators, defaultResources, defaultSubjects, defaultActions, defaultEnvironments);
    }

    @Bean
    AbacMethodInterceptor abacMethodInterceptor(AbacContext context, AbacAttributePopulator populator) {
        return new AbacMethodInterceptor(context, populator);
    }

    @Bean
    AbacAdvisor abacAdvisor(AbacMethodInterceptor abacMethodInterceptor) {
        return new AbacAdvisor(abacMethodInterceptor);
    }

    @Bean
    AbacContext abacContext() {
        return new ThreadLocalAbacContext();
    }
}
