package com.socialplatform;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class Myposts extends JDialog {

    private String loggedInUsername;
    private Dashboard parentDashboard;
    private JPanel postListPanel;

    private static final Color BACKGROUND_DARK = new Color(25, 25, 25);
    private static final Color FOREGROUND_LIGHT = new Color(220, 220, 220);
    private static final Color ACCENT_PURPLE = new Color(138, 43, 226);
    private static final Color CARD_BACKGROUND = new Color(40, 40, 40);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public Myposts(Dashboard parent, String username) {
        super(parent, "Posts by " + username, true);
        this.parentDashboard = parent;
        this.loggedInUsername = username;

        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_DARK);
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel headerLabel = new JLabel("Your Posts", SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(ACCENT_PURPLE);
        add(headerLabel, BorderLayout.NORTH);

        postListPanel = new JPanel();
        postListPanel.setLayout(new BoxLayout(postListPanel, BoxLayout.Y_AXIS));
        postListPanel.setBackground(BACKGROUND_DARK);
        loadMyPosts();

        JScrollPane scrollPane = new JScrollPane(postListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_DARK);
        add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(CARD_BACKGROUND);
        closeButton.setForeground(FOREGROUND_LIGHT);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                dispose();
            }
        });

        JPanel southPanel = new JPanel();
        southPanel.setBackground(BACKGROUND_DARK);
        southPanel.add(closeButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadMyPosts() {
        postListPanel.removeAll();
        int loggedInUserId = parentDashboard.getUserId(loggedInUsername);

        if (loggedInUserId == -1) {
            JLabel errorLabel = new JLabel("Error: User ID could not be found.", SwingConstants.CENTER);
            errorLabel.setForeground(ACCENT_PURPLE);
            postListPanel.add(errorLabel);
            return;
        }

        String sql = "SELECT id, content, image_path, created_at FROM posts WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, loggedInUserId);

            try (ResultSet rs = pst.executeQuery()) {
                boolean hasPosts = false;

                while (rs.next()) {
                    hasPosts = true;
                    int postId = rs.getInt("id");
                    String content = rs.getString("content");
                    String imagePath = rs.getString("image_path"); // Path of image
                    String time = rs.getTimestamp("created_at").toString().substring(0, 16);

                    JPanel postPanel = createStyledMyPostPanel(postId, content, imagePath, time);
                    postListPanel.add(postPanel);
                    postListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }

                if (!hasPosts) {
                    JLabel emptyLabel = new JLabel("You have not made any posts yet.", SwingConstants.CENTER);
                    emptyLabel.setForeground(FOREGROUND_LIGHT);
                    postListPanel.add(emptyLabel);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JLabel errorLabel = new JLabel("Database Error: Could not load posts.", SwingConstants.CENTER);
            errorLabel.setForeground(ACCENT_PURPLE);
            postListPanel.add(errorLabel);
        }
    }

    private JPanel createStyledMyPostPanel(final int postId, String content, String imagePath, String time) {
        JPanel postPanel = new JPanel(new BorderLayout(5, 10));
        postPanel.setBackground(CARD_BACKGROUND);
        postPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BACKGROUND_DARK, 1, true),
                new EmptyBorder(15, 15, 10, 15)
        ));

        JLabel header = new JLabel("Post ID: " + postId, SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setForeground(ACCENT_PURPLE);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setBackground(CARD_BACKGROUND);
        contentArea.setForeground(FOREGROUND_LIGHT);
        contentArea.setFont(CONTENT_FONT);
        contentArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        postPanel.add(header, BorderLayout.NORTH);
        postPanel.add(contentArea, BorderLayout.CENTER);

        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            JLabel imageLabel = new JLabel();
            imageLabel.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(400, 250, Image.SCALE_SMOOTH)));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            postPanel.add(imageLabel, BorderLayout.SOUTH);
        }

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(CARD_BACKGROUND);

        JLabel timeLabel = new JLabel("🕒 Posted on: " + time);
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        timeLabel.setForeground(Color.GRAY);

        JButton deleteButton = new JButton("🗑️ Delete Post");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteButton.setBackground(new Color(75, 0, 130)); 
        deleteButton.setForeground(FOREGROUND_LIGHT);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                int confirm = JOptionPane.showConfirmDialog(Myposts.this,
                        "Are you sure you want to delete this post?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement pst = conn.prepareStatement("DELETE FROM posts WHERE id = ?")) {
                        pst.setInt(1, postId);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(Myposts.this, "Post #" + postId + " deleted.");
                        loadMyPosts();
                        parentDashboard.refreshFeed();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Myposts.this, "Error deleting post.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        actionPanel.add(timeLabel, BorderLayout.LINE_START);
        actionPanel.add(deleteButton, BorderLayout.LINE_END);

        postPanel.add(actionPanel, BorderLayout.SOUTH);

        return postPanel;
    }
}
