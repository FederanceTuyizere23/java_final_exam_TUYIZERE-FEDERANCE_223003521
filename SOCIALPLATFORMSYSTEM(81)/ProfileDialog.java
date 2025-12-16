package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ProfileDialog extends JDialog implements ActionListener {

    private String loggedInUsername;
    private Dashboard parentDashboard;

    private JTextField emailField;
    private JTextArea bioArea;
    private JButton saveButton;
    private JLabel statusLabel;

 
    private static final Font DIALOG_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_DARK = new Color(25, 25, 25);
    private static final Color FOREGROUND_LIGHT = new Color(220, 220, 220);
    private static final Color ACCENT_BLUE = new Color(135, 206, 250);
    private static final Color CARD_BACKGROUND = new Color(40, 40, 40);

    public ProfileDialog(Dashboard parent, String username) {
        super(parent, "User Profile: " + username, true);
        this.parentDashboard = parent;
        this.loggedInUsername = username;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_DARK);
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

       
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(BACKGROUND_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        JLabel emailLabel = createThemedLabel("Email:");
        emailField = createThemedTextField(25);
        
        JLabel bioLabel = createThemedLabel("Bio:");
        bioArea = createThemedTextArea(5, 25);
        JScrollPane bioScrollPane = new JScrollPane(bioArea);
        bioScrollPane.setBorder(new LineBorder(CARD_BACKGROUND.brighter(), 1));

        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        saveButton.setBackground(ACCENT_BLUE);
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);

        statusLabel = createThemedLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(ACCENT_BLUE); // Use accent blue for status messages

        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        inputPanel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        inputPanel.add(emailField, gbc);

        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        inputPanel.add(bioLabel, gbc);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(bioScrollPane, gbc);
         
        JLabel infoLabel = createThemedLabel("Logged in as: " + loggedInUsername);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setBackground(BACKGROUND_DARK);
        southPanel.add(saveButton, BorderLayout.NORTH);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(infoLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        
        // Load data on initialization
        loadProfileData();
    }
    

    private JLabel createThemedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(DIALOG_FONT);
        label.setForeground(FOREGROUND_LIGHT);
        return label;
    }

    private JTextField createThemedTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(DIALOG_FONT);
        field.setBackground(CARD_BACKGROUND);
        field.setForeground(FOREGROUND_LIGHT);
        field.setBorder(BorderFactory.createLineBorder(CARD_BACKGROUND.brighter(), 1));
        return field;
    }
    
    private JTextArea createThemedTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setFont(DIALOG_FONT);
        area.setBackground(CARD_BACKGROUND);
        area.setForeground(FOREGROUND_LIGHT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    // FUNCTIONALITY   

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveProfileChanges();
        }
    }

    private void loadProfileData() {
        int userId = parentDashboard.getUserId(this.loggedInUsername);
        if (userId == -1) {
            statusLabel.setText("Error: Could not retrieve user ID.");
            return;
        }

        String sql = "SELECT email, bio FROM users WHERE id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    emailField.setText(rs.getString("email"));
                    bioArea.setText(rs.getString("bio"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Error loading data: " + ex.getMessage());
        }
    }

    private void saveProfileChanges() {
        String newEmail = emailField.getText().trim();
        String newBio = bioArea.getText().trim();

        if (newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email cannot be empty.");
            return;
        }

        int userId = parentDashboard.getUserId(this.loggedInUsername);
        String sql = "UPDATE users SET email = ?, bio = ? WHERE id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, newEmail);
            pst.setString(2, newBio);
            pst.setInt(3, userId);
            
            if (pst.executeUpdate() > 0) {
                statusLabel.setText("Profile updated successfully!");
                statusLabel.setForeground(ACCENT_BLUE);
            } else {
                statusLabel.setText("No changes made or user not found.");
                statusLabel.setForeground(Color.ORANGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Database Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
}