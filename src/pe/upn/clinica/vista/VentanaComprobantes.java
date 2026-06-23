package pe.upn.clinica.vista;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.CitaController;
import pe.upn.clinica.controlador.PacienteController;
import pe.upn.clinica.modelo.Boleta;
import pe.upn.clinica.modelo.CitaMedica;
import pe.upn.clinica.modelo.ComprobantePago;
import pe.upn.clinica.modelo.Factura;
import pe.upn.clinica.modelo.Paciente;
import pe.upn.clinica.persistencia.ComprobanteDAO;
import pe.upn.clinica.util.GeneradorPDF;
import pe.upn.clinica.util.Validador;

public class VentanaComprobantes extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<CitaMedica> comboCitas;
    private JComboBox<String> comboTipo;
    private JTextField txtMonto;
    private JTextField txtRuc;
    private JTextField txtRazonSocial;
    private JComboBox<Paciente> comboPacienteHistorial;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private final ComprobanteDAO comprobanteDAO = new ComprobanteDAO();
    private final CitaController citaController = new CitaController();
    private final PacienteController pacienteController = new PacienteController();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaComprobantes frame = new VentanaComprobantes();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaComprobantes() {
        setTitle("Comprobantes de Pago");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 560);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblCita = new JLabel("Cita Médica:");
        lblCita.setBounds(20, 20, 100, 25);
        contentPane.add(lblCita);

        comboCitas = new JComboBox<>();
        comboCitas.setBounds(130, 20, 300, 25);
        // RF: al cambiar de cita, intenta autocompletar RUC/Razón Social si el paciente ya los tiene
        comboCitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autocompletarDatosFacturacion();
            }
        });
        contentPane.add(comboCitas);

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setBounds(450, 20, 60, 25);
        contentPane.add(lblTipo);

        comboTipo = new JComboBox<>(new String[]{"BOLETA", "FACTURA"});
        comboTipo.setBounds(510, 20, 150, 25);
        comboTipo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean esFactura = "FACTURA".equals(comboTipo.getSelectedItem());
                txtRuc.setEnabled(esFactura);
                txtRazonSocial.setEnabled(esFactura);
                if (esFactura) {
                    autocompletarDatosFacturacion();
                }
            }
        });
        contentPane.add(comboTipo);

        JLabel lblMonto = new JLabel("Monto (S/):");
        lblMonto.setBounds(20, 60, 100, 25);
        contentPane.add(lblMonto);

        txtMonto = new JTextField();
        txtMonto.setBounds(130, 60, 100, 25);
        contentPane.add(txtMonto);

        JLabel lblRuc = new JLabel("RUC (11 dígitos):");
        lblRuc.setBounds(250, 60, 110, 25);
        contentPane.add(lblRuc);

        txtRuc = new JTextField();
        txtRuc.setBounds(370, 60, 130, 25);
        txtRuc.setEnabled(false);
        contentPane.add(txtRuc);
        Validador.restringirSoloNumeros(txtRuc, 11);

        JLabel lblRazonSocial = new JLabel("Razón Social:");
        lblRazonSocial.setBounds(510, 60, 100, 25);
        contentPane.add(lblRazonSocial);

        txtRazonSocial = new JTextField();
        txtRazonSocial.setBounds(600, 60, 160, 25);
        txtRazonSocial.setEnabled(false);
        contentPane.add(txtRazonSocial);

        JButton btnGenerar = new JButton("Generar Comprobante (PDF)");
        btnGenerar.setBounds(20, 100, 220, 30);
        btnGenerar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generarComprobantePDF();
            }
        });
        contentPane.add(btnGenerar);

        JLabel lblGuardarDatos = new JLabel("(El RUC/Razón Social se guardan en el paciente para la próxima vez)");
        lblGuardarDatos.setBounds(250, 100, 400, 30);
        lblGuardarDatos.setFont(lblGuardarDatos.getFont().deriveFont(java.awt.Font.ITALIC, 10f));
        contentPane.add(lblGuardarDatos);

        JLabel lblHistorial = new JLabel("Historial de pagos por paciente:");
        lblHistorial.setBounds(20, 150, 250, 25);
        contentPane.add(lblHistorial);

        comboPacienteHistorial = new JComboBox<>();
        comboPacienteHistorial.setBounds(20, 180, 250, 25);
        contentPane.add(comboPacienteHistorial);

        JButton btnVerPagos = new JButton("Ver Pagos");
        btnVerPagos.setBounds(280, 180, 110, 25);
        btnVerPagos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verHistorialPagos();
            }
        });
        contentPane.add(btnVerPagos);

        modeloHistorial = new DefaultTableModel(new Object[]{"ID", "Tipo", "Fecha", "Monto", "IGV"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloHistorial);
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBounds(20, 220, 740, 280);
        contentPane.add(scrollHistorial);

        cargarCombos();
    }

    // Si la cita seleccionada corresponde a un paciente que ya tiene RUC/Razón Social
    // guardados de una factura anterior, los autocompleta para no volver a escribirlos
    private void autocompletarDatosFacturacion() {
        CitaMedica cita = (CitaMedica) comboCitas.getSelectedItem();
        if (cita == null || !"FACTURA".equals(comboTipo.getSelectedItem())) {
            return;
        }
        Paciente paciente = pacienteController.buscarPorId(cita.getPacienteId());
        if (paciente != null) {
            txtRuc.setText(paciente.getRuc() != null ? paciente.getRuc() : "");
            txtRazonSocial.setText(paciente.getRazonSocial() != null ? paciente.getRazonSocial() : "");
        }
    }

    private void generarComprobantePDF() {
        CitaMedica cita = (CitaMedica) comboCitas.getSelectedItem();
        if (cita == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay citas atendidas pendientes de cobro.\nSolo se pueden facturar citas con diagnóstico registrado.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Doble verificación (defensiva, por si la lista no se refrescó a tiempo)
        if (!CitaMedica.ATENDIDA.equals(cita.getEstado())) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden generar comprobantes de citas ATENDIDAS.",
                    "Operación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (comprobanteDAO.existeComprobantePorCita(cita.getId())) {
            JOptionPane.showMessageDialog(this,
                    "Esta cita ya tiene un comprobante de pago generado.",
                    "Operación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Validador.validarNumerico(txtMonto.getText(), "Monto", this)) return;

        double monto = Double.parseDouble(txtMonto.getText().trim());
        String tipo = (String) comboTipo.getSelectedItem();

        JFileChooser fileChooser = new JFileChooser();
        String nombreSugerido = "FACTURA".equals(tipo) ? "factura.pdf" : "boleta.pdf";
        fileChooser.setSelectedFile(new File(nombreSugerido));
        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String ruta = fileChooser.getSelectedFile().getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }

        try {
            boolean resultado;

            if ("FACTURA".equals(tipo)) {
                if (!Validador.validarCampoObligatorio(txtRuc.getText(), "RUC", this)) return;
                if (!Validador.validarRuc(txtRuc.getText().trim(), this)) return;
                if (!Validador.validarCampoObligatorio(txtRazonSocial.getText(), "Razón Social", this)) return;

                String ruc = txtRuc.getText().trim();
                String razonSocial = txtRazonSocial.getText().trim();

                Factura factura = new Factura(0, cita.getId(), monto, LocalDate.now(), ruc, razonSocial);
                resultado = comprobanteDAO.registrarFactura(factura);

                if (resultado) {
                    GeneradorPDF.generarFacturaPDF(factura, cita.getNombrePaciente(), ruta);
                    // Punto 10: guarda el RUC/Razón Social en el paciente para autocompletar la próxima vez
                    pacienteController.actualizarDatosFacturacion(cita.getPacienteId(), ruc, razonSocial);
                }
            } else {
                Boleta boleta = new Boleta(0, cita.getId(), monto, LocalDate.now());
                resultado = comprobanteDAO.registrarBoleta(boleta);

                if (resultado) {
                    GeneradorPDF.generarBoletaPDF(boleta, cita.getNombrePaciente(), ruta);
                }
            }

            if (resultado) {
                JOptionPane.showMessageDialog(this, "Comprobante generado correctamente en PDF.");
                Desktop.getDesktop().open(new File(ruta));
                cargarCombos(); // refresca: esta cita ya no aparecerá disponible para cobrar de nuevo

                // Si el paciente de la cita recién cobrada es el mismo que está seleccionado
                // en el historial, refresca la tabla automáticamente
                Paciente pacienteHistorialActual = (Paciente) comboPacienteHistorial.getSelectedItem();
                if (pacienteHistorialActual != null && pacienteHistorialActual.getId() == cita.getPacienteId()) {
                    verHistorialPagos();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el comprobante en la base de datos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verHistorialPagos() {
        Paciente paciente = (Paciente) comboPacienteHistorial.getSelectedItem();
        if (paciente == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloHistorial.setRowCount(0);
        List<ComprobantePago> lista = comprobanteDAO.listarPorPaciente(paciente.getId());
        for (ComprobantePago c : lista) {
            String igv = (c instanceof Factura) ? String.format("%.2f", ((Factura) c).getIgv()) : "-";
            modeloHistorial.addRow(new Object[]{
                    c.getId(), c.getTipo(), c.getFecha(), String.format("%.2f", c.getMonto()), igv
            });
        }
    }

    private void cargarCombos() {
        comboCitas.removeAllItems();
        for (CitaMedica c : citaController.listarCitas()) {
            // Solo citas ATENDIDAS y que todavía no tengan comprobante generado
            if (CitaMedica.ATENDIDA.equals(c.getEstado())
                    && !comprobanteDAO.existeComprobantePorCita(c.getId())) {
                comboCitas.addItem(c);
            }
        }
        comboPacienteHistorial.removeAllItems();
        for (Paciente p : pacienteController.listarPacientes()) {
            comboPacienteHistorial.addItem(p);
        }
    }
}