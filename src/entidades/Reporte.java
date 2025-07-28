
package entidades;

public class Reporte {
    private int idReporte;
    private int idProducto;
    private String periodoReporte;
    private String fechaCalculoReporte;
    
    // Constructores
    public Reporte() {}
    
    public Reporte(int idProducto, String periodoReporte, String fechaCalculoReporte) {
        this.idProducto = idProducto;
        this.periodoReporte = periodoReporte;
        this.fechaCalculoReporte = fechaCalculoReporte;
    }
    
    // Getters y Setters
    public int getIdReporte() { return idReporte; }
    public void setIdReporte(int idReporte) { this.idReporte = idReporte; }
    
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    
    public String getPeriodoReporte() { return periodoReporte; }
    public void setPeriodoReporte(String periodoReporte) { this.periodoReporte = periodoReporte; }
    
    public String getFechaCalculoReporte() { return fechaCalculoReporte; }
    public void setFechaCalculoReporte(String fechaCalculoReporte) { this.fechaCalculoReporte = fechaCalculoReporte; }

}
