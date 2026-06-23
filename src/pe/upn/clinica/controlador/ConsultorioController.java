package pe.upn.clinica.controlador;

import java.util.List;

import pe.upn.clinica.modelo.Consultorio;
import pe.upn.clinica.persistencia.ConsultorioDAO;
import pe.upn.clinica.util.GestorArchivos;

public class ConsultorioController {

    private final ConsultorioDAO consultorioDAO;
    private final GestorArchivos gestorArchivos;

    public ConsultorioController() {
        this.consultorioDAO = new ConsultorioDAO();
        this.gestorArchivos = new GestorArchivos();
    }

    public boolean registrarConsultorio(Consultorio c) {
        boolean resultado = consultorioDAO.registrar(c);
        if (resultado) {
            gestorArchivos.registrarLog("Registro de consultorio: " + c.getNumero());
        }
        return resultado;
    }

    public boolean editarConsultorio(Consultorio c) {
        return consultorioDAO.editar(c);
    }

    public String eliminarConsultorio(int id) {
        if (consultorioDAO.tieneCitasPendientes(id)) {
            return "BLOQUEADO: este consultorio tiene citas pendientes asociadas y no puede eliminarse. "
                    + "Cancele o reasigne esas citas primero.";
        }
        boolean resultado = consultorioDAO.eliminar(id);
        if (resultado) {
            gestorArchivos.registrarLog("Eliminación de consultorio ID " + id);
            return "OK";
        }
        return "ERROR: no se pudo eliminar el consultorio.";
    }

    public List<Consultorio> listarConsultorios() {
        return consultorioDAO.listarTodos();
    }

    public List<String[]> listarDisponibilidad() {
        return consultorioDAO.listarDisponibilidadConMedico();
    }

    public String asignarMedico(int medicoId, int consultorioId) {
        String resultado = consultorioDAO.asignarMedico(medicoId, consultorioId);
        if ("OK".equals(resultado)) {
            gestorArchivos.registrarLog("Asignación de médico ID " + medicoId + " a consultorio ID " + consultorioId);
        }
        return resultado;
    }
}