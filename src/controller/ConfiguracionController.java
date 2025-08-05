package controller;

import dao.UsuarioDAO;
import entidades.Usuarios;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

public class ConfiguracionController {

    @FXML private Label lblTitulo;
    @FXML private Button btnVolver;
    @FXML private Label lblSaludo;
    @FXML private ImageView avatar;
    @FXML private Button btnCambiarFoto;
    @FXML private Label lblCorreoValor;
    @FXML private Button btnCambiarGmail;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblContrasenaVisible;
    @FXML private CheckBox chkMostrarContrasena;
    @FXML private Button btnCambiarContrasena;
    @FXML private Button btnGuardar;

    private File imagenSeleccionada;
    private Usuarios usuario;

    @FXML
    public void initialize() {
        // Obtener el usuario actual desde SessionManager
        usuario = SessionManager.getInstance().getUsuarioActual();

        if (usuario == null) {
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la información del usuario.").showAndWait();
            return;
        }

        // Cargar información del usuario
        cargarInformacionUsuario();
        cargarAvatarUsuario();
    }

    private void cargarInformacionUsuario() {
        lblSaludo.setText("¡Hola, " + usuario.getNombre_usuario() + "!");
        lblCorreoValor.setText(usuario.getCorreo_usuario());
        txtContrasena.setText(usuario.getContraseña_usuario());
        lblContrasenaVisible.setText(usuario.getContraseña_usuario());
    }

