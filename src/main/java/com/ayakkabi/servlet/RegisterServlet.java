package com.ayakkabi.servlet;

import com.ayakkabi.model.User;
import com.ayakkabi.util.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("kayit.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Şifre kontrolü
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Şifreler eşleşmiyor!");
            request.getRequestDispatcher("kayit.html").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // Gerçek uygulamada şifre hash'lenmelidir
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // Kullanıcı başarıyla kaydedildi
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                response.sendRedirect("giris.html");
            } else {
                request.setAttribute("error", "Kayıt işlemi başarısız!");
                request.getRequestDispatcher("kayit.html").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("kayit.html").forward(request, response);
        }
    }
} 