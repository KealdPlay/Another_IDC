package controller;


import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import dao.CategoriaDAO;
import dao.ProveedorDAO;
import dao.ProductoDAO;
import database.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgregarProductoController implements Initializable {

    @FXML private Button btnVolver;
    @FXML private ImageView imageViewProducto;
    @FXML private VBox placeholderImagen;
    @FXML private Button btnSeleccionarImagen;
    @FXML private TextField txtNombre;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtId;
    @FXML private TextField txtMedidas;
    @FXML private TextField txtColor;
    @FXML private ComboBox<String> cmbSeccion;
    @FXML private ComboBox<String> cmbProveedor;
    @FXML private Button btnAgregarProveedor;
    @FXML private TextArea txtDetalles;
    @FXML private Button btnGuardar;

    private String rutaImagenSeleccionada = null;
    
    // DAOs
    private CategoriaDAO categoriaDAO;
    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;

// MEJORA en AgregarProductoController.java - método initialize con mejor manejo de errores
@Override
public void initialize(URL url, ResourceBundle resourceBundle) {
    // Generar ID automático
    generarIdProducto();
    
    // Configurar validaciones (esto siempre debe ejecutarse)
    configurarValidaciones();
    
    try {
        // Verificar conexión antes de inicializar DAOs
        if (!verificarConexion()) {
            mostrarError("Error de Conexión", 
                "No se pudo establecer conexión con la base de datos. " +
                "Los ComboBox estarán vacíos.");
            return;
        }
        
        // Inicializar DAOs
        inicializarDAOs();
        
        // Cargar datos en ComboBox
        cargarCategorias();
        cargarProveedores();
        
    } catch (Exception e) {
        System.err.println("Error al inicializar AgregarProductoController: " + e.getMessage());
        e.printStackTrace();
        mostrarError("Error de Inicialización", 
            "Error al cargar los datos: " + e.getMessage());
    }
}

// Método para verificar la conexión
private boolean verificarConexion() {
    try {
        Conexion conexionInstance = Conexion.getInstance();
        Connection conn = conexionInstance.getConnection();
        return conn != null && !conn.isClosed();
    } catch (Exception e) {
        System.err.println("Error al verificar conexión: " + e.getMessage());
        return false;
    }
}

    private void inicializarDAOs() {
        categoriaDAO = new CategoriaDAO();
        proveedorDAO = new ProveedorDAO();
        productoDAO = new ProductoDAO();
    }

    private void generarIdProducto() {
        // Generar ID basado en fecha y hora actual
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String id = now.format(formatter);
        txtId.setText(id);
    }

    private void cargarCategorias() {
        try {
            if (categoriaDAO != null) {
                List<String> categorias = categoriaDAO.obtenerNombresCategorias();
                ObservableList<String> observableList = FXCollections.observableArrayList(categorias);
                cmbSeccion.setItems(observableList);
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        try {
            if (proveedorDAO != null) {
                List<String> proveedores = proveedorDAO.obtenerNombresProveedores();
                ObservableList<String> observableList = FXCollections.observableArrayList(proveedores);
                cmbProveedor.setItems(observableList);
            }
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar los proveedores: " + e.getMessage());
        }
    }

    private void configurarValidaciones() {
        // Validar que solo se ingresen números en Stock
        txtStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtStock.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Validar que solo se ingresen números y punto decimal en Precio
        txtPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtPrecio.setText(oldValue);
            }
        });
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Producto");
        
        // Filtros de extensión
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
            "Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"
        );
        fileChooser.getExtensionFilters().add(imageFilter);
        
        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imageViewProducto.setImage(image);
                imageViewProducto.setVisible(true);
                placeholderImagen.setVisible(false);
                rutaImagenSeleccionada = selectedFile.getAbsolutePath();
            } catch (Exception e) {
                mostrarError("Error", "No se pudo cargar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    @FXML
    private void agregarProveedor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Proveedor");
        dialog.setHeaderText("Nuevo Proveedor");
        dialog.setContentText("Nombre del proveedor:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String nombreProveedor = result.get().trim();
            
            try {
                // Verificar si ya existe el proveedor
                if (proveedorDAO.existeProveedor(nombreProveedor)) {
                    mostrarError("Error", "El proveedor ya existe");
                    return;
                }
                
                // Insertar nuevo proveedor
                boolean insertado = proveedorDAO.insertarProveedor(nombreProveedor);
                
                if (insertado) {
                    // Recargar la lista de proveedores
                    cargarProveedores();
                    
                    // Seleccionar el nuevo proveedor
                    cmbProveedor.setValue(nombreProveedor);
                    
                    mostrarInfo("Éxito", "Proveedor agregado correctamente");
                } else {
                    mostrarError("Error", "No se pudo agregar el proveedor");
                }
                
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo agregar el proveedor: " + e.getMessage());
            }
        }
    }

    @FXML
    private void guardarProducto() {
        if (validarCampos()) {
            try {
                // Verificar si el producto ya existe
                if (productoDAO.existe(Integer.parseInt(txtId.getText()))) {
                    mostrarError("Error", "Ya existe un producto con este ID");
                    generarIdProducto();
                    return;
                }
                
                // Obtener IDs de categoria y proveedor
                int idCategoria = categoriaDAO.obtenerIdCategoria(cmbSeccion.getValue());
                int idProveedor = proveedorDAO.obtenerIdProveedor(cmbProveedor.getValue());
                
                // Insertar producto
                boolean guardado = productoDAO.insertarProducto(
                    txtNombre.getText(),
                    txtDetalles.getText(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText()),
                    txtColor.getText(),
                    txtMedidas.getText(),
                    idCategoria,
                    idProveedor  // ← Quita esta coma
                );
                
                if (guardado) {
                    mostrarInfo("Éxito", "Producto guardado correctamente");
                    limpiarFormulario();
                } else {
                    mostrarError("Error", "No se pudo guardar el producto");
                }
                
            } catch (SQLException e) {
                mostrarError("Error de Base de Datos", "Error al guardar el producto: " + e.getMessage());
            } catch (NumberFormatException e) {
                mostrarError("Error de Formato", "Verifique que los campos numéricos tengan el formato correcto");
            }
        }
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre del producto es obligatorio\n");
        }

        if (txtStock.getText().trim().isEmpty()) {
            errores.append("- El stock es obligatorio\n");
        } else {
            try {
                int stock = Integer.parseInt(txtStock.getText().trim());
                if (stock < 0) {
                    errores.append("- El stock no puede ser negativo\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El stock debe ser un número válido\n");
            }
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            errores.append("- El precio es obligatorio\n");
        } else {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                if (precio <= 0) {
                    errores.append("- El precio debe ser mayor a 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El precio debe ser un número válido\n");
            }
        }

        if (cmbSeccion.getValue() == null) {
            errores.append("- Debe seleccionar una sección\n");
        }

        if (cmbProveedor.getValue() == null) {
            errores.append("- Debe seleccionar un proveedor\n");
        }

        if (errores.length() > 0) {
            mostrarError("Campos Obligatorios", "Complete los siguientes campos:\n" + errores.toString());
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtStock.clear();
        txtPrecio.clear();
        txtMedidas.clear();
        txtColor.clear();
        txtDetalles.clear();
        cmbSeccion.setValue(null);
        cmbProveedor.setValue(null);
        
        imageViewProducto.setImage(null);
        imageViewProducto.setVisible(false);
        placeholderImagen.setVisible(true);
        rutaImagenSeleccionada = null;
        
        generarIdProducto();
    }

    @FXML
    private void volverAtras() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para cerrar la conexión cuando se cierre la ventana
    public void cerrarConexion() {
        database.Conexion.getInstance().closeConnection();
    }
}