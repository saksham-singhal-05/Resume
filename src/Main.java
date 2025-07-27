public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("PostgreSQL JDBC driver not found.");
        }
        javax.swing.SwingUtilities.invokeLater(CollectionOfPages::new);
    }
}
