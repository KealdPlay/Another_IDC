package controller;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import dao.CategoriaDAO;
import dao.ProveedorDAO;
import java.util.Random;
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
        System.out.println("DEBUG - Iniciando initialize...");
        
        // Configurar validaciones (esto siempre debe ejecutarse)
        configurarValidaciones();
        System.out.println("DEBUG - Validaciones configuradas");
        
        try {
            // Verificar conexión antes de inicializar DAOs
            System.out.println("DEBUG - Verificando conexión...");
            if (!verificarConexion()) {
                System.err.println("ERROR - Conexión fallida");
                mostrarError("Error de Conexión", 
                    "No se pudo establecer conexión con la base de datos. " +
                    "Los ComboBox estarán vacíos.");
                return;
            }
            System.out.println("DEBUG - Conexión exitosa");
            
            // Inicializar DAOs
            System.out.println("DEBUG - Inicializando DAOs...");
            inicializarDAOs();
            System.out.println("DEBUG - DAOs inicializados");
            
            // Generar ID automático
            generarIdProducto();
            System.out.println("DEBUG - ID generado: " + txtId.getText());
            
            // Cargar datos en ComboBox
            System.out.println("DEBUG - Cargando datos en ComboBox...");
            cargarCategorias();
            cargarProveedores();
            System.out.println("DEBUG - Initialize completado");
            
        } catch (Exception e) {
            System.err.println("ERROR en initialize: " + e.getMessage());
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
    try {
        // Verificar que el ProductoDAO esté inicializado
        if (productoDAO == null) {
            productoDAO = new ProductoDAO();
        }
        
        Random random = new Random();
        String idGenerado;
        boolean idExiste = true;
        int intentos = 0;
        final int MAX_INTENTOS = 100; // Límite de seguridad para evitar bucles infinitos
        
        do {
            // Generar ID aleatorio de 12 dígitos
            StringBuilder sb = new StringBuilder();
            
            // Primer dígito no puede ser 0 para asegurar que tenga 12 dígitos
            sb.append(random.nextInt(9) + 1);
            
            // Los siguientes 11 dígitos pueden ser cualquier número del 0-9
            for (int i = 1; i < 12; i++) {
                sb.append(random.nextInt(10));
            }
            
            idGenerado = sb.toString();
            
            // Verificar si el ID ya existe en la base de datos
            try {
                idExiste = productoDAO.existe(Integer.parseInt(idGenerado));
            } catch (NumberFormatException e) {
                // En caso de error con el número, generar otro
                idExiste = true;
            }
            
            intentos++;
            
            // Seguridad: si se han hecho muchos intentos, mostrar error
            if (intentos >= MAX_INTENTOS) {
                mostrarError("Error", "No se pudo generar un ID único después de " + MAX_INTENTOS + " intentos");
                // Como fallback, usar timestamp
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                idGenerado = now.format(formatter);
                break;
            }
            
        } while (idExiste);
        
        // Establecer el ID generado en el campo de texto
        txtId.setText(idGenerado);
        
        System.out.println("ID generado: " + idGenerado + " (intentos: " + intentos + ")");
        
    } catch (Exception e) {
        System.err.println("Error al generar ID del producto: " + e.getMessage());
        e.printStackTrace();
        
        // Como fallback, usar el método anterior basado en timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String fallbackId = now.format(formatter);
        txtId.setText(fallbackId);
        
        mostrarError("Advertencia", "Se usó un ID basado en fecha/hora debido a un error en la generación aleatoria");
    }
}

    private void cargarCategorias() {
        try {
            System.out.println("DEBUG - Iniciando carga de categorías...");
            
            if (categoriaDAO == null) {
                System.err.println("ERROR - categoriaDAO es null");
                mostrarError("Error", "No se pudo inicializar el acceso a categorías");
                return;
            }
            
            List<String> categorias = categoriaDAO.obtenerNombresCategorias();
            System.out.println("DEBUG - Categorías obtenidas: " + categorias.size());
            
            if (categorias.isEmpty()) {
                System.out.println("WARNING - No se encontraron categorías en la base de datos");
                mostrarError("Aviso", "No se encontraron categorías en la base de datos. Agregue categorías primero.");
            } else {
                for (String categoria : categorias) {
                    System.out.println("DEBUG - Categoría: " + categoria);
                }
                
                ObservableList<String> observableList = FXCollections.observableArrayList(categorias);
                cmbSeccion.setItems(observableList);
                System.out.println("DEBUG - Categorías cargadas en ComboBox exitosamente");
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR SQL al cargar categorías: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "No se pudieron cargar las categorías: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR inesperado al cargar categorías: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "Error inesperado al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        try {
            System.out.println("DEBUG - Iniciando carga de proveedores...");
            
            if (proveedorDAO == null) {
                System.err.println("ERROR - proveedorDAO es null");
                mostrarError("Error", "No se pudo inicializar el acceso a proveedores");
                return;
            }
            
            List<String> proveedores = proveedorDAO.obtenerNombresProveedores();
            System.out.println("DEBUG - Proveedores obtenidos: " + proveedores.size());
            
            if (proveedores.isEmpty()) {
                System.out.println("WARNING - No se encontraron proveedores en la base de datos");
                mostrarError("Aviso", "No se encontraron proveedores en la base de datos. Puede agregar uno nuevo.");
            } else {
                for (String proveedor : proveedores) {
                    System.out.println("DEBUG - Proveedor: " + proveedor);
                }
                
                ObservableList<String> observableList = FXCollections.observableArrayList(proveedores);
                cmbProveedor.setItems(observableList);
                System.out.println("DEBUG - Proveedores cargados en ComboBox exitosamente");
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR SQL al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "No se pudieron cargar los proveedores: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR inesperado al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "Error inesperado al cargar proveedores: " + e.getMessage());
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
        // CORRECCIÓN: Permitir números enteros y decimales
        txtPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            // Permitir números enteros y decimales (ej: 69, 69.5, 69.50)
            if (!newValue.matches("\\d*\\.?\\d*")) {
                // Si no coincide, mantener el valor anterior
                txtPrecio.setText(oldValue);
            }
            // Evitar múltiples puntos decimales
            if (newValue.indexOf('.') != newValue.lastIndexOf('.')) {
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
        System.out.println("=== INICIANDO GUARDAR PRODUCTO ===");
        
        if (validarCampos()) {
            System.out.println("Validación exitosa, procediendo a guardar...");
            try {
                // Verificar si el producto ya existe
                int idProducto = Integer.parseInt(txtId.getText());
                System.out.println("ID del producto: " + idProducto);
                
                if (productoDAO.existe(idProducto)) {
                    mostrarError("Error", "Ya existe un producto con este ID");
                    generarIdProducto();
                    return;
                }
                
                // Obtener IDs de categoria y proveedor
                String nombreCategoria = cmbSeccion.getValue();
                String nombreProveedor = cmbProveedor.getValue();
                
                System.out.println("Obteniendo ID de categoría para: " + nombreCategoria);
                int idCategoria = categoriaDAO.obtenerIdCategoria(nombreCategoria);
                System.out.println("ID Categoría obtenido: " + idCategoria);
                
                System.out.println("Obteniendo ID de proveedor para: " + nombreProveedor);
                int idProveedor = proveedorDAO.obtenerIdProveedor(nombreProveedor);
                System.out.println("ID Proveedor obtenido: " + idProveedor);
                
                // Preparar valores
                String nombre = txtNombre.getText().trim();
                String detalles = txtDetalles.getText().trim();
                String precioStr = txtPrecio.getText().trim();
                String stockStr = txtStock.getText().trim();
                String color = txtColor.getText().trim();
                String medidas = txtMedidas.getText().trim();
                
                System.out.println("=== VALORES FINALES ===");
                System.out.println("Nombre: " + nombre);
                System.out.println("Detalles: " + detalles);
                System.out.println("Precio: " + precioStr);
                System.out.println("Stock: " + stockStr);
                System.out.println("Color: " + color);
                System.out.println("Medidas: " + medidas);
                System.out.println("ID Categoria: " + idCategoria);
                System.out.println("ID Proveedor: " + idProveedor);
                
                // Insertar producto
                boolean guardado = productoDAO.insertarProducto(
                    nombre,
                    detalles,
                    Double.parseDouble(precioStr),
                    Integer.parseInt(stockStr),
                    color,
                    medidas,
                    idCategoria,
                    idProveedor
                );
                
                if (guardado) {
                    System.out.println("=== PRODUCTO GUARDADO EXITOSAMENTE ===");
                    mostrarInfo("Éxito", "Producto guardado correctamente");
                    limpiarFormulario();
                } else {
                    System.err.println("ERROR: No se pudo guardar el producto en la base de datos");
                    mostrarError("Error", "No se pudo guardar el producto");
                }
                
            } catch (SQLException e) {
                System.err.println("ERROR SQL: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error de Base de Datos", "Error al guardar el producto: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("ERROR de formato: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error de Formato", 
                    "Verifique el formato de los campos numéricos:\n" +
                    "- Stock: debe ser un número entero (ej: 10)\n" +
                    "- Precio: debe ser un número (ej: 69 o 69.50)");
            } catch (Exception e) {
                System.err.println("ERROR inesperado: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error", "Error inesperado: " + e.getMessage());
            }
        } else {
            System.out.println("Validación falló, no se procede a guardar");
        }
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        
        System.out.println("=== DEBUG VALIDACION ===");

        // Validar nombre
        String nombre = txtNombre.getText().trim();
        System.out.println("Nombre: '" + nombre + "' (length: " + nombre.length() + ")");
        if (nombre.isEmpty()) {
            errores.append("- El nombre del producto es obligatorio\n");
        }

        // Validar stock
        String stockTexto = txtStock.getText().trim();
        System.out.println("Stock texto: '" + stockTexto + "' (length: " + stockTexto.length() + ")");
        if (stockTexto.isEmpty()) {
            errores.append("- El stock es obligatorio\n");
        } else {
            try {
                int stock = Integer.parseInt(stockTexto);
                System.out.println("Stock parseado: " + stock);
                if (stock < 0) {
                    errores.append("- El stock no puede ser negativo\n");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parseando stock: " + e.getMessage());
                errores.append("- El stock debe ser un número válido\n");
            }
        }

        // Validar precio
        String precioTexto = txtPrecio.getText().trim();
        System.out.println("Precio texto: '" + precioTexto + "' (length: " + precioTexto.length() + ")");
        if (precioTexto.isEmpty()) {
            errores.append("- El precio es obligatorio\n");
        } else {
            try {
                double precio = Double.parseDouble(precioTexto);
                System.out.println("Precio parseado: " + precio);
                if (precio <= 0) {
                    errores.append("- El precio debe ser mayor a 0\n");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parseando precio: " + e.getMessage());
                errores.append("- El precio debe ser un número válido (ejemplo: 69 o 69.50)\n");
            }
        }

        // Validar ComboBox
        String seccionSeleccionada = cmbSeccion.getValue();
        String proveedorSeleccionado = cmbProveedor.getValue();
        
        System.out.println("Sección seleccionada: '" + seccionSeleccionada + "'");
        System.out.println("Proveedor seleccionado: '" + proveedorSeleccionado + "'");

        if (seccionSeleccionada == null) {
            errores.append("- Debe seleccionar una sección\n");
        }

        if (proveedorSeleccionado == null) {
            errores.append("- Debe seleccionar un proveedor\n");
        }

        System.out.println("Errores encontrados: " + errores.length());
        if (errores.length() > 0) {
            System.out.println("Lista de errores:\n" + errores.toString());
            mostrarError("Campos Obligatorios", "Complete los siguientes campos:\n" + errores.toString());
            return false;
        }

        System.out.println("=== VALIDACION EXITOSA ===");
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