package interfaces;
 
import controller.SessionManager;
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
import javafx.stage.Modality;//este va por default
import javafx.stage.Stage;
 
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
 
public class Configuracion extends Application {
 
    private TextField txtCorreo;
    private PasswordField txtContrasena;
    private ImageView avatar;
    private File imagenSeleccionada;
    private Usuarios usuario;
 
    @Override
    public void start(Stage primaryStage) {
        usuario = SessionManager.getInstance().getUsuarioActual();
 
        if (usuario == null) {
            new Alert(Alert.AlertType.ERROR, "No se pudo cargar la información del usuario.").showAndWait();
            return;
        }
 
        Label lblTitulo = new Label("Configuración");
        lblTitulo.setFont(Font.font("Arial", 20));
        lblTitulo.setTextFill(Color.WHITE);
        HBox header = new HBox(lblTitulo);
        header.setAlignment(Pos.TOP_LEFT);
        header.setPadding(new Insets(10, 0, 0, 10));
 
        Label lblSaludo = new Label("Hola, " + usuario.getNombre_usuario() + "!");
        lblSaludo.setFont(Font.font("Arial", 16));
        lblSaludo.setTextFill(Color.WHITE);
 
        String rutaImagen = "src/view/img/perfil.png";
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
 
        VBox fotoBox = new VBox(10, lblSaludo, avatar, btnCambiarFoto);
        fotoBox.setAlignment(Pos.CENTER);
        fotoBox.setPadding(new Insets(10));
 
        Label lblCorreo = new Label("Correo:");
        lblCorreo.setTextFill(Color.WHITE);
        txtCorreo = new TextField(usuario.getCorreo_usuario());
        txtCorreo.setMaxWidth(250);
        txtCorreo.setAlignment(Pos.CENTER);
 
        Button btnCambiarGmail = new Button("Cambiar Correo");
        btnCambiarGmail.setOnAction(e -> abrirVentanaCambiarGmail());
 
        Label lblContrasena = new Label("Contraseña:");
        lblContrasena.setTextFill(Color.WHITE);
        txtContrasena = new PasswordField();
        txtContrasena.setText(usuario.getContraseña_usuario());
        txtContrasena.setMaxWidth(250);
        txtContrasena.setAlignment(Pos.CENTER);
 
        Button btnCambiarContrasena = new Button("Cambiar Contraseña");
        btnCambiarContrasena.setOnAction(e -> abrirVentanaCambiarContrasena());
 
        Button btnGuardar = new Button("Aceptar");
        btnGuardar.setOnAction(e -> guardarCambios());
 
        VBox content = new VBox(10,
                fotoBox,
                lblCorreo, txtCorreo,
                btnCambiarGmail,
                lblContrasena, txtContrasena,
                btnCambiarContrasena,
                btnGuardar
        );
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
 
        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: #111111;");
 
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Configuración de Usuario");
        primaryStage.show();
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
 
            try (PrintWriter writer = new PrintWriter("foto_usuario" + usuario.getId_usuario() + ".txt")) {
                writer.println(imagenSeleccionada.getAbsolutePath());
            } catch (Exception ex) {
                System.err.println("No se pudo guardar la ruta de la imagen: " + ex.getMessage());
            }
        }
    }
 
    private void abrirVentanaCambiarGmail() {
        Stage ventanaGmail = new Stage();
        ventanaGmail.setTitle("Cambiar Tu correo!");
 
        TextField campoGmail = new TextField();
        campoGmail.setPromptText("Nuevo correo");
 
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            String nuevoGmail = campoGmail.getText();
            int idUsuario = usuario.getId_usuario();
            UsuarioDAO dao = new UsuarioDAO();
            dao.actualizarGmail(idUsuario, nuevoGmail);
            txtCorreo.setText(nuevoGmail);
            ventanaGmail.close();
        });
 
        VBox layout = new VBox(10, new Label("Escribe el nuevo Correo:"), campoGmail, btnAceptar);
        layout.setPadding(new Insets(20));
 
        Scene scene = new Scene(layout, 300, 150);
        ventanaGmail.setScene(scene);
        ventanaGmail.initModality(Modality.APPLICATION_MODAL);
        ventanaGmail.showAndWait();
    }
 
    private void abrirVentanaCambiarContrasena() {
        Stage ventanaContrasena = new Stage();
        ventanaContrasena.setTitle("Cambia tu Contraseña!!");
 
        PasswordField campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Nueva contraseña");
 
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            String nuevaContrasena = campoContrasena.getText();
            txtContrasena.setText(nuevaContrasena);
            ventanaContrasena.close();
        });
 
        VBox layout = new VBox(10, new Label("Escribe la nueva contraseña por favor :"), campoContrasena, btnAceptar);
        layout.setPadding(new Insets(20));
 
        Scene scene = new Scene(layout, 300, 150);
        ventanaContrasena.setScene(scene);
        ventanaContrasena.initModality(Modality.APPLICATION_MODAL);
        ventanaContrasena.showAndWait();
    }
 
    private void guardarCambios() {
        String correo = txtCorreo.getText().trim();
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
 
    public static void main(String[] args) {
        launch(args);
    }
}