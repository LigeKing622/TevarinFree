package dev.tevarin.utils.math;



import dev.tevarin.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Killaura1 {
    public static void HWIDVerify() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("验证");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());

            JPanel panel = new JPanel();

            JLabel label1 = new JLabel("复制HWID");
            JTextArea textArea1;
            try {
                textArea1 = new JTextArea(getHWID(), 3, 20);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            JButton button1 = new JButton("复制");
            JTextArea finalTextArea = textArea1;
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = finalTextArea.getText();
                    StringSelection stringSelection = new StringSelection(text);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                }
            });

            JButton button2 = new JButton("进行HWID验证");
            button2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (verify2()){

                        frame.dispose();
                    }
                }
            });

            panel.add(label1);
            panel.add(textArea1);
            panel.add(button1);

            panel.add(button2);

            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
    }
    public static String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        StringBuilder lilililiiliilililil1ililili1ilil1lililili1li1lilil1i1li1li1l1i1li1l1il1il1i1l = new StringBuilder();
        String lililililiililililililili1ilil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME");
        byte[] lililililiilililil1ililili1ilil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l = lililililiililililililili1ilil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] lililililiilililililililililil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l = messageDigest.digest(lililililiilililil1ililili1ilil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l);
        int i = 0;
        for(byte b : lililililiilililililililililil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l) {
            lilililiiliilililil1ililili1ilil1lililili1li1lilil1i1li1li1l1i1li1l1il1il1i1l.append(Integer.toHexString((b & 0xFF) | 0x300),0,3);
            if(i != lililililiilililililililililil1lilili1li1li1lilil1i1li1li1l1i1li1l1il1il1i1l.length -1) {
                lilililiiliilililil1ililili1ilil1lililili1li1lilil1i1li1li1l1i1li1l1il1il1i1l.append("-");
            }
            i++;
        }
        return lilililiiliilililil1ililili1ilil1lililili1li1lilil1i1li1li1l1i1li1l1il1il1i1l.toString();
    }
    public static String check(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }

        in.close();

        return response.toString();
    }
    public static void windowsnoti(String Title, String Text, TrayIcon.MessageType type) {// :)逆天名字
        try {
            SystemTray systemTray = SystemTray.getSystemTray();
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            systemTray.add(trayIcon);
            trayIcon.displayMessage(Title, Text, type);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    public static boolean verify2() {
        try {
            if (check(Client.Verify_http).contains(getHWID())){
                JOptionPane.showMessageDialog(null,"验证成功");
                return true;
            } else {
                JOptionPane.showMessageDialog(null,"验证失败");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }
    public static void main(String[] args) {
        showMessageDialog();
    }

    public static void showMessageDialog() {

        JFrame frame = new JFrame("验证");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new FlowLayout());

        // 说明
        JLabel label = new JLabel("以下是您的hwid，请复制发送管理员");
        panel.add(label);

        // 输入框
        JTextArea textArea1;
        try {
            textArea1 = new JTextArea(getHWID(), 3, 20);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 按钮
        JButton button = new JButton("复制");
        JTextArea finalTextArea = textArea1;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = finalTextArea.getText();
                StringSelection stringSelection = new StringSelection(text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });
        JButton button2 = new JButton("进行HWID验证");
        button2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String hwid = getHWID();
                    if (check(Client.Verify_http).contains(hwid)){
                        JOptionPane.showMessageDialog(null,"验证成功");
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null,"验证失败");
                        System.exit(0);
                    }
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.add(textArea1);
        panel.add(button);
        panel.add(button2);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        while (frame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }
}

