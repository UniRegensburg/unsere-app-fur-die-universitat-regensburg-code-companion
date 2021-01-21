package app.listeners.base;


import app.services.application.ApplicationService;
import com.intellij.openapi.components.ServiceManager;

/**
 * Base listener class that other app.listeners can extend
 */
public class BaseListener {

    private final ApplicationService manager;
    private final String label;

    public BaseListener() {
        this("");
    }

    public BaseListener(String label) {
        manager = ServiceManager.getService(ApplicationService.class);
        this.label = label;
    }

    public void log(String msg) {
        Event logEvent = new Event(label, msg);
        manager.inspectEvent(logEvent);
    }

    protected ApplicationService getApplicationService() {
        return manager;
    }

}
