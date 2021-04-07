package app.listeners;

import app.App;
import app.listeners.topics.*;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;


public class ListenerHelper {

    public static void initListener() {
        Project currentProject = App.getCurrentProject();
        initMessageBusListener(currentProject.getMessageBus(), currentProject);
    }

    private static  void initMessageBusListener(MessageBus bus, Project currentProject) {
        bus.connect().subscribe(XBreakpointListener.TOPIC, new BreakpointListener());
        bus.connect().subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new CodeAnalyzerListener(currentProject));
        bus.connect().subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new LinesOfCodeListener(currentProject));
        bus.connect().subscribe(ExecutionManager.EXECUTION_TOPIC, new RunListener());
        bus.connect().subscribe(VirtualFileManager.VFS_CHANGES, new VFSListener());
    }

}
