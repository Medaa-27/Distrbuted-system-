
package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainDashboard extends JFrame {

    private final String username;
    private final CardLayout contentCards = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentCards);
    private JLabel sectionTitle;
    private JButton dashboardButton;
    private JButton tasksButton;
    private JButton addTaskButton;
    private JButton updateTaskButton;
    private JButton deleteTaskButton;

    public MainDashboard(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Distributed Work Management System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        add(createContentArea(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(14, 85, 129));
        menuBar.setBorderPainted(false);

        JMenu menu = new JMenu("Menu");
        menu.setForeground(Color.WHITE);
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(logoutItem);
        menu.addSeparator();
        menu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(menu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 102, 153));
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setForeground(Color.WHITE);

        sectionTitle = new JLabel("Dashboard");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sectionTitle.setForeground(Color.WHITE);

        JLabel time = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        time.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        time.setForeground(Color.WHITE);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(welcome, BorderLayout.NORTH);
        left.add(sectionTitle, BorderLayout.SOUTH);

        header.add(left, BorderLayout.WEST);
        header.add(time, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(18, 33, 71));
        sidebar.setPreferredSize(new Dimension(230, 0));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(20, 16, 20, 16));

        JLabel brand = new JLabel("WorkManager");
        brand.setForeground(new Color(174, 214, 241));
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(brand);

        JLabel tagline = new JLabel("Organize, track, deliver");
        tagline.setForeground(new Color(181, 202, 226));
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(tagline);

        topPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        sidebar.add(topPanel, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(0, 10, 20, 10));

        dashboardButton = createSidebarButton("Dashboard");
        tasksButton = createSidebarButton("Task Board");
        addTaskButton = createSidebarButton("Add Task");
        updateTaskButton = createSidebarButton("Update Task");
        deleteTaskButton = createSidebarButton("Delete Task");

        nav.add(dashboardButton);
        nav.add(Box.createRigidArea(new Dimension(0, 8)));
        nav.add(tasksButton);
        nav.add(Box.createRigidArea(new Dimension(0, 8)));
        nav.add(addTaskButton);
        nav.add(Box.createRigidArea(new Dimension(0, 8)));
        nav.add(updateTaskButton);
        nav.add(Box.createRigidArea(new Dimension(0, 8)));
        nav.add(deleteTaskButton);

        sidebar.add(nav, BorderLayout.CENTER);

        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        foot.setBorder(new EmptyBorder(0, 16, 16, 16));
        JLabel version = new JLabel("v1.0 • Distributed System");
        version.setForeground(new Color(181, 202, 226));
        version.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        foot.add(version, BorderLayout.SOUTH);
        sidebar.add(foot, BorderLayout.SOUTH);

        setActiveButton(dashboardButton);
        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(22, 45, 96));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.addActionListener(e -> switchSection(text));
        return button;
    }

    private JPanel createContentArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel headerDetail = new JLabel("Efficient team collaboration and task tracking");
        headerDetail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        headerDetail.setForeground(new Color(56, 62, 83));
        top.add(headerDetail, BorderLayout.WEST);

        JButton refresh = new JButton("Refresh Tasks");
        refresh.setBackground(new Color(0, 123, 200));
        refresh.setForeground(Color.WHITE);
        refresh.setFocusPainted(false);
        refresh.addActionListener(e -> {
            if (TaskPanel.instance != null) {
                TaskPanel.instance.loadTasks();
            }
        });
        top.add(refresh, BorderLayout.EAST);

        main.add(top, BorderLayout.NORTH);

        contentPanel.add(new OverviewPanel(username), "Dashboard");
        contentPanel.add(new TaskPanel(username), "Task Board");
        contentPanel.add(new AddTaskPanel(username), "Add Task");
        contentPanel.add(new UpdateTaskPanel(), "Update Task");
        contentPanel.add(new DeleteTaskPanel(), "Delete Task");

        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        statusBar.setBackground(new Color(238, 241, 246));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 215, 230)));
        JLabel status = new JLabel("Ready | Connected to server");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        status.setForeground(new Color(87, 103, 143));
        statusBar.add(status);
        return statusBar;
    }

    private void switchSection(String section) {
        sectionTitle.setText(section);
        contentCards.show(contentPanel, section);
        setActiveButton(getButtonForSection(section));
    }

    private JButton getButtonForSection(String section) {
        switch (section) {
            case "Dashboard":
                return dashboardButton;
            case "Task Board":
                return tasksButton;
            case "Add Task":
                return addTaskButton;
            case "Update Task":
                return updateTaskButton;
            case "Delete Task":
                return deleteTaskButton;
            default:
                return dashboardButton;
        }
    }

    private void setActiveButton(JButton activeButton) {
        for (JButton button : new JButton[] { dashboardButton, tasksButton, addTaskButton, updateTaskButton,
                deleteTaskButton }) {
            button.setBackground(button == activeButton ? new Color(0, 123, 200) : new Color(22, 45, 96));
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginGUI();
            dispose();
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Distributed Work Management System\n"
                        + "Course: Distributed Systems\n"
                        + "Aksum University",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
