
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.io.IOException;

public class LibrarySystemito extends JFrame {

    // COLORS
    static final Color PURPLE = new Color(116, 87, 230);
    static final Color DARK_PURPLE = new Color(66, 49, 130);
    static final Color LIGHT_PURPLE = new Color(244, 240, 255);
    static final Color BLUE = new Color(85, 174, 230);
    static final Color PINK = new Color(255, 135, 180);
    static final Color YELLOW = new Color(255, 193, 92);
    static final Color GREEN = new Color(72, 190, 150);
    static final Color BACKGROUND = new Color(247, 246, 252);
    static final Color TEXT = new Color(48, 47, 70);

    // BOOK CLASS
    static class LibraryBook {
        String id;
        String title;
        String category;
        String status;

        LibraryBook(String id, String title,
                    String category, String status) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.status = status;
        }
    }

    ArrayList<LibraryBook> books = new ArrayList<>();

    DefaultTableModel tableModel;
    JTable bookTable;

    JLabel totalValue;
    JLabel availableValue;
    JLabel borrowedValue;
    JLabel welcomeLabel;

    JTextField searchField;

    public LibrarySystemito() {

        setTitle("BookBloom | Library Management");
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // SAMPLE BOOKS
        books.add(new LibraryBook(
            "110709", "Rene", "Comedy", "Available"
        ));

        books.add(new LibraryBook(
            "110710", "Java Rice", "Programming", "Available"
        ));

        books.add(new LibraryBook(
            "110711", "Computer Programming",
            "Education", "Available"
        ));

        // MAIN CONTAINER
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);

        // SIDEBAR
        main.add(createSidebar(), BorderLayout.WEST);

        // CONTENT
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(BACKGROUND);
        content.setBorder(
            BorderFactory.createEmptyBorder(25, 28, 25, 28)
        );

        content.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setBackground(BACKGROUND);

        center.add(createDashboardCards(), BorderLayout.NORTH);
        center.add(createBookSection(), BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);

        main.add(content, BorderLayout.CENTER);

        setContentPane(main);

        refreshTable();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitProgram();
            }
        });
    }

    // SIDEBAR
    JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(DARK_PURPLE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.setBorder(
            BorderFactory.createEmptyBorder(25, 16, 20, 16)
        );

        JLabel logo = new JLabel("  BOOKBLOOM");
        logo.setFont(new Font("Arial", Font.BOLD, 23));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("  Your happy reading place");
        tagline.setFont(new Font("Arial", Font.PLAIN, 11));
        tagline.setForeground(new Color(210, 200, 255));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(tagline);

        sidebar.add(Box.createVerticalStrut(55));

        JLabel menuLabel = new JLabel("  MAIN MENU");
        menuLabel.setFont(new Font("Arial", Font.BOLD, 11));
        menuLabel.setForeground(new Color(190, 180, 230));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(15));

        JButton dashboard = createSidebarButton("  🏠  Dashboard");
        JButton catalog = createSidebarButton("  📚  Book Catalog");
        JButton add = createSidebarButton("  ➕  Add New Book");

        sidebar.add(dashboard);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(catalog);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(add);

        dashboard.addActionListener(e -> {
            bookTable.clearSelection();
            searchField.setText("");
            refreshTable();
            welcomeLabel.setText("Welcome to your library dashboard!");
        });

        catalog.addActionListener(e -> {
            bookTable.clearSelection();
            searchField.setText("");
            refreshTable();
            welcomeLabel.setText("Explore your book collection!");
        });

        add.addActionListener(e -> addBook());

        sidebar.add(Box.createVerticalGlue());

        JLabel bottomText = new JLabel("  HAPPY READING! ✨");
        bottomText.setFont(new Font("Arial", Font.BOLD, 12));
        bottomText.setForeground(new Color(255, 211, 135));
        bottomText.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(bottomText);
        sidebar.add(Box.createVerticalStrut(20));

        JButton logout = createSidebarButton("  ↪  Logout");
        logout.addActionListener(e -> logout());

        sidebar.add(logout);

        return sidebar;
    }

    JButton createSidebarButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(DARK_PURPLE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(210, 48));
        button.setPreferredSize(new Dimension(210, 48));

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(PURPLE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setOpaque(false);
                button.setBackground(DARK_PURPLE);
            }
        });

        return button;
    }

    // HEADER
    JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Library Dashboard ✨");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(TEXT);

        welcomeLabel = new JLabel(
            "Welcome back! Ready for a little reading?"
        );

        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(130, 125, 150));

        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(welcomeLabel);

        JLabel profile = new JLabel("📖  BOOKBLOOM");
        profile.setFont(new Font("Arial", Font.BOLD, 13));
        profile.setForeground(PURPLE);

        header.add(left, BorderLayout.WEST);
        header.add(profile, BorderLayout.EAST);

        return header;
    }

    // DASHBOARD CARDS
    JPanel createDashboardCards() {

        JPanel cards = new JPanel(
            new GridLayout(1, 3, 16, 0)
        );

        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 115));

        JPanel totalCard = createStatCard(
            "📚", "TOTAL BOOKS", "0", PURPLE
        );

        JPanel availableCard = createStatCard(
            "✨", "AVAILABLE BOOKS", "0", BLUE
        );

        JPanel borrowedCard = createStatCard(
            "📖", "BORROWED BOOKS", "0", PINK
        );

        totalValue = (JLabel) totalCard.getClientProperty("value");
        availableValue = (JLabel) availableCard.getClientProperty("value");
        borrowedValue = (JLabel) borrowedCard.getClientProperty("value");

        cards.add(totalCard);
        cards.add(availableCard);
        cards.add(borrowedCard);

        return cards;
    }

    JPanel createStatCard(
        String icon, String title, String value, Color accent
    ) {

        JPanel card = new JPanel(new BorderLayout(12, 0));

        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(235, 232, 245), 1
                ),
                BorderFactory.createEmptyBorder(15, 17, 15, 17)
            )
        );

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconLabel.setForeground(accent);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        titleLabel.setForeground(new Color(135, 130, 150));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 27));
        valueLabel.setForeground(accent);

        details.add(titleLabel);
        details.add(Box.createVerticalStrut(7));
        details.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(details, BorderLayout.CENTER);

        card.putClientProperty("value", valueLabel);

        return card;
    }

    // BOOK SECTION
    JPanel createBookSection() {

        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(Color.WHITE);

        section.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(235, 232, 245), 1
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        );

        // SECTION HEADER
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel sectionTitle = new JLabel("Your Book Collection");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sectionTitle.setForeground(TEXT);

        JLabel sectionSubtitle = new JLabel(
            "Manage and organize your favorite books."
        );

        sectionSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        sectionSubtitle.setForeground(Color.GRAY);

        labels.add(sectionTitle);
        labels.add(Box.createVerticalStrut(5));
        labels.add(sectionSubtitle);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setToolTipText("Search by ID, title, or category");

        JButton searchButton = new RoundedButton(
            "Search", PURPLE
        );

        JPanel searchPanel = new JPanel(new FlowLayout(
            FlowLayout.RIGHT, 8, 0
        ));

        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(e -> searchBook());

        searchField.addActionListener(e -> searchBook());

        top.add(labels, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);

        section.add(top, BorderLayout.NORTH);

        // TABLE
        String[] columns = {
            "BOOK ID", "BOOK TITLE", "CATEGORY", "STATUS"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);

        bookTable.setRowHeight(42);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookTable.setForeground(TEXT);
        bookTable.setBackground(Color.WHITE);
        bookTable.setSelectionBackground(LIGHT_PURPLE);
        bookTable.setSelectionForeground(DARK_PURPLE);
        bookTable.setShowVerticalLines(false);
        bookTable.setGridColor(new Color(240, 238, 247));
        bookTable.setIntercellSpacing(new Dimension(0, 1));

        bookTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        bookTable.getTableHeader().setBackground(
            LIGHT_PURPLE
        );

        bookTable.getTableHeader().setForeground(
            DARK_PURPLE
        );

        bookTable.getTableHeader().setPreferredSize(
            new Dimension(0, 40)
        );

        bookTable.getTableHeader().setReorderingAllowed(false);

        // CENTER TEXT
        DefaultTableCellRenderer centerRenderer =
            new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        for (int i = 0; i < 3; i++) {
            bookTable.getColumnModel()
                .getColumn(i)
                .setCellRenderer(centerRenderer);
        }

        // STATUS COLORS
        bookTable.getColumnModel()
            .getColumn(3)
            .setCellRenderer(new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean selected, boolean focused,
                    int row, int column
                ) {

                    JLabel label = (JLabel)
                        super.getTableCellRendererComponent(
                            table, value, selected,
                            focused, row, column
                        );

                    label.setHorizontalAlignment(
                        SwingConstants.CENTER
                    );

                    label.setFont(
                        new Font("Arial", Font.BOLD, 12)
                    );

                    if (!selected) {
                        if ("Available".equals(value)) {
                            label.setForeground(GREEN);
                        } else {
                            label.setForeground(PINK);
                        }
                    }

                    return label;
                }
            });

        JScrollPane scroll = new JScrollPane(bookTable);
        scroll.setBorder(
            BorderFactory.createLineBorder(
                new Color(240, 238, 247)
            )
        );

        section.add(scroll, BorderLayout.CENTER);

        // ACTION BUTTONS
        JPanel actions = new JPanel(
            new GridLayout(1, 4, 12, 0)
        );

        actions.setOpaque(false);

        JButton addButton = new RoundedButton(
            "＋ Add Book", PURPLE
        );

        JButton borrowButton = new RoundedButton(
            "↗ Borrow", BLUE
        );

        JButton returnButton = new RoundedButton(
            "↩ Return", GREEN
        );

        JButton deleteButton = new RoundedButton(
            "✕ Remove", PINK
        );

        actions.add(addButton);
        actions.add(borrowButton);
        actions.add(returnButton);
        actions.add(deleteButton);

        addButton.addActionListener(e -> addBook());
        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        deleteButton.addActionListener(e -> deleteBook());

        section.add(actions, BorderLayout.SOUTH);

        return section;
    }

    // ADD BOOK
    void addBook() {

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField categoryField = new JTextField();

        Object[] fields = {
            "Book ID:", idField,
            "Book Title:", titleField,
            "Book Category:", categoryField
        };

        int choice = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Add a New Book",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String category = categoryField.getText().trim();

        if (id.isEmpty() || title.isEmpty()
            || category.isEmpty()) {

            showMessage("Please complete all book information!");
            return;
        }

        for (LibraryBook book : books) {
            if (book.id.equalsIgnoreCase(id)) {
                showMessage("That Book ID already exists!");
                return;
            }
        }

        books.add(new LibraryBook(
            id, title, category, "Available"
        ));

        refreshTable();

        showMessage("Your new book has been added! ✨");
    }

    // BORROW BOOK
    void borrowBook() {

        String id = getSelectedOrEnteredId(
            "Enter the Book ID to borrow:"
        );

        if (id == null) return;

        for (LibraryBook book : books) {

            if (book.id.equalsIgnoreCase(id)) {

                if (book.status.equals("Available")) {

                    book.status = "Borrowed";
                    refreshTable();

                    showMessage(
                        "Enjoy reading " + book.title + "! 📖"
                    );

                } else {
                    showMessage("This book is already borrowed!");
                }

                return;
            }
        }

        showMessage("Book not found!");
    }

    // RETURN BOOK
    void returnBook() {

        String id = getSelectedOrEnteredId(
            "Enter the Book ID to return:"
        );

        if (id == null) return;

        for (LibraryBook book : books) {

            if (book.id.equalsIgnoreCase(id)) {

                if (book.status.equals("Borrowed")) {

                    book.status = "Available";
                    refreshTable();

                    showMessage(
                        "Thank you for returning " +
                        book.title + "! ✨"
                    );

                } else {
                    showMessage("This book is already available!");
                }

                return;
            }
        }

        showMessage("Book not found!");
    }

    // DELETE BOOK
    void deleteBook() {

        String id = getSelectedOrEnteredId(
            "Enter the Book ID to remove:"
        );

        if (id == null) return;

        for (LibraryBook book : books) {

            if (book.id.equalsIgnoreCase(id)) {

                int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Remove \"" + book.title + "\" from the library?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    books.remove(book);
                    refreshTable();
                    showMessage("Book removed successfully!");
                }

                return;
            }
        }

        showMessage("Book not found!");
    }

    // GET SELECTED OR ENTERED ID
    String getSelectedOrEnteredId(String message) {

        int row = bookTable.getSelectedRow();

        if (row != -1) {
            return tableModel.getValueAt(
                bookTable.convertRowIndexToModel(row), 0
            ).toString();
        }

        String id = JOptionPane.showInputDialog(
            this, message
        );

        if (id == null) return null;

        return id.trim();
    }

    // SEARCH BOOK
    void searchBook() {

        String keyword = searchField.getText()
            .trim().toLowerCase();

        if (keyword.isEmpty()) {
            keyword = JOptionPane.showInputDialog(
                this,
                "Search by Book ID, Title, or Category:"
            );

            if (keyword == null) return;

            keyword = keyword.trim().toLowerCase();
        }

        if (keyword.isEmpty()) return;

        tableModel.setRowCount(0);

        int matches = 0;

        for (LibraryBook book : books) {

            if (book.id.toLowerCase().contains(keyword)
                || book.title.toLowerCase().contains(keyword)
                || book.category.toLowerCase().contains(keyword)) {

                tableModel.addRow(new Object[] {
                    book.id,
                    book.title,
                    book.category,
                    book.status
                });

                matches++;
            }
        }

        if (matches == 0) {
            showMessage("No matching books found!");
        } else {
            welcomeLabel.setText(
                matches + " matching book(s) found."
            );
        }
    }

    // REFRESH TABLE AND STATISTICS
    void refreshTable() {

        tableModel.setRowCount(0);

        int available = 0;
        int borrowed = 0;

        for (LibraryBook book : books) {

            tableModel.addRow(new Object[] {
                book.id,
                book.title,
                book.category,
                book.status
            });

            if (book.status.equals("Available")) {
                available++;
            } else {
                borrowed++;
            }
        }

        totalValue.setText(String.valueOf(books.size()));
        availableValue.setText(String.valueOf(available));
        borrowedValue.setText(String.valueOf(borrowed));
    }

    // MESSAGE
    void showMessage(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "BOOKBLOOM",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // LOGOUT
    void logout() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // QUIT
    void quitProgram() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to close BookBloom?",
            "Exit Library",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                this,
                "Thanks for visiting BookBloom! Happy reading! 💜"
            );

            System.exit(0);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

