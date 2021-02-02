package app;
import com.intellij.openapi.project.*;

public class TaskHandler {
    private Project projects[];

    public void init(){
        projects = ProjectManager.getInstance().getOpenProjects();

    }
    public String getProjectName(){
        String projectName = projects[0].getName();
        System.out.println("ProjectName: "+projectName);
        return projectName;
    }
    public String getTaskInfo(){

        return "";
    }


}
