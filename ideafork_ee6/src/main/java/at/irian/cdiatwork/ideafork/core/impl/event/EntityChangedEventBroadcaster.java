package at.irian.cdiatwork.ideafork.core.impl.event;

import akka.actor.ActorRef;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.IdeaChangedEvent;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.domain.role.UserChangedEvent;
import at.irian.cdiatwork.ideafork.core.impl.actor.Actor;
import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

@TransactionScoped
public class EntityChangedEventBroadcaster {
    @Inject
    @Default
    private Event<UserChangedEvent> userChangedEvent;

    @Inject
    @Default
    private Event<IdeaChangedEvent> ideaChangedEvent;

    @Inject
    @Actor(type = IdeaChangedEventActor.class)
    private ActorRef asyncBroadcaster;

    public void broadcastUserChangedEvent(User entity) {
        UserChangedEvent userChangedEvent = new UserChangedEvent(entity);
        this.asyncBroadcaster.tell(userChangedEvent, this.asyncBroadcaster);
        this.userChangedEvent.fire(userChangedEvent);
    }

    public void broadcastIdeaChangedEvent(Idea entity) {
        IdeaChangedEvent ideaChangedEvent = new IdeaChangedEvent(entity);
        this.asyncBroadcaster.tell(ideaChangedEvent, this.asyncBroadcaster);
        this.ideaChangedEvent.fire(ideaChangedEvent);
    }
}
