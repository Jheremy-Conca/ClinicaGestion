package pe.upn.clinica.controlador;

import java.time.LocalDate;
import java.util.List;

import pe.upn.clinica.modelo.CitaMedica;
import pe.upn.clinica.persistencia.CitaMedicaDAO;
import pe.upn.clinica.util.GestorArchivos;

public class CitaController {

    private final CitaMedicaDAO citaDAO;
    private final GestorArchivos gestorArchivos;

    public CitaController() {
        this.citaDAO = new CitaMedicaDAO();
        this.gestorArchivos = new GestorArchivos();
    }

    public String registrarCita(CitaMedica c) {
        if (c.getFecha().isBefore(LocalDate.now())) {
            return "INVALIDO: no se puede registrar una cita en una fecha pasada.";
        }
        if (citaDAO.existeConflictoHorario(c.getFecha(), c.getHora(), c.getConsultorioId())) {
            return "CONFLICTO: el consultorio ya está ocupado en ese horario.";
        }
        boolean resultado = citaDAO.registrar(c);
        if (resultado) {
            gestorArchivos.registrarLog("Registro de cita ID " + c.getId() + " - " + c.getFecha() + " " + c.getHora());
            return "OK";
        }
        return "ERROR: no se pudo registrar la cita.";
    }

    public boolean editarCita(CitaMedica c) {
        boolean resultado = citaDAO.editar(c);
        if (resultado) {
            gestorArchivos.registrarLog("Edición de cita ID " + c.getId());
        }
        return resultado;
    }

    public String cancelarCita(int id) {
        CitaMedica cita = citaDAO.buscarPorId(id);
        if (cita == null) {
            return "ERROR: la cita no existe.";
        }
        if (CitaMedica.ATENDIDA.equals(cita.getEstado())) {
            return "BLOQUEADO: esta cita ya fue atendida (tiene diagnóstico registrado) y no puede cancelarse.";
        }
        if (CitaMedica.CANCELADA.equals(cita.getEstado())) {
            return "BLOQUEADO: esta cita ya estaba cancelada.";
        }
        boolean resultado = citaDAO.cancelarCita(id);
        if (resultado) {
            gestorArchivos.registrarLog("Cancelación de cita ID " + id);
            return "OK";
        }
        return "ERROR: no se pudo cancelar la cita.";
    }

    public boolean marcarComoAtendida(int id) {
        boolean resultado = citaDAO.marcarComoAtendida(id);
        if (resultado) {
            gestorArchivos.registrarLog("Cita ID " + id + " marcada como ATENDIDA");
        }
        return resultado;
    }
    
    public List<CitaMedica> listarCitas() {
        return citaDAO.listarTodos();
    }

    public List<CitaMedica> listarCitasDelDia() {
        return citaDAO.listarCitasDelDia(LocalDate.now());
    }

    public List<CitaMedica> buscarCitas(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarCitas();
        }
        return citaDAO.buscarPorFechaOPaciente(criterio);
    }

    public List<CitaMedica> filtrarPorEstado(String estado) {
        return citaDAO.filtrarPorEstado(estado);
    }

    public List<CitaMedica> reportePorMedico(int medicoId) {
        return citaDAO.listarPorMedico(medicoId);
    }

    // RF29: exportar a PDF
    public void exportarCitasPDF(String ruta) throws Exception {
        gestorArchivos.exportarCitasPDF(listarCitas(), ruta);
    }
}