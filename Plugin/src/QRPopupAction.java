import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;

/**
 * @author Fabian Schiller
 */
public class QRPopupAction extends AnAction {

    private Project currentProject;
    private JMenuBar menuBar;
    private int qrDimensions = 500;

    /**
     * Default method of intellij plugins
     * @param event
     */
    @Override
    public void update(AnActionEvent event) {

    }

    /**
     * Default method of intellij plugins
     * @param event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        currentProject = event.getProject();
        menuBar = WindowManager.getInstance().getFrame(currentProject).getJMenuBar();


        createPopup();
    }

    /**
     * Creates popup and appends it under the main menu bar
     */
    public void createPopup() {
        JPanel panel = new JPanel(new BorderLayout());

        ComponentPopupBuilder popupFactory = JBPopupFactory
                .getInstance()
                .createComponentPopupBuilder(panel, panel)
                .setCancelOnClickOutside(true)
                .setResizable(true);

        if(currentProject != null) {
            JBPopup popup = popupFactory.createPopup();
            popup.showUnderneathOf(menuBar);
            popup.setMinimumSize(new JBDimension(qrDimensions, qrDimensions));
        }
    }
}
