package controller;

import dao.CategoriaDAO;
import dao.ProductoDAO;
import dao.ProveedorDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import entidades.Producto;
import database.Conexion;
import entidades.Categoria;
import entidades.Proveedor;
import utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class DetallesProductoController implements Initializable {

    @FXML
    private Button btnVolver;
    
    @FXML
    private ImageView imageViewProducto;
    
    
    @FXML
    private Label lblNombre;
    
    @FXML
    private Label lblStock;
    
    @FXML
    private Label lblPrecio;
    
    @FXML
    private Label lblId;
    
    @FXML
    private Label lblMedidas;
    
    @FXML
    private Label lblColor;
    
    @FXML
    private Label lblCategoria;
    
    @FXML
    private Label lblDetalles;
    
    @FXML
    private Label lblProveedor;
    
    @FXML
    private Button btnEditar;
    
    @FXML
    private Button btnEliminar;
    
    // Variable para almacenar el producto seleccionado
    private Producto productoSeleccionado;
    
    // Variable para almacenar el proveedor actual
    private Proveedor proveedorActual;
    
    // DAOs
    private ProductoDAO productoDAO;
    private CategoriaDAO categoriaDAO;
    private ProveedorDAO proveedorDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar los DAOs
        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();
        proveedorDAO = new ProveedorDAO();
        
        // Configurar el label del proveedor para que sea clickeable
        configurarLabelProveedorClickeable();
    }
    
    /**
     * Configura el label del proveedor para que sea clickeable y muestre cursor de mano
     */
    private void configurarLabelProveedorClickeable() {
        lblProveedor.setStyle("-fx-text-fill: #4A90E2; -fx-cursor: hand;");
        lblProveedor.setOnMouseClicked(event -> mostrarInfoProveedor());
        
        // Efecto hover
        lblProveedor.setOnMouseEntered(event -> {
            lblProveedor.setStyle("-fx-text-fill: #357ABD; -fx-cursor: hand; -fx-underline: true;");
        });
        
        lblProveedor.setOnMouseExited(event -> {
            lblProveedor.setStyle("-fx-text-fill: #4A90E2; -fx-cursor: hand;");
        });
    }
    
    /**
     * Método para mostrar la ventana emergente con información del proveedor
     */
    private void mostrarInfoProveedor() {
        if (proveedorActual == null) {
            mostrarError("No hay información del proveedor disponible.");
            return;
        }
        
        try {
            // Cargar el FXML de la ventana de información del proveedor
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/InfoProveedorVEInterfaz.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador
            InfoProveedorVEController controller = loader.getController();
            
            // Crear nueva ventana
            Stage ventanaInfo = new Stage();
            ventanaInfo.initModality(Modality.APPLICATION_MODAL);
            ventanaInfo.setTitle("Información del Proveedor");
            ventanaInfo.setResizable(false);
            
            // Configurar la escena
            Scene scene = new Scene(root);
            ventanaInfo.setScene(scene);
            
            // Pasar los datos al controlador
            controller.setProveedor(proveedorActual);
            controller.setStage(ventanaInfo);
            
            // Mostrar la ventana
            ventanaInfo.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de información del proveedor: " + e.getMessage());
            mostrarError("Error al mostrar la información del proveedor.");
        }
    }
    
    /**
     * Método para establecer el producto que se mostrará en los detalles
     * Este método debe ser llamado desde el controller anterior
     */
    public void setProducto(Producto producto) {
        this.productoSeleccionado = producto;
        if (producto != null) {
            cargarDetallesProducto();
        }
    }
    
    /**
     * Método alternativo para cargar producto por ID
     * En caso de que solo se pase el ID desde la interfaz anterior
     */
    public void cargarProductoPorId(int idProducto) {
        try {
            productoSeleccionado = productoDAO.obtenerProductoPorId(idProducto);
            if (productoSeleccionado != null) {
                cargarDetallesProducto();
            } else {
                mostrarError("No se pudo cargar la información del producto.");
            }
        } catch (Exception e) {
            mostrarError("Error al cargar el producto: " + e.getMessage());
        }
    }
    
    
    private void cargarDetallesProducto() {
        if (productoSeleccionado == null) return;
        
        // Cargar información básica del producto
        lblNombre.setText(productoSeleccionado.getNombreProducto() != null ? 
            productoSeleccionado.getNombreProducto() : "Sin nombre");
            
        lblId.setText(String.valueOf(productoSeleccionado.getIdProducto()));
        
        lblStock.setText(productoSeleccionado.getStockProducto() + " piezas");
        
        // Formatear el precio
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        lblPrecio.setText(df.format(productoSeleccionado.getPrecioProducto()));
        
        // Cargar medidas (asumiendo que están en una columna como "70x95" o similar)
        String medidas = productoSeleccionado.getMedidasProducto();
        lblMedidas.setText(medidas != null && !medidas.isEmpty() ? medidas : "Sin especificar");
        
        // Color del producto
        lblColor.setText(productoSeleccionado.getColorProducto() != null ? 
            productoSeleccionado.getColorProducto() : "Sin especificar");
            
        // Descripción del producto
        String descripcion = productoSeleccionado.getDescripcionProducto();
        lblDetalles.setText(descripcion != null && !descripcion.isEmpty() ? 
            descripcion : "Sin descripción disponible");
        
        // Cargar categoría
        cargarCategoria();
        
        // Cargar proveedor
        cargarProveedor();
        
        // Cargar imagen
        cargarImagen();
    }
    
   
    private void cargarCategoria() {
        try {
            if (productoSeleccionado.getIdCategoria() > 0) {
                Categoria categoria = categoriaDAO.obtenerPorId(productoSeleccionado.getIdCategoria());
                if (categoria != null) {
                    lblCategoria.setText(categoria.getNombreCategoria());
                } else {
                    lblCategoria.setText("Categoría no encontrada");
                }
            } else {
                lblCategoria.setText("Sin categoría");
            }
        } catch (Exception e) {
            lblCategoria.setText("Error al cargar categoría");
            System.err.println("Error al cargar categoría: " + e.getMessage());
        }
    }
    
    
    private void cargarProveedor() {
        try {
            if (productoSeleccionado.getIdProveedor() > 0) {
                proveedorActual = proveedorDAO.obtenerPorId(productoSeleccionado.getIdProveedor());
                if (proveedorActual != null) {
                    lblProveedor.setText(proveedorActual.getNombreProveedor());
                } else {
                    lblProveedor.setText("Proveedor no encontrado");
                    proveedorActual = null;
                }
            } else {
                lblProveedor.setText("Sin proveedor");
                proveedorActual = null;
            }
        } catch (Exception e) {
            lblProveedor.setText("Error al cargar proveedor");
            proveedorActual = null;
            System.err.println("Error al cargar proveedor: " + e.getMessage());
        }
    }
    
    
    private void cargarImagen() {
        try {
            System.out.println("=== CARGAR IMAGEN PRODUCTO ===");
            System.out.println("ID Producto: " + productoSeleccionado.getIdProducto());
            System.out.println("Nombre imagen en BD: " + productoSeleccionado.getImagenProducto());
            
            // Verificar si el producto tiene imagen guardada
            String imagenProducto = productoSeleccionado.getImagenProducto();
            
            if (imagenProducto != null && !imagenProducto.trim().isEmpty()) {
                // Intentar cargar la imagen guardada
                String imageURL = ImageUtils.getImageURL(imagenProducto);
                System.out.println("URL imagen generada: " + imageURL);
                
                if (imageURL != null) {
                    Image productImage = new Image(imageURL);
                    
                    // Verificar que la imagen se cargó correctamente
                    if (!productImage.isError()) {
                        imageViewProducto.setImage(productImage);
                        imageViewProducto.setVisible(true);
                        
                        System.out.println("✓ Imagen cargada exitosamente para producto " + productoSeleccionado.getIdProducto());
                        return;
                    } else {
                        System.err.println("✗ Error al cargar imagen desde URL: " + imageURL);
                        System.err.println("Error de imagen: " + productImage.getException());
                    }
                } else {
                    System.err.println("✗ No se encontró el archivo de imagen: " + imagenProducto);
                    // Verificar si el archivo existe físicamente
                    String imagePath = ImageUtils.getImagePath(imagenProducto);
                    System.err.println("Ruta verificada: " + imagePath);
                    System.err.println("Archivo existe: " + ImageUtils.imageExists(imagenProducto));
                }
            } else {
                System.out.println("! No hay imagen guardada para este producto");
            }
            
            
        } catch (Exception e) {
            System.err.println("ERROR inesperado al cargar imagen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
   
    @FXML
    private void volverAtras() {
        try {
            // Cerrar la ventana actual
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            mostrarError("Error al cerrar la ventana: " + e.getMessage());
        }
    }
    
    @FXML
    private void editarProducto() {
        // Implementar lógica para editar producto
        // Por ahora, mostrar mensaje informativo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Funcionalidad pendiente");
        alert.setHeaderText("Editar Producto");
        alert.setContentText("Esta funcionalidad será implementada próximamente.");
        alert.showAndWait();
    }
    
    @FXML
    private void eliminarProducto() {
        // Confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar producto?");
        confirmacion.setContentText("¿Está seguro de que desea eliminar el producto '" + 
                                   productoSeleccionado.getNombreProducto() + "'? Esta acción no se puede deshacer.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = productoDAO.eliminarProducto(productoSeleccionado.getIdProducto());
                if (eliminado) {
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Producto eliminado");
                    exito.setHeaderText("Eliminación exitosa");
                    exito.setContentText("El producto ha sido eliminado correctamente.");
                    exito.showAndWait();
                    
                    // Cerrar la ventana
                    volverAtras();
                } else {
                    mostrarError("No se pudo eliminar el producto.");
                }
            } catch (SQLException e) {
                mostrarError("Error al eliminar el producto: " + e.getMessage());
            }
        }
    }
    
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    
    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }
}