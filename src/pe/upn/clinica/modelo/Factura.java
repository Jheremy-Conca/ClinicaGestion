package pe.upn.clinica.modelo;

import java.time.LocalDate;

public class Factura extends ComprobantePago {

    public static final double TASA_IGV = 0.18;

    private String ruc;
    private String razonSocial;
    private double igv;

    public Factura() {
        super();
        setTipo(FACTURA);
    }

    public Factura(int id, int citaId, double monto, LocalDate fecha, String ruc, String razonSocial) {
        super(id, citaId, monto, fecha, FACTURA);
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.igv = calcularIgv(monto);
    }

    // Cálculo automático del IGV (RF23)
    public double calcularIgv(double montoBase) {
        return montoBase * TASA_IGV;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    // POLIMORFISMO: la Factura sí desglosa IGV y datos de empresa
    @Override
    public String generarComprobante() {
        double subtotal = getMonto();
        double total = subtotal + igv;
        return "----- FACTURA ELECTRÓNICA -----\n"
                + "N°: " + getId() + "\n"
                + "Fecha: " + getFecha() + "\n"
                + "RUC: " + ruc + "\n"
                + "Razón Social: " + razonSocial + "\n"
                + "Subtotal: S/ " + String.format("%.2f", subtotal) + "\n"
                + "IGV (18%): S/ " + String.format("%.2f", igv) + "\n"
                + "TOTAL: S/ " + String.format("%.2f", total) + "\n"
                + "--------------------------------";
    }
}