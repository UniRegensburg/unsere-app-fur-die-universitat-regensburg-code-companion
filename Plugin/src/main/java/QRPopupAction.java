import com.google.zxing.WriterException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ui.JBDimension;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class QRPopupAction extends AnAction {

    private Project currentProject;
    private JMenuBar menuBar;
    private QRCodeGenerator qrGenerator;
    private int qrDimensions = 500;
    private WebRTC webRTC;

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
        qrGenerator = new QRCodeGenerator();
        menuBar = WindowManager.getInstance().getFrame(currentProject).getJMenuBar();
        double id = Math.random();
        webRTC = new WebRTC();
        String stringId = Double.toString(id);
        webRTC.init(stringId);


        try {
            byte[] imageData = qrGenerator.getQRCodeImage(stringId, 350, 350);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage qrCodeImage = ImageIO.read(bis);

            createPopup(qrCodeImage, "");

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates popup and appends it under the main menu bar
     * Also contains QR-Code as parameter
     * @param qrCodeImage > QRCode Image
     * @param connectId > ConnectId in PlainText
     */
    public void createPopup(BufferedImage qrCodeImage, String connectId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(new ImageIcon(qrCodeImage)));

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
