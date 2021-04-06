package app;

import app.interfaces.ApplicationServiceListener;
import app.services.application.ApplicationService;
import com.google.zxing.WriterException;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.ToolWindow;
import dev.onvoid.webrtc.RTCPeerConnectionState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CCToolWindow implements ApplicationService.ApplicationServiceListener {

    private JPanel myToolWindowContent;
    private WebRTC webRTC;
    private QRCodeGenerator qrGenerator;

    public CCToolWindow() {

        myToolWindowContent = new JPanel();
        myToolWindowContent.setLayout(new GridBagLayout());
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
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = GridBagConstraints.CENTER;
            c.gridy = 0;
            myToolWindowContent.add(qrCode,c);

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        JLabel id = new JLabel("ID: " + webRTC.getId());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = GridBagConstraints.CENTER;
        c.gridy = 1;
        myToolWindowContent.add(id,c);
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
        if(state == RTCPeerConnectionState.CONNECTING){
            onConnecting();
        }
        if(state == RTCPeerConnectionState.CONNECTED){
            onConnected();
        }
        if(state == RTCPeerConnectionState.DISCONNECTED){
            onDisconnected();
        }
    }

    @Override
    public void onConnectedToSignaling() {
        createQRCode();
    }

    private void onDisconnected() {
        myToolWindowContent.removeAll();
        JLabel connecting = new JLabel("Reconnecting to Signaling Server...");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = GridBagConstraints.CENTER;
        c.gridy = 0;
        myToolWindowContent.add(connecting,c);
        myToolWindowContent.updateUI();
    }

    private void onConnecting(){
        myToolWindowContent.removeAll();
        JLabel connecting = new JLabel("Connecting...");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = GridBagConstraints.CENTER;
        c.gridy = 0;
        myToolWindowContent.add(connecting,c);
        myToolWindowContent.updateUI();
    }

    private void onConnected(){
        myToolWindowContent.removeAll();
        JLabel connected = new JLabel("Connected!");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = GridBagConstraints.CENTER;
        c.gridy = 0;
        myToolWindowContent.add(connected,c);
        myToolWindowContent.updateUI();
    }
}
