package at.irian.cdiatwork.ideafork.core.impl.monitoring;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class MonitoredStorage {
    private List<String> slowMethods = new CopyOnWriteArrayList<String>();

    public void recordSlowMethod(String name) {
        this.slowMethods.add(name);
    }

    public List<String> getSlowMethods() {
        return slowMethods;
    }
}
