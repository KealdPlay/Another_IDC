package controller;

import dao.ProductoDAO;
import entidades.Producto;
import entidades.Usuarios;
import interfaces.Configuracion;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController {
    
    @FXML
    private Label lblBienvenida;
    
    @FXML
    private Label lblHora;
    
    @FXML
    private Label lblFecha;
    
    @FXML
    private Label lblEstadoStock;
    
    @FXML
    private VBox vboxProductos;
    
    @FXML
    private Button btnReportes;
    
    @FXML
    private Button btnGestionInventario;
    
    @FXML
    private Button btnVerMas;
    
    @FXML
    private Button btnMenu;

    @FXML
    private Pane overlayPane;
    
    @FXML
    private VBox sideMenu;
    
    private Usuarios usuarioActual;
    private ProductoDAO productoDAO;
    private Timeline clockTimeline;
    private boolean menuVisible = false;
    
    
@FXML
public void initialize() {
    productoDAO = new ProductoDAO();
    inicializarReloj();
    cargarCentroNotificaciones();
    inicializarMenuLateral();
    
    try {
        String imagePath = getClass().getResource("/images/menu_icon.png").toExternalForm();
        
        btnMenu.setStyle(
            "-fx-background-image: url('" + imagePath + "'); " +
            "-fx-background-repeat: no-repeat; " +
            "-fx-background-position: center; " +
            "-fx-border-color: #1e1e1e;"+
            "-fx-background-size: contain;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;" 
        );
        
        // Establecer tamaño del botón
        btnMenu.setPrefWidth(41);
        btnMenu.setPrefHeight(41);
        
    } catch (Exception e) {
        System.err.println("Error cargando imagen: " + e.getMessage());
    }
}

// Método modificado para configurar el menú después de inicializar el usuario
public void inicializarConUsuario(Usuarios usuario) {
    this.usuarioActual = usuario;
    cargarInformacionUsuario();
    
    // Reconfigurar el menú ahora que tenemos el usuario
    configurarMenuItems();
    
    System.out.println("Usuario inicializado: " + usuario.getNombre_usuario() + ", ID: " + usuario.getId_usuario()); // Debug
}
    
private void inicializarMenuLateral() {
        if (overlayPane != null && sideMenu != null) {
            // Configurar el overlay (fondo semi-transparente)
            overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            overlayPane.setVisible(false);
            overlayPane.setOnMouseClicked(e -> ocultarMenu());
            
            // Configurar el menú lateral para que abarque toda la altura
            sideMenu.setStyle(
                "-fx-background-color: #2d2d2d; " +
                "-fx-background-radius: 0; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 2, 0);"
            );
            
            // Inicialmente oculto (fuera de la pantalla)
            sideMenu.setTranslateX(-350);
            
            // Configurar los elementos del menú
            configurarMenuItems();
        }
    }
    
private void configurarMenuItems() {
    if (sideMenu == null) return;
    
    sideMenu.getChildren().clear();
    sideMenu.setSpacing(0);
    sideMenu.setPrefWidth(350);
    sideMenu.setMaxHeight(Double.MAX_VALUE);
    
    // Header del menú con información del usuario
    VBox headerSection = new VBox();
    headerSection.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 30 20 30 20;");
    headerSection.setSpacing(15);
    headerSection.setAlignment(Pos.CENTER);
    headerSection.setPrefHeight(200);
    
    // Crear avatar circular con imagen del usuario
    javafx.scene.layout.StackPane avatarContainer = crearAvatarUsuario();
    
    // Obtener información del usuario - CORREGIDO
    String nombreUsuario = "Usuario"; // Valor por defecto
    String nombreRol = "Usuario"; // Valor por defecto
    
    // Verificar si usuarioActual no es nulo
    if (usuarioActual != null) {
        nombreUsuario = usuarioActual.getNombre_usuario();
        
        // Determinar el rol basado en el id_rol
        int idRol = usuarioActual.getId_rol();
        switch (idRol) {
            case 1:
                nombreRol = "Owner";
                break;
            case 2:
                nombreRol = "Admin";
                break;
            default:
                nombreRol = "Usuario";
                break;
        }
        
        System.out.println("Usuario: " + nombreUsuario + ", Rol ID: " + idRol + ", Rol: " + nombreRol); // Debug
    } else {
        System.out.println("usuarioActual es nulo en configurarMenuItems()"); // Debug
        
        // Intentar obtener el usuario de SessionManager
        usuarioActual = SessionManager.getInstance().getUsuarioActual();
        if (usuarioActual != null) {
            nombreUsuario = usuarioActual.getNombre_usuario();
            int idRol = usuarioActual.getId_rol();
            switch (idRol) {
                case 1:
                    nombreRol = "Owner";
                    break;
                case 2:
                    nombreRol = "Admin";
                    break;
                default:
                    nombreRol = "Usuario";
                    break;
            }
            System.out.println("Usuario obtenido de SessionManager: " + nombreUsuario + ", Rol: " + nombreRol); // Debug
        }
    }
    
    Label lblUsuario = new Label(nombreUsuario);
    lblUsuario.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
    
    Label lblRol = new Label(nombreRol);
    lblRol.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px;");
    
    headerSection.getChildren().addAll(avatarContainer, lblUsuario, lblRol);
    
    // Separador
    Pane separator = new Pane();
    separator.setStyle("-fx-background-color: #404040;");
    separator.setPrefHeight(1);
    separator.setMaxHeight(1);
    
    // Opciones del menú
    VBox menuOptions = new VBox();
    menuOptions.setSpacing(0);
    menuOptions.setStyle("-fx-padding: 20 0;");
    
    // Crear botones del menú con iconos
    Button btnConfiguracion = crearBotonMenu("⚙️", "Configuración", this::irAConfiguracion);
    
    menuOptions.getChildren().addAll(btnConfiguracion);
    
    // Spacer para empujar el footer hacia abajo
    Region spacer = new Region();
    VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
    
    // Sección inferior con botón de cerrar sesión
    VBox footerSection = new VBox();
    footerSection.setStyle("-fx-padding: 20; -fx-background-color: #1e1e1e;");
    footerSection.setPrefHeight(80);
    
    Button btnCerrarSesion = crearBotonMenu("🚪", "Cerrar Sesión", this::cerrarSesion);
    btnCerrarSesion.setStyle(
        "-fx-background-color: transparent; " +
        "-fx-text-fill: #ff6b6b; " +
        "-fx-font-size: 16px; " +
        "-fx-alignment: center-left; " +
        "-fx-padding: 15 20; " +
        "-fx-pref-width: 310; " +
        "-fx-cursor: hand;"
    );
    
    footerSection.getChildren().add(btnCerrarSesion);
    
    sideMenu.getChildren().addAll(
        headerSection, separator, menuOptions, spacer, footerSection
    );
}
    
    private Button crearBotonMenu(String icono, String texto, Runnable accion) {
        Button btn = new Button();
        
        // Crear HBox para el contenido del botón
        HBox contenido = new HBox();
        contenido.setSpacing(15);
        contenido.setAlignment(Pos.CENTER_LEFT);
        
        // Label para el icono
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        lblIcono.setPrefWidth(30);
        
        // Label para el texto
        Label lblTexto = new Label(texto);
        lblTexto.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        
        contenido.getChildren().addAll(lblIcono, lblTexto);
        
        btn.setGraphic(contenido);
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-alignment: center-left; " +
            "-fx-padding: 15 20; " +
            "-fx-pref-width: 310; " +
            "-fx-cursor: hand; " +
            "-fx-border-width: 0; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent;"
        );
        
        // Efectos hover
        btn.setOnMouseEntered(e -> 
            btn.setStyle(btn.getStyle() + "-fx-background-color: #404040;")
        );
        btn.setOnMouseExited(e -> 
            btn.setStyle(btn.getStyle().replace("-fx-background-color: #404040;", ""))
        );
        
        btn.setOnAction(e -> {
            ocultarMenu();
            accion.run();
        });
        
        return btn;
    }
    