    private void cargarAvatarUsuario() {
        String rutaImagen = "src/view/img/perfil.png";
        
        // Intentar leer la ruta personalizada del archivo de texto
        File archivoRuta = new File("foto_usuario" + usuario.getId_usuario() + ".txt");
        if (archivoRuta.exists()) {
            try (Scanner scanner = new Scanner(archivoRuta)) {
                if (scanner.hasNextLine()) {
                    String rutaGuardada = scanner.nextLine().trim();
                    if (new File(rutaGuardada).exists()) {
                        rutaImagen = rutaGuardada;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al leer ruta de imagen: " + e.getMessage());
            }
        }

        try {
            Image imagen = null;
            
            // Manejar diferentes tipos de rutas
            if (rutaImagen.startsWith("src/")) {
                // Para imágenes de recursos internos
                String resourcePath = "/" + rutaImagen.substring(4);
                imagen = new Image(getClass().getResourceAsStream(resourcePath));
            } else {
                // Para imágenes externas seleccionadas por el usuario
                File archivoImagen = new File(rutaImagen);
                if (archivoImagen.exists()) {
                    imagen = new Image(archivoImagen.toURI().toString());
                } else {
                    // Cargar imagen por defecto
                    imagen = new Image(getClass().getResourceAsStream("/view/img/perfil.png"));
                }
            }

            if (imagen != null && !imagen.isError()) {
                avatar.setImage(imagen);
                avatar.setFitWidth(120);
                avatar.setFitHeight(120);
                avatar.setPreserveRatio(false);
                avatar.setSmooth(true);
                
                // Aplicar máscara circular
                Circle clip = new Circle(60, 60, 60);
                avatar.setClip(clip);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen de perfil: " + e.getMessage());
        }
    }

    @FXML
    private void handleVolver() {
        try {
            URL fxmlUrl = getClass().getResource("/main-view.fxml");
            System.out.println("FXML encontrado en: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Inicializar el controller del main con el usuario actual
            MainController mainController = loader.getController();
            mainController.inicializarConUsuario(usuario);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Menú Principal - IDC");
            stage.show();

            // Cerrar ventana actual
            ((Stage) btnVolver.getScene().getWindow()).close();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la vista principal.").showAndWait();
        }
    }

    @FXML
    private void handleCambiarFoto() {
        Stage stage = (Stage) btnCambiarFoto.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva foto");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        imagenSeleccionada = fileChooser.showOpenDialog(stage);

        if (imagenSeleccionada != null) {
            avatar.setImage(new Image(imagenSeleccionada.toURI().toString()));
            
            // Aplicar máscara circular
            Circle clip = new Circle(60, 60, 60);
            avatar.setClip(clip);
            
            try (PrintWriter writer = new PrintWriter("foto_usuario" + usuario.getId_usuario() + ".txt")) {
                writer.println(imagenSeleccionada.getAbsolutePath());
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la ruta de la imagen: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleCambiarGmail() {
        Stage ventanaGmail = new Stage();
        ventanaGmail.setTitle("Cambiar Correo");

        TextField campoGmail = new TextField();
        campoGmail.setPromptText("Nuevo Correo");

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            String nuevoGmail = campoGmail.getText();
            if (nuevoGmail.trim().isEmpty() || !nuevoGmail.contains("@")) {
                new Alert(Alert.AlertType.WARNING, "Correo inválido.").showAndWait();
                return;
            }
            
            int idUsuario = usuario.getId_usuario();
            UsuarioDAO dao = new UsuarioDAO();
            dao.actualizarGmail(idUsuario, nuevoGmail);
            lblCorreoValor.setText(nuevoGmail);
            usuario.setCorreo_usuario(nuevoGmail);
            
            new Alert(Alert.AlertType.INFORMATION, "Correo actualizado correctamente.").showAndWait();
            ventanaGmail.close();
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> ventanaGmail.close());
        
        btnAceptar.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        btnCancelar.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
            new Label("Escribe el nuevo Correo:"), 
            campoGmail, 
            new javafx.scene.layout.HBox(10, btnAceptar, btnCancelar)
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 180);
        ventanaGmail.setScene(scene);
        ventanaGmail.initModality(Modality.APPLICATION_MODAL);
        ventanaGmail.showAndWait();
    }

    @FXML
    private void handleCambiarContrasena() {
        Stage ventanaContrasena = new Stage();
        ventanaContrasena.setTitle("Cambiar Contraseña");

        PasswordField campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Nueva contraseña");

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            String nuevaContrasena = campoContrasena.getText();
            if (nuevaContrasena.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "La contraseña no puede estar vacía.").showAndWait();
                return;
            }
            
            txtContrasena.setText(nuevaContrasena);
            lblContrasenaVisible.setText(nuevaContrasena);
            usuario.setContraseña_usuario(nuevaContrasena);
            
            new Alert(Alert.AlertType.INFORMATION, "Contraseña actualizada temporalmente.\nRecuerda guardar los cambios.").showAndWait();
            ventanaContrasena.close();
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> ventanaContrasena.close());
        
        btnAceptar.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        btnCancelar.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
            new Label("Escribe la nueva contraseña:"), 
            campoContrasena, 
            new javafx.scene.layout.HBox(10, btnAceptar, btnCancelar)
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 180);
        ventanaContrasena.setScene(scene);
        ventanaContrasena.initModality(Modality.APPLICATION_MODAL);
        ventanaContrasena.showAndWait();
    }

    @FXML
    private void handleMostrarContrasena() {
        boolean mostrar = chkMostrarContrasena.isSelected();
        lblContrasenaVisible.setVisible(mostrar);
        txtContrasena.setVisible(!mostrar);
    }

    @FXML
    private void handleGuardar() {
        String correo = lblCorreoValor.getText().trim();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || !correo.contains("@")) {
            new Alert(Alert.AlertType.WARNING, "Correo inválido.").showAndWait();
            return;
        }

        if (contrasena.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "La contraseña no puede estar vacía.").showAndWait();
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        usuario.setCorreo_usuario(correo);
        usuario.setContraseña_usuario(contrasena);

        if (dao.actualizarUsuario(usuario)) {
            SessionManager.getInstance().setUsuarioActual(usuario);
            new Alert(Alert.AlertType.INFORMATION, "Cambios guardados correctamente.").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error al guardar cambios.").showAndWait();
        }
    }

    // Método para inicializar con un usuario específico (si es necesario)
    public void inicializarConUsuario(Usuarios usuario) {
        this.usuario = usuario;
        cargarInformacionUsuario();
        cargarAvatarUsuario();
    }
}