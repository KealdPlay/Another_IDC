
package entidades;
public class ImagenProducto {
    private int idImagen;
    private int idProducto;
    private String urlImagen;
    private String descripcion;
    
    // Constructores
    public ImagenProducto() {}
    
    public ImagenProducto(int idProducto, String urlImagen, String descripcion) {
        this.idProducto = idProducto;
        this.urlImagen = urlImagen;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public int getIdImagen() { return idImagen; }
    public void setIdImagen(int idImagen) { this.idImagen = idImagen; }
    
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
}
