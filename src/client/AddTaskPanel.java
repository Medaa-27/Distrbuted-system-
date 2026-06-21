package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskPanel extends JPanel {

    private JTextField titleField;
    private JTextArea descField;
    private JComboBox<String> statusBox;
    private JComboBox<String> priorityBox;
    private JSpinner dueDateSpinner;
    private JButton addBtn;
    private String currentUser;

    public AddTaskPanel(String username) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 254));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        this.currentUser = username;

        add(createHeader(), BorderLayout.NORTH);
        add(createForm(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Add New Task");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(37, 60, 96));

        JLabel subtitle = new JLabel("Create a task with details, priority, and due date");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(102, 119, 146));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createLineBorder(new Color(200, 209, 224), 1));
        titleField.setBackground(Color.WHITE);
        form.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descField = new JTextArea(4, 20);
        descField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descField.setBorder(BorderFactory.createLineBorder(new Color(200, 209, 224), 1));
        descField.setBackground(Color.WHITE);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descField);
        descScroll.setPreferredSize(new Dimension(0, 80));
        form.add(descScroll, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        statusBox = new JComboBox<>(new String[] { "Pending", "Completed" });
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(statusBox, gbc);

        // Priority
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Priority:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priorityBox = new JComboBox<>(new String[] { "Low", "Medium", "High" });
        priorityBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(priorityBox, gbc);

        // Due Date
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        form.add(new JLabel("Due Date:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dueDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd");
        dueDateSpinner.setEditor(dateEditor);
        dueDateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(dueDateSpinner, gbc);

        // Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        addBtn = new JButton("Add Task");
        addBtn.setBackground(new Color(0, 123, 198));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(150, 44));
        form.add(addBtn, gbc);

        addBtn.addActionListener(e -> addTask());

        return form;
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String desc = descField.getText().trim();
        String status = statusBox.getSelectedItem().toString();
        String priority = priorityBox.getSelectedItem().toString();
        Date selectedDate = (Date) dueDateSpinner.getValue();
        String normalizedDueDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        if (title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Fill all required fields before adding the task.");
            return;
        }

        String host = ClientConfig.getHost();
        int[] ports = ClientConfig.getPorts();

        boolean sent = false;
        for (int port : ports) {
            try (Socket socket = new Socket(host, port)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("ADD_TASK|" + title + "|" + desc + "|" + status + "|" + this.currentUser + "|"
                        + priority + "|" + normalizedDueDate);

                String res = in.readLine();

                if ("TASK_ADDED".equals(res)) {
                    JOptionPane.showMessageDialog(this, "✅ Task added");

                    if (TaskPanel.instance != null) {
                        TaskPanel.instance.loadTasks();
                    }

                    titleField.setText("");
                    descField.setText("");
                    dueDateSpinner.setValue(new Date());
                    sent = true;
                    break;

                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed");
                    sent = true; // don't fallback if server replied but failed
                    break;
                }
            } catch (Exception connEx) {
                // try next port
            }
        }

        if (!sent) {
            JOptionPane.showMessageDialog(this, "❌ Server unavailable");
        }
    }
}