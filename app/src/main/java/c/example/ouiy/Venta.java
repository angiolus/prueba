package c.example.ouiy;

public class Venta {
    private String producto;
    private int cantidad;
    private String descripcion;

    public Venta() {}

    public Venta(String producto, int cantidad, String descripcion) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
