package dao;

import database.Conexion;
import entidades.Foto;
import entidades.UsuarioConfiguracion;

import java.sql.*;

public class UsuariosConfDAO {
        
    public UsuarioConfiguracion obtenerUsuarioPorId(int id) {
        UsuarioConfiguracion usuario = null;
        String query = """
             SELECT u.id, u.nombre, u.correo, u.contrasena, 
                               fp.foto_id, fp.direccion_foto 
                        FROM usuarios u 
                        LEFT JOIN foto_perfil fp ON u.id = fp.id_imagen 
                        WHERE u.id = ?
        """;

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Foto foto = null;
                  if (rs.getObject("foto_id") != null) {
                    foto = new Foto(
                            rs.getInt("foto_id"),
                            rs.getString("direccion_foto")
                    );
                }

                usuario = new UsuarioConfiguracion(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contrasena"),
                        foto
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return usuario;
    }

    public boolean actualizarUsuario(UsuarioConfiguracion usuario) {
        String query = "UPDATE usuarios SET nombre = ?, correo = ?, contrasena = ? WHERE id = ?";
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getContrasena());
            stmt.setInt(4, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
       // Método adicional para actualizar la foto del usuario
    public boolean actualizarFotoUsuario(int userId, String rutaFoto) {
        String query = "UPDATE foto_perfil SET direccion_foto = ? WHERE id_imagen = ?";
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, rutaFoto);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar foto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
