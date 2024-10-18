package Productos_BD;

import java.sql.*;
import java.util.Scanner;

public class productosMetodo {
    private static final String URL = "jdbc:mysql://localhost:3306/BD_Tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "patrici0";
    static Scanner teclado = new Scanner(System.in);

    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conexion;
    }

    public static void insertarProducto() {
        String codigoProducto; String nombreProducto;  double precioUnitario;  int cantidadProducto;
        System.out.println("INGRESO DE DATOS A LA BASE DE DATOS DE LA TIENDA");
        System.out.print("Ingrese el número de productos a ingresar: ");
        int cant= teclado.nextInt();
        teclado.nextLine();
        for (int i = 0; i <cant; i++) {
            System.out.print("Ingrese el código del producto: ");
            codigoProducto = teclado.nextLine();
            System.out.print("Ingrese el nombre de producto: ");
            nombreProducto = teclado.nextLine();
            System.out.print("Ingrese el precio del producto: ");
            precioUnitario = teclado.nextDouble();
            System.out.print("Ingrese la cantidad del producto: ");
            cantidadProducto = teclado.nextInt();
            teclado.nextLine();
            String query = "INSERT INTO productos (codigoProducto, nombreProducto, precioUnitario, cantidadProducto) VALUES (?,?, ?, ?)";
            try (Connection con = productosMetodo.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, codigoProducto);
                pst.setString(2, nombreProducto);
                pst.setDouble(3, precioUnitario);
                pst.setInt(4, cantidadProducto);
                pst.executeUpdate();
                System.out.println("Producto insertado correctamente");
            } catch (SQLException e) {
            }
        }
    }

    public static void mostrarProductos() {
        String query = "select * from productos;";
        try (Connection con = productosMetodo.conectar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                System.out.println("Código: " + rs.getString("codigoProducto"));
                System.out.println("Nombre: " + rs.getString("nombreProducto"));
                System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                System.out.println("");
            }
            if (!hayResultados) {
                System.out.println("No hay productos disponibles.");
            }//fin if

        } catch (SQLException e) {
        }//fin catch
    }

    public static void buscarProducto() {
        System.out.print("Ingrese el codigo del producto: ");
        String codigoProducto = teclado.nextLine();
        String query = "SELECT * FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("PRODUCTO ENCONTRADO:");
                    System.out.println("Código: " + rs.getString("codigoProducto"));
                    System.out.println("Nombre: " + rs.getString("nombreProducto"));
                    System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                    System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                } else {
                    System.out.println("Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
    }

    public static void actualizarProducto() {
        String codigoProducto = ""; String nombre = ""; double precio = 0; int cantidad = 0;
        System.out.print("Ingrese el codigo del producto: ");
        codigoProducto = teclado.nextLine();
        // First, check if the product exists
        String checkQuery = "SELECT * FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
            checkPst.setString(1, codigoProducto);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("El producto con código " + codigoProducto + " no existe.");
                    return; // Exit the method if the product doesn't exist
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la existencia del producto: " + e.getMessage());
            return; // Sale del metodo si hay error
        }

        //corre el programa si no hay error
        System.out.print("Ingrese el nuevo nombre: ");
        nombre = teclado.nextLine();
        System.out.print("Ingrese el nuevo precio: ");
        precio = teclado.nextDouble();
        System.out.print("Ingrese la nueva cantidad: ");
        cantidad = teclado.nextInt();
        String updateQuery = "UPDATE productos SET nombreProducto = ?, precioUnitario = ?, cantidadProducto = ? WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(updateQuery)) {
            pst.setString(1, nombre);
            pst.setDouble(2, precio);
            pst.setInt(3, cantidad);
            pst.setString(4, codigoProducto);
            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto actualizado correctamente");
            } else {
                System.out.println("No se pudo actualizar el producto. Por favor, intente de nuevo.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    public static void eliminarProducto() {
        System.out.print("Ingrese el código del producto a eliminar: ");
        String codigoProducto = teclado.nextLine();

        //se verifica si el codigo existe
        String checkQuery = "SELECT nombreProducto FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
            checkPst.setString(1, codigoProducto);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No se encontró el producto con el código especificado");
                    return; //sale de la verificacion si no se encuentra
                }
                String nombreProducto = rs.getString("nombreProducto");

                // confirma la eliminacion
                System.out.printf("¿Está seguro que desea eliminar el producto '%s' (código: %s)? (si/no): ",
                        nombreProducto, codigoProducto);
                String confirmacion = teclado.nextLine().trim().toLowerCase();

                if (!confirmacion.equals("si")) {
                    System.out.println("Operación de eliminación cancelada.");
                    return; // se cancela la eliminacion
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la existencia del producto: " + e.getMessage());
            return; //catch si hay error en la query
        }
        // si el usuario confirma
        String deleteQuery = "DELETE FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(deleteQuery)) {
            pst.setString(1, codigoProducto);
            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto eliminado correctamente");
            } else {
                System.out.println("No se pudo eliminar el producto. Por favor, intente de nuevo.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    public static void menuPrincipal(){
        Scanner teclado = new Scanner(System.in);
        int opcion=0;
        do{
            System.out.println("BIENVENIDO AL SISTEMA DE DATOS DE DATOS DE TIENDA");
            System.out.println("MENU PRINCIPAL");
            System.out.println("1. Ingresar Producto");
            System.out.println("2. Mostrar Productos");
            System.out.println("3. Buscar Producto");
            System.out.println("4. Modificar Producto");
            System.out.println("5. Eliminar Producto");
            System.out.println("6. Salir del Menú Principal");
            System.out.print("Ingrese una opción del menu: ");
            opcion= teclado.nextInt();
            switch(opcion){
                case 1: insertarProducto();
                break;
                case 2: mostrarProductos();
                break;
                case 3: buscarProducto();
                break;
                case 4: actualizarProducto();
                break;
                case 5: eliminarProducto();
                break;
                default:
                   System.out.println("Opción no válida. Por favor, elige una opción del 1 al 5.");
                   break;
            }
        }while(opcion!=6);
        System.out.println("Saliendo... ¡Gracias por utilizar el sistema de gestion de base de datos!");
    }

    public static void main(String[] args) {
        menuPrincipal();
    }
}// fin main

