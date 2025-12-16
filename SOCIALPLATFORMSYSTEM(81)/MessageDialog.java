package com.socialplatform;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MessageDialog extends JDialog implements ActionListener {

    private String loggedInUsername;
    private String recipientUsername;
    private Dashboard parentDashboard;

    private JTextArea conversationArea;
    private JTextField messageInput;
    private JButton sendButton;

    private static final Font DIALOG_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_DARK = new Color(25, 25, 25);
    private static final Color FOREGROUND_LIGHT = new Color(220, 220, 220);
    private static final Color ACCENT_GREEN = new Color(124, 252, 0); // Bright green for messages
    private static final Color CARD_BACKGROUND = new Color(40, 40, 40);

    public MessageDialog(Dashboard parent, String sender, String recipient) {
        super(parent, "Chat with " + recipient, false); // Non-modal is often better for chat
        this.parentDashboard = parent;
        this.loggedInUsername = sender;
        this.recipientUsername = recipient;

        setSize(550, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_DARK);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Read Operation
        conversationArea = new JTextArea();
        conversationArea.setEditable(false);
        conversationArea.setFont(DIALOG_FONT);
        conversationArea.setBackground(CARD_BACKGROUND);
        conversationArea.setForeground(FOREGROUND_LIGHT);
        conversationArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(conversationArea);
        scrollPane.setBorder(new LineBorder(ACCENT_GREEN.darker(), 1));
        add(scrollPane, BorderLayout.CENTER);

        // Create Operation
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(BACKGROUND_DARK);
        
        messageInput = new JTextField();
        messageInput.setFont(DIALOG_FONT);
        messageInput.setBackground(CARD_BACKGROUND);
        messageInput.setForeground(FOREGROUND_LIGHT);
        messageInput.setBorder(new LineBorder(ACCENT_GREEN, 1));
        messageInput.addActionListener(this); // Allow ENTER key to send

        sendButton = new JButton("Send");
        sendButton.setBackground(ACCENT_GREEN.darker());
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(this);

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        loadMessages();
    }

    // FUNCTIONALITY

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton || e.getSource() == messageInput) {
            String content = messageInput.getText().trim();
            if (!content.isEmpty()) {
                if (sendMessage(content)) {
                    messageInput.setText(""); 
                    loadMessages(); // Refresh conversation view
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send message.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /** Fetches the conversation history between the logged-in user and the recipient. */
    private void loadMessages() {
        conversationArea.setText("");
        int senderId = parentDashboard.getUserId(this.loggedInUsername);
        int recipientId = parentDashboard.getUserId(this.recipientUsername);

        if (senderId == -1 || recipientId == -1) {
            conversationArea.setText("[Error: Could not determine user IDs.]");
            return;
        }
        String sql = "SELECT m.content, u.username AS sender, m.timestamp FROM messages m " +
                     "JOIN users u ON m.sender_id = u.id " +
                     "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR " +
                     "      (m.sender_id = ? AND m.receiver_id = ?) " +
                     "ORDER BY m.timestamp ASC";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, senderId);
            pst.setInt(2, recipientId);
            pst.setInt(3, recipientId);
            pst.setInt(4, senderId);

            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    conversationArea.append("Start your conversation with " + recipientUsername + ".\n");
                }

                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String content = rs.getString("content");
                    String time = rs.getTimestamp("timestamp").toString().substring(11, 16);
                    
                    String line;
                    if (sender.equals(this.loggedInUsername)) {
                        // Highlight user's own messages
                        line = String.format("[%s] You: %s\n", time, content);
                    } else {
                        // Recipient's messages
                        line = String.format("[%s] %s: %s\n", time, sender, content);
                    }
                    conversationArea.append(line);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            conversationArea.append("[Database Error loading messages: " + ex.getMessage() + "]");
        }
    }

    /** Inserts a new message into the database. */
    private boolean sendMessage(String content) {
        int senderId = parentDashboard.getUserId(this.loggedInUsername);
        int receiverId = parentDashboard.getUserId(this.recipientUsername);

        if (senderId == -1 || receiverId == -1) return false;

        // SQL INSERT for a new message (Create Operation)
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, senderId);
            pst.setInt(2, receiverId);
            pst.setString(3, content);

            return pst.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}