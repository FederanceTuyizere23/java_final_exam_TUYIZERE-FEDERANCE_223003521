package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CommentDialog extends JDialog implements ActionListener {
    
    private int postId;
    private String loggedInUsername;
    private Dashboard parentDashboard;
    
    private JTextArea commentContentArea;
    private JButton submitButton;
    
    private static final Font DIALOG_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_DARK = new Color(25, 25, 25);
    private static final Color FOREGROUND_LIGHT = new Color(220, 220, 220);
    private static final Color ACCENT_BLUE = new Color(135, 206, 250);
    private static final Color CARD_BACKGROUND = new Color(40, 40, 40);

    public CommentDialog(Dashboard parent, String username, int postId) {
        super(parent, "Comment on Post #" + postId, true); 
        this.parentDashboard = parent;
        this.loggedInUsername = username;
        this.postId = postId;

        setSize(400, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_DARK);

        commentContentArea = new JTextArea(6, 30);
        commentContentArea.setLineWrap(true);
        commentContentArea.setWrapStyleWord(true);
        commentContentArea.setFont(DIALOG_FONT);
        commentContentArea.setBackground(CARD_BACKGROUND);
        commentContentArea.setForeground(FOREGROUND_LIGHT);
        JScrollPane scrollPane = new JScrollPane(commentContentArea);
        
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(ACCENT_BLUE.darker(), 1, true),
            "Your Comment:",
            0, 2, 
            new Font("Segoe UI", Font.BOLD, 14), 
            ACCENT_BLUE
        ));

        // Button 
        submitButton = new JButton("Submit Comment");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        submitButton.setBackground(ACCENT_BLUE); 
        submitButton.setForeground(Color.BLACK);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(this); 

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_DARK);
        buttonPanel.add(submitButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String content = commentContentArea.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Comment content cannot be empty.");
                return;
            }
            if (submitCommentToDatabase(content)) {
                JOptionPane.showMessageDialog(this, "Comment submitted successfully!");
                parentDashboard.refreshFeed(); 
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit comment. Check console for database error.");
            }
        }
    }

    private boolean submitCommentToDatabase(String content) {
        int userId = parentDashboard.getUserId(this.loggedInUsername);
        if (userId == -1) return false;

        String sql = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, this.postId);
            pst.setInt(2, userId);
            pst.setString(3, content);
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}