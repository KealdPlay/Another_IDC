package dao;
import database.Conexion;
import entidades.Usuarios;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    
    private final Connection connection;
    
    public UsuarioDAO() {
        this.connection = Conexion.getInstance().getConnection();
    }
    
    public Usuarios autenticarUsuario(String correoUsuario, String contrasenaUsuario) {
        String sql = "SELECT id_usuario, nombre_usuario, correo_usuario, contrasena_usuario, id_rol " +
                    "FROM usuarios WHERE correo_usuario = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correoUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String contrasenaAlmacenada = rs.getString("contrasena_usuario");
                
                // Verificar contraseña (asumiendo que está hasheada)
                if (verificarContrasena(contrasenaUsuario, contrasenaAlmacenada)) {
                    Usuarios usuario = new Usuarios();
                    usuario.setId_usuario(rs.getInt("id_usuario"));
                    usuario.setNombre_usuario(rs.getString("nombre_usuario"));
                    usuario.setCorreo_usuario(rs.getString("correo_usuario"));
                    usuario.setContraseña_usuario(contrasenaAlmacenada);
                    usuario.setId_rol(rs.getInt("id_rol"));
                    
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        
        return null;
    }
    
    public Usuarios buscarUsuarioPorId(int idUsuario) {
        String sql = "SELECT id_usuario, nombre_usuario, correo_usuario, contrasena_usuario, id_rol " +
                    "FROM usuarios WHERE id_usuario = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Usuarios usuario = new Usuarios();
                usuario.setId_usuario(rs.getInt("id_usuario"));
                usuario.setNombre_usuario(rs.getString("nombre_usuario"));
                usuario.setCorreo_usuario(rs.getString("correo_usuario"));
                usuario.setContraseña_usuario(rs.getString("contrasena_usuario"));
                usuario.setId_rol(rs.getInt("id_rol"));
                
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean actualizarUsuario(Usuarios usuario) {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, correo_usuario = ?, contrasena_usuario = ? WHERE id_usuario = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre_usuario());
            pstmt.setString(2, usuario.getCorreo_usuario());
            
            // Hashear la contraseña antes de almacenarla si no está ya hasheada
            String contrasenaParaGuardar = usuario.getContraseña_usuario();
            if (!contrasenaParaGuardar.matches("[a-fA-F0-9]{64}")) { // Si no es un hash SHA-256
                contrasenaParaGuardar = hashearContrasena(contrasenaParaGuardar);
            }
            
            pstmt.setString(3, contrasenaParaGuardar);
            pstmt.setInt(4, usuario.getId_usuario());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean existeCorreoUsuario(String correoUsuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo_usuario = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correoUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean existeCorreoUsuarioExceptoActual(String correoUsuario, int idUsuarioActual) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo_usuario = ? AND id_usuario != ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correoUsuario);
            pstmt.setInt(2, idUsuarioActual);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
        }
        
        return false;
    }

    public String hashearContrasena(String contrasena) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(contrasena.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al hashear contraseña: " + e.getMessage());
            return contrasena; // En caso de error, devolver la contraseña original
        }
    }
    
    private boolean verificarContrasena(String contrasenaPlana, String contrasenaHasheada) {
        // Si la contraseña almacenada no está hasheada (para desarrollo/testing)
        if (contrasenaPlana.equals(contrasenaHasheada)) {
            return true;
        }
        
        // Verificar con hash
        String hashContrasenaPlana = hashearContrasena(contrasenaPlana);
        return hashContrasenaPlana.equals(contrasenaHasheada);
    }
}