private javafx.scene.layout.StackPane crearAvatarUsuario() {
    // Ruta por defecto
    String rutaImagen = "src/view/img/perfil.png";
    
    // Intentar leer la ruta personalizada del archivo de texto
    if (usuarioActual != null) {
        File archivoRuta = new File("foto_usuario" + usuarioActual.getId_usuario() + ".txt");
        if (archivoRuta.exists()) {
            try (Scanner scanner = new Scanner(archivoRuta)) {
                if (scanner.hasNextLine()) {
                    String rutaGuardada = scanner.nextLine().trim(); // Agregar trim()
                    if (new File(rutaGuardada).exists()) {
                        rutaImagen = rutaGuardada;
                        System.out.println("Cargando imagen personalizada: " + rutaImagen); // Debug
                    } else {
                        System.out.println("Archivo de imagen no existe: " + rutaGuardada); // Debug
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al leer ruta de imagen: " + e.getMessage());
            }
        } else {
            System.out.println("Archivo de ruta no existe: foto_usuario" + usuarioActual.getId_usuario() + ".txt"); // Debug
        }
    }
    
    // Crear StackPane contenedor
    javafx.scene.layout.StackPane avatarContainer = new javafx.scene.layout.StackPane();
    avatarContainer.setPrefSize(80, 80);
    avatarContainer.setMaxSize(80, 80);
    
    // Crear ImageView
    ImageView avatarImageView = null;
    boolean imagenCargada = false;
    
    try {
        Image imagen = null;
        
        // Manejar diferentes tipos de rutas
        if (rutaImagen.startsWith("src/")) {
            // Para imágenes de recursos internos
            String resourcePath = "/" + rutaImagen.substring(4);
            System.out.println("Intentando cargar recurso: " + resourcePath); // Debug
            imagen = new Image(getClass().getResourceAsStream(resourcePath));
        } else {
            // Para imágenes externas seleccionadas por el usuario
            File archivoImagen = new File(rutaImagen);
            if (archivoImagen.exists()) {
                System.out.println("Cargando imagen externa: " + rutaImagen); // Debug
                imagen = new Image(archivoImagen.toURI().toString());
            } else {
                System.out.println("Archivo de imagen externa no existe: " + rutaImagen); // Debug
                // Intentar cargar imagen por defecto
                imagen = new Image(getClass().getResourceAsStream("/view/img/perfil.png"));
            }
        }
        
        if (imagen != null && !imagen.isError()) {
            avatarImageView = new ImageView(imagen);
            avatarImageView.setFitWidth(80);
            avatarImageView.setFitHeight(80);
            avatarImageView.setPreserveRatio(false); // Cambiar a false para llenar el círculo
            avatarImageView.setSmooth(true); // Mejorar calidad
            
            // Aplicar máscara circular
            Circle clip = new Circle(40, 40, 40);
            avatarImageView.setClip(clip);
            
            avatarContainer.getChildren().add(avatarImageView);
            imagenCargada = true;
            System.out.println("Imagen cargada exitosamente"); // Debug
        } else {
            System.out.println("Error en la imagen o imagen nula"); // Debug
        }
    } catch (Exception e) {
        System.err.println("Error al cargar imagen de perfil: " + e.getMessage());
        e.printStackTrace(); // Mostrar stack trace completo
    }
    
    // Si no se pudo cargar la imagen, crear avatar por defecto
    if (!imagenCargada) {
        System.out.println("Creando avatar por defecto"); // Debug
        Circle circuloDefecto = new Circle(40);
        circuloDefecto.setFill(Color.web("#4FD1C7"));
        avatarContainer.getChildren().add(circuloDefecto);
        
        // Agregar inicial del nombre si está disponible
        if (usuarioActual != null && !usuarioActual.getNombre_usuario().isEmpty()) {
            Label inicial = new Label(String.valueOf(usuarioActual.getNombre_usuario().charAt(0)).toUpperCase());
            inicial.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
            avatarContainer.getChildren().add(inicial);
        }
    }
    
    // Agregar borde blanco
    Circle borde = new Circle(40);
    borde.setFill(Color.TRANSPARENT);
    borde.setStroke(Color.WHITE);
    borde.setStrokeWidth(3);
    avatarContainer.getChildren().add(borde);
    
    return avatarContainer;
}
    
    private ImageView crearAvatarPorDefecto() {
        // Crear un círculo de color como avatar por defecto
        Circle circuloDefecto = new Circle(40);
        circuloDefecto.setFill(Color.web("#4FD1C7"));
        circuloDefecto.setStroke(Color.WHITE);
        circuloDefecto.setStrokeWidth(3);
        
        // Convertir el círculo a imagen (método simplificado)
        // Por simplicidad, vamos a crear un ImageView vacío y manejar esto diferente
        ImageView avatarDefecto = new ImageView();
        avatarDefecto.setFitWidth(80);
        avatarDefecto.setFitHeight(80);
        
        return avatarDefecto;
    }
    
    @FXML
    public void toggleMenu() {
        if (menuVisible) {
            ocultarMenu();
        } else {
            mostrarMenu();
        }
    }
    
    private void mostrarMenu() {
        if (overlayPane != null && sideMenu != null) {
            overlayPane.setVisible(true);
            
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), sideMenu);
            slideIn.setFromX(-350);
            slideIn.setToX(0);
            slideIn.play();
            
            menuVisible = true;
        }
    }
    
    private void ocultarMenu() {
        if (overlayPane != null && sideMenu != null) {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), sideMenu);
            slideOut.setFromX(0);
            slideOut.setToX(-350);
            slideOut.setOnFinished(e -> overlayPane.setVisible(false));
            slideOut.play();
            
            menuVisible = false;
        }
    }

    private void irAConfiguracion() {
        System.out.println("Ir a Configuración");
        try {
            Configuracion configuracion = new Configuracion();
            Stage nuevaVentana = new Stage();
            configuracion.start(nuevaVentana);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void cerrarSesion() {
        System.out.println("Cerrando sesión...");
        volverALogin();
    }

    private void cargarInformacionUsuario() {
        if (usuarioActual != null) {
            lblBienvenida.setText("Bienvenida, " + usuarioActual.getNombre_usuario() + ".");
        }
    }
    
    private void inicializarReloj() {
        // Actualizar reloj cada segundo
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> actualizarReloj()));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
        
        // Inicializar inmediatamente
        actualizarReloj();
    }
    
    private void actualizarReloj() {
        LocalDateTime ahora = LocalDateTime.now();
        
        String hora = ahora.format(DateTimeFormatter.ofPattern("HH:mm"));
        String fecha = ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        lblHora.setText(hora);
        lblFecha.setText(fecha);
    }
    
    private void cargarCentroNotificaciones() {
        cargarEstadoStock();
        cargarProductosRecientes();
    }
    
    private void cargarEstadoStock() {
        try {
            // Obtener estadísticas generales
            int totalProductos = productoDAO.obtenerTotalProductos();
            List<Producto> productosStockBajo = productoDAO.obtenerProductosConStockBajo(50);
            
            // Calcular porcentaje de stock general (simulado)
            double porcentajeStock = calcularPorcentajeStock();
            
            String estadoTexto;
            String colorTexto;
            
            if (porcentajeStock >= 80) {
                estadoTexto = String.format("Lleno al %.0f%%", porcentajeStock);
                colorTexto = "#4FD1C7"; // Verde agua
            } else if (porcentajeStock >= 50) {
                estadoTexto = String.format("Medio al %.0f%%", porcentajeStock);
                colorTexto = "#F6E05E"; // Amarillo
            } else {
                estadoTexto = String.format("Bajo al %.0f%%", porcentajeStock);
                colorTexto = "#FC8181"; // Rojo
            }
            
            lblEstadoStock.setText(estadoTexto);
            lblEstadoStock.setStyle("-fx-text-fill: " + colorTexto + ";");
            
        } catch (Exception e) {
            System.err.println("Error al cargar estado del stock: " + e.getMessage());
            lblEstadoStock.setText("Error al cargar");
            lblEstadoStock.setStyle("-fx-text-fill: #FC8181;");
        }
    }
    
    private double calcularPorcentajeStock() {
        try {
            List<Producto> todosProductos = productoDAO.obtenerTodos();
            if (todosProductos.isEmpty()) return 0;
            
            int stockTotal = 0;
            int stockMaximoEstimado = 0;
            
            for (Producto producto : todosProductos) {
                stockTotal += producto.getStockProducto();
                // Estimamos que el máximo deseable es el doble del stock actual o mínimo 100
                stockMaximoEstimado += Math.max(producto.getStockProducto() * 2, 100);
            }
            
            return Math.min(100, (double) stockTotal / stockMaximoEstimado * 100);
            
        } catch (Exception e) {
            System.err.println("Error al calcular porcentaje de stock: " + e.getMessage());
            return 75; // Valor por defecto
        }
    }
    
    private void cargarProductosRecientes() {
        try {
            vboxProductos.getChildren().clear();
            
            // Obtener los primeros 5 productos de la base de datos
            List<Producto> productos = productoDAO.obtenerTodos();
            int limite = Math.min(8, productos.size());
            
            for (int i = 0; i < limite; i++) {
                Producto producto = productos.get(i);
                VBox itemProducto = crearItemProducto(producto);
                vboxProductos.getChildren().add(itemProducto);
            }
            
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            
            // Mostrar mensaje de error
            Label lblError = new Label("Error al cargar productos");
            lblError.setStyle("-fx-text-fill: #FC8181; -fx-font-size: 12px;");
            vboxProductos.getChildren().add(lblError);
        }
    }
    
    private VBox crearItemProducto(Producto producto) {
        VBox itemContainer = new VBox();
        itemContainer.setSpacing(5);
        itemContainer.setStyle("-fx-background-color: #333333; -fx-background-radius: 8; -fx-padding: 12;");
        
        // Nombre del producto
        Label lblNombre = new Label(producto.getNombreProducto());
        lblNombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        // Información del stock
        HBox stockInfo = new HBox();
        stockInfo.setSpacing(10);
        
        Label lblStock = new Label(producto.getStockProducto() + " piezas");
        
        // Determinar color según el nivel de stock
        String colorStock;
        if (producto.getStockProducto() < 25) {
            colorStock = "#FC8181"; // Rojo para stock crítico
        } else if (producto.getStockProducto() < 50) {
            colorStock = "#F6E05E"; // Amarillo para stock bajo
        } else {
            colorStock = "#4FD1C7"; // Verde para stock normal
        }
        
        lblStock.setStyle("-fx-text-fill: " + colorStock + "; -fx-font-size: 11px;");
        
        // Precio (opcional)
        Label lblPrecio = new Label("$" + producto.getPrecioProducto().toString());
        lblPrecio.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 11px;");
        
        stockInfo.getChildren().addAll(lblStock, lblPrecio);
        
        itemContainer.getChildren().addAll(lblNombre, stockInfo);
        
        // Efecto hover
        itemContainer.setOnMouseEntered(e -> 
            itemContainer.setStyle("-fx-background-color: #404040; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;")
        );
        itemContainer.setOnMouseExited(e -> 
            itemContainer.setStyle("-fx-background-color: #333333; -fx-background-radius: 8; -fx-padding: 12;")
        );
        
        return itemContainer;
    }
    
    @FXML
    public void handleVerMasProductos(ActionEvent event) {
        System.out.println("Ver más productos clickeado");
        handleReportes(event);
    }
    
    @FXML
    public void handleGestionInventario(ActionEvent event) {
        System.out.println("Gestión de Inventario clickeado");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/InventarioInterfaz.fxml"));
            Scene scene = new Scene(loader.load());
            
            InventarioController inventarioController = loader.getController();
            inventarioController.setUsuarioActual(usuarioActual);
            
            Stage stage = new Stage();
            stage.setTitle("IDC - Gestión de Inventario");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
            Stage currentStage = (Stage) btnGestionInventario.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de inventario: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleReportes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/ReportesInterfaces-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            ReportesInterfaces reportesController = loader.getController();
            reportesController.inicializarConUsuario(usuarioActual);
            
            Stage stage = new Stage();
            stage.setTitle("IDC - Reportes de Inventario");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
            Stage currentStage = (Stage) btnReportes.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void volverALogin() {
        try {
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = new Stage();
            stage.setTitle("Sistema de Autenticación");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            Stage currentStage = (Stage) btnReportes.getScene().getWindow();
            currentStage.close();
           
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void cleanup() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
    }
}