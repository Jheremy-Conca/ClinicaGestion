package pe.upn.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaMedica {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ATENDIDA = "ATENDIDA";
    public static final String CANCELADA = "CANCELADA";

    private int id;
    private int pacienteId;
    private int medicoId;
    private int consultorioId;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    // Campos auxiliares para mostrar en tablas (no se guardan en la tabla citas_medicas)
    private String nombrePaciente;
    private String nombreMedico;
    private String numeroConsultorio;

    public CitaMedica() {
        this.estado = PENDIENTE;
    }

    public CitaMedica(int id, int pacienteId, int medicoId, int consultorioId,
                       LocalDate fecha, LocalTime hora, String estado) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.consultorioId = consultorioId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }
    @Override
    public String toString() {
        // Esto es lo que el JComboBox leerá y pintará en la pantalla
        return this.getNombrePaciente();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public int getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(int medicoId) {
        this.medicoId = medicoId;
    }

    public int getConsultorioId() {
        return consultorioId;
    }

    public void setConsultorioId(int consultorioId) {
        this.consultorioId = consultorioId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getNumeroConsultorio() {
        return numeroConsultorio;
    }

    public void setNumeroConsultorio(String numeroConsultorio) {
        this.numeroConsultorio = numeroConsultorio;
    }
}