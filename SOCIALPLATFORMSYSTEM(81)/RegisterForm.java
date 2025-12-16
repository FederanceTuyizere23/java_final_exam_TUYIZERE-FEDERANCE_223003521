package com.socialplatform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // CRITICAL: For ActionListener and ActionEvent
import java.sql.*;       // CRITICAL: For JDBC (Connection, PreparedStatement, etc.)

public class RegisterForm extends JFrame implements ActionListener {
    private JTextField txtUsername, txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnLogin;

    public RegisterForm() {
        setTitle("Register - Social Platform");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        btnRegister = new JButton("Register");
        btnRegister.addActionListener(this);    // Calls the @Override actionPerformed below
        add(btnRegister);

        // Go to Login
        
        btnLogin = new JButton("Go to Login");
        btnLogin.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
        add(btnLogin);
        
    } 

    @Override 
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegister) {
            String username = txtUsername.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword()).trim(); 

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            try (Connection conn = DBconnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Could not connect to database.");
                    return;
                }
                
                String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                try(PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, username);
                    pst.setString(2, email);
                    pst.setString(3, password);

                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "🎉 Registration successful! You can now log in.");
                        txtUsername.setText("");
                        txtEmail.setText("");
                        txtPassword.setText("");
                    }
                }
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Error: Username or Email already in use.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred during registration: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

	}