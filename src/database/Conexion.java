package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB = "idc_software";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "H5S@idS3b@s142006";
    private static final String FULL_URL = URL + DB + "?useSSL=false&serverTimezone=UTC";

    public  static Conexion instance;
    public Connection connection;
    
    public Conexion() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(FULL_URL, USERNAME, PASSWORD);
            System.out.println("Conexión a la base de datos establecida exitosamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
    
    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

public Connection getConnection() {
    try {
        // Verificar si la conexión está cerrada y reconectar si es necesario
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(FULL_URL, USERNAME, PASSWORD); // ← CAMBIO: usar FULL_URL en lugar de URL
        }
    } catch (SQLException e) {
        System.err.println("Error al verificar/reconectar la base de datos: " + e.getMessage());
    }
    return connection;
}

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada exitosamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
