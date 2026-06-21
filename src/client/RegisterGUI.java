package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private JButton registerBtn;
    private JButton backBtn;

    public RegisterGUI() {
        setTitle("Distributed Work Management System - Register");
        setSize(760, 540);
        setMinimumSize(new Dimension(700, 500));
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
        left.setPreferredSize(new Dimension(280, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(40, 24, 40, 24));

        JLabel title = new JLabel("Join WorkManager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Build your workflow with confidence");
        tagline.setForeground(new Color(194, 213, 242));
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prompt = new JLabel("<html>Create your account to start<br>managing tasks now.</html>");
        prompt.setForeground(new Color(184, 204, 236));
        prompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        prompt.setBorder(new EmptyBorder(16, 0, 24, 0));

        JLabel benefits = new JLabel(
                "<html>• Secure credentials<br>• Easy team setup<br>• Simple task workflow</html>");
        benefits.setForeground(new Color(184, 204, 236));
        benefits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        benefits.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(Box.createRigidArea(new Dimension(0, 16)));
        left.add(tagline);
        left.add(Box.createRigidArea(new Dimension(0, 16)));
        left.add(prompt);
        left.add(benefits);

        return left;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(26, 33, 66));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        JLabel subtitle = new JLabel("Register and start organizing tasks");
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
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 209, 224), 1));
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
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 209, 224), 1));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setForeground(new Color(45, 61, 96));
        formPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(200, 209, 224), 1));
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;

        registerBtn = new JButton("REGISTER");
        registerBtn.setBackground(new Color(0, 123, 198));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(0, 48));
        formPanel.add(registerBtn, gbc);

        gbc.gridy++;
        backBtn = new JButton("BACK TO LOGIN");
        backBtn.setBackground(new Color(95, 100, 179));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(new Dimension(0, 44));
        formPanel.add(backBtn, gbc);

        gbc.gridy++;
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(186, 62, 80));
        formPanel.add(statusLabel, gbc);

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            new LoginGUI();
            dispose();
        });
        confirmPasswordField.addActionListener(e -> register());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1));

        return wrapper;
    }

    private String validateRegisterInput(String username, String password, String confirmPassword) {
        if (username.isEmpty())
            return "Username is required";
        if (username.length() < 3 || username.length() > 20)
            return "Username must be 3-20 characters";
        if (!username.matches("[a-zA-Z0-9_]+"))
            return "Username can only contain letters, numbers, and underscores";
        if (password.isEmpty())
            return "Password is required";
        if (password.length() < 6)
            return "Password must be at least 6 characters";
        if (!password.equals(confirmPassword))
            return "Passwords do not match";
        return null;
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        String validationError = validateRegisterInput(username, password, confirmPassword);
        if (validationError != null) {
            statusLabel.setForeground(new Color(186, 62, 80));
            statusLabel.setText(validationError);
            return;
        }

        registerBtn.setEnabled(false);
        statusLabel.setForeground(new Color(46, 125, 50));
        statusLabel.setText("Registering...");

        int[] ports = client.ClientConfig.getPorts();
        boolean connected = false;
        for (int p : ports) {
            if (attemptRegister(p, username, password)) {
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

        registerBtn.setEnabled(true);
    }

    private boolean attemptRegister(int port, String username, String password) {
        String host = client.ClientConfig.getHost();
        try (Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("REGISTER|" + username + "|" + password);
            String response = in.readLine();

            if (response == null)
                return false;

            if ("REGISTER_SUCCESS".equals(response)) {
                statusLabel.setForeground(new Color(46, 125, 50));
                statusLabel.setText("Registration Successful!");
                JOptionPane.showMessageDialog(this, "User registered successfully! You can now login.");
                new LoginGUI();
                dispose();
                return true;
            } else if ("SERVER_UNAVAILABLE".equals(response)) {
                return false; // treat as connection failure so fallback can try other ports
            } else if ("USER_EXISTS".equals(response)) {
                statusLabel.setForeground(new Color(186, 62, 80));
                statusLabel.setText("Username already exists");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterGUI::new);
    }
}