package app.listeners.base;


import app.MessageHandler;
import app.services.application.ApplicationService;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

import java.util.List;

/**
 * Base listener class that other app.listeners can extend
 */
public class BaseListener {

    private final ApplicationService manager;
    private MessageHandler messageHandler;
    private final String label;

    public BaseListener() {
        this("");
    }

    public BaseListener(String label) {
        manager = ServiceManager.getService(ApplicationService.class);
        this.label = label;
    }

    public void handleErrors(List<HighlightInfo> highlightInfoList, Document document) throws Exception {
        if (messageHandler == null) {
            messageHandler = ApplicationService.getInstance().getMessageHandler();
        }
        if(messageHandler != null){
            messageHandler.handleMessage(highlightInfoList, document);
        }
    }

    public void handleLinesOfCode(int lineCount, Document document) throws Exception {
        if (messageHandler == null) {
            messageHandler = ApplicationService.getInstance().getMessageHandler();
        }
        if(messageHandler != null){
            messageHandler.handleLinesOfCodeMessage(lineCount, document);
        }
    }

    protected ApplicationService getApplicationService() {
        return manager;
    }

}
