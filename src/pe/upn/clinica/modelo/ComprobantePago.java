package pe.upn.clinica.modelo;

import java.time.LocalDate;

public abstract class ComprobantePago {

    public static final String BOLETA = "BOLETA";
    public static final String FACTURA = "FACTURA";

    private int id;
    private int citaId;
    private double monto;
    private LocalDate fecha;
    private String tipo;

    public ComprobantePago() {
    }

    public ComprobantePago(int id, int citaId, double monto, LocalDate fecha, String tipo) {
        this.id = id;
        this.citaId = citaId;
        this.monto = monto;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    // Método abstracto: cada subclase genera su propio comprobante (ABSTRACCIÓN + POLIMORFISMO)
    public abstract String generarComprobante();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCitaId() {
        return citaId;
    }

    public void setCitaId(int citaId) {
        this.citaId = citaId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}