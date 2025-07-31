package interfaces;

import controller.SessionManager; // Cambiar import
import dao.UsuarioDAO;
import entidades.Usuarios;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Configuracion extends Application {

    private TextField txtNombre, txtCorreo;
    private PasswordField txtContrasena;
    private ImageView avatar;
    private File imagenSeleccionada;
    private Usuarios usuario;

    @Override
    public void start(Stage primaryStage) {
        // Cambiar esta línea para usar SessionManager
        usuario = SessionManager.getInstance().getUsuarioActual();

        if (usuario == null) {
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la información del usuario.\nNo hay sesión activa.").showAndWait();
            return;
        }

        Label lblTitulo = new Label("Configuración");
        lblTitulo.setFont(Font.font("Arial", 20));
        lblTitulo.setTextFill(Color.WHITE);

        // Cargar imagen desde archivo .txt si existe, sino usar predeterminada
        String rutaImagen = "src/view/img/perfil.png";
        // Cambiar para usar el ID del usuario de SessionManager
        File archivoRuta = new File("foto_usuario" + usuario.getId_usuario() + ".txt");
        if (archivoRuta.exists()) {
            try (Scanner scanner = new Scanner(archivoRuta)) {
                if (scanner.hasNextLine()) {
                    String rutaGuardada = scanner.nextLine();
                    if (new File(rutaGuardada).exists()) {
                        rutaImagen = rutaGuardada;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al leer ruta de imagen: " + e.getMessage());
            }
        }
 
        avatar = new ImageView(new Image("file:" + rutaImagen));
        avatar.setFitWidth(120);
        avatar.setFitHeight(120);
        Circle clip = new Circle(60, 60, 60);
        avatar.setClip(clip);
 
        Button btnCambiarFoto = new Button("Cambiar Foto");
        btnCambiarFoto.setOnAction(e -> cambiarFoto(primaryStage));
 
        txtNombre = new TextField(usuario.getNombre_usuario());
        estiloCampo(txtNombre);
 
        txtCorreo = new TextField(usuario.getCorreo_usuario());
        estiloCampo(txtCorreo);
 
        txtContrasena = new PasswordField();
        txtContrasena.setText(usuario.getContraseña_usuario());
        estiloCampo(txtContrasena);
 
        Button btnGuardar = new Button("Aceptar");
        btnGuardar.setOnAction(e -> guardarCambios());
 
        VBox root = new VBox(15,
                lblTitulo,
                avatar,
                btnCambiarFoto,
                new Label("Nombre:"), txtNombre,
                new Label("Correo:"), txtCorreo,
                new Label("Contraseña:"), txtContrasena,
                btnGuardar
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #111111;");
 
        // Estilo blanco para labels
        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof Label lbl) {
                lbl.setTextFill(Color.WHITE);
            }
        }
 
        Scene scene = new Scene(root, 400, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Configuración de Usuario");
        primaryStage.show();
    }
 
    private void estiloCampo(TextField campo) {
        campo.setMaxWidth(250);
        campo.setAlignment(Pos.CENTER);
    }
 
    private void cambiarFoto(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva foto");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        imagenSeleccionada = fileChooser.showOpenDialog(stage);
 
        if (imagenSeleccionada != null) {
            avatar.setImage(new Image(imagenSeleccionada.toURI().toString()));
 
            // Cambiar para usar el ID del usuario de SessionManager
            try (PrintWriter writer = new PrintWriter("foto_usuario" + usuario.getId_usuario() + ".txt")) {
                writer.println(imagenSeleccionada.getAbsolutePath());
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la ruta de la imagen: " + ex.getMessage());
            }
        }
    }
 
    private void guardarCambios() {
        String correo = txtCorreo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String contrasena = txtContrasena.getText();
        
        // Validaciones
        if (nombre.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "El nombre no puede estar vacío.").showAndWait();
            return;
        }
        
        if (correo.isEmpty() || !correo.contains("@")) {
            new Alert(Alert.AlertType.WARNING, "Ingrese un correo válido.").showAndWait();
            return;
        }
        
        if (contrasena.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "La contraseña no puede estar vacía.").showAndWait();
            return;
        }
        
        UsuarioDAO dao = new UsuarioDAO();
        
        // Verificar si el correo ya existe para otro usuario
        if (!correo.equals(usuario.getCorreo_usuario()) && 
            dao.existeCorreoUsuarioExceptoActual(correo, usuario.getId_usuario())) {
            new Alert(Alert.AlertType.WARNING, "Este correo ya está registrado por otro usuario.").showAndWait();
            return;
        }
 
        // Actualizar los datos del usuario
        usuario.setNombre_usuario(nombre);
        usuario.setCorreo_usuario(correo);
        usuario.setContraseña_usuario(contrasena);
 
        if (dao.actualizarUsuario(usuario)) {
            // Actualizar también la información en SessionManager
            SessionManager.getInstance().setUsuarioActual(usuario);
            new Alert(Alert.AlertType.INFORMATION, "Cambios guardados correctamente.").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error al guardar cambios.").showAndWait();
        }
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}