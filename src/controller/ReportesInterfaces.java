package controller;

import dao.ProductoDAO;
import entidades.Usuarios;
import entidades.Producto;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javafx.application.Platform;

public class ReportesInterfaces extends Application {
    
    @FXML
    private BorderPane mainPanel;
    
    @FXML
    private Button backButton;
    
    @FXML
    private VBox monthlyCard;
    
    @FXML
    private VBox lowStockCard;
    
    @FXML
    private VBox overStockCard;
    
    @FXML
    private Label monthlyContent;
    
    @FXML
    private Label lowStockContent;
    
    @FXML
    private Label overStockContent;
    
 // Variable para mantener referencia al usuario actual
    private Usuarios usuarioActual;
    
public void inicializarConUsuario(Usuarios usuario) {
    this.usuarioActual = usuario;
    
    // Validación adicional de seguridad
    if (!validarPermisos(usuario)) {
        mostrarErrorPermisos();
        return;
    }
    
    System.out.println("Acceso a reportes autorizado para: " + usuario.getNombre_usuario());
}

private boolean validarPermisos(Usuarios usuario) {
    if (usuario == null) {
        System.err.println("Usuario nulo intentando acceder a reportes");
        return false;
    }
    
    // Solo Owner (id_rol = 1) puede acceder
    if (usuario.getId_rol() != 1) {
        System.err.println("Usuario sin permisos intentando acceder a reportes: " + 
                          usuario.getNombre_usuario() + " (Rol: " + usuario.getId_rol() + ")");
        return false;
    }
    
    return true;
}

// Nuevo método para mostrar error de permisos
private void mostrarErrorPermisos() {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error de Seguridad");
    alert.setHeaderText("Acceso No Autorizado");
    alert.setContentText("Ha ocurrido un error de seguridad. No tienes permisos para acceder a esta sección.");
    
    alert.showAndWait();
    
    // Cerrar la ventana inmediatamente
    Platform.runLater(() -> {
        if (backButton != null && backButton.getScene() != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        }
    });
}
    
