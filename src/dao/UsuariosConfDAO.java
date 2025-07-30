package dao;

import database.Conexion;
import entidades.UsuarioConfiguracion;

import java.sql.*;

public class UsuariosConfDAO {

    public UsuarioConfiguracion obtenerUsuarioPorId(int id) {
        UsuarioConfiguracion usuario = null;
        String query = "SELECT * FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new UsuarioConfiguracion(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contrasena")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }

        return usuario;
    }

    public boolean actualizarUsuario(UsuarioConfiguracion usuario) {
        String query = "UPDATE usuarios SET nombre = ?, correo = ?, contrasena = ? WHERE id_usuario = ?";
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getContrasena());
            stmt.setInt(4, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }

        return false;
    }
}