// ROUNDED BUTTON WITH HOVER AND CLICK EFFECT
class RoundedButton extends JButton {

    private Color buttonColor;

    RoundedButton(String text, Color color) {

        super(text);

        buttonColor = color;

        setFont(new Font("Arial", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setBorder(
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRolloverEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (getModel().isPressed()) {
            g2.setColor(buttonColor.darker());
            g2.translate(0, 2);
        } else if (getModel().isRollover()) {
            g2.setColor(buttonColor.brighter());
        } else {
            g2.setColor(buttonColor);
        }

        g2.fillRoundRect(
            0, 0, getWidth(), getHeight(), 22, 22
        );

        super.paintComponent(g2);
        g2.dispose();
    }
}

// LOGIN FRAME
class LoginFrame extends JFrame {

    Color PURPLE = new Color(116, 87, 230);
    Color DARK_PURPLE = new Color(66, 49, 130);
    Color LIGHT = new Color(247, 246, 252);

    JTextField usernameField;
    JPasswordField passwordField;
    JCheckBox showPassword;

    LoginFrame() {

        setTitle("BookBloom | Login");
        setSize(480, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(LIGHT);

        // TOP HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DARK_PURPLE);
        header.setBorder(
            BorderFactory.createEmptyBorder(30, 20, 30, 20)
        );

        JLabel logo = new JLabel("📚");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("BOOKBLOOM");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "A little place for big imaginations."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(220, 210, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(10));
        header.add(title);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitle);

        main.add(header, BorderLayout.NORTH);

        // LOGIN FORM
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBackground(LIGHT);
        form.setBorder(
            BorderFactory.createEmptyBorder(25, 50, 25, 50)
        );

        JLabel welcome = new JLabel("Welcome back! ✨");
        welcome.setFont(new Font("Arial", Font.BOLD, 21));
        welcome.setForeground(DARK_PURPLE);

        JLabel instruction = new JLabel(
            "Login to access your library dashboard."
        );
        instruction.setFont(new Font("Arial", Font.PLAIN, 12));
        instruction.setForeground(Color.GRAY);

        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setFont(new Font("Arial", Font.BOLD, 11));

        usernameField = new JTextField();
        styleInput(usernameField);

        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(new Font("Arial", Font.BOLD, 11));

        passwordField = new JPasswordField();
        styleInput(passwordField);

        showPassword = new JCheckBox("Show Password");
        showPassword.setBackground(LIGHT);
        showPassword.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton loginButton = new RoundedButton(
            "LOGIN TO YOUR ACCOUNT", PURPLE
        );

        JButton signupButton = new JButton("Create an account");
        JButton forgotButton = new JButton("Forgot Password?");

        styleLink(signupButton);
        styleLink(forgotButton);

        JPanel links = new JPanel(
            new GridLayout(2, 1, 0, 5)
        );

        links.setBackground(LIGHT);
        links.add(signupButton);
        links.add(forgotButton);

        form.add(welcome);
        form.add(instruction);
        form.add(Box.createVerticalStrut(5));
        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passwordField);
        form.add(showPassword);
        form.add(loginButton);
        form.add(links);

        main.add(form, BorderLayout.CENTER);

        add(main);

        showPassword.addActionListener(e -> {

            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022');
            }
        });

        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> signup());
        forgotButton.addActionListener(e -> forgotPassword());

        getRootPane().setDefaultButton(loginButton);
    }

