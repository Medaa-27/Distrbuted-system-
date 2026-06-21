package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateTaskPanel extends JPanel {

    private JTextField idField;
    private JTextField titleField;
    private JTextArea descField;
    private JComboBox<String> statusBox;
    private JComboBox<String> priorityBox;
    private JSpinner dueDateSpinner;
    private JButton updateBtn;

    public UpdateTaskPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 254));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createForm(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Update Task");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(37, 60, 96));

        JLabel subtitle = new JLabel("Modify task details, status, or priority");
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

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Task ID:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        idField = new JTextField();
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idField.setBorder(BorderFactory.createLineBorder(new Color(200, 209, 224), 1));
        idField.setBackground(Color.WHITE);
        form.add(idField, gbc);

        // Title
        gbc.gridx = 0;
        gbc.gridy++;
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

        updateBtn = new JButton("Update Task");
        updateBtn.setBackground(new Color(0, 123, 198));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setFocusPainted(false);
        updateBtn.setPreferredSize(new Dimension(150, 44));
        form.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> updateTask());

        return form;
    }

    private void updateTask() {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String desc = descField.getText().trim();
        String status = statusBox.getSelectedItem().toString();
        String priority = priorityBox.getSelectedItem().toString();
        Date selectedDate = (Date) dueDateSpinner.getValue();
        String dueDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        if (id.isEmpty() || title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Fill all fields");
            return;
        }

        String host = client.ClientConfig.getHost();
        int[] ports = client.ClientConfig.getPorts();
        boolean done = false;
        for (int p : ports) {
            try (Socket socket = new Socket(host, p)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println(
                        "UPDATE_TASK|" + id + "|" + title + "|" + desc + "|" + status + "|" + priority + "|" + dueDate);

                String res = in.readLine();

                if ("TASK_UPDATED".equals(res)) {
                    JOptionPane.showMessageDialog(this, "✅ Updated");

                    if (TaskPanel.instance != null) {
                        TaskPanel.instance.loadTasks();
                    }
                    done = true;
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed");
                    done = true;
                    break;
                }
            } catch (Exception ex) {
                // try next port
            }
        }

        if (!done) {
            JOptionPane.showMessageDialog(this, "❌ Server unavailable");
        }
    }
}