package pe.upn.clinica.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pe.upn.clinica.modelo.Boleta;
import pe.upn.clinica.modelo.ComprobantePago;
import pe.upn.clinica.modelo.Factura;

public class ComprobanteDAO {

    // RF21: generar Boleta
    public boolean registrarBoleta(Boleta b) {
        String sql = "INSERT INTO comprobantes_pago (cita_id, monto, fecha, tipo) VALUES (?, ?, ?, 'BOLETA')";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getCitaId());
            ps.setDouble(2, b.getMonto());
            ps.setDate(3, java.sql.Date.valueOf(b.getFecha()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                b.setId(rs.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar boleta: " + e.getMessage());
            return false;
        }
    }

    // RF22 y RF23: generar Factura con cálculo automático de IGV
    public boolean registrarFactura(Factura f) {
        String sql = "INSERT INTO comprobantes_pago (cita_id, monto, fecha, tipo, ruc, razon_social, igv) "
                + "VALUES (?, ?, ?, 'FACTURA', ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getCitaId());
            ps.setDouble(2, f.getMonto());
            ps.setDate(3, java.sql.Date.valueOf(f.getFecha()));
            ps.setString(4, f.getRuc());
            ps.setString(5, f.getRazonSocial());
            ps.setDouble(6, f.getIgv());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                f.setId(rs.getInt(1));
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar factura: " + e.getMessage());
            return false;
        }
    }

    // RF24: historial de pagos de un paciente
    public List<ComprobantePago> listarPorPaciente(int pacienteId) {
        List<ComprobantePago> lista = new ArrayList<>();
        String sql = "SELECT cp.* FROM comprobantes_pago cp "
                + "INNER JOIN citas_medicas c ON cp.cita_id = c.id "
                + "WHERE c.paciente_id = ? ORDER BY cp.fecha DESC";

        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, pacienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearComprobante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar comprobantes: " + e.getMessage());
        }
        return lista;
    }

    public List<ComprobantePago> listarTodos() {
        List<ComprobantePago> lista = new ArrayList<>();
        String sql = "SELECT * FROM comprobantes_pago ORDER BY fecha DESC";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearComprobante(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar comprobantes: " + e.getMessage());
        }
        return lista;
    }

    private ComprobantePago mapearComprobante(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        if ("FACTURA".equals(tipo)) {
            Factura f = new Factura();
            f.setId(rs.getInt("id"));
            f.setCitaId(rs.getInt("cita_id"));
            f.setMonto(rs.getDouble("monto"));
            f.setFecha(rs.getDate("fecha").toLocalDate());
            f.setRuc(rs.getString("ruc"));
            f.setRazonSocial(rs.getString("razon_social"));
            f.setIgv(rs.getDouble("igv"));
            return f;
        } else {
            Boleta b = new Boleta();
            b.setId(rs.getInt("id"));
            b.setCitaId(rs.getInt("cita_id"));
            b.setMonto(rs.getDouble("monto"));
            b.setFecha(rs.getDate("fecha").toLocalDate());
            return b;
        }
    }
 // Verifica si una cita ya tiene un comprobante de pago generado
    public boolean existeComprobantePorCita(int citaId) {
        String sql = "SELECT COUNT(*) AS total FROM comprobantes_pago WHERE cita_id = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, citaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar comprobante existente: " + e.getMessage());
        }
        return false;
    }
}