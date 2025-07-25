package entidades;

public class UsuarioConfiguracion {
    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private Foto foto;

    public UsuarioConfiguracion() {}

    public UsuarioConfiguracion(int id, String nombre, String correo, String contrasena, Foto foto) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }
}
