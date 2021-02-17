package app;

import app.services.application.ApplicationService;
import com.google.zxing.WriterException;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.WindowManager;
import dev.onvoid.webrtc.RTCPeerConnectionState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CCToolWindow {

    private JPanel myToolWindowContent;
    private WebRTC webRTC;
    private QRCodeGenerator qrGenerator;

    public CCToolWindow(ToolWindow toolWindow) {
        myToolWindowContent = new JPanel();
        createQRCode();
    }

    private void createQRCode(){
        webRTC = ApplicationService.getInstance().getWebRTC();
        qrGenerator = new QRCodeGenerator();
        webRTC.connect();


        try {
            byte[] imageData = qrGenerator.getQRCodeImage(webRTC.getId(), 150, 150);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage qrCodeImage = ImageIO.read(bis);

            JLabel qrCode = new JLabel(new ImageIcon(qrCodeImage));
            myToolWindowContent.add(qrCode);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }


    public JPanel getContent() {
        return myToolWindowContent;
    }
}
