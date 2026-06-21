package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OverviewPanel extends JPanel {

    public OverviewPanel(String username) {
        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(248, 250, 254));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel hero = new JPanel(new BorderLayout());
        hero.setBackground(new Color(231, 244, 255));
        hero.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Hello, " + username + " — welcome to your workspace");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(21, 48, 86));

        JLabel subtitle = new JLabel("Use the sidebar to browse tasks, add new work, and manage updates with ease.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(86, 101, 131));

        hero.add(title, BorderLayout.NORTH);
        hero.add(subtitle, BorderLayout.SOUTH);

        add(hero, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 18));
        cards.setOpaque(false);

        cards.add(createCard("Task Board", "Browse and refresh open tasks in one place."));
        cards.add(createCard("Add New Task", "Quickly create a task with priority, due date, and status."));
        cards.add(createCard("Updates", "Keep team work on track with easy task updates."));

        add(cards, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(18, 0, 0, 0));

        JLabel prompt = new JLabel("Tip: Use the sidebar buttons to switch screens and keep your process flowing.");
        prompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prompt.setForeground(new Color(96, 112, 141));

        footer.add(prompt, BorderLayout.WEST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardTitle.setForeground(new Color(37, 60, 96));

        JLabel cardDescription = new JLabel("<html><body style='width:180px;'>" + description + "</body></html>");
        cardDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardDescription.setForeground(new Color(102, 119, 146));

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(cardDescription, BorderLayout.CENTER);

        return card;
    }
}
