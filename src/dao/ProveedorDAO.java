
package dao;
import database.Conexion;
import entidades.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    private Connection connection;
    
    public ProveedorDAO(Connection connection) {
        this.connection = connection;
    }
    
    public boolean crear(Proveedor proveedor) {
        String sql = "INSERT INTO proveedores (nombre_proveedor, correo_proveedor, telefono_proveedor) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, proveedor.getNombreProveedor());
            stmt.setString(2, proveedor.getCorreoProveedor());
            stmt.setString(3, proveedor.getTelefonoProveedor());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM proveedores WHERE id_proveedor = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearProveedor(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }
    
    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET nombre_proveedor = ?, correo_proveedor = ?, telefono_proveedor = ? WHERE id_proveedor = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, proveedor.getNombreProveedor());
            stmt.setString(2, proveedor.getCorreoProveedor());
            stmt.setString(3, proveedor.getTelefonoProveedor());
            stmt.setInt(4, proveedor.getIdProveedor());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(rs.getInt("id_proveedor"));
        proveedor.setNombreProveedor(rs.getString("nombre_proveedor"));
        proveedor.setCorreoProveedor(rs.getString("correo_proveedor"));
        proveedor.setTelefonoProveedor(rs.getString("telefono_proveedor"));
        return proveedor;
    }
}