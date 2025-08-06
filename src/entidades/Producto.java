package entidades;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal precioProducto;
    private int stockProducto;
    private String colorProducto;
    private String medidasProducto;
    private int idCategoria;
    private int idProveedor;
    private String imagenProducto; // Nuevo campo para la imagen
    
    //CONSTRUCTORES
    public Producto() {
         
    }

    public Producto(int idProducto, String nombreProducto, String descripcionProducto, 
                   BigDecimal precioProducto, int stockProducto, String colorProducto, 
                   String medidasProducto, int idCategoria, int idProveedor) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.precioProducto = precioProducto;
        this.stockProducto = stockProducto;
        this.colorProducto = colorProducto;
        this.medidasProducto = medidasProducto;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
    }
    
    // Constructor con imagen
    public Producto(int idProducto, String nombreProducto, String descripcionProducto, 
                   BigDecimal precioProducto, int stockProducto, String colorProducto, 
                   String medidasProducto, int idCategoria, int idProveedor, String imagenProducto) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.precioProducto = precioProducto;
        this.stockProducto = stockProducto;
        this.colorProducto = colorProducto;
        this.medidasProducto = medidasProducto;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.imagenProducto = imagenProducto;
    }
    
    // Constructor sin ID para inserciones
    public Producto(String nombreProducto, String descripcionProducto, BigDecimal precioProducto, 
                   int stockProducto, String colorProducto, String medidasProducto, 
                   int idCategoria, int idProveedor, String imagenProducto) {
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.precioProducto = precioProducto;
        this.stockProducto = stockProducto;
        this.colorProducto = colorProducto;
        this.medidasProducto = medidasProducto;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.imagenProducto = imagenProducto;
    }

    //GETTERS Y SETTERS
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public BigDecimal getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(BigDecimal precioProducto) {
        this.precioProducto = precioProducto;
    }

    public int getStockProducto() {
        return stockProducto;
    }

    public void setStockProducto(int stockProducto) {
        this.stockProducto = stockProducto;
    }

    public String getColorProducto() {
        return colorProducto;
    }

    public void setColorProducto(String colorProducto) {
        this.colorProducto = colorProducto;
    }

    public String getMedidasProducto() {
        return medidasProducto;
    }

    public void setMedidasProducto(String medidasProducto) {
        this.medidasProducto = medidasProducto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }
    
    //TO STRING
    @Override
    public String toString() {
        return "Producto{" + 
               "idProducto=" + idProducto + 
               ", nombreProducto=" + nombreProducto + 
               ", descripcionProducto=" + descripcionProducto + 
               ", precioProducto=" + precioProducto + 
               ", stockProducto=" + stockProducto + 
               ", colorProducto=" + colorProducto + 
               ", medidasProducto=" + medidasProducto + 
               ", idCategoria=" + idCategoria + 
               ", idProveedor=" + idProveedor + 
               ", imagenProducto=" + imagenProducto + 
               '}';
    }
}