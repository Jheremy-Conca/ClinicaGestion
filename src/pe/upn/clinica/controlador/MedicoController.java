package pe.upn.clinica.controlador;

import java.util.List;

import pe.upn.clinica.modelo.Medico;
import pe.upn.clinica.persistencia.MedicoDAO;
import pe.upn.clinica.util.GestorArchivos;

public class MedicoController {

    private final MedicoDAO medicoDAO;
    private final GestorArchivos gestorArchivos;

    public MedicoController() {
        this.medicoDAO = new MedicoDAO();
        this.gestorArchivos = new GestorArchivos();
    }

    public boolean registrarMedico(Medico m) {
        boolean resultado = medicoDAO.registrar(m);
        if (resultado) {
            gestorArchivos.registrarLog("Registro de médico: " + m.getCmp() + " - " + m.getNombre());
        }
        return resultado;
    }

    public boolean editarMedico(Medico m) {
        boolean resultado = medicoDAO.editar(m);
        if (resultado) {
            gestorArchivos.registrarLog("Edición de médico ID " + m.getId());
        }
        return resultado;
    }

    // RF08: advierte si tiene citas pendientes antes de eliminar
    public boolean tieneCitasPendientes(int medicoId) {
        return medicoDAO.tieneCitasPendientes(medicoId);
    }

    public boolean eliminarMedico(int id) {
        boolean resultado = medicoDAO.eliminar(id);
        if (resultado) {
            gestorArchivos.registrarLog("Eliminación de médico ID " + id);
        }
        return resultado;
    }

    public List<Medico> listarMedicos() {
        return medicoDAO.listarTodos();
    }

    public List<Medico> filtrarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            return listarMedicos();
        }
        return medicoDAO.listarPorEspecialidad(especialidad);
    }
}