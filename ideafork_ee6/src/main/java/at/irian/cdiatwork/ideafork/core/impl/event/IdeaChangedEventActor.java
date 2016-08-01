package at.irian.cdiatwork.ideafork.core.impl.event;

import akka.actor.UntypedActor;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.IdeaChangedEvent;
import at.irian.cdiatwork.ideafork.core.impl.actor.Async;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class IdeaChangedEventActor extends UntypedActor {
    private final Event<IdeaChangedEvent> entityChangedEvent;

    @Inject
    public IdeaChangedEventActor(@Async Event<IdeaChangedEvent> entityChangedEvent) {
        this.entityChangedEvent = entityChangedEvent;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof IdeaChangedEvent) {
            this.entityChangedEvent.fire((IdeaChangedEvent)message);
        } else {
            unhandled(message);
        }
    }
}