
package controller;

import com.sun.tools.javac.Main;
import dao.UsuarioDAO;
import entidades.Usuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    
    @FXML
    private TextField txtCorreoUsuario;
    
    @FXML
    private PasswordField txtContrasenaUsuario;
    
    @FXML
    private Button btnIniciarSesion;
    
    @FXML
    private Label lblMensaje;
    
    @FXML
    private CheckBox chkMostrarContrasena;
    
    @FXML
    private TextField txtContrasenaVisible;
    
    private UsuarioDAO usuarioDAO;
    
    public LoginController() {
        usuarioDAO = new UsuarioDAO();
    }
    
    @FXML
    private void initialize() {
        // Inicializar el campo de contraseña visible como oculto
        txtContrasenaVisible.setVisible(false);
        txtContrasenaVisible.setManaged(false);
        
        // Vincular los campos de contraseña
        txtContrasenaVisible.textProperty().bindBidirectional(txtContrasenaUsuario.textProperty());
        
        // Configurar el comportamiento del checkbox
        chkMostrarContrasena.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtContrasenaVisible.setVisible(true);
                txtContrasenaVisible.setManaged(true);
                txtContrasenaUsuario.setVisible(false);
                txtContrasenaUsuario.setManaged(false);
            } else {
                txtContrasenaUsuario.setVisible(true);
                txtContrasenaUsuario.setManaged(true);
                txtContrasenaVisible.setVisible(false);
                txtContrasenaVisible.setManaged(false);
            }
        });
        
        // Limpiar mensaje al escribir
        txtCorreoUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            lblMensaje.setText("");
        });
        
        txtContrasenaUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            lblMensaje.setText("");
        });
    }
    
    @FXML
    private void handleIniciarSesion(ActionEvent event) {
        String correoUsuario = txtCorreoUsuario.getText().trim();
        String contrasenaUsuario = txtContrasenaUsuario.getText();
        
        // Validar campos vacíos
        if (correoUsuario.isEmpty() || contrasenaUsuario.isEmpty()) {
            mostrarMensajeError("Por favor, complete todos los campos");
            return;
        }
        
        // Validar formato de email
        if (!esEmailValido(correoUsuario)) {
            mostrarMensajeError("Por favor, ingrese un email válido");
            return;
        }
        
        // Intentar autenticación
        try {
            Usuarios usuario = usuarioDAO.autenticarUsuario(correoUsuario, contrasenaUsuario);
            
            if (usuario != null) {
                System.out.println("Inicio de sesión exitoso provisional");
                mostrarMensajeExito("Inicio de sesión exitoso");
                
                // Guardar información del usuario en sesión
                SessionManager.getInstance().setUsuarioActual(usuario);
                
                // Redirigir a la ventana principal
                abrirVentanaPrincipal(usuario);
                
            } else {
                mostrarMensajeError("Credenciales incorrectas");
            }
            
        } catch (Exception e) {
            mostrarMensajeError("Error en el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLimpiarCampos(ActionEvent event) {
        txtCorreoUsuario.clear();
        txtContrasenaUsuario.clear();
        lblMensaje.setText("");
        chkMostrarContrasena.setSelected(false);
    }
    
    private void mostrarMensajeError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: red;");
    }
    
    private void mostrarMensajeExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: green;");
    }
    
    private boolean esEmailValido(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    private void abrirVentanaPrincipal(Usuarios usuario) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load());
            
            // Obtener el controlador de la ventana principal y pasar el usuario
            MainController mainController = loader.getController();
            mainController.inicializarConUsuario(usuario);
            
            Stage stage = new Stage();
            stage.setTitle("Sistema Principal - Bienvenido " + usuario.getNombre_usuario());
            stage.setScene(scene);
            stage.show();
            
            // Cerrar la ventana de login
            Stage loginStage = (Stage) btnIniciarSesion.getScene().getWindow();
            loginStage.close();
            
        } catch (IOException e) {
            mostrarMensajeError("⚠"+"Error al cargar la ventana principal"+"⚠");
            e.printStackTrace();
        }
    }
}