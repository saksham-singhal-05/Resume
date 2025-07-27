import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPanel extends JPanel {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn, createBtn;
    JLabel status;

    public interface LoginListener { void onLoginSuccess(String username); }

    public LoginPanel(LoginListener listener) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(700, 600));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        userField = new JTextField(15);
        passField = new JPasswordField(15);
        loginBtn = new JButton("Login");
        createBtn = new JButton("Create Account");
        status = new JLabel(" ");

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(loginBtn, gbc);
        gbc.gridx = 1; add(createBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; add(status, gbc);

        loginBtn.addActionListener(e -> handleLogin(listener));
        createBtn.addActionListener(e -> handleCreate());
    }

    private void handleLogin(LoginListener listener) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                status.setText("Login successful!");
                listener.onLoginSuccess(username);
            } else {
                status.setText("Invalid credentials!");
            }
        } catch (Exception ex) {
            status.setText("DB error");
        }
    }

    private void handleCreate() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            status.setText("Both fields required.");
            return;
        }
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement psTest = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ?");
            psTest.setString(1, username);
            ResultSet rs = psTest.executeQuery();
            if (rs.next()) {
                status.setText("Username already exists!");
            } else {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(username, password) VALUES (?,?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                status.setText("Account created! Now login.");
            }
        } catch (Exception ex) {
            status.setText("DB error");
            ex.printStackTrace();
        }
    }
}
