package pe.upn.clinica.modelo;

import java.time.LocalDate;

public class Paciente extends Persona {

    private LocalDate fechaNacimiento;

    public Paciente() {
        super();
    }

    public Paciente(int id, String nombre, String apellido, String dni, String telefono, LocalDate fechaNacimiento) {
        super(id, nombre, apellido, dni, telefono);
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    private String ruc;
    private String razonSocial;

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
    // POLIMORFISMO: implementación específica para Paciente
    @Override
    public String mostrarInfo() {
        return "PACIENTE | DNI: " + getDni() + " | Nombre: " + getNombre() + " " + getApellido()
                + " | Tel: " + getTelefono() + " | F. Nac: " + fechaNacimiento;
    }
}