    @Override
    public void start(Stage primaryStage) {
        try {
            initComponents(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ReportesInterfaces() {

    }
    // Método para mostrar la ventana desde otra clase
    public void show() {
        try {
            Stage stage = new Stage();
            initComponents(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
private void initComponents(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/ReportesInterfaces-view.fxml"));
    loader.setController(this);
    BorderPane root = loader.load();
    
    stage.setTitle("IDC - Reportes de Inventario");
    
    // Obtener dimensiones de la pantalla
    Screen screen = Screen.getPrimary();
    double screenWidth = screen.getBounds().getWidth();
    double screenHeight = screen.getBounds().getHeight();
    
    // Crear la escena
    Scene scene = new Scene(root, screenWidth, screenHeight);
    stage.setScene(scene);
    stage.setMaximized(true);
    
    // Configurar efectos hover 
    setupCardHoverEffects();
    
    stage.show();
}
    
    @FXML
    private void initialize() {
        
        loadReportsData();
    }
    
    private void loadReportsData() {
        monthlyContent.setText(getMonthlyReportDetails());
        lowStockContent.setText(getLowStockReportDetails());
        overStockContent.setText(getOverStockReportDetails());
    }
    
    private void setupCardHoverEffects() {
        setupCardHover(monthlyCard);
        setupCardHover(lowStockCard);
        setupCardHover(overStockCard);
    }
    
    private void setupCardHover(VBox card) {
        String originalStyle = card.getStyle();
        String hoverStyle = originalStyle.replace("#2a2a2a", "#333333");
        
        card.setOnMouseEntered(e -> {
            card.setStyle(hoverStyle);
            card.setScaleX(1.02);
            card.setScaleY(1.02);
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(originalStyle);
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });
    }
    
@FXML
    private void goBack() {
        try {
            // Cargar la ventana principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador y pasarle el usuario
            MainController mainController = loader.getController();
            if (usuarioActual != null) {
                mainController.inicializarConUsuario(usuarioActual);
            }
            
            // Crear nueva ventana para main-view
            Stage stage = new Stage();
            stage.setTitle("Sistema Principal");
            stage.setScene(scene);
            stage.setMaximized(true); // Para que se abra maximizada
            stage.show();
            
            // Cerrar la ventana actual (ReportesInterfaces-view)
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    private String getMonthlyReportDetails() {
        StringBuilder report = new StringBuilder();
        report.append(" REPORTE MENSUAL DE INVENTARIO \n\n");
        report.append("Fecha: ").append(LocalDate.now()).append("\n");
        report.append("Hora: ").append(LocalTime.now().toString().substring(0, 8)).append("\n\n");
        
        ProductoDAO productoDAO = new ProductoDAO();
        int totalProductos = productoDAO.obtenerTotalProductos();
        int totalCategorias = productoDAO.obtenerTotalCategorias();
        int productosStockBajo = productoDAO.obtenerProductosConStockBajo(50).size();
        BigDecimal valorTotal = productoDAO.obtenerValorTotalInventario();
        
        report.append("RESUMEN EJECUTIVO:\n");
        report.append("Durante el mes de ").append(LocalDate.now().getMonth().name().toLowerCase())
              .append(" ").append(LocalDate.now().getYear()).append(", la papelería mantiene un inventario\n");
        report.append("total de ").append(totalProductos).append(" productos distribuidos en ")
              .append(totalCategorias).append(" categorías diferentes,\n");
        report.append("con un valor total de inventario de ").append(valorTotal).append(".\n\n");
        
        return report.toString();
    }
    
    private String getLowStockReportDetails() {
        StringBuilder report = new StringBuilder();
        report.append(" REPORTE DE PRODUCTOS CON BAJO STOCK \n\n");
        report.append("Fecha: ").append(LocalDate.now()).append("\n\n");
        
        ProductoDAO productoDAO = new ProductoDAO();        
        List<Producto> productosStockBajo = productoDAO.obtenerProductosConStockBajo(50);
        
        if (productosStockBajo.isEmpty()) {
            report.append("¡EXCELENTE! No hay productos con stock bajo en este momento.\n\n");
        } else {
            report.append("PRODUCTOS CRÍTICOS (Menos de 50 unidades):\n");
            report.append("----------------------------------------\n");
            report.append("PRODUCTO                 CANTIDAD    ESTADO\n");
            report.append("----------------------------------------\n");
            
            for (Producto producto : productosStockBajo) {
                String nombreFormateado = String.format("%-25s", 
                    producto.getNombreProducto().length() > 25 ? 
                    producto.getNombreProducto().substring(0, 22) + "..." : 
                    producto.getNombreProducto());
                
                String estado = "";
                if (producto.getStockProducto() < 10) {
                    estado = "CRÍTICO";
                } else if (producto.getStockProducto() < 25) {
                    estado = "BAJO";
                } else {
                    estado = "ATENCIÓN";
                }
                
                report.append(String.format("%s %6d      %s\n", 
                    nombreFormateado, producto.getStockProducto(), estado));
            }
        }
        return report.toString();
    }
    
    private String getOverStockReportDetails() {
        StringBuilder report = new StringBuilder();
        report.append(" REPORTE DE PRODUCTOS CON SOBRE STOCK \n\n");
        report.append("Fecha: ").append(LocalDate.now()).append("\n\n");
        
        ProductoDAO productoDAO = new ProductoDAO();
        List<Producto> productosSobrestock = productoDAO.obtenerProductosConSobrestock(200);
        
        if (productosSobrestock.isEmpty()) {
            report.append("No se han identificado productos con exceso de inventario.\n\n");
        } else {
            report.append("PRODUCTOS CON EXCESO DE INVENTARIO (Más de 200 unidades):\n");
            report.append("----------------------------------------\n");
            report.append("PRODUCTO                 CANTIDAD    ROTACIÓN\n");
            report.append("----------------------------------------\n");
            
            for (Producto producto : productosSobrestock) {
                String nombreFormateado = String.format("%-25s", 
                    producto.getNombreProducto().length() > 25 ? 
                    producto.getNombreProducto().substring(0, 22) + "..." : 
                    producto.getNombreProducto());
                
                String rotacion = "";
                if (producto.getStockProducto() > 500) {
                    rotacion = "MUY BAJA";
                } else if (producto.getStockProducto() > 350) {
                    rotacion = "BAJA";
                } else {
                    rotacion = "MEDIA";
                }
                
                report.append(String.format("%s %6d      %s\n", 
                    nombreFormateado, producto.getStockProducto(), rotacion));
            }
        }
        return report.toString();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}