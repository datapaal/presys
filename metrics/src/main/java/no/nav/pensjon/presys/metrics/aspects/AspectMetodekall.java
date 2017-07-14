package no.nav.pensjon.presys.metrics.aspects;


import no.nav.pensjon.presys.metrics.Metodekall;
import org.aspectj.lang.ProceedingJoinPoint;

class AspectMetodekall implements Metodekall {

    private final ProceedingJoinPoint joinPoint;

    AspectMetodekall(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    @Override
    public Object kallMetode() throws Throwable {
        return joinPoint.proceed();
    }
}
