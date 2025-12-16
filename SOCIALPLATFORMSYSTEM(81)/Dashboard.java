package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Dashboard extends JFrame implements ActionListener {

    private String loggedInUsername;
    private JPanel feedPanel;

    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private static final Color BACKGROUND_LIGHT = new Color(240, 240, 245);
    private static final Color FOREGROUND_DARK = new Color(40, 40, 40);
    private static final Color ACCENT_PURPLE = new Color(120, 81, 169);
    private static final Color CARD_BACKGROUND = new Color(230, 230, 245);

    public Dashboard(String username) {
        this.loggedInUsername = username;

        setTitle("SocialFeed - Logged in as: " + username);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_LIGHT);

        // Header 
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("SocialFeed", SwingConstants.CENTER);
        welcomeLabel.setFont(HEADER_FONT);
        welcomeLabel.setForeground(ACCENT_PURPLE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton btnNewPost = new JButton("📝 Create Post");
        btnNewPost.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNewPost.setBackground(ACCENT_PURPLE);
        btnNewPost.setForeground(Color.WHITE);
        btnNewPost.setFocusPainted(false);
        btnNewPost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new NewPostDialog(Dashboard.this, loggedInUsername).setVisible(true);
            }
        });


        headerPanel.add(btnNewPost, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 0, 15));
        sidebar.setBorder(new EmptyBorder(15, 10, 15, 10));
        sidebar.setBackground(CARD_BACKGROUND);

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        sidebar.add(createStyledButton("Followers (" + getFollowerCount(username) + ")", "Followers", buttonFont, ACCENT_PURPLE, Color.WHITE));
        sidebar.add(createStyledButton("Profile", "Profile", buttonFont, ACCENT_PURPLE, Color.WHITE));
        sidebar.add(createStyledButton("My Posts", "My Posts", buttonFont, ACCENT_PURPLE, Color.WHITE));
        sidebar.add(createStyledButton("Messages", "Messages", buttonFont, ACCENT_PURPLE, Color.WHITE));
        sidebar.add(createStyledButton("Find Friends", "Find Friends", buttonFont, ACCENT_PURPLE, Color.WHITE));
        sidebar.add(createStyledButton("Logout", "Logout", buttonFont, FOREGROUND_DARK, Color.WHITE));

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(180, 0));
        westPanel.setBackground(BACKGROUND_LIGHT);
        westPanel.add(sidebar, BorderLayout.NORTH);
        add(westPanel, BorderLayout.WEST);

        // Main Feed
        JTabbedPane mainContent = new JTabbedPane();
        mainContent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.setForeground(FOREGROUND_DARK);

        feedPanel = new JPanel();
        feedPanel.setBackground(BACKGROUND_LIGHT);
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        feedPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        loadPostsFromDatabase(feedPanel, username);

        JScrollPane scrollPane = new JScrollPane(feedPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_LIGHT);

        mainContent.addTab("Home Feed", scrollPane);
        add(mainContent, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("SocialFeed © 2025 | Developed with Java Swing", SwingConstants.CENTER);
        footerLabel.setFont(BODY_FONT);
        footerLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        footerLabel.setForeground(FOREGROUND_DARK);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        footerPanel.setBackground(BACKGROUND_LIGHT);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, String actionCommand, Font font, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Logout")) {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                dispose();
            }
        } else if (command.equals("Followers")) {
           
            new FollowerDialog(this, loggedInUsername).setVisible(true);
        } else if (command.equals("Profile")) {
            new ProfileDialog(this, loggedInUsername).setVisible(true);
        } else if (command.equals("My Posts")) {
            new Myposts(this, loggedInUsername).setVisible(true);
        } else if (command.equals("Messages")) {
            
            String recipient = JOptionPane.showInputDialog(this, "Enter the username of the person you want to message:", "Send Message", JOptionPane.QUESTION_MESSAGE);
            if (recipient != null && !recipient.trim().isEmpty()) {
                if (getUserId(recipient) != -1) {
                    new MessageDialog(this, loggedInUsername, recipient.trim()).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "User '" + recipient.trim() + "' not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
           
        } else if (command.equals("Find Friends")) {
            new FindFriends(this, loggedInUsername).setVisible(true);
        }
    }

    public void refreshFeed() {
        feedPanel.removeAll();
        loadPostsFromDatabase(feedPanel, loggedInUsername);
        feedPanel.revalidate();
        feedPanel.repaint();
    }

    public int getUserId(String username) {
        if (username == null || username.trim().isEmpty()) return -1; // Added check
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
        }
        return -1;
    }

    private int getFollowerCount(String username) {
        int userId = getUserId(username);
        if (userId == -1) return 0;
        String sql = "SELECT COUNT(*) AS count FROM follows WHERE followee_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    private Map<String, Integer> getPostCounts(int postId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("likes", 0);
        counts.put("comments", 0);
        try (Connection conn = DBconnection.getConnection()) {
            String likesSql = "SELECT COUNT(*) AS count FROM likes WHERE post_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(likesSql)) {
                pst.setInt(1, postId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) counts.put("likes", rs.getInt("count"));
            }
            String commentsSql = "SELECT COUNT(*) AS count FROM comments WHERE post_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(commentsSql)) {
                pst.setInt(1, postId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) counts.put("comments", rs.getInt("count"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return counts;
    }

    private void loadPostsFromDatabase(JPanel feedPanel, String username) {
        int loggedInUserId = getUserId(username);
        if (loggedInUserId == -1) return;

        String sql = "SELECT p.id, p.content, p.image_path, u.username, p.created_at " +
                     "FROM posts p JOIN users u ON p.user_id = u.id " +
                     "WHERE p.user_id = ? OR p.user_id IN (SELECT followee_id FROM follows WHERE follower_id = ?) " +
                     "ORDER BY p.created_at DESC";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, loggedInUserId);
            pst.setInt(2, loggedInUserId);
            ResultSet rs = pst.executeQuery();

            if (!rs.isBeforeFirst()) {
                JLabel emptyLabel = new JLabel("Your feed is empty. Follow some users or post something!", SwingConstants.CENTER);
                emptyLabel.setForeground(FOREGROUND_DARK);
                feedPanel.add(emptyLabel);
            }

            while (rs.next()) {
                int postId = rs.getInt("id");
                String uname = rs.getString("username");
                String content = rs.getString("content");
                String imgPath = rs.getString("image_path");
                String time = rs.getTimestamp("created_at").toString().substring(0, 16);

                feedPanel.add(createPostPanel(postId, uname, content, imgPath, time));
                feedPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }

        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private JPanel createPostPanel(final int postId, String user, String content, String imagePath, String time) {
        Map<String, Integer> counts = getPostCounts(postId);

        JPanel postPanel = new JPanel(new BorderLayout(5, 10));
        postPanel.setBackground(CARD_BACKGROUND);
        postPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FOREGROUND_DARK, 1, true),
            new EmptyBorder(15, 15, 10, 15)
        ));

        JLabel userLabel = new JLabel("👤 " + user, SwingConstants.LEFT);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userLabel.setForeground(ACCENT_PURPLE);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setBackground(CARD_BACKGROUND);
        contentArea.setForeground(FOREGROUND_DARK);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        postPanel.add(userLabel, BorderLayout.NORTH);
        postPanel.add(contentArea, BorderLayout.CENTER);

        
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
            postPanel.add(imageLabel, BorderLayout.SOUTH);
        }

        // Bottom Actions 
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(CARD_BACKGROUND);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);

        JButton likeButton = new JButton("👍 Like (" + counts.get("likes") + ")");
        likeButton.setBackground(CARD_BACKGROUND);
        likeButton.setForeground(FOREGROUND_DARK);
        likeButton.setFocusPainted(false);
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int userId = getUserId(loggedInUsername);
                if (insertLike(postId, userId)) {
                    refreshFeed();
                }
            }
        });


        JButton commentButton = new JButton("💬 Comment (" + counts.get("comments") + ")");
        commentButton.setBackground(CARD_BACKGROUND);
        commentButton.setForeground(FOREGROUND_DARK);
        commentButton.setFocusPainted(false);
        commentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new CommentDialog(Dashboard.this, loggedInUsername, postId).setVisible(true);
            }
        });


        buttonPanel.add(likeButton);
        buttonPanel.add(commentButton);

        JLabel timeLabel = new JLabel("🕒 Posted on: " + time, SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        timeLabel.setForeground(FOREGROUND_DARK);

        actionPanel.add(buttonPanel, BorderLayout.WEST);
        actionPanel.add(timeLabel, BorderLayout.EAST);

        postPanel.add(actionPanel, BorderLayout.SOUTH);

        return postPanel;
    }

    private boolean insertLike(int postId, int userId) {
        if (userId == -1) return false;
        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, postId);
            pst.setInt(2, userId);
            pst.executeUpdate();
            return true;
        } catch (SQLException ex) { return false; }
    }
}