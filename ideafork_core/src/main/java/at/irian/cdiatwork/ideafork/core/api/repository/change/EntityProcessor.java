package at.irian.cdiatwork.ideafork.core.api.repository.change;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding

@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface EntityProcessor {
}
