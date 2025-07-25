package entidades;

public class Usuarios {
    private int id_usuario, id_rol;
    private String nombre_usuario, correo_usuario, contraseña_usuario;
    
    public Usuarios (){
        
    }

    public Usuarios(int id_usuario, int id_rol, String nombre_usuario, String correo_usuario, String contraseña_usuario) {
        this.id_usuario = id_usuario;
        this.id_rol = id_rol;
        this.nombre_usuario = nombre_usuario;
        this.correo_usuario = correo_usuario;
        this.contraseña_usuario = contraseña_usuario;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getCorreo_usuario() {
        return correo_usuario;
    }

    public void setCorreo_usuario(String correo_usuario) {
        this.correo_usuario = correo_usuario;
    }

    public String getContraseña_usuario() {
        return contraseña_usuario;
    }

    public void setContraseña_usuario(String contraseña_usuario) {
        this.contraseña_usuario = contraseña_usuario;
    }

    @Override
    public String toString() {
        return "Usuarios{" + "id_usuario=" + id_usuario + ", id_rol=" + id_rol + ", nombre_usuario=" + nombre_usuario + ", correo_usuario=" + correo_usuario + ", contrase\u00f1a_usuario=" + contraseña_usuario + '}';
    }
    
    
}
