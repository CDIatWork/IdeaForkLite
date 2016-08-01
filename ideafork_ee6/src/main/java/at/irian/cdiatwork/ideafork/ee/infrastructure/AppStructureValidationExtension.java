package at.irian.cdiatwork.ideafork.ee.infrastructure;

import akka.actor.Actor;
import at.irian.cdiatwork.ideafork.ee.backend.service.Service;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.ViewController;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class AppStructureValidationExtension implements Extension {
    private static final Logger LOG = Logger.getLogger(AppStructureValidationExtension.class.getName());

    private List<String> violations = new ArrayList<String>();

    public void validateArtifacts(@Observes ProcessManagedBean pmb, BeanManager beanManager) {
        Class beanClass = pmb.getAnnotatedBeanClass().getJavaClass();
        if (beanClass.isAnnotationPresent(ViewController.class)) {
            validateViewController(pmb.getAnnotatedBeanClass());
        }

        if (beanClass.getPackage().getName().endsWith(".service")) {
            validateService(pmb.getAnnotatedBeanClass());
        }

        if (Actor.class.isAssignableFrom(beanClass)) {
            validateActor(beanClass, pmb.getAnnotatedBeanClass().getAnnotations(), beanManager);
        }
    }

    public void checkAndAddViolations(@Observes AfterDeploymentValidation afterDeploymentValidation) {
        if (this.violations.isEmpty()) {
            LOG.info("The rules defined in " + getClass().getName() + " passed!");
            return;
        }

        StringBuilder violationMessage = new StringBuilder();

        violationMessage.append(this.violations.size()).append(" violations found: ");
        for (String violation : this.violations) {
            violationMessage.append(System.getProperty("line.separator")).append(violation);
        }
        this.violations.clear();
        afterDeploymentValidation.addDeploymentProblem(new IllegalStateException(violationMessage.toString()));
    }

    private void validateViewController(AnnotatedType annotatedType) {
        String beanClassName = annotatedType.getJavaClass().getName();

        for (Annotation annotation : annotatedType.getAnnotations()) {
            if (annotation.annotationType().getPackage().getName().equals("javax.ejb")) {
                this.violations.add(beanClassName + " is annotated with @" + annotation.annotationType().getName() +
                        " and with @" + ViewController.class.getName());
            }
        }

        if (!beanClassName.endsWith("ViewCtrl")) {
            LOG.warning(beanClassName + " is annotated with @" + ViewController.class.getName() +
                    " but doesn't follow the naming convention *ViewCtrl");
        }
    }

    private void validateService(AnnotatedType annotatedType) {
        if (!annotatedType.isAnnotationPresent(Service.class) /*check annotation known by cdi at runtime*/) {
            this.violations.add(annotatedType.getJavaClass().getName() + " is a service, " +
                    "but not annotated with a service-annotation.");
        }
    }

    private void validateActor(Class beanClass, Set<Annotation> annotations, BeanManager beanManager) {
        for (Annotation annotation : annotations) {
            if (beanManager.isScope(annotation.annotationType()) && !Dependent.class.isAssignableFrom(annotation.annotationType())) {
                this.violations.add(
                    "It isn't allowed to use " + annotation.annotationType() + " for Akka-Actors. " +
                    "Please remove the annotation from " + beanClass.getName());
            }
        }
    }
}