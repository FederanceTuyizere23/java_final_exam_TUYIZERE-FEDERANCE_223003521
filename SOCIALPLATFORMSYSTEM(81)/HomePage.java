package com.socialplatform;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245); 
    private static final Color PANEL_LIGHT = Color.WHITE; 
    private static final Color TEXT_BLACK = Color.BLACK; 
    private static final Color ACCENT_PURPLE = new Color(128, 0, 128); 

    public HomePage(String username) {
        setTitle("Home - Social Platform");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //  background
        getContentPane().setBackground(BACKGROUND_LIGHT);

        // Panel to hold label
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setLayout(new BorderLayout());

        // Welcome label
        JLabel lblWelcome = new JLabel("Welcome to Social Platform, " + username + "!");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(ACCENT_PURPLE);

        panel.add(lblWelcome, BorderLayout.CENTER);
        add(panel);
    }
}
