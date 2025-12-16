package com.socialplatform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/social_platform_db"; 
            String user = "root"; 
            String pass = "";    

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Connected to database successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed! Check your server status, credentials, and JDBC driver.");
            e.printStackTrace();
        }
        return conn;
    }
}