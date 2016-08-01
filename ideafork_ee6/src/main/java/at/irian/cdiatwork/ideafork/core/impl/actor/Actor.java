package at.irian.cdiatwork.ideafork.core.impl.actor;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface Actor {
    String AKKA_DEFAULT = "default";

    @Nonbinding
    Class<? extends akka.actor.Actor> type();

    @Nonbinding
    String systemName() default AKKA_DEFAULT;
}