    void styleInput(JTextField field) {

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 38));

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 215, 235)
                ),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        );
    }

    void styleLink(JButton button) {

        button.setForeground(PURPLE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // LOGIN
    void login() {

        String username = usernameField.getText().trim();
        String password = new String(
            passwordField.getPassword()
        );

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Please enter your username and password."
            );
            return;
        }

        if (UserStore.login(username, password)) {

            JOptionPane.showMessageDialog(
                this,
                "Welcome to BookBloom, " + username + "! 💜"
            );

            dispose();
            new LibrarySystemito().setVisible(true);

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Incorrect username or password!",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
        }
    }

    // SIGN UP
    void signup() {

        JTextField newUsername = new JTextField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();
        JTextField recoveryAnswer = new JTextField();

        Object[] fields = {
            "Create Username:", newUsername,
            "Create Password:", newPassword,
            "Confirm Password:", confirmPassword,
            "Favorite Color (Recovery Answer):", recoveryAnswer
        };

        int choice = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Create Your BookBloom Account",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (choice != JOptionPane.OK_OPTION) return;

        String username = newUsername.getText().trim();
        String password = new String(newPassword.getPassword());
        String confirm = new String(confirmPassword.getPassword());
        String answer = recoveryAnswer.getText().trim();

        if (username.isEmpty() || password.isEmpty()
            || confirm.isEmpty() || answer.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Please complete all fields!"
            );
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(
                this, "Password must have at least 6 characters!"
            );
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(
                this, "Passwords do not match!"
            );
            return;
        }

        if (UserStore.usernameExists(username)) {
            JOptionPane.showMessageDialog(
                this, "Username already exists!"
            );
            return;
        }

        if (UserStore.register(username, password, answer)) {

            JOptionPane.showMessageDialog(
                this,
                "Your account has been created! Welcome to BookBloom! 💜"
            );

            usernameField.setText(username);
            passwordField.setText("");

        } else {
            JOptionPane.showMessageDialog(
                this, "Unable to create account. Please try again."
            );
        }
    }

    // FORGOT PASSWORD
    void forgotPassword() {

        JTextField usernameInput = new JTextField();
        JTextField answerInput = new JTextField();

        Object[] fields = {
            "Enter your username:", usernameInput,
            "What is your favorite color?", answerInput
        };

        int choice = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Recover Your Account",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (choice != JOptionPane.OK_OPTION) return;

        String username = usernameInput.getText().trim();
        String answer = answerInput.getText().trim();

        if (username.isEmpty() || answer.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Please complete all fields!"
            );
            return;
        }

        if (!UserStore.verifyRecovery(username, answer)) {
            JOptionPane.showMessageDialog(
                this,
                "Username or recovery answer is incorrect!"
            );
            return;
        }

        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();

        Object[] resetFields = {
            "New Password:", newPassword,
            "Confirm New Password:", confirmPassword
        };

        int resetChoice = JOptionPane.showConfirmDialog(
            this,
            resetFields,
            "Reset Password",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (resetChoice != JOptionPane.OK_OPTION) return;

        String password = new String(newPassword.getPassword());
        String confirm = new String(confirmPassword.getPassword());

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(
                this, "Password must have at least 6 characters!"
            );
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(
                this, "Passwords do not match!"
            );
            return;
        }

        if (UserStore.resetPassword(username, password)) {

            JOptionPane.showMessageDialog(
                this,
                "Password successfully changed! You can now login."
            );

            usernameField.setText(username);
            passwordField.setText("");

        } else {
            JOptionPane.showMessageDialog(
                this, "Unable to reset password."
            );
        }
    }
}

