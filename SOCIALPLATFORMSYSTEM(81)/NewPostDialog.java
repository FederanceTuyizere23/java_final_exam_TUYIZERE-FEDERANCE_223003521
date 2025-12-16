package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class NewPostDialog extends JDialog {

    private Dashboard parentDashboard;
    private String username;

    private JTextArea contentArea;
    private JTextField imagePathField;
    private JButton browseButton, postButton, cancelButton;

    private static final Color BACKGROUND_LIGHT = new Color(240, 240, 245);
    private static final Color FOREGROUND_DARK = new Color(40, 40, 40);
    private static final Color ACCENT_PURPLE = new Color(120, 81, 169);
    private static final Color CARD_BACKGROUND = new Color(230, 230, 245);

    public NewPostDialog(Dashboard parent, String username) {
        super(parent, "Create New Post", true);
        this.parentDashboard = parent;
        this.username = username;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_LIGHT);

        JLabel header = new JLabel("Create a New Post", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(ACCENT_PURPLE);
        add(header, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_LIGHT);

        contentArea = new JTextArea(6, 40);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBorder(BorderFactory.createTitledBorder("Post Content"));
        contentArea.setBackground(Color.WHITE);

        JPanel imagePanel = new JPanel(new BorderLayout(5,5));
        imagePanel.setBackground(BACKGROUND_LIGHT);
        imagePanel.setBorder(BorderFactory.createTitledBorder("Optional Image"));

        imagePathField = new JTextField();
        imagePathField.setEditable(false);
        browseButton = new JButton("Browse");
        browseButton.setBackground(ACCENT_PURPLE);
        browseButton.setForeground(Color.WHITE);
        browseButton.setFocusPainted(false);
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                browseImage();
            }
        });


        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);

        centerPanel.add(contentArea);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));
        centerPanel.add(imagePanel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(BACKGROUND_LIGHT);

        postButton = new JButton("Post");
        postButton.setBackground(ACCENT_PURPLE);
        postButton.setForeground(Color.WHITE);
        postButton.setFocusPainted(false);
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                createPost();
            }
        });


        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(CARD_BACKGROUND);
        cancelButton.setForeground(FOREGROUND_DARK);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });


        footerPanel.add(postButton);
        footerPanel.add(cancelButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    /** Browse image file */
    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /** Insert post into database */
    private void createPost() {
        String content = contentArea.getText().trim();
        String imagePath = imagePathField.getText().trim();

        if(content.isEmpty() && imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Post content or image is required!");
            return;
        }

        int userId = parentDashboard.getUserId(username);
        if(userId == -1) {
            JOptionPane.showMessageDialog(this, "Error: User ID not found.");
            return;
        }

        String sql = "INSERT INTO posts (user_id, content, image_path, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setString(2, content.isEmpty() ? null : content);
            pst.setString(3, imagePath.isEmpty() ? null : imagePath);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Post created successfully!");
            parentDashboard.refreshFeed();
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating post: " + ex.getMessage());
        }
    }
}
