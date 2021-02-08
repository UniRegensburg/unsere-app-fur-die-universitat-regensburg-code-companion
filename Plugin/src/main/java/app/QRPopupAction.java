package app;

import app.services.application.ApplicationService;
import com.google.zxing.WriterException;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ui.JBDimension;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

public class QRPopupAction extends AnAction implements PluginIcons {

    private Project currentProject;
    private JMenuBar menuBar;
    private QRCodeGenerator qrGenerator;
    private int qrDimensions = 500;
    private WebRTC webRTC;
    private MessageHandler messageHandler;
    private JFrame frame;

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
        frame = new JFrame("QR Code");
        currentProject = event.getProject();
        qrGenerator = new QRCodeGenerator();
        menuBar = WindowManager.getInstance().getFrame(currentProject).getJMenuBar();
        double id = Math.random();
        webRTC = new WebRTC();
        messageHandler = new MessageHandler();
        UUID uuid = UUID.randomUUID();
        String stringId = uuid.toString();
        webRTC.init(stringId);

        webRTC.setWebRTCListener(new WebRTC.WebRTCListener() {
            @Override
            public void onConnectionStateChanged(RTCPeerConnectionState state) {
                System.out.println("CURRENT STATE:" + state);
                if(state == RTCPeerConnectionState.CONNECTING || state == RTCPeerConnectionState.CONNECTED){
                    frame.setVisible(false);
                    frame.dispose();
                }
            }
        });

        // start listening for IDE events
        ApplicationService.getInstance().startSession();
        ApplicationService.getInstance().setMessageHandler(messageHandler);
        ApplicationService.getInstance().setWebRTC(webRTC);


        try {
            byte[] imageData = qrGenerator.getQRCodeImage(stringId, 150, 150);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage qrCodeImage = ImageIO.read(bis);

            createPopup(qrCodeImage, "");

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        webRTC.connectSignaling();
    }

    /**
     * Creates popup and appends it under the main menu bar
     * Also contains QR-Code as parameter
     * @param qrCodeImage > QRCode Image
     * @param connectId > ConnectId in PlainText
     */
    public void createPopup(BufferedImage qrCodeImage, String connectId) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        int size = 300;

        frame.pack();
        frame.getContentPane().add(new JLabel(new ImageIcon(qrCodeImage)),BorderLayout.CENTER);
        frame.getContentPane().setBackground(Color.WHITE);
        //frame.add(new JLabel(connectId));
        frame.setSize(size,size);
        frame.setLocation(width/2-size/2, height/2-size/2);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                webRTC.safeCloseRoom();
            }
        });
    }
}
