package pe.upn.clinica.vista;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.CitaController;
import pe.upn.clinica.controlador.ConsultorioController;
import pe.upn.clinica.controlador.MedicoController;
import pe.upn.clinica.controlador.PacienteController;
import pe.upn.clinica.modelo.CitaMedica;
import pe.upn.clinica.modelo.Consultorio;
import pe.upn.clinica.modelo.Medico;
import pe.upn.clinica.modelo.Paciente;
import pe.upn.clinica.util.Validador;

public class VentanaCitas extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<Paciente> comboPacientes;
    private JComboBox<Medico> comboMedicos;
    private JComboBox<Consultorio> comboConsultorios;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JComboBox<String> comboEstadoFiltro;
    private JTextField txtBuscar;
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;

    private final CitaController citaController = new CitaController();
    private final PacienteController pacienteController = new PacienteController();
    private final MedicoController medicoController = new MedicoController();
    private final ConsultorioController consultorioController = new ConsultorioController();
    private int idSeleccionado = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaCitas frame = new VentanaCitas();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaCitas() {
        setTitle("Citas Médicas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblPaciente = new JLabel("Paciente:");
        lblPaciente.setBounds(20, 20, 80, 25);
        contentPane.add(lblPaciente);

        comboPacientes = new JComboBox<>();
        comboPacientes.setBounds(100, 20, 180, 25);
        contentPane.add(comboPacientes);

        JLabel lblMedico = new JLabel("Médico:");
        lblMedico.setBounds(300, 20, 80, 25);
        contentPane.add(lblMedico);

        comboMedicos = new JComboBox<>();
        comboMedicos.setBounds(360, 20, 180, 25);
        contentPane.add(comboMedicos);

        JLabel lblConsultorio = new JLabel("Consultorio:");
        lblConsultorio.setBounds(560, 20, 90, 25);
        contentPane.add(lblConsultorio);

        comboConsultorios = new JComboBox<>();
        comboConsultorios.setBounds(650, 20, 150, 25);
        contentPane.add(comboConsultorios);
        javax.swing.ListCellRenderer<Object> rendererSeleccion = new javax.swing.DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Seleccione --");
                }
                return this;
            }
        };
        comboPacientes.setRenderer(rendererSeleccion);
        comboMedicos.setRenderer(rendererSeleccion);
        comboConsultorios.setRenderer(rendererSeleccion);
        JLabel lblFecha = new JLabel("Fecha (yyyy-mm-dd):");
        lblFecha.setBounds(20, 60, 140, 25);
        contentPane.add(lblFecha);

        txtFecha = new JTextField();
        txtFecha.setBounds(160, 60, 120, 25);
        contentPane.add(txtFecha);

        JLabel lblHora = new JLabel("Hora (HH:mm):");
        lblHora.setBounds(300, 60, 100, 25);
        contentPane.add(lblHora);

        txtHora = new JTextField();
        txtHora.setBounds(400, 60, 100, 25);
        contentPane.add(txtHora);

        JButton btnRegistrar = new JButton("Registrar Cita");
        btnRegistrar.setBounds(20, 100, 130, 30);
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarCita();
            }
        });
        contentPane.add(btnRegistrar);

        JButton btnCancelar = new JButton("Cancelar Cita");
        btnCancelar.setBounds(160, 100, 130, 30);
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelarCita();
            }
        });
        contentPane.add(btnCancelar);

        JButton btnHoy = new JButton("Citas del Día");
        btnHoy.setBounds(300, 100, 130, 30);
        btnHoy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(citaController.listarCitasDelDia());
            }
        });
        contentPane.add(btnHoy);

        JButton btnTodas = new JButton("Ver Todas");
        btnTodas.setBounds(440, 100, 130, 30);
        btnTodas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(citaController.listarCitas());
            }
        });
        contentPane.add(btnTodas);

        JButton btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setBounds(580, 100, 130, 30);
        btnExportarPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportarCitasPDF();
            }
        });
        contentPane.add(btnExportarPDF);

        JLabel lblBuscar = new JLabel("Buscar (fecha o paciente):");
        lblBuscar.setBounds(20, 145, 170, 25);
        contentPane.add(lblBuscar);

        txtBuscar = new JTextField();
        txtBuscar.setBounds(190, 145, 180, 25);
        contentPane.add(txtBuscar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(380, 145, 90, 25);
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(citaController.buscarCitas(txtBuscar.getText().trim()));
            }
        });
        contentPane.add(btnBuscar);

        JLabel lblEstado = new JLabel("Filtrar por estado:");
        lblEstado.setBounds(500, 145, 120, 25);
        contentPane.add(lblEstado);

        comboEstadoFiltro = new JComboBox<>(new String[]{"PENDIENTE", "ATENDIDA", "CANCELADA"});
        comboEstadoFiltro.setBounds(630, 145, 130, 25);
        contentPane.add(comboEstadoFiltro);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBounds(770, 145, 90, 25);
        btnFiltrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String estado = (String) comboEstadoFiltro.getSelectedItem();
                cargarTabla(citaController.filtrarPorEstado(estado));
            }
        });
        contentPane.add(btnFiltrar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Fecha", "Hora", "Paciente", "Médico", "Consultorio", "Estado"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCitas = new JTable(modeloTabla);
        tablaCitas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        scrollPane.setBounds(20, 185, 840, 350);
        contentPane.add(scrollPane);

        cargarCombos();
        cargarTabla(citaController.listarCitas());
    }

    private void registrarCita() {
        if (!Validador.validarCampoObligatorio(txtFecha.getText(), "Fecha", this)) return;
        if (!Validador.validarCampoObligatorio(txtHora.getText(), "Hora", this)) return;

        Paciente paciente = (Paciente) comboPacientes.getSelectedItem();
        Medico medico = (Medico) comboMedicos.getSelectedItem();
        Consultorio consultorio = (Consultorio) comboConsultorios.getSelectedItem();

        if (paciente == null || medico == null || consultorio == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar paciente, médico y consultorio.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CitaMedica cita = new CitaMedica();
            cita.setPacienteId(paciente.getId());
            cita.setMedicoId(medico.getId());
            cita.setConsultorioId(consultorio.getId());
            cita.setFecha(LocalDate.parse(txtFecha.getText().trim()));
            cita.setHora(LocalTime.parse(txtHora.getText().trim()));
            cita.setEstado(CitaMedica.PENDIENTE);

            String resultado = citaController.registrarCita(cita);
            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Cita registrada correctamente.");
                limpiarCampos();
                cargarTabla(citaController.listarCitas());
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Conflicto de horario", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha/hora inválido. Use yyyy-mm-dd y HH:mm.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarCita() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Cancelar esta cita?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            String resultado = citaController.cancelarCita(idSeleccionado);
            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Cita cancelada.");
                cargarTabla(citaController.listarCitas());
            } else {
                JOptionPane.showMessageDialog(this, resultado, "No se puede cancelar", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void exportarCitasPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("historial_citas.pdf"));
        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            try {
                String ruta = fileChooser.getSelectedFile().getAbsolutePath();
                if (!ruta.toLowerCase().endsWith(".pdf")) {
                    ruta += ".pdf";
                }
                citaController.exportarCitasPDF(ruta);
                JOptionPane.showMessageDialog(this, "PDF generado correctamente.");
                Desktop.getDesktop().open(new File(ruta));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarCombos() {
        comboPacientes.removeAllItems();
        comboPacientes.addItem(null); // fuerza selección explícita
        for (Paciente p : pacienteController.listarPacientes()) {
            comboPacientes.addItem(p);
        }
        comboMedicos.removeAllItems();
        comboMedicos.addItem(null);
        for (Medico m : medicoController.listarMedicos()) {
            comboMedicos.addItem(m);
        }
        comboConsultorios.removeAllItems();
        comboConsultorios.addItem(null);
        for (Consultorio c : consultorioController.listarConsultorios()) {
            comboConsultorios.addItem(c);
        }
    }
    private void seleccionarFila() {
        int fila = tablaCitas.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        }
    }

    private void limpiarCampos() {
        idSeleccionado = 0;
        txtFecha.setText("");
        txtHora.setText("");
    }

    private void cargarTabla(List<CitaMedica> citas) {
        modeloTabla.setRowCount(0);
        for (CitaMedica c : citas) {
            modeloTabla.addRow(new Object[]{
                    c.getId(), c.getFecha(), c.getHora(), c.getNombrePaciente(),
                    c.getNombreMedico(), c.getNumeroConsultorio(), c.getEstado()
            });
        }
    }
}