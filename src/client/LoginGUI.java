package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private JButton loginBtn;
    private JButton registerBtn;

    public LoginGUI() {
        setTitle("Distributed Work Management System - Login");
        setSize(700, 500);
        setMinimumSize(new Dimension(640, 460));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(240, 244, 252));
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(outer);

        outer.add(createLeftPanel(), BorderLayout.WEST);
        outer.add(createFormPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setBackground(new Color(14, 46, 97));
        left.setPreferredSize(new Dimension(260, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(40, 24, 40, 24));

        JLabel title = new JLabel("WorkManager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html>Smart task tracking and<br>collaboration</html>");
        tagline.setForeground(new Color(194, 213, 242));
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel features = new JLabel("<html>• Fast login<br>• Secure access<br>• Easy task control</html>");
        features.setForeground(new Color(184, 204, 236));
        features.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        features.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(Box.createRigidArea(new Dimension(0, 20)));
        left.add(tagline);
        left.add(Box.createRigidArea(new Dimension(0, 24)));
        left.add(features);

        return left;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(26, 33, 66));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to access your workspace");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(103, 113, 139));

        gbc.gridy++;
        formPanel.add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.weightx = 0;

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(new Color(45, 61, 96));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 209, 224), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(new Color(45, 61, 96));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 209, 224), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;

        loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(0, 123, 198));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(0, 48));
        formPanel.add(loginBtn, gbc);

        gbc.gridy++;
        registerBtn = new JButton("CREATE NEW ACCOUNT");
        registerBtn.setBackground(new Color(95, 100, 179));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(0, 44));
        formPanel.add(registerBtn, gbc);

        gbc.gridy++;
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(186, 62, 80));
        formPanel.add(statusLabel, gbc);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegisterGUI();
            dispose();
        });
        passwordField.addActionListener(e -> login());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(255, 255, 255));
        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1));

        return wrapper;
    }

    private String validateLoginInput(String username, String password) {
        if (username.isEmpty())
            return "Username is required";
        if (username.length() < 3 || username.length() > 20)
            return "Username must be 3-20 characters";
        if (!username.matches("[a-zA-Z0-9_]+"))
            return "Username is alphanumeric only";
        if (password.isEmpty())
            return "Password is required";
        if (password.length() < 6)
            return "Password must be at least 6 characters";
        return null;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        String validationError = validateLoginInput(username, password);
        if (validationError != null) {
            statusLabel.setForeground(new Color(186, 62, 80));
            statusLabel.setText(validationError);
            return;
        }

        loginBtn.setEnabled(false);
        statusLabel.setForeground(new Color(46, 125, 50));
        statusLabel.setText("Connecting to server...");

        // Try configured ports
        int[] ports = client.ClientConfig.getPorts();
        boolean connected = false;
        for (int p : ports) {
            if (attemptLogin(username, password, p)) {
                connected = true;
                break;
            }
        }

        if (!connected) {
            statusLabel.setForeground(new Color(186, 62, 80));
            statusLabel.setText("Server unavailable");
            JOptionPane.showMessageDialog(this,
                    "Unable to connect to server. Please ensure the server is running and reachable.");
        }
        loginBtn.setEnabled(true);
    }

    private boolean attemptLogin(String username, String password, int port) {
        String host = client.ClientConfig.getHost();
        try (Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("LOGIN|" + username + "|" + password);
            String response = in.readLine();

            if (response == null)
                return false;

            if ("SUCCESS".equals(response)) {
                statusLabel.setForeground(new Color(46, 125, 50));
                statusLabel.setText("Login Successful!");
                JOptionPane.showMessageDialog(this, "Welcome " + username);
                new MainDashboard(username); // Ensure MainDashboard is defined
                dispose();
                return true;
            } else if ("SERVER_UNAVAILABLE".equals(response)) {
                return false; // treat as connection failure so fallback can try other ports
            } else {
                statusLabel.setForeground(new Color(186, 62, 80));
                statusLabel.setText("Invalid credentials");
                return true; // Connection worked, but credentials failed
            }
        } catch (Exception e) {
            return false; // Connection failed
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}