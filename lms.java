import java.sql.*;
import java.util.*;

public class LibraryApp {
    static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "root";        // change if needed
    static final String PASS = "password";    // change if needed
    static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            Scanner sc = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n=== Library Management System ===");
                System.out.println("1. Add Book");
                System.out.println("2. View All Books");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> addBook(sc);
                    case 2 -> viewBooks();
                    case 3 -> issueBook(sc);
                    case 4 -> returnBook(sc);
                    case 5 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice != 5);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addBook(Scanner sc) throws SQLException {
        System.out.print("Enter book title: ");
        String title = sc.nextLine();
        System.out.print("Enter author name: ");
        String author = sc.nextLine();

        String query = "INSERT INTO books (title, author, available) VALUES (?, ?, TRUE)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, title);
        ps.setString(2, author);
        ps.executeUpdate();
        System.out.println("Book added successfully!");
    }

    static void viewBooks() throws SQLException {
        String query = "SELECT * FROM books";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\nID | Title | Author | Available");
        System.out.println("------------------------------------");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %s\n",
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getBoolean("available") ? "Yes" : "No");
        }
    }

    static void issueBook(Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to issue: ");
        int bookId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter name of person: ");
        String person = sc.nextLine();

        String check = "SELECT available FROM books WHERE id = ?";
        PreparedStatement chk = conn.prepareStatement(check);
        chk.setInt(1, bookId);
        ResultSet rs = chk.executeQuery();

        if (rs.next() && rs.getBoolean("available")) {
            String issue = "INSERT INTO issued_books (book_id, issued_to, issue_date) VALUES (?, ?, CURDATE())";
            PreparedStatement ps = conn.prepareStatement(issue);
            ps.setInt(1, bookId);
            ps.setString(2, person);
            ps.executeUpdate();

            String update = "UPDATE books SET available = FALSE WHERE id = ?";
            PreparedStatement up = conn.prepareStatement(update);
            up.setInt(1, bookId);
            up.executeUpdate();

            System.out.println("Book issued successfully!");
        } else {
            System.out.println("Book not available or invalid ID.");
        }
    }

    static void returnBook(Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to return: ");
        int bookId = sc.nextInt();

        String check = "SELECT * FROM issued_books WHERE book_id = ? AND return_date IS NULL";
        PreparedStatement ps = conn.prepareStatement(check);
        ps.setInt(1, bookId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String updateIssue = "UPDATE issued_books SET return_date = CURDATE() WHERE book_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateIssue);
            ps2.setInt(1, bookId);
            ps2.executeUpdate();

            String updateBook = "UPDATE books SET available = TRUE WHERE id = ?";
            PreparedStatement ps3 = conn.prepareStatement(updateBook);
            ps3.setInt(1, bookId);
            ps3.executeUpdate();

            System.out.println("Book returned successfully!");
        } else {
            System.out.println("No issued record found for this book.");
        }
    }
}
