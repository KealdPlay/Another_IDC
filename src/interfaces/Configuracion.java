package interfaces;

import controller.sesion;
import dao.UsuariosConfDAO;
import entidades.UsuarioConfiguracion;
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
    private UsuarioConfiguracion usuario;

    @Override
    public void start(Stage primaryStage) {
        UsuariosConfDAO dao = new UsuariosConfDAO();
        usuario = dao.obtenerUsuarioPorId(sesion.idUsuarioActivo);

        Label lblTitulo = new Label("Configuración");
        lblTitulo.setFont(Font.font("Arial", 20));
        lblTitulo.setTextFill(Color.WHITE);

        // Cargar imagen desde archivo .txt si existe, sino usar predeterminada
        String rutaImagen = "src/view/img/perfil.png";
        File archivoRuta = new File("foto_usuario" + sesion.idUsuarioActivo + ".txt");
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
 
        txtNombre = new TextField(usuario.getNombre());
        estiloCampo(txtNombre);
 
        txtCorreo = new TextField(usuario.getCorreo());
        estiloCampo(txtCorreo);
 
        txtContrasena = new PasswordField();
        txtContrasena.setText(usuario.getContrasena());
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
 
            // Guardar ruta en archivo .txt
            try (PrintWriter writer = new PrintWriter("foto_usuario" + sesion.idUsuarioActivo + ".txt")) {
                writer.println(imagenSeleccionada.getAbsolutePath());
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la ruta de la imagen: " + ex.getMessage());
            }
        }
    }
 
    private void guardarCambios() {
        String correo = txtCorreo.getText();
 
        if (!correo.contains("@")) {
            new Alert(Alert.AlertType.WARNING, "El correo debe contener '@'.").showAndWait();
            return;
        }
 
        usuario.setNombre(txtNombre.getText());
        usuario.setCorreo(txtCorreo.getText());
        usuario.setContrasena(txtContrasena.getText());
 
        UsuariosConfDAO dao = new UsuariosConfDAO();
        if (dao.actualizarUsuario(usuario)) {
            new Alert(Alert.AlertType.INFORMATION, "Cambios guardados correctamente.").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Error al guardar cambios.").showAndWait();
        }
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
