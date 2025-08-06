
package dao;

import database.Conexion;
import entidades.Categoria;
import entidades.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    private Connection connection;
    
    public CategoriaDAO(Connection connection) {
        this.connection = connection;
    }

    public CategoriaDAO() {
    }
    
    
    public boolean crear(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre_categoria, descripcion) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombreCategoria());
            stmt.setString(2, categoria.getDescripcion());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Categoria obtenerPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearCategoria(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<String> obtenerNombresCategorias() throws SQLException {
        String query = "SELECT nombre_categoria FROM categorias";
        List<String> categorias = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(rs.getString("nombre_categoria"));
            }
        }
        
        return categorias;
    }
    
    public List<Categoria> obtenerTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }
    
    public int obtenerIdCategoria(String nombreCategoria) throws SQLException {
        String query = "SELECT id_categoria FROM categorias WHERE nombre_categoria = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombreCategoria);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_categoria");
            }
            throw new SQLException("No se encontró la categoría: " + nombreCategoria);
        }
    }
    
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre_categoria = ?, descripcion = ? WHERE id_categoria = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombreCategoria());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setInt(3, categoria.getIdCategoria());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String obtenerNombreCategoria(int id) throws SQLException {
    String sql = "SELECT nombre_categoria FROM categorias WHERE id_categoria = ?";
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, id);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nombre_categoria");
            }
        }
    }
    
    return null;
}

    
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombreCategoria(rs.getString("nombre_categoria"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }
}