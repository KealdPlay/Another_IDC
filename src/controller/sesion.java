package controller;

public class sesion {
    // Variable estática para mantener el ID del usuario activo durante la sesión
    public static int idUsuarioActivo = -1; // -1 indica que no hay usuario logueado
    
    // Otros datos de sesión que podrías necesitar
    public static String nombreUsuarioActivo = "";
    public static String rolUsuarioActivo = "";
    
    // Métodos para manejar la sesión
    public static void iniciarSesion(int id, String nombre, String rol) {
        idUsuarioActivo = id;
        nombreUsuarioActivo = nombre;
        rolUsuarioActivo = rol;
    }
    
    public static void cerrarSesion() {
        idUsuarioActivo = -1;
        nombreUsuarioActivo = "";
        rolUsuarioActivo = "";
    }
    
    public static boolean haySesionActiva() {
        return idUsuarioActivo != -1;
    }
}
