package controller;

import entidades.Proveedor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoProveedorVEController implements Initializable {
    
    @FXML
    private Label lblNombreProveedor;
    
    @FXML
    private Label lblCorreoProveedor;
    
    @FXML
    private Label lblTelefonoProveedor;
    
    private Stage stage;
    private Proveedor proveedor;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar valores por defecto
        lblNombreProveedor.setText("Sin información");
        lblCorreoProveedor.setText("Sin correo");
        lblTelefonoProveedor.setText("Sin teléfono");
    }
    
    /**
     * Establece el proveedor y actualiza los labels con su información
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
        if (proveedor != null) {
            // Nombre del proveedor
            String nombre = proveedor.getNombreProveedor();
            lblNombreProveedor.setText(nombre != null && !nombre.trim().isEmpty() ? 
                                     nombre : "Sin información");
            
            // Correo del proveedor
            String correo = proveedor.getCorreoProveedor();
            lblCorreoProveedor.setText(correo != null && !correo.trim().isEmpty() ? 
                                     correo : "Sin correo");
            
            // Teléfono del proveedor
            String telefono = proveedor.getTelefonoProveedor();
            lblTelefonoProveedor.setText(telefono != null && !telefono.trim().isEmpty() ? 
                                       telefono : "Sin teléfono");
        }
    }
    
    /**
     * Establece la referencia al Stage para poder cerrarlo
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Cierra la ventana emergente
     */
    @FXML
    private void cerrarVentana() {
        if (stage != null) {
            stage.close();
        }
    }
}