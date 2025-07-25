
package controller;


import entidades.Usuarios;
import java.time.LocalDateTime;

public class SessionManager {
    private static SessionManager instance;
    private Usuarios usuarioActual;
    private LocalDateTime inicioSesion;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setUsuarioActual(Usuarios usuario) {
        this.usuarioActual = usuario;
        this.inicioSesion = LocalDateTime.now();
    }
    
    public Usuarios getUsuarioActual() {
        return usuarioActual;
    }
    
    public LocalDateTime getInicioSesion() {
        return inicioSesion;
    }
    
    public boolean isUsuarioLogueado() {
        return usuarioActual != null;
    }
    
    public void cerrarSesion() {
        this.usuarioActual = null;
        this.inicioSesion = null;
    }
    
    public String getNombreUsuarioActual() {
        return usuarioActual != null ? usuarioActual.getNombre_usuario() : "Invitado";
    }
    
    public int getIdRolUsuarioActual() {
        return usuarioActual != null ? usuarioActual.getId_rol() : 0;
    }
}