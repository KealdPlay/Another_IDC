package dao;

import database.Conexion;
import entidades.Producto;
import entidades.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ProductoDAO {
    private Conexion conexion;
    
    public ProductoDAO(){
        this.conexion = Conexion.getInstance();
    }
    
    //TODOS LOS PRODUCTOS
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        
        try (Statement stmt = conexion.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los productos: " + e.getMessage());
        }
        return productos;
    }
    
    //OBTENER TOTAL DE PRODUCTOS
    public int obtenerTotalProductos(){
        String sql = "SELECT COUNT(*) FROM productos";
        
        try (Statement stmt = conexion.getConnection().createStatement();
            ResultSet rs =stmt.executeQuery(sql)){
            
            if (rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e){
            System.err.println("Error al obtener la cantidad de productos: "+e.getMessage());
        } return 0;
    } 
    
    public BigDecimal obtenerValorTotalInventario() {
        String sql = "SELECT SUM(precio_producto * stock_producto) as valor_total FROM productos";
        
        try (Statement stmt = conexion.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal valor = rs.getBigDecimal("valor_total");
                return valor != null ? valor : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener valor total del inventario: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    
    // PRODUCTOS POR CATEGORIA
    public List<Producto> obtenerPorCategoria(int idCategoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE id_categoria = ?";
        
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
        }
        return productos;
    }
    
    //OBTENER TOTAL DE CATEGORIAS
    public int obtenerTotalCategorias(){
        String sql = "SELECT COUNT(DISTINCT id_categoria) FROM productos";
        
        try (Statement stmt = conexion.getConnection().createStatement();
            ResultSet rs =stmt.executeQuery(sql)){
            
            if (rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e){
            System.err.println("Error al obtener el total de categorias: "+e.getMessage());
        } return 0;
    }    
    
    // BUSCAR PRODUCTOS POR NOMBRE
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre_producto LIKE ?";
        
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
        }
        return productos;
    }
    
    // PRODUCTOS CON BAJO STOCK
    public List<Producto> obtenerProductosConStockBajo(int stockMinimo) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock_producto < ?";
        
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, stockMinimo);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
        }
        return productos;
    }
    
    //PRODUCTOS CON SOBRESTOCK
    public List<Producto> obtenerProductosConSobrestock(int stockMaximo) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock_producto > ?";
        
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, stockMaximo);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con sobrestock: " + e.getMessage());
        }
        return productos;
    }
    
    // ============ MÉTODOS ESPECÍFICOS PARA EL CENTRO DE NOTIFICACIONES ============
    
    /**
     * Calcula el porcentaje general del stock basado en un stock ideal promedio
     */
    public double calcularPorcentajeStockGeneral() {
        // Para calcular un porcentaje general, usaremos la fórmula:
        // (stock_actual / stock_ideal) * 100
        // Donde stock_ideal = promedio de stock de todos los productos * 1.5
        
        String sql = "SELECT " +
                    "SUM(stock_producto) as total_actual, " +
                    "COUNT(*) as total_productos, " +
                    "AVG(stock_producto) as promedio_stock " +
                    "FROM productos";
        
        try (Statement stmt = conexion.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int totalActual = rs.getInt("total_actual");
                int totalProductos = rs.getInt("total_productos");
                double promedioStock = rs.getDouble("promedio_stock");
                
                if (totalProductos > 0 && promedioStock > 0) {
                    // Stock ideal = promedio * total de productos * 1.5 (factor de stock óptimo)
                    double stockIdeal = promedioStock * totalProductos * 1.5;
                    return Math.min(100, (totalActual / stockIdeal) * 100);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular porcentaje de stock: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Obtiene productos con stock bajo (menos de 30 unidades)
     */
    public List<ProductoNotificacion> obtenerProductosParaNotificaciones() {
        List<ProductoNotificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT p.nombre_producto, p.stock_producto, c.nombre_categoria " +
                    "FROM productos p " +
                    "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                    "WHERE p.stock_producto <= 30 " + // Stock bajo
                    "ORDER BY p.stock_producto ASC " +
                    "LIMIT 5"; // Máximo 5 notificaciones
        
        try (Statement stmt = conexion.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nombre = rs.getString("nombre_producto");
                int stock = rs.getInt("stock_producto");
                String categoria = rs.getString("nombre_categoria");
                
                ProductoNotificacion notif = new ProductoNotificacion(nombre, stock, categoria);
                notificaciones.add(notif);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos para notificaciones: " + e.getMessage());
        }
        
        return notificaciones;
    }
    
    /**
     * Obtiene resumen por categorías para las notificaciones
     */
    public List<CategoriaResumen> obtenerResumenCategorias() {
        List<CategoriaResumen> resumen = new ArrayList<>();
        String sql = "SELECT c.nombre_categoria, SUM(p.stock_producto) as total_stock " +
                    "FROM productos p " +
                    "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                    "GROUP BY c.id_categoria, c.nombre_categoria " +
                    "ORDER BY total_stock DESC " +
                    "LIMIT 5";
        
        try (Statement stmt = conexion.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String categoria = rs.getString("nombre_categoria");
                int totalStock = rs.getInt("total_stock");
                
                // Si la categoría es null, usar "Sin categoría"
                if (categoria == null) categoria = "Sin categoría";
                
                CategoriaResumen cat = new CategoriaResumen(categoria, totalStock);
                resumen.add(cat);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener resumen de categorías: " + e.getMessage());
        }
        
        return resumen;
    }
    
    //VERIFICACION DE EXISTENCIA
    public boolean existe(int id) {
        String sql = "SELECT COUNT(*) FROM productos WHERE id_producto = ?";
        
        try (PreparedStatement stmt = conexion.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia del producto: " + e.getMessage());
        }
        return false;
    }
    
    private Producto mapearResultSet(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id_producto"),
            rs.getString("nombre_producto"),
            rs.getString("descripcion_producto"),
            rs.getBigDecimal("precio_producto"),
            rs.getInt("stock_producto"),
            rs.getString("color_producto"),
            rs.getString("medidas_producto"),
            rs.getInt("id_categoria"),
            rs.getInt("id_proveedor")
        );
    }
    
    // ============ CLASES AUXILIARES PARA NOTIFICACIONES ============
    
    public static class ProductoNotificacion {
        private String nombre;
        private int stock;
        private String categoria;
        
        public ProductoNotificacion(String nombre, int stock, String categoria) {
            this.nombre = nombre;
            this.stock = stock;
            this.categoria = categoria;
        }
        
        public String getNombre() { return nombre; }
        public int getStock() { return stock; }
        public String getCategoria() { return categoria; }
        
        public boolean isStockCritico() { return stock <= 10; }
        public boolean isStockBajo() { return stock <= 30; }
    }
    
    public static class CategoriaResumen {
        private String categoria;
        private int totalStock;
        
        public CategoriaResumen(String categoria, int totalStock) {
            this.categoria = categoria;
            this.totalStock = totalStock;
        }
        
        public String getCategoria() { return categoria; }
        public int getTotalStock() { return totalStock; }
    }
}