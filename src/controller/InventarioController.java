package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import dao.ProductoDAO;
import entidades.Producto;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
    
    @FXML private AnchorPane mainPane;
    @FXML private Button backButton;
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane inventoryGrid;
    @FXML private Button addProductButton;
    
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
            "-fx-background: #2a2a2a; " +
            "-fx-background-color: #2a2a2a; " +
            "-fx-border-color: #2a2a2a;"
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
        
        try {
            // Si hay imágenes almacenadas en la base de datos o en una carpeta específica
            // Aquí se puede cargar la imagen real del producto
            // Por ahora, se usara una imagen placeholder basada en el tipo de producto
            Image placeholderImage = getPlaceholderImage(producto.getNombreProducto());
            imageView.setImage(placeholderImage);
        } catch (Exception e) {
            // Si no se puede cargar la imagen, usar un placeholder genérico
            imageView.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10px;");
        }
        
        return imageView;
    }
    
    private Image getPlaceholderImage(String productName) {
        // Aquí se puede implementar lógica para devolver diferentes imágenes
        // basadas en el tipo de producto o usar imágenes almacenadas
        String imagePath = "/images/placeholder.png"; // Ruta por defecto
        
        try {
            // Intentar cargar imagen específica del producto si existe
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            // Crear un placeholder simple si no hay imagen
            return null;
        }
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
        System.out.println("Navegando hacia atrás...");
    }
    
    @FXML
    private void handleAddProduct() {
        // Implementar lógica para agregar nuevo producto
        System.out.println("Abriendo formulario para agregar producto...");
        // abrir una nueva ventana o cambiar de escena
        // para mostrar el formulario de agregar producto
    }
    
    private void handleProductDetails(Producto producto) {
        // Implementar lógica para mostrar detalles del producto
        System.out.println("Mostrando detalles del producto: " + producto.getNombreProducto());
        // abrir una ventana de detalles o edición del producto
    }
    
    // Método para refrescar la vista después de agregar/editar productos
    public void refreshProducts() {
        loadProducts();
    }
}