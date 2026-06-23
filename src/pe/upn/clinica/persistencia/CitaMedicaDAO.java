package pe.upn.clinica.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pe.upn.clinica.modelo.CitaMedica;

public class CitaMedicaDAO implements ICrud<CitaMedica> {

    private static final String SELECT_BASE =
            "SELECT c.id, c.paciente_id, c.medico_id, c.consultorio_id, c.fecha, c.hora, c.estado, "
                    + "CONCAT(pp.nombre, ' ', pp.apellido) AS nombre_paciente, "
                    + "CONCAT(pm.nombre, ' ', pm.apellido) AS nombre_medico, "
                    + "co.numero AS numero_consultorio "
                    + "FROM citas_medicas c "
                    + "INNER JOIN personas pp ON c.paciente_id = pp.id "
                    + "INNER JOIN personas pm ON c.medico_id = pm.id "
                    + "INNER JOIN consultorios co ON c.consultorio_id = co.id ";

    // RF26: validar que no se dupliquen citas en el mismo horario y consultorio
    public boolean existeConflictoHorario(LocalDate fecha, java.time.LocalTime hora, int consultorioId) {
        String sql = "SELECT COUNT(*) AS total FROM citas_medicas "
                + "WHERE fecha = ? AND hora = ? AND consultorio_id = ? AND estado <> 'CANCELADA'";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setTime(2, java.sql.Time.valueOf(hora));
            ps.setInt(3, consultorioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar conflicto de horario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean registrar(CitaMedica c) {
        String sql = "INSERT INTO citas_medicas (paciente_id, medico_id, consultorio_id, fecha, hora, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getPacienteId());
            ps.setInt(2, c.getMedicoId());
            ps.setInt(3, c.getConsultorioId());
            ps.setDate(4, java.sql.Date.valueOf(c.getFecha()));
            ps.setTime(5, java.sql.Time.valueOf(c.getHora()));
            ps.setString(6, c.getEstado() != null ? c.getEstado() : CitaMedica.PENDIENTE);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean editar(CitaMedica c) {
        String sql = "UPDATE citas_medicas SET paciente_id = ?, medico_id = ?, consultorio_id = ?, "
                + "fecha = ?, hora = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, c.getPacienteId());
            ps.setInt(2, c.getMedicoId());
            ps.setInt(3, c.getConsultorioId());
            ps.setDate(4, java.sql.Date.valueOf(c.getFecha()));
            ps.setTime(5, java.sql.Time.valueOf(c.getHora()));
            ps.setString(6, c.getEstado());
            ps.setInt(7, c.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al editar cita: " + e.getMessage());
            return false;
        }
    }

    // RF12: cancelar cita (cambia estado, no elimina)
    public boolean cancelarCita(int id) {
        String sql = "UPDATE citas_medicas SET estado = 'CANCELADA' WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al cancelar cita: " + e.getMessage());
            return false;
        }
        
    }
 // Marca la cita como ATENDIDA (se usa al registrar el diagnóstico)
    public boolean marcarComoAtendida(int id) {
        String sql = "UPDATE citas_medicas SET estado = 'ATENDIDA' WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al marcar cita como atendida: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas_medicas WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<CitaMedica> listarTodos() {
        List<CitaMedica> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.fecha DESC, c.hora ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar citas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public CitaMedica buscarPorId(int id) {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCita(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cita: " + e.getMessage());
        }
        return null;
    }

    // RF13: citas del día ordenadas por hora
    public List<CitaMedica> listarCitasDelDia(LocalDate fecha) {
        List<CitaMedica> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.fecha = ? ORDER BY c.hora ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar citas del día: " + e.getMessage());
        }
        return lista;
    }

    // RF14: búsqueda por fecha o nombre de paciente
    public List<CitaMedica> buscarPorFechaOPaciente(String criterio) {
        List<CitaMedica> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.fecha = ? OR pp.nombre LIKE ? OR pp.apellido LIKE ? "
                + "ORDER BY c.fecha DESC, c.hora ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            LocalDate fechaBusqueda;
            try {
                fechaBusqueda = LocalDate.parse(criterio);
            } catch (Exception ex) {
                fechaBusqueda = LocalDate.of(1, 1, 1); // fecha imposible si no es una fecha válida
            }
            ps.setDate(1, java.sql.Date.valueOf(fechaBusqueda));
            ps.setString(2, "%" + criterio + "%");
            ps.setString(3, "%" + criterio + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar citas: " + e.getMessage());
        }
        return lista;
    }

    // RF39: filtrar citas por estado
    public List<CitaMedica> filtrarPorEstado(String estado) {
        List<CitaMedica> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.estado = ? ORDER BY c.fecha DESC, c.hora ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar citas por estado: " + e.getMessage());
        }
        return lista;
    }

    // RF38: reporte de citas por médico
    public List<CitaMedica> listarPorMedico(int medicoId) {
        List<CitaMedica> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.medico_id = ? ORDER BY c.fecha DESC, c.hora ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, medicoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar citas por médico: " + e.getMessage());
        }
        return lista;
    }

    private CitaMedica mapearCita(ResultSet rs) throws SQLException {
        CitaMedica c = new CitaMedica();
        c.setId(rs.getInt("id"));
        c.setPacienteId(rs.getInt("paciente_id"));
        c.setMedicoId(rs.getInt("medico_id"));
        c.setConsultorioId(rs.getInt("consultorio_id"));
        c.setFecha(rs.getDate("fecha").toLocalDate());
        c.setHora(rs.getTime("hora").toLocalTime());
        c.setEstado(rs.getString("estado"));
        c.setNombrePaciente(rs.getString("nombre_paciente"));
        c.setNombreMedico(rs.getString("nombre_medico"));
        c.setNumeroConsultorio(rs.getString("numero_consultorio"));
        return c;
    }
}