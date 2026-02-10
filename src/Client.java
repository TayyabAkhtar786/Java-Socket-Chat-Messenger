import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client implements ActionListener {
    JFrame frame;
    JTextArea chatArea;
    JTextField messageField;
    JButton sendButton, cprbtn, fbtn;
    JLabel proPic, stLbl;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    String username;

    public Client(String user) {
        username = user;
        frame = new JFrame("Chat Window of " + username);
        frame.setSize(400, 500);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBounds(20, 20, 350, 200);
        frame.add(scroll);

        proPic = new JLabel();
        proPic.setBounds(20, 230, 100, 100);
        frame.add(proPic);

        stLbl = new JLabel("Status:");
        stLbl.setBounds(130, 230, 240, 20);
        frame.add(stLbl);

        messageField = new JTextField();
        messageField.setBounds(20, 350, 250, 30);
        frame.add(messageField);

        sendButton = new JButton("Send");
        sendButton.setBounds(280, 350, 90, 30);
        sendButton.addActionListener(this);
        frame.add(sendButton);

        cprbtn = new JButton("Create Profile");
        cprbtn.setBounds(20, 390, 120, 30);
        cprbtn.addActionListener(this);
        frame.add(cprbtn);

         fbtn = new JButton("My Friends");
        fbtn.setBounds(150, 390, 120, 30);
        fbtn.addActionListener(this);
        frame.add(fbtn);

        frame.setVisible(true);
        connectToServer();
    }

    public Client() {
        this(JOptionPane.showInputDialog("Enter username:"));
    }

    void connectToServer() {
        try {
            socket = new Socket("localhost", 6002);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(username);
            new Thread(new MessageReceiver()).start();
        } catch (Exception e) {
            chatArea.append("Cannot connect to server\n");
        }
    }

    class MessageReceiver implements Runnable {
        public void run() {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("Picture: ")) {
                        String picName = msg.substring(9);
                        ImageIcon icon = new ImageIcon("C:/Project/ChatAPP_Project/icons/" + picName);
                        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                        proPic.setIcon(new ImageIcon(img));
                    } else if (msg.startsWith("Status: ")) {
                        stLbl.setText(msg);
                    } else {
                        chatArea.append(msg + "\n");
                    }
                }
            } catch (Exception e) {
                chatArea.append("Disconnected from server\n");
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            out.println(messageField.getText());
            messageField.setText("");
        } else if (e.getSource() == cprbtn) {
            String status = JOptionPane.showInputDialog(frame, "Enter status:");
            String pic = JOptionPane.showInputDialog(frame, "Enter picture(e.g superman.png):");
            if (status != null) out.println("/setstatus " + status);
            if (pic != null) out.println("/setpic " + pic);
            out.println("/profile");//show updated profile once
        } else if (e.getSource() == fbtn) {
            out.println("/friends");
        }
    }
    public static void main(String[] args) {
        new Client();
    }
}
