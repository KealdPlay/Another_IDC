
package dao;

import database.Conexion;
import entidades.Producto;
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
}
