package app.services.log;


import app.data.StringValues;
import com.intellij.openapi.components.ServiceManager;
import pluginhelper.User.User;
import pluginhelper.logger.Log;
import pluginhelper.logger.LogDataType;
import pluginhelper.logger.LogManager;


public class LogService implements StringValues {

    private User localUser;
    private Log currentLog;

    public static LogService getInstance() {
        return ServiceManager.getService(LogService.class);
    }

    public void init() {
        localUser = User.getLocalUser();
    }

    public void createSessionLog() {
        currentLog = LogManager.openLog(localUser.getSessionID(), LOG_TITLE);
    }


    public void logAction(String label, String action) {
        currentLog.log(localUser.getSessionID(), LogDataType.IDE, label, action);
    }

}
