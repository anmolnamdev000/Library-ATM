import java.io.*;
import java.util.*;

public class ATMLibrarySystem {

    // ---------------- User Record ----------------
    static class UserRecord {
        String userid;
        String password;
        String secans;
        boolean isAdmin;

        public UserRecord(String uid, String pwd, String sec, boolean adm) {
            userid = uid;
            password = pwd;
            secans = sec;
            isAdmin = adm;
        }
    }

    // ---------------- Book Class ----------------
    static class Book {
        int id;
        String title;
        String author;
        boolean issued;

        public Book(int id, String t, String a, boolean i) {
            this.id = id;
            this.title = t;
            this.author = a;
            this.issued = i;
        }
    }

    // ---------------- Library Class ----------------
    static class Library {
        String datafile;
        ArrayList<Book> books = new ArrayList<>();

        Library(String filename) {
            datafile = filename;
            loadData();
        }

        void loadData() {
            books.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(datafile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split("\\|");
                    if (p.length != 4) continue;

                    int id = Integer.parseInt(p[0]);
                    String title = p[1];
                    String author = p[2];
                    boolean issued = p[3].equals("1");

                    books.add(new Book(id, title, author, issued));
                }
            } catch (Exception ignored) {}
        }

        void saveData() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(datafile))) {
                for (Book b : books) {
                    pw.println(b.id + "|" + b.title + "|" + b.author + "|" + (b.issued ? "1" : "0"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void addBook(Scanner sc) {
            System.out.print("\nEnter Book ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Title: ");
            String title = sc.nextLine();

            System.out.print("Enter Author: ");
            String author = sc.nextLine();

            for (Book b : books) {
                if (b.id == id) {
                    System.out.println("⚠ Book ID already exists.");
                    return;
                }
            }

            books.add(new Book(id, title, author, false));
            saveData();
            System.out.println("✔ Book Added Successfully!");
        }

        void issueBook(Scanner sc) {
            System.out.print("\nEnter Book ID to Issue: ");
            int id = sc.nextInt();

            for (Book b : books) {
                if (b.id == id) {
                    if (!b.issued) {
                        b.issued = true;
                        System.out.println("✔ Book Issued Successfully!");
                    } else {
                        System.out.println("⚠ Book Already Issued!");
                    }
                    saveData();
                    return;
                }
            }
            System.out.println("✖ Book Not Found!");
        }

        void returnBook(Scanner sc) {
            System.out.print("\nEnter Book ID to Return: ");
            int id = sc.nextInt();

            for (Book b : books) {
                if (b.id == id) {
                    if (b.issued) {
                        b.issued = false;
                        System.out.println("✔ Book Returned Successfully!");
                    } else {
                        System.out.println("⚠ This Book Was Not Issued!");
                    }
                    saveData();
                    return;
                }
            }
            System.out.println("✖ Book Not Found!");
        }

        void showBooks() {
            System.out.println("\n----- Library Book List -----");
            System.out.printf("%-10s %-30s %-30s %-10s\n", "ID", "TITLE", "AUTHOR", "STATUS");

            for (Book b : books) {
                System.out.printf("%-10d %-30s %-30s %s\n",
                        b.id, b.title, b.author,
                        b.issued ? "Issued" : "Available");
            }
        }

        void clearAll() {
            books.clear();
            saveData();
            System.out.println("✔ All Library Data Cleared Successfully!");
        }
    }

    // ---------------- User File Handling ----------------
    static final String USERS_FILE = "users.txt";

    static ArrayList<UserRecord> readAllUsers() {
        ArrayList<UserRecord> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) continue;

                users.add(new UserRecord(
                        p[0], p[1], p[2], p[3].equals("1")
                ));
            }
        } catch (Exception ignored) {}

        return users;
    }

    static void writeAllUsers(ArrayList<UserRecord> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (UserRecord u : users) {
                pw.println(u.userid + "|" + u.password + "|" + u.secans + "|" + (u.isAdmin ? "1" : "0"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean userExists(String uid) {
        for (UserRecord u : readAllUsers()) {
            if (u.userid.equals(uid)) return true;
        }
        return false;
    }

    // ---------------- Register ----------------
    static boolean registerUser(Scanner sc) {
        System.out.println("\n--- Register New User ---");
        System.out.print("Enter User ID: ");
        String uid = sc.next();

        if (userExists(uid)) {
            System.out.println("⚠ UserID already exists.");
            return false;
        }

        System.out.print("Enter Password: ");
        String pwd = sc.next();

        sc.nextLine();
        System.out.print("Security Answer: ");
        String sec = sc.nextLine();

        System.out.print("Is Admin? (y/n): ");
        boolean isAdmin = sc.next().equalsIgnoreCase("y");

        ArrayList<UserRecord> users = readAllUsers();
        users.add(new UserRecord(uid, pwd, sec, isAdmin));
        writeAllUsers(users);

        new File("library_" + uid + ".txt");

        System.out.println("✔ Registration Successful!");
        return true;
    }

    // ---------------- Login ----------------
    static boolean loginUser(Scanner sc, String[] loggedUser, boolean[] isAdmin) {
        System.out.println("\n--- Login ---");
        System.out.print("Enter User ID: ");
        String uid = sc.next();

        System.out.print("Enter Password: ");
        String pwd = sc.next();

        for (UserRecord u : readAllUsers()) {
            if (u.userid.equals(uid) && u.password.equals(pwd)) {
                loggedUser[0] = uid;
                isAdmin[0] = u.isAdmin;
                System.out.println("✔ Login Successful! Welcome " + uid);
                return true;
            }
        }
        System.out.println("✖ Invalid Credentials.");
        return false;
    }

    // ---------------- Forgot Password ----------------
    static void forgotPassword(Scanner sc) {
        System.out.println("\n--- Forgot Password ---");
        System.out.print("Enter User ID: ");
        String uid = sc.next();

        ArrayList<UserRecord> users = readAllUsers();
        for (UserRecord u : users) {
            if (u.userid.equals(uid)) {
                sc.nextLine();
                System.out.print("Enter Security Answer: ");
                String ans = sc.nextLine();

                if (ans.equals(u.secans)) {
                    System.out.print("Enter New Password: ");
                    String newpwd = sc.next();
                    u.password = newpwd;
                    writeAllUsers(users);
                    System.out.println("✔ Password Reset Successfully!");
                } else {
                    System.out.println("✖ Wrong Security Answer!");
                }
                return;
            }
        }
        System.out.println("✖ User Not Found!");
    }

    // ---------------- ADMIN MENU (with Library Access) ----------------
    static void adminMenu(Scanner sc, String adminId) {
        Library lib = new Library("library_" + adminId + ".txt");

        int ch;
        do {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. View All Users");
            System.out.println("2. Delete User");
            System.out.println("3. Reset User Password");
            System.out.println("4. Register New User");
            System.out.println("5. Total Users");
            System.out.println("6. Library Menu");
            System.out.println("7. Logout");
            System.out.print("Enter Choice: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1 -> {
                    System.out.println("\n--- All Users ---");
                    for (UserRecord u : readAllUsers()) {
                        System.out.println(u.userid + " - " + (u.isAdmin ? "ADMIN" : "USER"));
                    }
                }
                case 2 -> {
                    System.out.print("Enter UserID to delete: ");
                    String uid = sc.next();
                    ArrayList<UserRecord> users = readAllUsers();
                    users.removeIf(u -> u.userid.equals(uid));
                    writeAllUsers(users);

                    File f = new File("library_" + uid + ".txt");
                    if (f.exists()) f.delete();

                    System.out.println("✔ User Deleted Successfully!");
                }
                case 3 -> {
                    System.out.print("Enter UserID: ");
                    String uid = sc.next();
                    ArrayList<UserRecord> users = readAllUsers();
                    for (UserRecord u : users) {
                        if (u.userid.equals(uid)) {
                            System.out.print("Enter New Password: ");
                            u.password = sc.next();
                            writeAllUsers(users);
                            System.out.println("✔ Password Reset!");
                        }
                    }
                }
                case 4 -> registerUser(sc);
                case 5 -> System.out.println("Total Users: " + readAllUsers().size());

                case 6 -> {
                    // Admin Library Menu
                    userLibraryMenu(sc, adminId);
                }

                case 7 -> System.out.println("✔ Admin Logged Out!");
            }

        } while (ch != 7);
    }

    // ---------------- USER LIBRARY MENU ----------------
    static void userLibraryMenu(Scanner sc, String userid) {
        Library lib = new Library("library_" + userid + ".txt");
        int ch;
        do {
            System.out.println("\n===== LIBRARY (" + userid + ") =====");
            System.out.println("1. Add Book");
            System.out.println("2. Issue Book");
            System.out.println("3. Return Book");
            System.out.println("4. Show All Books");
            System.out.println("5. Clear All Data");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1 -> lib.addBook(sc);
                case 2 -> lib.issueBook(sc);
                case 3 -> lib.returnBook(sc);
                case 4 -> lib.showBooks();
                case 5 -> lib.clearAll();
                case 6 -> System.out.println("✔ Going Back...");
                default -> System.out.println("Invalid Option!");
            }
        } while (ch != 6);
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int ch;
        String[] loggedUser = new String[1];
        boolean[] isAdmin = new boolean[1];

        System.out.println("========= ATM LIBRARY SYSTEM =========");

        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            System.out.print("Enter Choice: ");
            ch = sc.nextInt();

            switch (ch) {
                case 1 -> registerUser(sc);
                case 2 -> {
                    if (loginUser(sc, loggedUser, isAdmin)) {
                        if (isAdmin[0]) adminMenu(sc, loggedUser[0]);
                        else userLibraryMenu(sc, loggedUser[0]);
                    }
                }
                case 3 -> forgotPassword(sc);
                case 4 -> System.out.println("✔ Exiting... Goodbye!");
            }
        } while (ch != 4);
    }
}
