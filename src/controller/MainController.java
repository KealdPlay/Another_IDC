package controller;

import dao.ProductoDAO;
import entidades.Producto;
import entidades.Usuarios;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
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
    
    private Usuarios usuarioActual;
    private ProductoDAO productoDAO;
    private Timeline clockTimeline;
    
    public void inicializarConUsuario(Usuarios usuario) {
        this.usuarioActual = usuario;
        cargarInformacionUsuario();
    }
    
    @FXML
    public void initialize() {
        productoDAO = new ProductoDAO();
        inicializarReloj();
        cargarCentroNotificaciones();
        try {
        String imagePath = getClass().getResource("/images/menu_icon.png").toExternalForm();
        
        btnMenu.setStyle(
            "-fx-background-image: url('" + imagePath + "'); " +
            "-fx-background-repeat: no-repeat; " +
            "-fx-background-position: center; " +
            "-fx-border-color: #1e1e1e;"+
            "-fx-background-size: contain;"
        );
        
        // Establecer tamaño del botón
        btnMenu.setPrefWidth(41);
        btnMenu.setPrefHeight(41);
        
    } catch (Exception e) {
        System.err.println("Error cargando imagen: " + e.getMessage());
    }
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
        // Implementar navegación a gestión de inventario
        // Por ahora redirige a reportes como placeholder
        handleReportes(event);
    }
    
    @FXML
    public void handleReportes(ActionEvent event) {
        try {
            // Cargar el archivo FXML con la ruta correcta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/ReportesInterfaces-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador de ReportesInterfaces
            ReportesInterfaces reportesController = loader.getController();
            reportesController.inicializarConUsuario(usuarioActual);
            
            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("IDC - Reportes de Inventario");
            stage.setScene(scene);
            stage.setMaximized(true); // Para que se abra maximizada
            stage.show();
            
            // Detener el timeline del reloj antes de cerrar
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
            // Cerrar la ventana actual (main-view)
            Stage currentStage = (Stage) btnReportes.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void volverALogin() {
        try {
            // Detener el timeline del reloj
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
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
    
    // Método para limpiar recursos cuando se cierre la ventana
    public void cleanup() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
    }
}