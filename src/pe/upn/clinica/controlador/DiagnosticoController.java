package pe.upn.clinica.controlador;

import java.util.List;

import pe.upn.clinica.modelo.Diagnostico;
import pe.upn.clinica.persistencia.DiagnosticoDAO;
import pe.upn.clinica.util.GestorArchivos;

public class DiagnosticoController {

    private final DiagnosticoDAO diagnosticoDAO;
    private final CitaController citaController;
    private final GestorArchivos gestorArchivos;

    public DiagnosticoController() {
        this.diagnosticoDAO = new DiagnosticoDAO();
        this.citaController = new CitaController();
        this.gestorArchivos = new GestorArchivos();
    }

    // RF15: registrar diagnóstico Y cambiar el estado de la cita a ATENDIDA
    // Retorna un mensaje de resultado para que la vista informe el motivo exacto si falla
    public String registrarDiagnostico(Diagnostico d) {
        if (diagnosticoDAO.existeDiagnosticoPorCita(d.getCitaId())) {
            return "DUPLICADO: esta cita ya tiene un diagnóstico registrado.";
        }
        boolean resultado = diagnosticoDAO.registrar(d);
        if (resultado) {
            citaController.marcarComoAtendida(d.getCitaId());
            gestorArchivos.registrarLog("Diagnóstico registrado para cita ID " + d.getCitaId() + " - cita marcada como ATENDIDA");
            return "OK";
        }
        return "ERROR: no se pudo registrar el diagnóstico.";
    }

    public boolean editarDiagnostico(Diagnostico d) {
        return diagnosticoDAO.editar(d);
    }

    public List<Diagnostico> listarDiagnosticos() {
        return diagnosticoDAO.listarTodos();
    }

    public List<Diagnostico> historialPorPaciente(int pacienteId) {
        return diagnosticoDAO.listarPorPaciente(pacienteId);
    }
}