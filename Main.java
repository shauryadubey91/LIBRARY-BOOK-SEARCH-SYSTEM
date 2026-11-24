import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {

    private static final String FILE = "books.txt";

    private ArrayList<Book> books = new ArrayList<>();

    private DefaultTableModel model;
    private JTable table;

    private JTextField titleField = new JTextField();
    private JTextField authorField = new JTextField();
    private JTextField searchField = new JTextField();

    private JLabel status = new JLabel("Welcome to Library Management System!");

    // BOOK CLASS
    static class Book {
        String title;
        String author;

        Book(String title, String author) {
            this.title = title;
            this.author = author;
        }
    }

    // CONSTRUCTOR
    public Main() {

        setTitle("Library Book Search System");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        loadBooks();

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(25, 25, 20, 25));
        root.setBackground(new Color(245, 247, 251));

        setContentPane(root);

        // HEADER
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("LIBRARY BOOK SEARCH SYSTEM");
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(new Color(30, 55, 90));

        JLabel subtitle = new JLabel(
                "Manage Books | Search Books | Organize Library"
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        header.add(heading);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        // MAIN CENTER
        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);

        root.add(center, BorderLayout.CENTER);

        // LEFT PANEL
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(280, 400));
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        center.add(left, BorderLayout.WEST);

        // ADD BOOK PANEL
        JPanel addPanel = createCard();

        addPanel.setLayout(new BoxLayout(
                addPanel, BoxLayout.Y_AXIS
        ));

        addPanel.add(createLabel("ADD NEW BOOK"));

        addPanel.add(Box.createVerticalStrut(15));

        addPanel.add(new JLabel("Book Title"));

        styleField(titleField);
        addPanel.add(titleField);

        addPanel.add(Box.createVerticalStrut(12));

        addPanel.add(new JLabel("Author Name"));

        styleField(authorField);
        addPanel.add(authorField);

        addPanel.add(Box.createVerticalStrut(15));

        JButton addButton = primaryButton("ADD BOOK");

        addButton.addActionListener(e -> addBook());

        addPanel.add(addButton);

        left.add(addPanel);

        left.add(Box.createVerticalStrut(15));

        // SEARCH PANEL
        JPanel searchPanel = createCard();

        searchPanel.setLayout(new BoxLayout(
                searchPanel, BoxLayout.Y_AXIS
        ));

        searchPanel.add(createLabel("SEARCH BOOK"));

        searchPanel.add(Box.createVerticalStrut(15));

        searchPanel.add(new JLabel("Enter Title / Author"));

        styleField(searchField);
        searchPanel.add(searchField);

        searchPanel.add(Box.createVerticalStrut(12));

        JButton titleButton = primaryButton("SEARCH BY TITLE");

        titleButton.addActionListener(e -> searchByTitle());

        JButton authorButton = secondaryButton("SEARCH BY AUTHOR");

        authorButton.addActionListener(e -> searchByAuthor());

        JButton showButton = secondaryButton("DISPLAY ALL BOOKS");

        showButton.addActionListener(e -> {
            refreshTable();
            searchField.setText("");
            status.setText("Displaying all books.");
        });

        searchPanel.add(titleButton);

        searchPanel.add(Box.createVerticalStrut(8));

        searchPanel.add(authorButton);

        searchPanel.add(Box.createVerticalStrut(8));

        searchPanel.add(showButton);

        left.add(searchPanel);

        // TABLE PANEL
        JPanel tablePanel = createCard();

        tablePanel.setLayout(new BorderLayout(10, 10));

        tablePanel.add(
                createLabel("BOOK COLLECTION"),
                BorderLayout.NORTH
        );

        model = new DefaultTableModel(
                new String[]{"No.", "Book Title", "Author"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(35);

        table.setFont(new Font("Arial", Font.PLAIN, 13));

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scroll = new JScrollPane(table);

        tablePanel.add(scroll, BorderLayout.CENTER);

        center.add(tablePanel, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new BorderLayout());

        footer.setOpaque(false);

        footer.add(status, BorderLayout.CENTER);

        JButton saveButton = secondaryButton("SAVE BOOKS");

        saveButton.addActionListener(e -> {
            saveBooks();
            status.setText("Books saved successfully!");
        });

        JButton exitButton = primaryButton("EXIT");

        exitButton.addActionListener(e -> exitApp());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setOpaque(false);

        buttonPanel.add(saveButton);
        buttonPanel.add(exitButton);

        footer.add(buttonPanel, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);

        refreshTable();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApp();
            }
        });
    }

    // CARD DESIGN
    private JPanel createCard() {

        JPanel panel = new JPanel();

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),
                        new EmptyBorder(18, 18, 18, 18)
                )
        );

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    // LABEL
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 14));

        label.setForeground(new Color(30, 55, 90));

        return label;
    }

    // TEXT FIELD DESIGN
    private void styleField(JTextField field) {

        field.setMaximumSize(new Dimension(500, 38));

        field.setFont(new Font("Arial", Font.PLAIN, 13));

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // PRIMARY BUTTON
private JButton primaryButton(String text) {

    JButton button = new JButton(text);

    button.setBackground(new Color(40, 100, 190));
    button.setForeground(Color.BLACK);

    button.setFont(new Font("Arial", Font.BOLD, 13));

    button.setFocusPainted(false);
    button.setOpaque(true);
    button.setContentAreaFilled(true);
    button.setBorderPainted(false);

    button.setBorder(new EmptyBorder(12, 15, 12, 15));

    return button;
}

   // SECONDARY BUTTON
private JButton secondaryButton(String text) {

    JButton button = new JButton(text);

    button.setBackground(new Color(225, 235, 250));
    button.setForeground(Color.BLACK);

    button.setFont(new Font("Arial", Font.BOLD, 13));

    button.setFocusPainted(false);
    button.setOpaque(true);
    button.setContentAreaFilled(true);
    button.setBorderPainted(false);

    button.setBorder(new EmptyBorder(12, 15, 12, 15));

    return button;
}

    // ADD BOOK
    private void addBook() {

        String title = titleField.getText().trim();

        String author = authorField.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter book title and author name!"
            );

            return;
        }

        if (title.contains("\t") || author.contains("\t")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tab characters are not allowed!"
            );

            return;
        }

        books.add(new Book(title, author));

        sortBooks();

        refreshTable();

        titleField.setText("");

        authorField.setText("");

        status.setText("Book added successfully: " + title);
    }

    // SEARCH BY TITLE
    // BINARY SEARCH
    private void searchByTitle() {

        String target = searchField.getText().trim();

        if (target.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Enter book title!"
            );

            return;
        }

        int index = binarySearch(target);

        if (index != -1) {

            Book b = books.get(index);

            table.setRowSelectionInterval(index, index);

            table.scrollRectToVisible(
                    table.getCellRect(index, 0, true)
            );

            status.setText(
                    "Book Found: " + b.title
                    + " | Author: " + b.author
            );

        } else {

            JOptionPane.showMessageDialog(
                    this, "Book Not Found!"
            );

            status.setText("Book not found!");
        }
    }

    // BINARY SEARCH METHOD
    private int binarySearch(String target) {

        int low = 0;

        int high = books.size() - 1;

        while (low <= high) {

            int mid = low + (high - low) / 2;

            int result = books.get(mid).title
                    .compareToIgnoreCase(target);

            if (result == 0) {

                return mid;
            }

            else if (result < 0) {

                low = mid + 1;
            }

            else {

                high = mid - 1;
            }
        }

        return -1;
    }

    // SEARCH BY AUTHOR
    // LINEAR SEARCH
    private void searchByAuthor() {

        String target = searchField.getText().trim();

        if (target.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Enter author name!"
            );

            return;
        }

        table.clearSelection();

        boolean found = false;

        for (int i = 0; i < books.size(); i++) {

            Book b = books.get(i);

            if (b.author.equalsIgnoreCase(target)) {

                table.addRowSelectionInterval(i, i);

                found = true;
            }
        }

        if (found) {

            status.setText("Author books found: " + target);

        } else {

            JOptionPane.showMessageDialog(
                    this, "Author Not Found!"
            );

            status.setText("Author not found!");
        }
    }

    // SORT BOOKS
    private void sortBooks() {

        books.sort(
                Comparator.comparing(
                        b -> b.title,
                        String.CASE_INSENSITIVE_ORDER
                )
        );
    }

    // DISPLAY BOOKS
    private void refreshTable() {

        sortBooks();

        model.setRowCount(0);

        for (int i = 0; i < books.size(); i++) {

            Book b = books.get(i);

            model.addRow(new Object[]{
                    i + 1,
                    b.title,
                    b.author
            });
        }
    }

    // LOAD BOOKS FROM FILE
    private void loadBooks() {

        Path path = Paths.get(FILE);

        if (!Files.exists(path)) {
            return;
        }

        try (
                BufferedReader reader = Files.newBufferedReader(
                        path, StandardCharsets.UTF_8
                )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split("\t", 2);

                if (data.length == 2
                        && !data[0].trim().isEmpty()
                        && !data[1].trim().isEmpty()) {

                    books.add(
                            new Book(
                                    data[0].trim(),
                                    data[1].trim()
                            )
                    );
                }
            }

            sortBooks();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this, "Error loading books!"
            );
        }
    }

    // SAVE BOOKS TO FILE
    private void saveBooks() {

        sortBooks();

        try (
                BufferedWriter writer = Files.newBufferedWriter(
                        Paths.get(FILE),
                        StandardCharsets.UTF_8
                )
        ) {

            for (Book b : books) {

                writer.write(b.title + "\t" + b.author);

                writer.newLine();
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this, "Error saving books!"
            );
        }
    }

    // EXIT
    private void exitApp() {

        saveBooks();

        dispose();

        System.exit(0);
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Main().setVisible(true);
        });
    }
}