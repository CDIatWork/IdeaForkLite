package at.irian.cdiatwork.ideafork.core.impl.event;

import akka.actor.ActorRef;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.domain.role.UserRegisteredEvent;
import at.irian.cdiatwork.ideafork.core.impl.actor.Actor;
import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

@TransactionScoped
public class UserRegisteredEventBroadcaster {
    @Inject
    @Default
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @Actor(type = UserRegisteredEventActor.class)
    private ActorRef asyncBroadcaster;

    public void broadcastUserRegisteredEvent(User entity) {
        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent(entity);
        this.asyncBroadcaster.tell(userRegisteredEvent, this.asyncBroadcaster);
        this.userRegisteredEvent.fire(userRegisteredEvent);
    }
}
