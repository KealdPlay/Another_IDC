
package controller;

import entidades.Usuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MainController {
    
    @FXML
    private Label lblBienvenida;
   
    @FXML
    private Button btnReportes;
    
    private Usuarios usuarioActual;
    
    public void inicializarConUsuario(Usuarios usuario) {
        this.usuarioActual = usuario;
        cargarInformacionUsuario();
    }
    
    @FXML
    public void initialize() {
        // Configuraciones iniciales del controlador
    }
    
    private void cargarInformacionUsuario() {
        if (usuarioActual != null) {
            lblBienvenida.setText("Bienvenida, " + usuarioActual.getNombre_usuario());
        }
    }
    
    private String obtenerNombreRol(int idRol) {
        switch (idRol) {
            case 1:
                return "Owner";
            case 2:
                return "Admin";
            default:
                return "Rol desconocido";
        }
    }
    
    @FXML
    public void handleConfiguracion(ActionEvent event) {
 try {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("interfaces.ReportesInterfaces-view.fxml"));
        Scene scene = new Scene(loader.load());
            
            // Obtener el controlador de la ventana principal y pasar el usuario
            ReportesInterfaces mainController = loader.getController();
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            
            // Cerrar la ventana de login
            Stage loginStage = (Stage) btnReportes.getScene().getWindow();
            loginStage.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void volverALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/app/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = new Stage();
            stage.setTitle("Sistema de Autenticación");
            stage.setScene(scene);
            stage.show();
           
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de login: " + e.getMessage());
            e.printStackTrace();
        }
    }


}