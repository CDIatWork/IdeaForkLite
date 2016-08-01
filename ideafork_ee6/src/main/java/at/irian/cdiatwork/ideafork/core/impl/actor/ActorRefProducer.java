package at.irian.cdiatwork.ideafork.core.impl.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ActorRefProducer {
    private Map<String, ActorSystem> actorSystemMap = new HashMap<String, ActorSystem>();

    @Produces
    @Actor(type = akka.actor.Actor.class /*just used as placeholder*/)
    protected ActorRef createActorRef(InjectionPoint injectionPoint) {
        final Actor actorQualifier = injectionPoint.getAnnotated().getAnnotation(Actor.class);

        ActorSystem actorSystem = getActorSystem(actorQualifier.systemName());

        if (!UntypedActor.class.isAssignableFrom(actorQualifier.type())) {
            actorSystem.actorOf(Props.create(actorQualifier.type()));
        }
        return actorSystem.actorOf(Props.create(new CdiAwareCreator(actorQualifier.type())));
    }

    public ActorSystem getActorSystem(String actorSystemName) {
        ActorSystem actorSystem = actorSystemMap.get(actorSystemName);
        if (actorSystem == null || actorSystem.isTerminated()) {
            actorSystem = bootActorSystem(actorSystemName);
        }

        return actorSystem;
    }

    private synchronized ActorSystem bootActorSystem(String actorSystemName) {
        ActorSystem actorSystem = actorSystemMap.get(actorSystemName);
        if (actorSystem != null && !actorSystem.isTerminated()) {
            return actorSystem;
        }

        actorSystem = ActorSystem.create(actorSystemName);
        actorSystemMap.put(actorSystemName, actorSystem);
        return actorSystem;
    }

    @PreDestroy
    protected void cleanup() {
        for (ActorSystem actorSystem : actorSystemMap.values()) {
            if (!actorSystem.isTerminated()) {
                actorSystem.shutdown();
            }
        }
    }

    private static class CdiAwareCreator implements Creator<akka.actor.Actor> {
        private static final long serialVersionUID = 3739310463390426896L;

        private final Class<? extends akka.actor.Actor> actorClass;

        public CdiAwareCreator(Class<? extends akka.actor.Actor> actorClass) {
            this.actorClass = actorClass;
        }

        @Override
        public akka.actor.Actor create() throws Exception {
            return BeanProvider.getDependent(actorClass).get();
        }
    }
}