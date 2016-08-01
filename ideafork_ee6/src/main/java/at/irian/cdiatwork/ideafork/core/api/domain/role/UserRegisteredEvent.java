package at.irian.cdiatwork.ideafork.core.api.domain.role;

import at.irian.cdiatwork.ideafork.core.api.domain.EntityChangedEvent;

public class UserRegisteredEvent extends EntityChangedEvent<User> {
    public UserRegisteredEvent(User createdEntity) {
        super(createdEntity);
    }
}
