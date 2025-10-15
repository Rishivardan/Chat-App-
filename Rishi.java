import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Rishi extends JFrame implements ActionListener {
    JTextArea chatArea;
    JTextField inputField;
    JButton sendButton;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream din;
    DataOutputStream dout;

    Thread readThread;

    Rishi() {
        // Frame settings
        setTitle("🖥 Rishi - Server");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Chat area (non-editable)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(chatArea);
        add(scroll, BorderLayout.CENTER);

        // Input panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this);

        setVisible(true);

        // Server socket setup
        try {
            serverSocket = new ServerSocket(1200);
            appendText("🟢 Waiting for client to connect...\n");
            socket = serverSocket.accept();
            appendText("✅ Client connected!\n");

            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            // Receiving messages
            readThread = new Thread(() -> {
                try {
                    while (true) {
                        String msg = din.readUTF();
                        appendText("👤 Client: " + msg + "\n");
                    }
                } catch (IOException e) {
                    appendText("⚠️ Connection closed.\n");
                }
            });
            readThread.start();

        } catch (Exception e) {
            appendText("Error: " + e.getMessage() + "\n");
        }
    }

    private void appendText(String msg) {
        chatArea.append(msg);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String msg = inputField.getText().trim();
            if (!msg.isEmpty()) {
                dout.writeUTF(msg);
                appendText("🧑 You: " + msg + "\n");
                inputField.setText("");
            }
        } catch (IOException ex) {
            appendText("⚠️ Error sending message.\n");
        }
    }

    public static void main(String[] args) {
        new Rishi();
    }
}