// USER ACCOUNT STORAGE
class UserStore {

    private static final Path FILE = Paths.get("users.txt");

    // PASSWORD HASH
    private static String hash(String value) {

        try {

            MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

            byte[] result = digest.digest(
                value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex = new StringBuilder();

            for (byte b : result) {
                hex.append(String.format("%02x", b & 0xff));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String encode(String value) {

        return Base64.getEncoder().encodeToString(
            value.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static ArrayList<String[]> readUsers() {

        ArrayList<String[]> users = new ArrayList<>();

        try {

            if (!Files.exists(FILE)) {
                return users;
            }

            for (String line : Files.readAllLines(FILE)) {

                String[] parts = line.split(",", -1);

                if (parts.length == 3) {
                    users.add(parts);
                }
            }

        } catch (IOException e) {
            System.out.println(
                "Error reading accounts: " + e.getMessage()
            );
        }

        return users;
    }

    private static boolean saveUsers(ArrayList<String[]> users) {

        try {

            ArrayList<String> lines = new ArrayList<>();

            for (String[] user : users) {
                lines.add(
                    user[0] + "," + user[1] + "," + user[2]
                );
            }

            Files.write(
                FILE,
                lines,
                StandardCharsets.UTF_8
            );

            return true;

        } catch (IOException e) {
            System.out.println(
                "Error saving accounts: " + e.getMessage()
            );

            return false;
        }
    }

    static boolean usernameExists(String username) {

        String encoded = encode(username.toLowerCase());

        for (String[] user : readUsers()) {

            if (user[0].equals(encoded)) {
                return true;
            }
        }

        return false;
    }

    static boolean register(
        String username, String password, String answer
    ) {

        if (usernameExists(username)) {
            return false;
        }

        ArrayList<String[]> users = readUsers();

        users.add(new String[] {
            encode(username.toLowerCase()),
            hash(password),
            hash(answer.toLowerCase())
        });

        return saveUsers(users);
    }

    static boolean login(String username, String password) {

        String encoded = encode(username.toLowerCase());
        String passwordHash = hash(password);

        for (String[] user : readUsers()) {

            if (user[0].equals(encoded)
                && user[1].equals(passwordHash)) {
                return true;
            }
        }

        return false;
    }

    static boolean verifyRecovery(
        String username, String answer
    ) {

        String encoded = encode(username.toLowerCase());
        String answerHash = hash(answer.toLowerCase());

        for (String[] user : readUsers()) {

            if (user[0].equals(encoded)
                && user[2].equals(answerHash)) {
                return true;
            }
        }

        return false;
    }

    static boolean resetPassword(
        String username, String newPassword
    ) {

        String encoded = encode(username.toLowerCase());

        ArrayList<String[]> users = readUsers();

        for (String[] user : users) {

            if (user[0].equals(encoded)) {
                user[1] = hash(newPassword);
                return saveUsers(users);
            }
        }

        return false;
    }
}
