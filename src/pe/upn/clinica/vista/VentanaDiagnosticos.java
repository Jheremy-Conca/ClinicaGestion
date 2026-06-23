package pe.upn.clinica.vista;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.CitaController;
import pe.upn.clinica.controlador.DiagnosticoController;
import pe.upn.clinica.controlador.PacienteController;
import pe.upn.clinica.modelo.CitaMedica;
import pe.upn.clinica.modelo.Diagnostico;
import pe.upn.clinica.modelo.Paciente;
import pe.upn.clinica.util.Validador;

public class VentanaDiagnosticos extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<CitaMedica> comboCitasPendientes;
    private JTextArea txtAreaDescripcion;
    private JComboBox<Paciente> comboPacienteHistorial;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private final DiagnosticoController diagnosticoController = new DiagnosticoController();
    private final CitaController citaController = new CitaController();
    private final PacienteController pacienteController = new PacienteController();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaDiagnosticos frame = new VentanaDiagnosticos();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaDiagnosticos() {
        setTitle("Registro de Diagnósticos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 750, 580);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblCita = new JLabel("Cita pendiente a atender:");
        lblCita.setBounds(20, 20, 180, 25);
        contentPane.add(lblCita);

        comboCitasPendientes = new JComboBox<>();
        comboCitasPendientes.setBounds(200, 20, 480, 25);
        contentPane.add(comboCitasPendientes);

        JLabel lblDescripcion = new JLabel("Diagnóstico:");
        lblDescripcion.setBounds(20, 60, 100, 25);
        contentPane.add(lblDescripcion);

        txtAreaDescripcion = new JTextArea();
        txtAreaDescripcion.setLineWrap(true);
        txtAreaDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtAreaDescripcion);
        scrollDescripcion.setBounds(20, 90, 660, 120);
        contentPane.add(scrollDescripcion);

        JButton btnGuardar = new JButton("Guardar Diagnóstico y Marcar Cita como Atendida");
        btnGuardar.setBounds(20, 225, 380, 30);
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarDiagnostico();
            }
        });
        contentPane.add(btnGuardar);

        JLabel lblHistorial = new JLabel("Historial de diagnósticos por paciente:");
        lblHistorial.setBounds(20, 270, 260, 25);
        contentPane.add(lblHistorial);

        comboPacienteHistorial = new JComboBox<>();
        comboPacienteHistorial.setBounds(20, 300, 250, 25);
        contentPane.add(comboPacienteHistorial);

        JButton btnVerHistorial = new JButton("Ver Historial");
        btnVerHistorial.setBounds(280, 300, 120, 25);
        btnVerHistorial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verHistorial();
            }
        });
        contentPane.add(btnVerHistorial);

        modeloHistorial = new DefaultTableModel(new Object[]{"ID", "Fecha", "Descripción"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloHistorial);
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBounds(20, 335, 660, 190);
        contentPane.add(scrollHistorial);

        cargarCombos();
    }

    private void guardarDiagnostico() {
        CitaMedica cita = (CitaMedica) comboCitasPendientes.getSelectedItem();
        if (cita == null) {
            JOptionPane.showMessageDialog(this, "No hay citas pendientes para diagnosticar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Validador.validarCampoObligatorio(txtAreaDescripcion.getText(), "Diagnóstico", this)) return;

        Diagnostico d = new Diagnostico();
        d.setCitaId(cita.getId());
        d.setDescripcion(txtAreaDescripcion.getText().trim());
        d.setFechaDiagnostico(LocalDate.now());

        String resultado = diagnosticoController.registrarDiagnostico(d);
        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this,
                    "Diagnóstico guardado. La cita de " + cita.getNombrePaciente() + " ahora está ATENDIDA.");
            txtAreaDescripcion.setText("");
            cargarCombos();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verHistorial() {
        Paciente paciente = (Paciente) comboPacienteHistorial.getSelectedItem();
        if (paciente == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloHistorial.setRowCount(0);
        List<Diagnostico> lista = diagnosticoController.historialPorPaciente(paciente.getId());
        for (Diagnostico d : lista) {
            modeloHistorial.addRow(new Object[]{d.getId(), d.getFechaDiagnostico(), d.getDescripcion()});
        }
    }

    private void cargarCombos() {
        comboCitasPendientes.removeAllItems();
        List<CitaMedica> todas = citaController.listarCitas();
        for (CitaMedica c : todas) {
            if (CitaMedica.PENDIENTE.equals(c.getEstado())) {
                comboCitasPendientes.addItem(c);
            }
        }
        comboPacienteHistorial.removeAllItems();
        for (Paciente p : pacienteController.listarPacientes()) {
            comboPacienteHistorial.addItem(p);
        }
    }
}