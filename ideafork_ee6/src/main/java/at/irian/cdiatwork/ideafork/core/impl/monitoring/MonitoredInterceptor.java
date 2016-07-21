package at.irian.cdiatwork.ideafork.core.impl.monitoring;

import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import org.apache.deltaspike.core.util.AnnotationUtils;
import org.apache.deltaspike.core.util.ProxyUtils;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

@Monitored
@Interceptor
public class MonitoredInterceptor implements Serializable {
    private static final long serialVersionUID = 3503111146284126952L;

    @Inject
    private MonitoredStorage monitoredStorage;

    @Inject
    private MonitoringConfig monitoringConfig;

    @Inject
    private BeanManager beanManager;

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Exception {
        long start = System.currentTimeMillis();

        try {
            return ic.proceed();
        } finally {
            Monitored monitored = extractMonitoredAnnotation(ic);
            int maxThreshold = monitored.maxThreshold();

            if (maxThreshold < 1) {
                maxThreshold = this.monitoringConfig.methodInvocationThreshold();
            }

            if (isSlowInvocation(start, maxThreshold)) {
                this.monitoredStorage.recordSlowMethod(ic.getTarget().getClass().getName() + "#" + ic.getMethod().getName());
            }
        }
    }

    private Monitored extractMonitoredAnnotation(InvocationContext ic) {
        Monitored result = ic.getMethod().getAnnotation(Monitored.class);

        if (result != null) {
            return result;
        }

        Class<?> targetClass = ic.getTarget().getClass();

        targetClass = ProxyUtils.getUnproxiedClass(targetClass);

        result = targetClass.getAnnotation(Monitored.class);

        if (result == null) {
            return AnnotationUtils.findAnnotation(beanManager, targetClass.getAnnotations(), Monitored.class);
        }

        return result;
    }

    protected boolean isSlowInvocation(long start, int maxThreshold) {
        return System.currentTimeMillis() - start > maxThreshold;
    }
}
