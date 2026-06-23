package pe.upn.clinica.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pe.upn.clinica.modelo.Consultorio;

public class ConsultorioDAO implements ICrud<Consultorio> {

    @Override
    public boolean registrar(Consultorio c) {
        String sql = "INSERT INTO consultorios (numero, piso, disponible) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNumero());
            ps.setInt(2, c.getPiso());
            ps.setBoolean(3, true); // estado inicial: Disponible (RF18)
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar consultorio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean editar(Consultorio c) {
        String sql = "UPDATE consultorios SET numero = ?, piso = ?, disponible = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNumero());
            ps.setInt(2, c.getPiso());
            ps.setBoolean(3, c.isDisponible());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al editar consultorio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM consultorios WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar consultorio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Consultorio> listarTodos() {
        List<Consultorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM consultorios ORDER BY numero ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearConsultorio(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar consultorios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Consultorio buscarPorId(int id) {
        String sql = "SELECT * FROM consultorios WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearConsultorio(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar consultorio: " + e.getMessage());
        }
        return null;
    }

    // RF20: disponibilidad de consultorios con médico asignado (si lo tiene)
    public List<String[]> listarDisponibilidadConMedico() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT c.numero, c.piso, c.disponible, "
                + "CONCAT(p.nombre, ' ', p.apellido) AS medico "
                + "FROM consultorios c "
                + "LEFT JOIN medico_consultorio mc ON c.id = mc.consultorio_id "
                + "LEFT JOIN personas p ON mc.medico_id = p.id "
                + "ORDER BY c.numero ASC";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String estado = rs.getBoolean("disponible") ? "Disponible" : "Ocupado";
                String medico = rs.getString("medico") != null ? rs.getString("medico") : "-";
                lista.add(new String[]{rs.getString("numero"), String.valueOf(rs.getInt("piso")), estado, medico});
            }
        } catch (SQLException e) {
            System.err.println("Error al listar disponibilidad: " + e.getMessage());
        }
        return lista;
    }

 // Verifica si el consultorio tiene citas pendientes asociadas
    public boolean tieneCitasPendientes(int consultorioId) {
        String sql = "SELECT COUNT(*) AS total FROM citas_medicas WHERE consultorio_id = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, consultorioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar citas del consultorio: " + e.getMessage());
        }
        return false;
    }
 // RF19: asignar un médico a un consultorio, actualizando también la disponibilidad real
    public String asignarMedico(int medicoId, int consultorioId) {
        Consultorio consultorio = buscarPorId(consultorioId);
        if (consultorio == null) {
            return "ERROR: el consultorio no existe.";
        }
        if (!consultorio.isDisponible()) {
            return "BLOQUEADO: este consultorio ya está ocupado por otro médico.";
        }

        String sqlBorrarAsignacionPrevia = "DELETE FROM medico_consultorio WHERE medico_id = ?";
        String sqlInsertar = "INSERT INTO medico_consultorio (medico_id, consultorio_id) VALUES (?, ?)";
        String sqlLiberarConsultorioAnterior =
                "UPDATE consultorios SET disponible = TRUE WHERE id IN "
                        + "(SELECT consultorio_id FROM medico_consultorio WHERE medico_id = ?)";
        String sqlOcuparNuevo = "UPDATE consultorios SET disponible = FALSE WHERE id = ?";

        try {
            // Libera el consultorio que el médico tenía asignado antes (si tenía alguno)
            PreparedStatement psLiberar = ConexionBD.getConexion().prepareStatement(sqlLiberarConsultorioAnterior);
            psLiberar.setInt(1, medicoId);
            psLiberar.executeUpdate();

            PreparedStatement psBorrar = ConexionBD.getConexion().prepareStatement(sqlBorrarAsignacionPrevia);
            psBorrar.setInt(1, medicoId);
            psBorrar.executeUpdate();

            PreparedStatement psInsertar = ConexionBD.getConexion().prepareStatement(sqlInsertar);
            psInsertar.setInt(1, medicoId);
            psInsertar.setInt(2, consultorioId);
            psInsertar.executeUpdate();

            PreparedStatement psOcupar = ConexionBD.getConexion().prepareStatement(sqlOcuparNuevo);
            psOcupar.setInt(1, consultorioId);
            psOcupar.executeUpdate();

            return "OK";

        } catch (SQLException e) {
            System.err.println("Error al asignar médico a consultorio: " + e.getMessage());
            return "ERROR: no se pudo asignar el médico al consultorio.";
        }
    }

    private Consultorio mapearConsultorio(ResultSet rs) throws SQLException {
        Consultorio c = new Consultorio();
        c.setId(rs.getInt("id"));
        c.setNumero(rs.getString("numero"));
        c.setPiso(rs.getInt("piso"));
        c.setDisponible(rs.getBoolean("disponible"));
        return c;
    }
}