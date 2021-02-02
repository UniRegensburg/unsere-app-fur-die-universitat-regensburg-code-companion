package app;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class TaskHandler {
    private Project projects[];
    private VirtualFile taskDescription;
    private String projectPath;

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


}
