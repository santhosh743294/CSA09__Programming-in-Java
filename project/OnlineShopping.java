import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class OnlineShopping {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/onlineshopping";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "mahesh@890";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void displayProducts() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT id, name, price, quantity FROM products";
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("ID\tName\tPrice\tQuantity");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");
                System.out.println(id + "\t" + name + "\t" + price + "\t" + quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addProduct() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter product name:");
            String name = scanner.nextLine();
            System.out.println("Enter product price:");
            double price = scanner.nextDouble();
            System.out.println("Enter product quantity:");
            int quantity = scanner.nextInt();

            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setInt(3, quantity);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Product added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateProduct() {
        try (Connection connection = getConnection()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the ID of the product to update:");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Ask which fields to update
            System.out.println("Which fields do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Price");
            System.out.println("3. Quantity");
            System.out.println("4. All");
            System.out.print("Enter choice(s) separated by commas: ");
            String[] fieldChoices = scanner.nextLine().split(",");

            // Prepare update statement
            StringBuilder updateQuery = new StringBuilder("UPDATE products SET ");
            for (String choice : fieldChoices) {
                switch (choice.trim()) {
                    case "1":
                        updateQuery.append("name=?, ");
                        break;
                    case "2":
                        updateQuery.append("price=?, ");
                        break;
                    case "3":
                        updateQuery.append("quantity=?, ");
                        break;
                    case "4":
                        updateQuery = new StringBuilder("UPDATE products SET name=?, price=?, quantity=?, ");
                        break;
                    default:
                        System.out.println("Invalid choice: " + choice.trim());
                        return;
                }
            }
            updateQuery.delete(updateQuery.length() - 2, updateQuery.length()); // Remove trailing comma and space
            updateQuery.append(" WHERE id=?");

            // Set values for the chosen fields
            try (PreparedStatement statement = connection.prepareStatement(updateQuery.toString())) {
                int parameterIndex = 1;
                for (String choice : fieldChoices) {
                    switch (choice.trim()) {
                        case "1":
                            System.out.println("Enter new name:");
                            statement.setString(parameterIndex++, scanner.nextLine());
                            break;
                        case "2":
                            System.out.println("Enter new price:");
                            statement.setDouble(parameterIndex++, scanner.nextDouble());
                            break;
                        case "3":
                            System.out.println("Enter new quantity:");
                            statement.setInt(parameterIndex++, scanner.nextInt());
                            break;
                        case "4":
                            // Do nothing as "All" is handled separately
                            break;
                    }
                }
                statement.setInt(parameterIndex, id); // Set ID for WHERE clause

                // Execute the update
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Product information updated successfully!");
                } else {
                    System.out.println("No product found with the provided ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteProduct() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM products WHERE id=?")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the ID of the product to delete:");
            int id = scanner.nextInt();

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("No product found with the provided ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. View all products");
            System.out.println("2. Add a product");
            System.out.println("3. Update a product");
            System.out.println("4. Delete a product");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nAll Products:");
                    displayProducts();
                    break;
                case 2:
                    addProduct();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }
}
