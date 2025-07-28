
package controller;

import entidades.Proveedor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoProveedorVEController {
    
@FXML
    private Label lblNombreProveedor;
    
    @FXML
    private Label lblCorreoProveedor;
    
    @FXML
    private Label lblTelefonoProveedor;
    
    private Stage stage;
    private Proveedor proveedor;
    
    
    
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
        if (proveedor != null) {
            lblNombreProveedor.setText(proveedor.getNombreProveedor());
            lblCorreoProveedor.setText(proveedor.getCorreoProveedor());
            lblTelefonoProveedor.setText(proveedor.getTelefonoProveedor());
        }
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void cerrarVentana() {
        if (stage != null) {
            stage.close();
        }
    }
}