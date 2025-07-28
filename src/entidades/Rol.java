
package entidades;

public class Rol {
    private int idRol;
    private String nombreRol;
    
    // Constructores
    public Rol() {}
    
    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    // Getters y Setters
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}