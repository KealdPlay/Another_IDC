
package dao;


import database.Conexion;
import entidades.ImagenProducto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImagenProductoDAO {
    private Connection connection;
    
    public ImagenProductoDAO(Connection connection) {
        this.connection = connection;
    }
    
    public boolean crear(ImagenProducto imagen) {
        String sql = "INSERT INTO imagenes_productos (id_producto, url_imagen, descripcion) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, imagen.getIdProducto());
            stmt.setString(2, imagen.getUrlImagen());
            stmt.setString(3, imagen.getDescripcion());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ImagenProducto obtenerPorId(int id) {
        String sql = "SELECT * FROM imagenes_productos WHERE id_imagen = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearImagenProducto(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ImagenProducto> obtenerPorProducto(int idProducto) {
        List<ImagenProducto> imagenes = new ArrayList<>();
        String sql = "SELECT * FROM imagenes_productos WHERE id_producto = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                imagenes.add(mapearImagenProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imagenes;
    }
    
    public List<ImagenProducto> obtenerTodos() {
        List<ImagenProducto> imagenes = new ArrayList<>();
        String sql = "SELECT * FROM imagenes_productos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                imagenes.add(mapearImagenProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imagenes;
    }
    
    public boolean actualizar(ImagenProducto imagen) {
        String sql = "UPDATE imagenes_productos SET id_producto = ?, url_imagen = ?, descripcion = ? WHERE id_imagen = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, imagen.getIdProducto());
            stmt.setString(2, imagen.getUrlImagen());
            stmt.setString(3, imagen.getDescripcion());
            stmt.setInt(4, imagen.getIdImagen());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM imagenes_productos WHERE id_imagen = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminarPorProducto(int idProducto) {
        String sql = "DELETE FROM imagenes_productos WHERE id_producto = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private ImagenProducto mapearImagenProducto(ResultSet rs) throws SQLException {
        ImagenProducto imagen = new ImagenProducto();
        imagen.setIdImagen(rs.getInt("id_imagen"));
        imagen.setIdProducto(rs.getInt("id_producto"));
        imagen.setUrlImagen(rs.getString("url_imagen"));
        imagen.setDescripcion(rs.getString("descripcion"));
        return imagen;
    }
}
