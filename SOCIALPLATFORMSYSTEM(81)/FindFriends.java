
package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindFriends extends JDialog {
    
    private String loggedInUsername;
    private Dashboard parentDashboard;
    

    public FindFriends(Dashboard parent, String username) {
        super(parent, "Find Friends", true); // Modal dialog
        this.loggedInUsername = username;
        this.parentDashboard = parent;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));
        friendListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadUsersToSuggest(friendListPanel);

        JScrollPane scrollPane = new JScrollPane(friendListPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUsersToSuggest(JPanel panel) {
        int loggedInUserId = parentDashboard.getUserId(this.loggedInUsername);

        if (loggedInUserId == -1) {
            panel.add(new JLabel("Error: Cannot determine your user ID."));
            return;
        }

        // SQL to select users who are NOT:
        // 1. The logged-in user (u.id != ?)
        // 2. Already followed by the logged-in user (u.id NOT IN (SELECT followee_id...))
        String sql = "SELECT u.id, u.username, u.bio FROM users u " +
                     "WHERE u.id != ? " +
                     "AND u.id NOT IN (" +
                     "    SELECT followee_id FROM follows WHERE follower_id = ?" +
                     ")";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, loggedInUserId);
            pst.setInt(2, loggedInUserId);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    panel.add(new JLabel("You are following everyone! Check back later for new users."));
                }

                while (rs.next()) {
                    int userId = rs.getInt("id");
                    String suggestedName = rs.getString("username");
                    String suggestedBio = rs.getString("bio");
                    
                    panel.add(createSuggestionPanel(userId, suggestedName, suggestedBio));
                    panel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            panel.add(new JLabel("Database error loading suggestions: " + ex.getMessage()));
        }
    }
    
    /** Creates a panel displaying a suggested user and a 'Follow' button */
    private JPanel createSuggestionPanel(final int userId, final String username, String bio) {
        JPanel suggestionPanel = new JPanel(new BorderLayout(10, 5));
        suggestionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 255)), // Light blue border
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        
        JLabel nameLabel = new JLabel("👤 " + username);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel bioLabel = new JLabel("Bio: " + bio);
        bioLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        
        infoPanel.add(nameLabel);
        infoPanel.add(bioLabel);
        
        JButton followButton = new JButton("➕ Follow");
        followButton.setPreferredSize(new Dimension(100, 30));
        
        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (attemptFollow(userId)) {
                    JOptionPane.showMessageDialog(FindFriends.this, 
                        "Successfully followed " + username + "!");
                    
                    FindFriends.this.dispose(); 
                    parentDashboard.refreshFeed();
                } else {
                    JOptionPane.showMessageDialog(FindFriends.this, 
                        "Failed to follow " + username + ". Error occurred.");
                }
            }

		
		
        });
        
        suggestionPanel.add(infoPanel, BorderLayout.CENTER);
        suggestionPanel.add(followButton, BorderLayout.EAST);
        
        return suggestionPanel;
    }
    
    /** Inserts a new row into the 'follows' table */
    private boolean attemptFollow(int followeeId) {
        int followerId = parentDashboard.getUserId(this.loggedInUsername);
        
        if (followerId == -1) return false;
        
        String sql = "INSERT INTO follows (follower_id, followee_id) VALUES (?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, followerId);
            pst.setInt(2, followeeId);
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLIntegrityConstraintViolationException ex) {
            return true; 
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}