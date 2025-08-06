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
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;

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
    @FXML private Button btnAgregarCategoria;
    @FXML private TextArea txtDetalles;
    @FXML private Button btnGuardar;

    private String rutaImagenSeleccionada = null;
    
    // DAOs
    private CategoriaDAO categoriaDAO;
    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;

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
            if (productoDAO == null) {
                productoDAO = new ProductoDAO();
            }
            
            Random random = new Random();
            String idGenerado;
            boolean idExiste = true;
            int intentos = 0;
            final int MAX_INTENTOS = 50;
            
            do {
                int idInt = 100000000 + random.nextInt(900000000);
                idGenerado = String.valueOf(idInt);
                
                try {
                    idExiste = productoDAO.existe(idInt);
                    System.out.println("DEBUG - Verificando ID: " + idInt + ", existe: " + idExiste);
                } catch (Exception e) {
                    System.err.println("Error al verificar ID: " + e.getMessage());
                    idExiste = true;
                }
                
                intentos++;
                
                if (intentos >= MAX_INTENTOS) {
                    System.err.println("ADVERTENCIA: Se alcanzó el máximo de intentos");
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String timestamp = now.format(formatter);
                    int randomSuffix = random.nextInt(999) + 1;
                    idGenerado = timestamp + String.format("%03d", randomSuffix);
                    
                    try {
                        Integer.parseInt(idGenerado);
                    } catch (NumberFormatException e) {
                        idGenerado = idGenerado.substring(idGenerado.length() - 9);
                    }
                    break;
                }
                
            } while (idExiste);
            
            txtId.setText(idGenerado);
            System.out.println("ID generado exitosamente: " + idGenerado + " (intentos: " + intentos + ")");
            
        } catch (Exception e) {
            System.err.println("Error al generar ID del producto: " + e.getMessage());
            e.printStackTrace();
            
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
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
        txtPrecio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                txtPrecio.setText(oldValue);
            }
            if (newValue.indexOf('.') != newValue.lastIndexOf('.')) {
                txtPrecio.setText(oldValue);
            }
        });
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Producto");
        
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Agregar Proveedor");
        dialog.setHeaderText("Complete la información del nuevo proveedor");
        
        // Crear el contenido del diálogo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del proveedor");
        
        TextField correoField = new TextField();
        correoField.setPromptText("correo@ejemplo.com");
        
        TextField telefonoField = new TextField();
        telefonoField.setPromptText("Teléfono");
        
        // Validar que solo se ingresen números en teléfono
        telefonoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                telefonoField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Correo:"), 0, 1);
        grid.add(correoField, 1, 1);
        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(telefonoField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Agregar botones
        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarButtonType, ButtonType.CANCEL);
        
        // Enfocar el campo nombre al abrir
        Platform.runLater(() -> nombreField.requestFocus());
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == agregarButtonType) {
            String nombre = nombreField.getText().trim();
            String correo = correoField.getText().trim();
            String telefono = telefonoField.getText().trim();
            
            // Validar campos obligatorios
            if (nombre.isEmpty()) {
                mostrarError("Error", "El nombre del proveedor es obligatorio");
                return;
            }
            
            // Validar formato de correo si no está vacío
            if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                mostrarError("Error", "El formato del correo electrónico no es válido");
                return;
            }
            
            try {
                // Verificar si ya existe el proveedor
                if (proveedorDAO.existeProveedor(nombre)) {
                    mostrarError("Error", "Ya existe un proveedor con ese nombre");
                    return;
                }
                
                // Insertar nuevo proveedor
                boolean insertado = proveedorDAO.insertarProveedorCompleto(nombre, correo, telefono);
                
                if (insertado) {
                    // Recargar la lista de proveedores
                    cargarProveedores();
                    
                    // Seleccionar el nuevo proveedor
                    cmbProveedor.setValue(nombre);
                    
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
    private void agregarCategoria() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Agregar Categoría");
        dialog.setHeaderText("Complete la información de la nueva categoría");
        
        // Crear el contenido del diálogo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre de la categoría");
        
        TextArea descripcionArea = new TextArea();
        descripcionArea.setPromptText("Descripción de la categoría (opcional)");
        descripcionArea.setPrefRowCount(3);
        descripcionArea.setWrapText(true);
        
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(descripcionArea, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Agregar botones
        ButtonType agregarButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(agregarButtonType, ButtonType.CANCEL);
        
        // Enfocar el campo nombre al abrir
        Platform.runLater(() -> nombreField.requestFocus());
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == agregarButtonType) {
            String nombre = nombreField.getText().trim();
            String descripcion = descripcionArea.getText().trim();
            
            // Validar campo obligatorio
            if (nombre.isEmpty()) {
                mostrarError("Error", "El nombre de la categoría es obligatorio");
                return;
            }
            
            try {
                // Verificar si ya existe la categoría
                if (categoriaDAO.existeCategoria(nombre)) {
                    mostrarError("Error", "Ya existe una categoría con ese nombre");
                    return;
                }
                
                // Insertar nueva categoría
                boolean insertado = categoriaDAO.insertarCategoria(nombre, descripcion);
                
                if (insertado) {
                    // Recargar la lista de categorías
                    cargarCategorias();
                    
                    // Seleccionar la nueva categoría
                    cmbSeccion.setValue(nombre);
                    
                    mostrarInfo("Éxito", "Categoría agregada correctamente");
                } else {
                    mostrarError("Error", "No se pudo agregar la categoría");
                }
                
            } catch (SQLException e) {
                mostrarError("Error", "No se pudo agregar la categoría: " + e.getMessage());
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
                System.out.println("ID: " + idProducto);
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
                    idProducto,
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