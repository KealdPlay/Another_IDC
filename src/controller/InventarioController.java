package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import dao.ProductoDAO;
import entidades.Producto;
import entidades.Usuarios;
import utils.ImageUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
    
    // Referencias FXML actualizadas
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane inventoryGrid;
    @FXML private Button backButton;
    @FXML private Button addProductButton;
    
    private Usuarios usuarioActual;
    private ProductoDAO productoDAO;
    private static final int COLUMNS = 4; // Número de columnas en el grid
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productoDAO = new ProductoDAO();
        setupScrollPane();
        loadProducts();
    }
    
    private void setupScrollPane() {
        // Configurar el ScrollPane 
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Estilo para las barras de scroll
        scrollPane.setStyle(
            "-fx-background: #1a1a1a; " +
            "-fx-background-color: #1a1a1a; " +
            "-fx-border-color: #1a1a1a;"
        );
    }
    
    private void loadProducts() {
        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            displayProducts(productos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void displayProducts(List<Producto> productos) {
        inventoryGrid.getChildren().clear();
        
        int row = 0;
        int col = 0;
        
        for (Producto producto : productos) {
            VBox productCard = createProductCard(producto);
            inventoryGrid.add(productCard, col, row);
            
            col++;
            if (col >= COLUMNS) {
                col = 0;
                row++;
            }
        }
        
        // Agregar la tarjeta "Agregar Nuevo Producto" al final
        VBox addCard = createAddProductCard();
        inventoryGrid.add(addCard, col, row);
    }
    
    private VBox createProductCard(Producto producto) {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(320);
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        
        // Estilo de la tarjeta
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Imagen del producto
        ImageView productImage = createProductImage(producto);
        
        // Nombre del producto
        Label nameLabel = new Label(producto.getNombreProducto());
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #333333; " +
            "-fx-text-alignment: center;"
        );
        nameLabel.setMaxWidth(230);
        
        // Cantidad en stock
        Label stockLabel = new Label(producto.getStockProducto() + " piezas");
        stockLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #666666;"
        );
        
        // Precio
        Label priceLabel = new Label(currencyFormat.format(producto.getPrecioProducto()));
        priceLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2E7D32;"
        );
        
        // Botón "Ver más detalles"
        Button detailsButton = new Button("Ver más detalles");
        detailsButton.setPrefWidth(200);
        detailsButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #666666; " +
            "-fx-font-size: 12px; " +
            "-fx-underline: true; " +
            "-fx-border-color: transparent; " +
            "-fx-padding: 5px;"
        );
        
        // Agregar efecto hover al botón
        detailsButton.setOnMouseEntered(e -> 
            detailsButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #333333; " +
                "-fx-font-size: 12px; " +
                "-fx-underline: true; " +
                "-fx-border-color: transparent; " +
                "-fx-padding: 5px;"
            )
        );
        
        detailsButton.setOnMouseExited(e -> 
            detailsButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #666666; " +
                "-fx-font-size: 12px; " +
                "-fx-underline: true; " +
                "-fx-border-color: transparent; " +
                "-fx-padding: 5px;"
            )
        );
        
        // Acción del botón de detalles
        detailsButton.setOnAction(e -> handleProductDetails(producto));
        
        // Agregar hover effect a la tarjeta completa
        card.setOnMouseEntered(e -> 
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3); " +
                "-fx-scale-x: 1.02; " +
                "-fx-scale-y: 1.02;"
            )
        );
        
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
                "-fx-scale-x: 1.0; " +
                "-fx-scale-y: 1.0;"
            )
        );
        
        // Espaciador para empujar el botón hacia abajo
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        card.getChildren().addAll(productImage, nameLabel, stockLabel, priceLabel, spacer, detailsButton);
        
        return card;
    }
    
    private ImageView createProductImage(Producto producto) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        
        // Estilo del contenedor de la imagen
        imageView.setStyle(
            "-fx-background-color: #f5f5f5; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 10px; " +
            "-fx-border-width: 1px;"
        );
        
        try {
            // Verificar si el producto tiene imagen guardada
            String imagenProducto = producto.getImagenProducto();
            
            if (imagenProducto != null && !imagenProducto.trim().isEmpty()) {
                // Intentar cargar la imagen guardada
                String imageURL = ImageUtils.getImageURL(imagenProducto);
                
                if (imageURL != null) {
                    Image productImage = new Image(imageURL);
                    
                    // Verificar que la imagen se cargó correctamente
                    if (!productImage.isError()) {
                        imageView.setImage(productImage);
                        System.out.println("Imagen cargada para producto " + producto.getIdProducto() + ": " + imagenProducto);
                        return imageView;
                    } else {
                        System.err.println("Error al cargar imagen: " + imagenProducto);
                    }
                } else {
                    System.err.println("No se encontró el archivo de imagen: " + imagenProducto);
                }
            }
            
            // Si no hay imagen o no se pudo cargar, usar placeholder
            Image placeholderImage = getPlaceholderImage(producto.getNombreProducto());
            if (placeholderImage != null && !placeholderImage.isError()) {
                imageView.setImage(placeholderImage);
            } else {
                // Si no hay placeholder, crear un fondo gris con texto
                setPlaceholderStyle(imageView);
            }
            
        } catch (Exception e) {
            System.err.println("Error al cargar imagen para producto " + producto.getIdProducto() + ": " + e.getMessage());
            e.printStackTrace();
            setPlaceholderStyle(imageView);
        }
        
        return imageView;
    }
    
    /**
     * Establece el estilo placeholder para imágenes que no se pueden cargar
     */
    private void setPlaceholderStyle(ImageView imageView) {
        imageView.setStyle(
            "-fx-background-color: #f0f0f0; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 10px; " +
            "-fx-border-width: 1px;"
        );
    }
    
    /**
     * Obtiene una imagen placeholder desde recursos
     */
    private Image getPlaceholderImage(String productName) {
        try {
            // Intentar cargar imagen placeholder desde recursos
            String placeholderPath = "/images/placeholder-product.png";
            URL placeholderURL = getClass().getResource(placeholderPath);
            
            if (placeholderURL != null) {
                return new Image(placeholderURL.toExternalForm());
            }
            
            // Si no existe el placeholder específico, intentar uno genérico
            placeholderPath = "/images/placeholder.png";
            placeholderURL = getClass().getResource(placeholderPath);
            
            if (placeholderURL != null) {
                return new Image(placeholderURL.toExternalForm());
            }
            
        } catch (Exception e) {
            System.err.println("Error al cargar imagen placeholder: " + e.getMessage());
        }
        
        return null;
    }
    
    private VBox createAddProductCard() {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(320);
        card.setAlignment(Pos.CENTER);
        card.setSpacing(15);
        
        // Estilo de la tarjeta
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Icono de más (cruz)
        Label plusIcon = new Label("+");
        plusIcon.setStyle(
            "-fx-font-size: 48px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        
        // Texto
        Label addLabel = new Label("Agregar Nuevo\nProducto");
        addLabel.setAlignment(Pos.CENTER);
        addLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold; " +
            "-fx-text-alignment: center;"
        );
        
        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle(
                "-fx-background-color: #f8f8f8; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3); " +
                "-fx-scale-x: 1.02; " +
                "-fx-scale-y: 1.02;"
            )
        );
        
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
                "-fx-scale-x: 1.0; " +
                "-fx-scale-y: 1.0;"
            )
        );
        
        // Acción al hacer click
        card.setOnMouseClicked(e -> handleAddProduct());
        
        card.getChildren().addAll(plusIcon, addLabel);
        
        return card;
    }
    
    @FXML
    private void handleBackButton() {
        try {
            // Cargar la ventana principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador principal
            MainController mainController = loader.getController();
            if (usuarioActual != null) {
                mainController.inicializarConUsuario(usuarioActual);
            }
            
            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("IDC - Sistema Principal");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            // Cerrar la ventana actual
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("Error al regresar a la ventana principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
        
    @FXML
    private void handleAddProduct() {
        try {
            // Cargar la interfaz de agregar producto
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/AgregarProductoInterfaz.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador de agregar producto
            AgregarProductoController agregarController = loader.getController();
            
            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Agregar Nuevo Producto");
            stage.setScene(scene);
            stage.setResizable(false); // Opcional: hacer la ventana no redimensionable
            
            // Hacer la ventana modal (opcional)
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            
            // Centrar la ventana en la pantalla
            stage.centerOnScreen();
            
            // Mostrar la ventana y esperar a que se cierre
            stage.showAndWait();
            
            // Después de cerrar la ventana de agregar producto, refrescar el inventario
            refreshProducts();
            
        } catch (IOException e) {
            System.err.println("Error al abrir la ventana de agregar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }
        
    private void handleProductDetails(Producto producto) {
        try {
            // Cargar la interfaz de detalles del producto
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/DetallesProductoInterfaz.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador de detalles del producto
            DetallesProductoController detallesController = loader.getController();
            
            // Pasar el producto seleccionado al controlador de detalles
            detallesController.setProducto(producto);
            
            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Detalles del Producto - " + producto.getNombreProducto());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(false);
            
            // Hacer la ventana modal (opcional)
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Centrar la ventana en la pantalla
            stage.centerOnScreen();
            
            // Mostrar la ventana y esperar a que se cierre
            stage.showAndWait();
            
            // Después de cerrar la ventana de detalles, refrescar el inventario por si hubo cambios
            refreshProducts();
            
        } catch (IOException e) {
            System.err.println("Error al abrir la ventana de detalles del producto: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar un mensaje de error al usuario
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la ventana de detalles");
            alert.setContentText("Ha ocurrido un error al intentar mostrar los detalles del producto: " + e.getMessage());
            alert.showAndWait();
        }
    }
        
    // Método para refrescar la vista después de agregar/editar productos
    public void refreshProducts() {
        loadProducts();
    }

    // Método para establecer el usuario actual
    public void setUsuarioActual(Usuarios usuario) {
        this.usuarioActual = usuario;
    }
}