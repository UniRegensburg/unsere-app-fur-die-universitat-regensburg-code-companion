package app;
import app.services.application.ApplicationService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

/**
 * Handles and sends taskMessages through WebRTC
 */
public class TaskHandler {
    private final ApplicationService manager;
    private Project projects[];
    private VirtualFile taskDescription;
    private String projectPath;
    private boolean sent;

    public TaskHandler() {
        manager = ServiceManager.getService(ApplicationService.class);
    }

    /* Init Method, meant to be run first*/
    public void init(){
        projects = ProjectManager.getInstance().getOpenProjects();
        projectPath = projects[0].getBasePath();

    }

    /* Returns ProjectName*/
    public String getProjectName(){
        String projectName = projects[0].getName();
        System.out.println("ProjectName: "+projectName);
        return projectName;
    }

    /* Returns JSon Task Information (as String), located in ProjectRoot "task.json"*/
    public String getTaskInfo() {
        String taskInfoString = "";
        System.out.println(projectPath);
        taskDescription=LocalFileSystem.getInstance().findFileByPath(projectPath+"/task.json");
        System.out.println(taskDescription);
        if(taskDescription!=null) {
            try {
                taskInfoString=VfsUtil.loadText(taskDescription);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return taskInfoString;
    }

    public boolean isSent(){
        return sent;
    }

    public void sendTaskInfo() {
        String task = getTaskInfo();
        if (task != "") {
            try {
                System.out.println("Sending TaskInfo!");
                manager.getMessageHandler().send(task);
                sent = true;
            } catch (Exception i) {
                i.printStackTrace();
                System.out.println(i);
                sent = false;
            }
        }
    }


}
