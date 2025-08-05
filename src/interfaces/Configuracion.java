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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

public class Configuracion extends Application {

    private Label lblCorreoValor;
    private PasswordField txtContrasena;
    private Label lblContrasenaVisible;
    private CheckBox chkMostrarContrasena;
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

        // Título
        Label lblTitulo = new Label("Configuración");
        lblTitulo.setFont(Font.font("Arial", 50));
        lblTitulo.setTextFill(Color.WHITE);

        // Botón volver
        Button btnVolver = new Button("Volver al menú");
        btnVolver.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        btnVolver.setOnAction(e -> {
            try {
                URL fxmlUrl = getClass().getResource("/main-view.fxml");
                System.out.println("FXML encontrado en: " + fxmlUrl);
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.setTitle("Menú Principal");
                stage.show();

                ((Stage) btnVolver.getScene().getWindow()).close();
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "No se pudo cargar la vista principal.").showAndWait();
            }
        });

        // Encabezado con título + botón
        VBox header = new VBox(10, lblTitulo, btnVolver);
        header.setAlignment(Pos.TOP_LEFT);
        header.setPadding(new Insets(10, 0, 0, 10));

        Label lblSaludo = new Label("Hola , " + usuario.getNombre_usuario() + "!");
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
        lblCorreoValor = new Label(usuario.getCorreo_usuario());
        lblCorreoValor.setTextFill(Color.WHITE);
        lblCorreoValor.setFont(Font.font("Arial", 14));

        Button btnCambiarGmail = new Button("Cambiar Correo");
        btnCambiarGmail.setOnAction(e -> abrirVentanaCambiarGmail());

        Label lblContrasena = new Label("Contraseña:");
        lblContrasena.setTextFill(Color.WHITE);

        txtContrasena = new PasswordField();
        txtContrasena.setText(usuario.getContraseña_usuario());
        txtContrasena.setMaxWidth(250);
        txtContrasena.setAlignment(Pos.CENTER);

        lblContrasenaVisible = new Label(usuario.getContraseña_usuario());
        lblContrasenaVisible.setTextFill(Color.WHITE);
        lblContrasenaVisible.setFont(Font.font("Arial", 14));
        lblContrasenaVisible.setVisible(false);

        chkMostrarContrasena = new CheckBox("Mostrar Contraseña");
        chkMostrarContrasena.setTextFill(Color.WHITE);
        chkMostrarContrasena.setOnAction(e -> {
            boolean mostrar = chkMostrarContrasena.isSelected();
            lblContrasenaVisible.setVisible(mostrar);
            txtContrasena.setVisible(!mostrar);
        });

        Button btnCambiarContrasena = new Button("Cambiar Contraseña");
        btnCambiarContrasena.setOnAction(e -> abrirVentanaCambiarContrasena());

        Button btnGuardar = new Button("Aceptar");
        btnGuardar.setOnAction(e -> guardarCambios());

        // Estilos
        String estiloBoton = "-fx-background-color: #333333; -fx-text-fill: white;";
        btnCambiarFoto.setStyle(estiloBoton);
        btnCambiarGmail.setStyle(estiloBoton);
        btnCambiarContrasena.setStyle(estiloBoton);
        btnGuardar.setStyle(estiloBoton);

        VBox content = new VBox(10,
                fotoBox,
                lblCorreo, lblCorreoValor, btnCambiarGmail,
                lblContrasena, txtContrasena, lblContrasenaVisible, chkMostrarContrasena, btnCambiarContrasena,
                btnGuardar
        );
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: #111111;");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Configuración de Usuario");
        primaryStage.setMaximized(true);
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
        ventanaGmail.setTitle("Cambiar Gmail");

        TextField campoGmail = new TextField();
        campoGmail.setPromptText("Nuevo Correo");

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            String nuevoGmail = campoGmail.getText();
            int idUsuario = usuario.getId_usuario();
            UsuarioDAO dao = new UsuarioDAO();
            dao.actualizarGmail(idUsuario, nuevoGmail);
            lblCorreoValor.setText(nuevoGmail);
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
        ventanaContrasena.setTitle("Cambiar Contraseña");

        PasswordField campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Nueva contraseña");

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            String nuevaContrasena = campoContrasena.getText();
            txtContrasena.setText(nuevaContrasena);
            lblContrasenaVisible.setText(nuevaContrasena);
            ventanaContrasena.close();
        });

        VBox layout = new VBox(10, new Label("Escribe la nueva contraseña:"), campoContrasena, btnAceptar);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 300, 150);
        ventanaContrasena.setScene(scene);
        ventanaContrasena.initModality(Modality.APPLICATION_MODAL);
        ventanaContrasena.showAndWait();
    }

    private void guardarCambios() {
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

    public static void main(String[] args) {
        launch(args);
    }
}