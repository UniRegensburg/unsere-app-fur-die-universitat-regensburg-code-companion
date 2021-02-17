package app;

import app.services.application.ApplicationService;
import com.google.zxing.WriterException;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.ToolWindow;
import dev.onvoid.webrtc.RTCPeerConnectionState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CCToolWindow implements ApplicationService.ApplicationServiceListener {

    private JPanel myToolWindowContent;
    private WebRTC webRTC;
    private QRCodeGenerator qrGenerator;

    public CCToolWindow() {
        myToolWindowContent = new JPanel();
    }

    public void createQRCode(){
        myToolWindowContent.removeAll();

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
        myToolWindowContent.updateUI();
    }


    public JPanel getContent() {
        return myToolWindowContent;
    }

    public void init() {
        ApplicationService.getInstance().setListener(this);
        if(ApplicationService.getInstance().isStarted){
            createQRCode();
        }
    }

    @Override
    public void onStarted() {
        createQRCode();
    }

    @Override
    public void onConnectionStateChanged(RTCPeerConnectionState state) {
        if(state == RTCPeerConnectionState.DISCONNECTED || state == RTCPeerConnectionState.CLOSED || state == RTCPeerConnectionState.FAILED){
            createQRCode();
        }
        if(state == RTCPeerConnectionState.CONNECTING){
            onConnecting();
        }
        if(state == RTCPeerConnectionState.CONNECTED){
            onConnected();
        }
    }

    private void onConnecting(){
        myToolWindowContent.removeAll();
        JLabel connecting = new JLabel("Connecting...");
        myToolWindowContent.add(connecting);
        myToolWindowContent.updateUI();
    }

    private void onConnected(){
        myToolWindowContent.removeAll();
        JLabel connected = new JLabel("Connected!");
        myToolWindowContent.add(connected);
        myToolWindowContent.updateUI();
    }
}
