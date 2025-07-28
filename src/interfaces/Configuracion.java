package interfaces;

import controller.sesion;
import dao.UsuariosConfDAO;
import entidades.UsuarioConfiguracion;
import entidades.Foto;
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

public class Configuracion extends Application {

    private TextField txtNombre, txtCorreo;
    private PasswordField txtContrasena;
    private ImageView avatar;
    private UsuarioConfiguracion usuario;

    @Override
    public void start(Stage primaryStage) {
        UsuariosConfDAO dao = new UsuariosConfDAO();
        usuario = dao.obtenerUsuarioPorId(sesion.idUsuarioActivo);

        Label lblTitulo = new Label("Configuración");
        lblTitulo.setFont(Font.font("Arial", 20));
        lblTitulo.setTextFill(Color.WHITE);

        // Avatar Circular
        avatar = new ImageView(new Image("file:" + usuario.getFoto().getDireccionFoto()));
        avatar.setFitWidth(120);
        avatar.setFitHeight(120);
        Circle clip = new Circle(60, 60, 60);
        avatar.setClip(clip);

        Button btnCambiarFoto = new Button("Cambiar Foto");
        btnCambiarFoto.setOnAction(e -> cambiarFoto(primaryStage));

        // Nombre
        txtNombre = new TextField(usuario.getNombre());
        estiloCampo(txtNombre);

        // Correo
        txtCorreo = new TextField(usuario.getCorreo());
        estiloCampo(txtCorreo);

        // Contraseña
        txtContrasena = new PasswordField();
        txtContrasena.setText(usuario.getContrasena());
        estiloCampo(txtContrasena);

        // Botón guardar
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

        // Etiquetas blancas
        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof Label lbl) lbl.setTextFill(Color.WHITE);
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
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            avatar.setImage(new Image(file.toURI().toString()));
            usuario.getFoto().setDireccionFoto(file.getAbsolutePath());
          if (usuario.getFoto() == null) {
                usuario.setFoto(new Foto());
            }
            usuario.getFoto().setDireccionFoto(file.getAbsolutePath());
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
