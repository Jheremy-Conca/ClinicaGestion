package pe.upn.clinica.modelo;

import java.time.LocalDate;

public class Diagnostico {

    private int id;
    private int citaId;
    private String descripcion;
    private LocalDate fechaDiagnostico;

    // Campo auxiliar para mostrar en tablas
    private String nombrePaciente;

    public Diagnostico() {
    }

    public Diagnostico(int id, int citaId, String descripcion, LocalDate fechaDiagnostico) {
        this.id = id;
        this.citaId = citaId;
        this.descripcion = descripcion;
        this.fechaDiagnostico = fechaDiagnostico;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaDiagnostico() {
        return fechaDiagnostico;
    }

    public void setFechaDiagnostico(LocalDate fechaDiagnostico) {
        this.fechaDiagnostico = fechaDiagnostico;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }
}