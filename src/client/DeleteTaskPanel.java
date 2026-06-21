package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class DeleteTaskPanel extends JPanel {

    private JList<Task> taskList;
    private DefaultListModel<Task> listModel;
    private JButton deleteBtn;

    public DeleteTaskPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 254));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createTaskList(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadTasks();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Delete Task");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(37, 60, 96));

        JLabel subtitle = new JLabel("Select a task from the list below to delete");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(102, 119, 146));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JScrollPane createTaskList() {
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskListCellRenderer());
        taskList.setBackground(Color.WHITE);
        taskList.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235), 1));

        JScrollPane scroll = new JScrollPane(taskList);
        scroll.setPreferredSize(new Dimension(0, 300));
        return scroll;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        deleteBtn = new JButton("Delete Selected Task");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(200, 44));
        deleteBtn.addActionListener(e -> deleteTask());

        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setBackground(new Color(108, 117, 125));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadTasks());

        panel.add(refreshBtn);
        panel.add(deleteBtn);
        return panel;
    }

    private void loadTasks() {
        listModel.clear();
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
            JOptionPane.showMessageDialog(this, "❌ Server unavailable");
        }
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
                        listModel.addElement(new Task(cols[0], cols[1], cols[2], cols[3], cols[4], cols[5], cols[6]));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            // ignore and try next
        }
        return false;
    }

    private void deleteTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "❌ Select a task to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete task: " + selected.title + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        String host = client.ClientConfig.getHost();
        int[] ports = client.ClientConfig.getPorts();
        boolean ok = false;
        for (int p : ports) {
            if (sendDeleteRequest(host, p, selected.id)) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            JOptionPane.showMessageDialog(this, "❌ Server unavailable or delete failed");
        }
    }

    private boolean sendDeleteRequest(String host, int port, String taskId) {
        try (Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("DELETE_TASK|" + taskId);
            String res = in.readLine();

            if ("TASK_DELETED".equals(res)) {
                JOptionPane.showMessageDialog(this, "✅ Task deleted");
                loadTasks();
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    // --- Inner Classes MUST be inside the outer class if they are private ---

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

        @Override
        public String toString() {
            return title;
        }
    }

    private static class TaskListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task) {
                Task task = (Task) value;
                String descSnippet = task.description.length() > 50 ? task.description.substring(0, 50) + "..."
                        : task.description;

                setText("<html><div style='padding:5px;'><b>" + task.title + "</b><br>" +
                        "<small>" + descSnippet + "</small><br>" +
                        "<small style='color:gray;'>Status: " + task.status + " | Priority: " + task.priority
                        + "</small></div></html>");
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }

            if (isSelected) {
                setBackground(new Color(0, 123, 198));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}