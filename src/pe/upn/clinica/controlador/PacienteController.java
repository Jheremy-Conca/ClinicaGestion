package pe.upn.clinica.controlador;

import java.util.List;

import pe.upn.clinica.modelo.Paciente;
import pe.upn.clinica.persistencia.PacienteDAO;
import pe.upn.clinica.util.GestorArchivos;
import pe.upn.clinica.util.Validador;

public class PacienteController {

    private final PacienteDAO pacienteDAO;
    private final GestorArchivos gestorArchivos;

    public PacienteController() {
        this.pacienteDAO = new PacienteDAO();
        this.gestorArchivos = new GestorArchivos();
    }

    public boolean registrarPaciente(Paciente p) {
        normalizarTexto(p);
        boolean resultado = pacienteDAO.registrar(p);
        if (resultado) {
            gestorArchivos.registrarLog("Registro de paciente: " + p.getDni() + " - " + p.getNombre());
        }
        return resultado;
    }

    public boolean editarPaciente(Paciente p) {
        normalizarTexto(p);
        boolean resultado = pacienteDAO.editar(p);
        if (resultado) {
            gestorArchivos.registrarLog("Edición de paciente ID " + p.getId());
        }
        return resultado;
    }

    // Bloquea si tiene historial clínico/tributario asociado
    public String eliminarPaciente(int id) {
        if (pacienteDAO.tieneHistorialCitas(id)) {
            return "BLOQUEADO: este paciente tiene citas registradas en su historial y no puede eliminarse. "
                    + "Esto protege su historial clínico y los comprobantes de pago emitidos.";
        }
        boolean resultado = pacienteDAO.eliminar(id);
        if (resultado) {
            gestorArchivos.registrarLog("Eliminación de paciente ID " + id);
            return "OK";
        }
        return "ERROR: no se pudo eliminar el paciente.";
    }

    public List<Paciente> listarPacientes() {
        return pacienteDAO.listarTodos();
    }

    // Necesario para el autocompletado de RUC/Razón Social en VentanaComprobantes
    public Paciente buscarPorId(int id) {
        return pacienteDAO.buscarPorId(id);
    }

    public List<Paciente> buscarPacientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarPacientes();
        }
        return pacienteDAO.buscarPorNombreODni(criterio);
    }

    // RF28: exportar a PDF
    public void exportarPacientesPDF(String ruta) throws Exception {
        gestorArchivos.exportarPacientesPDF(listarPacientes(), ruta);
    }

    // Punto 10: guarda/actualiza los datos de facturación del paciente para reutilizarlos después
    public boolean actualizarDatosFacturacion(int pacienteId, String ruc, String razonSocial) {
        return pacienteDAO.actualizarDatosFacturacion(pacienteId, ruc, razonSocial);
    }

    // Normaliza nombres/apellidos para que nunca se mezclen mayúsculas/minúsculas
    private void normalizarTexto(Paciente p) {
        p.setNombre(Validador.capitalizarNombre(p.getNombre()));
        p.setApellido(Validador.capitalizarNombre(p.getApellido()));
    }
}