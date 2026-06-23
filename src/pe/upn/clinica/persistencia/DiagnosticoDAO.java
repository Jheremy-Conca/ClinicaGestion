package pe.upn.clinica.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pe.upn.clinica.modelo.Diagnostico;

public class DiagnosticoDAO implements ICrud<Diagnostico> {

    private static final String SELECT_BASE =
            "SELECT d.id, d.cita_id, d.descripcion, d.fecha_diagnostico, "
                    + "CONCAT(p.nombre, ' ', p.apellido) AS nombre_paciente "
                    + "FROM diagnosticos d "
                    + "INNER JOIN citas_medicas c ON d.cita_id = c.id "
                    + "INNER JOIN personas p ON c.paciente_id = p.id ";

    @Override
    public boolean registrar(Diagnostico d) {
        String sql = "INSERT INTO diagnosticos (cita_id, descripcion, fecha_diagnostico) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getCitaId());
            ps.setString(2, d.getDescripcion());
            ps.setDate(3, java.sql.Date.valueOf(d.getFechaDiagnostico()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                d.setId(rs.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar diagnóstico: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean editar(Diagnostico d) {
        String sql = "UPDATE diagnosticos SET descripcion = ?, fecha_diagnostico = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, d.getDescripcion());
            ps.setDate(2, java.sql.Date.valueOf(d.getFechaDiagnostico()));
            ps.setInt(3, d.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al editar diagnóstico: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM diagnosticos WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar diagnóstico: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Diagnostico> listarTodos() {
        List<Diagnostico> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY d.fecha_diagnostico DESC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDiagnostico(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar diagnósticos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Diagnostico buscarPorId(int id) {
        String sql = SELECT_BASE + "WHERE d.id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearDiagnostico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar diagnóstico: " + e.getMessage());
        }
        return null;
    }

    // RF17: historial de diagnósticos de un paciente, ordenado cronológicamente
    public List<Diagnostico> listarPorPaciente(int pacienteId) {
        List<Diagnostico> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.paciente_id = ? ORDER BY d.fecha_diagnostico ASC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDiagnostico(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar historial: " + e.getMessage());
        }
        return lista;
    }

    private Diagnostico mapearDiagnostico(ResultSet rs) throws SQLException {
        Diagnostico d = new Diagnostico();
        d.setId(rs.getInt("id"));
        d.setCitaId(rs.getInt("cita_id"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setFechaDiagnostico(rs.getDate("fecha_diagnostico").toLocalDate());
        d.setNombrePaciente(rs.getString("nombre_paciente"));
        return d;
    }
 // Verifica si una cita ya tiene un diagnóstico registrado
    public boolean existeDiagnosticoPorCita(int citaId) {
        String sql = "SELECT COUNT(*) AS total FROM diagnosticos WHERE cita_id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, citaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar diagnóstico existente: " + e.getMessage());
        }
        return false;
    }
}