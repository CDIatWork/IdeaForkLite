package at.irian.cdiatwork.ideafork.core.impl.change;

import at.irian.cdiatwork.ideafork.core.api.domain.BaseEntity;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.IdeaChangedEvent;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.domain.role.UserChangedEvent;
import at.irian.cdiatwork.ideafork.core.api.repository.change.EntityProcessor;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.apache.deltaspike.data.api.EntityRepository;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Interceptor
@EntityProcessor
public class EntityProcessorInterceptor implements Serializable {
    @Inject
    private BeanManager beanManager;

    @Inject
    @Default
    private Event<UserChangedEvent> userChangedEvent;

    @Inject
    @Default
    private Event<IdeaChangedEvent> ideaChangedEvent;

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Exception {
        boolean saveMethod = false;
        boolean validateEntityParameter = false;

        Object[] parameters = ic.getParameters();
        if (parameters.length == 1) {
            Class parameterType = resolveParameterType(ic.getTarget());

            if (parameterType != null && BaseEntity.class.isAssignableFrom(parameterType)) {
                String methodName = ic.getMethod().getName();
                if ("save".equals(methodName)) {
                    saveMethod = true;
                    validateEntityParameter = true;
                } else if ("remove".equals(methodName) || "attachAndRemove".equals(methodName)) {
                    validateEntityParameter = true;
                }
            }
        }

        if (validateEntityParameter) {
            checkEntity((BaseEntity) ic.getParameters()[0]);
        }

        Object result = ic.proceed();

        if (saveMethod) {
            if (parameters[0] instanceof User) {
                broadcastUserChangedEvent((User) ic.getParameters()[0]);
            } else if (parameters[0] instanceof Idea) {
                broadcastIdeaChangedEvent((Idea) ic.getParameters()[0]);
            }
        }

        return result;
    }

    private void checkEntity(BaseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("'null' as entity isn't allowed");
        }

        if (entity.getId() == null) {
            throw new IllegalArgumentException("'null' as entity-id isn't allowed");
        }
    }

    private Class resolveParameterType(Object target) {
        Class currentClass = ProxyUtils.getUnproxiedClass(target.getClass());

        for (Type interfaceClass : currentClass.getGenericInterfaces()) {
            if (interfaceClass instanceof ParameterizedType &&
                    EntityRepository.class.isAssignableFrom((Class) ((ParameterizedType) interfaceClass).getRawType())) {

                for (Type parameterizedType : ((ParameterizedType) interfaceClass).getActualTypeArguments()) {
                    if (BaseEntity.class.isAssignableFrom((Class) parameterizedType)) {
                        return (Class<? extends BaseEntity>) parameterizedType;
                    }
                }
            }
        }

        return null;
    }

    private void broadcastUserChangedEvent(User entity) {
        UserChangedEvent userChangedEvent = new UserChangedEvent(entity);
        this.userChangedEvent.fire(userChangedEvent);
    }

    private void broadcastIdeaChangedEvent(Idea entity) {
        IdeaChangedEvent ideaChangedEvent = new IdeaChangedEvent(entity);
        this.ideaChangedEvent.fire(ideaChangedEvent);
    }
}
