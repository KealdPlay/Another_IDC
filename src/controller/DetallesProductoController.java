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

public class DetallesProductoController implements Initializable {

    @FXML
    private Button btnVolver;
    
    @FXML
    private ImageView imageViewProducto;
    
    @FXML
    private VBox placeholderImagen;
    
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
                Proveedor proveedor = proveedorDAO.obtenerPorId(productoSeleccionado.getIdProveedor());
                if (proveedor != null) {
                    lblProveedor.setText(proveedor.getNombreProveedor());
                } else {
                    lblProveedor.setText("Proveedor no encontrado");
                }
            } else {
                lblProveedor.setText("Sin proveedor");
            }
        } catch (Exception e) {
            lblProveedor.setText("Error al cargar proveedor");
            System.err.println("Error al cargar proveedor: " + e.getMessage());
        }
    }
    
    
    private void cargarImagen() {
        try {
            // Aquí podrías implementar la lógica para cargar imágenes desde archivos
            // Por ahora, mostrar el placeholder
            if (imageViewProducto != null && placeholderImagen != null) {
                imageViewProducto.setVisible(false);
                placeholderImagen.setVisible(true);
            }
            
            // Si tienes imágenes almacenadas, puedes usar algo como:
            // String rutaImagen = "/imagenes/productos/" + productoSeleccionado.getIdProducto() + ".jpg";
            // Image imagen = new Image(getClass().getResourceAsStream(rutaImagen));
            // if (imagen != null && !imagen.isError()) {
            //     imageViewProducto.setImage(imagen);
            //     imageViewProducto.setVisible(true);
            //     placeholderImagen.setVisible(false);
            // }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
            // Mostrar placeholder en caso de error
            if (imageViewProducto != null && placeholderImagen != null) {
                imageViewProducto.setVisible(false);
                placeholderImagen.setVisible(true);
            }
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