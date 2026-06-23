package pe.upn.clinica.modelo;

import java.time.LocalDate;

public class Boleta extends ComprobantePago {

    public Boleta() {
        super();
        setTipo(BOLETA);
    }

    public Boleta(int id, int citaId, double monto, LocalDate fecha) {
        super(id, citaId, monto, fecha, BOLETA);
    }

    // POLIMORFISMO: la Boleta no desglosa IGV
    @Override
    public String generarComprobante() {
        return "----- BOLETA DE VENTA -----\n"
                + "N°: " + getId() + "\n"
                + "Fecha: " + getFecha() + "\n"
                + "Monto Total: S/ " + String.format("%.2f", getMonto()) + "\n"
                + "----------------------------";
    }
}