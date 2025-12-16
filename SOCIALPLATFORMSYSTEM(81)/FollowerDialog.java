package com.socialplatform;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class FollowerDialog extends JDialog {

    private String loggedInUsername;
    private Dashboard parentDashboard;
    private JList<String> followerList;

    private static final Font DIALOG_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_DARK = new Color(25, 25, 25);
    private static final Color FOREGROUND_LIGHT = new Color(220, 220, 220);
    private static final Color ACCENT_BLUE = new Color(135, 206, 250);
    private static final Color CARD_BACKGROUND = new Color(40, 40, 40);
    private static final Color LIST_BACKGROUND = new Color(30, 30, 30); 

    public FollowerDialog(Dashboard parent, String username) {
        super(parent, "Followers of " + username, true);
        this.parentDashboard = parent;
        this.loggedInUsername = username;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_DARK);
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JLabel headerLabel = new JLabel("Followers List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(ACCENT_BLUE);
        add(headerLabel, BorderLayout.NORTH);

        
        followerList = new JList<>(loadFollowers());
        followerList.setFont(DIALOG_FONT);
        followerList.setBackground(LIST_BACKGROUND);
        followerList.setForeground(FOREGROUND_LIGHT);
        followerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
       
        followerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(ACCENT_BLUE.darker()); 
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(LIST_BACKGROUND);
                    c.setForeground(FOREGROUND_LIGHT);
                }
                ((JComponent) c).setBorder(new EmptyBorder(8, 8, 8, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(followerList);
        scrollPane.setBorder(new LineBorder(CARD_BACKGROUND, 1));
        scrollPane.getViewport().setBackground(LIST_BACKGROUND); // Ensure scroll area is dark
        
        add(scrollPane, BorderLayout.CENTER);
        
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(CARD_BACKGROUND);
        closeButton.setForeground(FOREGROUND_LIGHT);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                dispose(); 
            }
        });
        
        JPanel southPanel = new JPanel();
        southPanel.setBackground(BACKGROUND_DARK);
        southPanel.add(closeButton);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /** Fetches the usernames of users who are following the logged-in user. */
    private Vector<String> loadFollowers() {
        Vector<String> followers = new Vector<>();
        int followeeId = parentDashboard.getUserId(this.loggedInUsername);

        if (followeeId == -1) {
            followers.add("[Error loading user ID]");
            return followers;
        }

        // SQL to select the usernames (u.username) of users who follow the logged-in user (followee_id)
        String sql = "SELECT u.username FROM users u " + "JOIN follows f ON u.id = f.follower_id " +
                "WHERE f.followee_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, followeeId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    followers.add(rs.getString("username"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            followers.add("[Database Error loading followers]");
        }
        
        if (followers.isEmpty()) {
            followers.add("[No current followers]");
        }
        
        return followers;
    }
}