package no.nav.pensjon.presys.metrics.aspects;

import no.nav.pensjon.presys.metrics.MetodeEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static no.nav.pensjon.presys.metrics.aspects.AspectUtil.*;

/**
 * HOWTO:
 * - aspectjweaver som en runtime dependency
 * - @EnableAspectJAutoProxy i Spring-config
 * - Gjør CountAspect tilgjengelig som en Spring-bean
 * - Sørg for at klassen der du bruker @Count er managed av Spring
 * <p>
 * Kan brukes f. eks. slik:
 * <pre>
 *
 * {@literal @}Count
 * public void methodname() {
 *
 * }
 *
 * eller slik:
 *
 * {@literal @}Count(name = "eventnavn")
 * public void methodname() {
 *
 * }
 *
 * eller om du vil ha med et field i rapporten, slik:
 *
 * {@literal @}Count(name = "eventnavn", fields = {{@literal @}Field(key = "orgnummer", argumentNumber = "2")})
 * public void methodname(String id, String orgnummer) {
 * }
 * </pre>
 */
@Aspect
@Component
public class CountAspect {

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && @annotation(count)")
    public Object countPaMetode(ProceedingJoinPoint joinPoint, Count count) throws Throwable {
        AspectMetodekall metodekall = new AspectMetodekall(joinPoint);
        boolean logMethodAsUniqueMeasurement = count.logMethodAsUniqueMeasurement();
        String eventNavn = lagMetodeTimernavn(joinPoint, count.name(), logMethodAsUniqueMeasurement);
        Map<String, String> felt = finnArgumentVerdier(joinPoint, count);
        Map<String, String> tags = new HashMap<>();
        if(!logMethodAsUniqueMeasurement) {
            tags = leggPaaMetodeNavnSomTag(joinPoint, tags);
        }

        return MetodeEvent.eventForMetode(metodekall, eventNavn, felt, tags);
    }

    @Around("publicMethod() && @within(count)")
    public Object countPaKlasse(ProceedingJoinPoint joinPoint, Count count) throws Throwable {
        if (metodeSkalIgnoreres(getMetodenavn(joinPoint), count.ignoredMethods())) {
            return joinPoint.proceed();
        }

        boolean logMethodAsUniqueMeasurement = count.logMethodAsUniqueMeasurement();

        AspectMetodekall metodekall = new AspectMetodekall(joinPoint);
        String eventNavn = lagKlasseTimernavn(joinPoint, count.name(), logMethodAsUniqueMeasurement);

        Map<String, String> tags = new HashMap<>();
        if(!logMethodAsUniqueMeasurement) {
            tags = leggPaaMetodeNavnSomTag(joinPoint, tags);
        }

        return MetodeEvent.eventForMetode(metodekall, eventNavn, null, tags);
    }

    private Map<String, String> finnArgumentVerdier(JoinPoint joinPoint, Count count) {
        if (count.fields().length == 0) {
            return null;
        }

        Map<String, String> verdier = new HashMap<>();

        Object[] args = joinPoint.getArgs();

        for (Field field : count.fields()) {
            String value = args[parseInt(field.argumentNumber()) - 1].toString();
            verdier.put(field.key(), value);
        }

        return verdier;
    }
}
