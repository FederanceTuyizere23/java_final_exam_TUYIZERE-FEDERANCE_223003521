package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245); 
    private static final Color PANEL_LIGHT = Color.WHITE; 
    private static final Color TEXT_BLACK = Color.BLACK; 
    private static final Color BUTTON_PURPLE = new Color(128, 0, 128); 
    private static final Color BUTTON_TEXT = Color.WHITE; 

    public LoginForm() {
        setTitle("Login - Social Platform");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_LIGHT);

        JPanel panel = new JPanel();
        panel.setBackground(PANEL_LIGHT);
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(TEXT_BLACK);
        usernameField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(TEXT_BLACK);
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");
        loginButton.setBackground(BUTTON_PURPLE);
        loginButton.setForeground(BUTTON_TEXT);
        loginButton.setFocusPainted(false);

        registerButton = new JButton("Go to Register");
        registerButton.setBackground(BUTTON_PURPLE);
        registerButton.setForeground(BUTTON_TEXT);
        registerButton.setFocusPainted(false);

        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(registerButton);
        panel.add(loginButton);

        add(panel);

        // Button Action 
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterForm().setVisible(true);
                dispose();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword()).trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Username and Password are required.");
                    return;
                }

                if (authenticateUser(user, pass)) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Login successful!");
                    dispose();
                    new Dashboard(user).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Invalid username or password.");
                }
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        String sql = "SELECT username FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBconnection.getConnection()) {
            if (conn == null) return false;

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, password);

                try (ResultSet rs = pst.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error during login: " + ex.getMessage());
            return false;
        }
    }
}
