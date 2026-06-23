package pe.upn.clinica.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pe.upn.clinica.modelo.Paciente;

public class PacienteDAO implements ICrud<Paciente> {

    @Override
    public boolean registrar(Paciente p) {
        String sqlPersona = "INSERT INTO personas (nombre, apellido, dni, telefono, tipo) VALUES (?, ?, ?, ?, 'PACIENTE')";
        String sqlPaciente = "INSERT INTO pacientes (id, fecha_nacimiento, ruc, razon_social) VALUES (?, ?, ?, ?)";

        Connection con = ConexionBD.getConexion();
        try {
            con.setAutoCommit(false);

            PreparedStatement psPersona = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psPersona.setString(1, p.getNombre());
            psPersona.setString(2, p.getApellido());
            psPersona.setString(3, p.getDni());
            psPersona.setString(4, p.getTelefono());
            psPersona.executeUpdate();

            ResultSet rs = psPersona.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            PreparedStatement psPaciente = con.prepareStatement(sqlPaciente);
            psPaciente.setInt(1, idGenerado);
            psPaciente.setDate(2, p.getFechaNacimiento() != null ? java.sql.Date.valueOf(p.getFechaNacimiento()) : null);
            psPaciente.setString(3, p.getRuc());
            psPaciente.setString(4, p.getRazonSocial());
            psPaciente.executeUpdate();

            con.commit();
            p.setId(idGenerado);
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al registrar paciente: " + e.getMessage());
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
    public boolean editar(Paciente p) {
        String sqlPersona = "UPDATE personas SET nombre = ?, apellido = ?, dni = ?, telefono = ? WHERE id = ?";
        String sqlPaciente = "UPDATE pacientes SET fecha_nacimiento = ?, ruc = ?, razon_social = ? WHERE id = ?";

        Connection con = ConexionBD.getConexion();
        try {
            con.setAutoCommit(false);

            PreparedStatement psPersona = con.prepareStatement(sqlPersona);
            psPersona.setString(1, p.getNombre());
            psPersona.setString(2, p.getApellido());
            psPersona.setString(3, p.getDni());
            psPersona.setString(4, p.getTelefono());
            psPersona.setInt(5, p.getId());
            psPersona.executeUpdate();

            PreparedStatement psPaciente = con.prepareStatement(sqlPaciente);
            psPaciente.setDate(1, p.getFechaNacimiento() != null ? java.sql.Date.valueOf(p.getFechaNacimiento()) : null);
            psPaciente.setString(2, p.getRuc());
            psPaciente.setString(3, p.getRazonSocial());
            psPaciente.setInt(4, p.getId());
            psPaciente.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error al editar paciente: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Punto 10: actualiza solo el RUC y la Razón Social del paciente (sin tocar el resto de sus datos)
    public boolean actualizarDatosFacturacion(int pacienteId, String ruc, String razonSocial) {
        String sql = "UPDATE pacientes SET ruc = ?, razon_social = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, ruc);
            ps.setString(2, razonSocial);
            ps.setInt(3, pacienteId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar datos de facturación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM personas WHERE id = ?";
        String sqlHijo = "DELETE FROM pacientes WHERE id = ?";

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
            System.err.println("Error al eliminar paciente: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Verifica si el paciente tiene cualquier cita registrada (sin importar el estado)
    public boolean tieneHistorialCitas(int pacienteId) {
        String sql = "SELECT COUNT(*) AS total FROM citas_medicas WHERE paciente_id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar historial del paciente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.apellido, p.dni, p.telefono, "
                + "pa.fecha_nacimiento, pa.ruc, pa.razon_social "
                + "FROM personas p INNER JOIN pacientes pa ON p.id = pa.id "
                + "ORDER BY p.nombre ASC";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPaciente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar pacientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Paciente buscarPorId(int id) {
        String sql = "SELECT p.id, p.nombre, p.apellido, p.dni, p.telefono, "
                + "pa.fecha_nacimiento, pa.ruc, pa.razon_social "
                + "FROM personas p INNER JOIN pacientes pa ON p.id = pa.id WHERE p.id = ?";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }
        return null;
    }

    // RF05: búsqueda por nombre o DNI
    public List<Paciente> buscarPorNombreODni(String criterio) {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.apellido, p.dni, p.telefono, "
                + "pa.fecha_nacimiento, pa.ruc, pa.razon_social "
                + "FROM personas p INNER JOIN pacientes pa ON p.id = pa.id "
                + "WHERE p.nombre LIKE ? OR p.apellido LIKE ? OR p.dni LIKE ? "
                + "ORDER BY p.nombre ASC";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            String like = "%" + criterio + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPaciente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }
        return lista;
    }

    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDni(rs.getString("dni"));
        p.setTelefono(rs.getString("telefono"));
        java.sql.Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) {
            p.setFechaNacimiento(fecha.toLocalDate());
        }
        p.setRuc(rs.getString("ruc"));
        p.setRazonSocial(rs.getString("razon_social"));
        return p;
    }
}