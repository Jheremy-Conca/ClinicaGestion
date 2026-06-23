package pe.upn.clinica.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pe.upn.clinica.modelo.Medico;

public class MedicoDAO implements ICrud<Medico> {

    @Override
    public boolean registrar(Medico m) {
        String sqlPersona = "INSERT INTO personas (nombre, apellido, dni, telefono, tipo) VALUES (?, ?, ?, ?, 'MEDICO')";
        String sqlMedico = "INSERT INTO medicos (id, cmp, especialidad) VALUES (?, ?, ?)";

        Connection con = ConexionBD.getConexion();
        try {
            con.setAutoCommit(false);

            PreparedStatement psPersona = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psPersona.setString(1, m.getNombre());
            psPersona.setString(2, m.getApellido());
            psPersona.setString(3, m.getDni());
            psPersona.setString(4, m.getTelefono());
            psPersona.executeUpdate();

            ResultSet rs = psPersona.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            PreparedStatement psMedico = con.prepareStatement(sqlMedico);
            psMedico.setInt(1, idGenerado);
            psMedico.setString(2, m.getCmp());
            psMedico.setString(3, m.getEspecialidad());
            psMedico.executeUpdate();

            con.commit();
            m.setId(idGenerado);
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al registrar médico: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean editar(Medico m) {
        String sqlPersona = "UPDATE personas SET nombre = ?, apellido = ?, dni = ?, telefono = ? WHERE id = ?";
        String sqlMedico = "UPDATE medicos SET cmp = ?, especialidad = ? WHERE id = ?";

        Connection con = ConexionBD.getConexion();
        try {
            con.setAutoCommit(false);

            PreparedStatement psPersona = con.prepareStatement(sqlPersona);
            psPersona.setString(1, m.getNombre());
            psPersona.setString(2, m.getApellido());
            psPersona.setString(3, m.getDni());
            psPersona.setString(4, m.getTelefono());
            psPersona.setInt(5, m.getId());
            psPersona.executeUpdate();

            PreparedStatement psMedico = con.prepareStatement(sqlMedico);
            psMedico.setString(1, m.getCmp());
            psMedico.setString(2, m.getEspecialidad());
            psMedico.setInt(3, m.getId());
            psMedico.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al editar médico: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // RF08: advertir si el médico tiene citas pendientes antes de eliminar
    public boolean tieneCitasPendientes(int medicoId) {
        String sql = "SELECT COUNT(*) AS total FROM citas_medicas WHERE medico_id = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, medicoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar citas del médico: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sqlHijo = "DELETE FROM medicos WHERE id = ?";
        String sql = "DELETE FROM personas WHERE id = ?";

        Connection con = ConexionBD.getConexion();
        try {
            con.setAutoCommit(false);

            PreparedStatement psHijo = con.prepareStatement(sqlHijo);
            psHijo.setInt(1, id);
            psHijo.executeUpdate();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al eliminar médico: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Medico> listarTodos() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.apellido, p.dni, p.telefono, m.cmp, m.especialidad "
                + "FROM personas p INNER JOIN medicos m ON p.id = m.id "
                + "ORDER BY p.nombre ASC";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMedico(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar médicos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Medico buscarPorId(int id) {
        String sql = "SELECT p.id, p.nombre, p.apellido, p.dni, p.telefono, m.cmp, m.especialidad "
                + "FROM personas p INNER JOIN medicos m ON p.id = m.id WHERE p.id = ?";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar médico: " + e.getMessage());
        }
        return null;
    }

    // RF09: listar médicos filtrados por especialidad
    public List<Medico> listarPorEspecialidad(String especialidad) {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.apellido, p.dni, p.telefono, m.cmp, m.especialidad "
                + "FROM personas p INNER JOIN medicos m ON p.id = m.id "
                + "WHERE m.especialidad LIKE ? ORDER BY p.nombre ASC";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, "%" + especialidad + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMedico(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar médicos: " + e.getMessage());
        }
        return lista;
    }

    private Medico mapearMedico(ResultSet rs) throws SQLException {
        Medico m = new Medico();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setApellido(rs.getString("apellido"));
        m.setDni(rs.getString("dni"));
        m.setTelefono(rs.getString("telefono"));
        m.setCmp(rs.getString("cmp"));
        m.setEspecialidad(rs.getString("especialidad"));
        return m;
    }
}