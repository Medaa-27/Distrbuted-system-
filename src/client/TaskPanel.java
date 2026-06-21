package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TaskPanel extends JPanel {

    public static TaskPanel instance;
    private JPanel tasksContainer;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;
    private List<Task> allTasks = new ArrayList<>();
    private String currentUser;

    public TaskPanel(String user) {
        instance = this;
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 254));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createTopBar(), BorderLayout.NORTH);
        add(createTasksArea(), BorderLayout.CENTER);

        loadTasks();
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filters.setOpaque(false);

        filters.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTasks();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTasks();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTasks();
            }
        });
        filters.add(searchField);

        filters.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[] { "All", "Pending", "Completed" });
        statusFilter.addActionListener(e -> filterTasks());
        filters.add(statusFilter);

        filters.add(new JLabel("Priority:"));
        priorityFilter = new JComboBox<>(new String[] { "All", "Low", "Medium", "High" });
        priorityFilter.addActionListener(e -> filterTasks());
        filters.add(priorityFilter);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0, 123, 200));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadTasks());
        filters.add(refreshBtn);

        top.add(filters, BorderLayout.WEST);
        return top;
    }

    private JScrollPane createTasksArea() {
        tasksContainer = new JPanel();
        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        tasksContainer.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tasksContainer);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        return scroll;
    }

    public void loadTasks() {
        allTasks.clear();
        int[] ports = client.ClientConfig.getPorts();
        String host = client.ClientConfig.getHost();
        boolean success = false;
        for (int p : ports) {
            if (fetchTasksFromServer(host, p)) {
                success = true;
                break;
            }
        }
        if (!success) {
            JOptionPane.showMessageDialog(this, "❌ Connection failed to all servers.");
        }
        filterTasks();
    }

    private boolean fetchTasksFromServer(String host, int port) {
        try (Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("VIEW_TASKS");
            String response = in.readLine();

            if (response != null && response.startsWith("TASKS")) {
                String[] rows = response.split("\\|");
                for (int i = 1; i < rows.length; i++) {
                    String[] cols = rows[i].split(",");
                    if (cols.length >= 7) {
                        allTasks.add(new Task(cols[0], cols[1], cols[2], cols[3], cols[4], cols[5], cols[6]));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void filterTasks() {
        tasksContainer.removeAll();
        String search = searchField.getText().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();
        String priority = (String) priorityFilter.getSelectedItem();

        for (Task task : allTasks) {
            boolean matches = true;
            if (!search.isEmpty() && !task.title.toLowerCase().contains(search)
                    && !task.description.toLowerCase().contains(search)) {
                matches = false;
            }
            if (!"All".equals(status) && !task.status.equals(status)) {
                matches = false;
            }
            if (!"All".equals(priority) && !task.priority.equals(priority)) {
                matches = false;
            }
            if (matches) {
                tasksContainer.add(createTaskCard(task));
                tasksContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        tasksContainer.revalidate();
        tasksContainer.repaint();
    }

    private JPanel createTaskCard(Task task) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                new EmptyBorder(16, 16, 16, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);

        JLabel title = new JLabel(task.title);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(37, 60, 96));

        JLabel desc = new JLabel("<html><body style='width:300px;'>" + task.description + "</body></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(102, 119, 146));

        left.add(title, BorderLayout.NORTH);
        left.add(desc, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridLayout(3, 1, 0, 4));
        right.setOpaque(false);

        JLabel statusLbl = new JLabel("Status: " + task.status);
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLbl.setForeground(task.status.equals("Completed") ? new Color(46, 125, 50) : new Color(186, 62, 80));

        JLabel priorityLbl = new JLabel("Priority: " + task.priority);
        priorityLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priorityLbl.setForeground(getPriorityColor(task.priority));

        JLabel dueLbl = new JLabel("Due: " + task.dueDate);
        dueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dueLbl.setForeground(new Color(87, 103, 143));

        right.add(statusLbl);
        right.add(priorityLbl);
        right.add(dueLbl);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private Color getPriorityColor(String priority) {
        // Updated to traditional switch for better compatibility
        switch (priority) {
            case "High":
                return new Color(186, 62, 80);
            case "Medium":
                return new Color(255, 152, 0);
            case "Low":
                return new Color(46, 125, 50);
            default:
                return Color.BLACK;
        }
    }

    private static class Task {
        String id, title, description, status, user, priority, dueDate;

        Task(String id, String title, String description, String status, String user, String priority, String dueDate) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.status = status;
            this.user = user;
            this.priority = priority;
            this.dueDate = dueDate;
        }
    }
}
