package pe.upn.clinica.vista;

import java.awt.EventQueue;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.PacienteController;
import pe.upn.clinica.modelo.Paciente;
import pe.upn.clinica.util.Validador;

public class VentanaPacientes extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtFechaNacimiento;
    private JTextField txtBuscar;
    private JTable tablaPacientes;
    private DefaultTableModel modeloTabla;

    private final PacienteController controller = new PacienteController();
    private int idSeleccionado = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaPacientes frame = new VentanaPacientes();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaPacientes() {
        setTitle("Gestión de Pacientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 750, 550);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblDni = new JLabel("DNI (8 dígitos):");
        lblDni.setBounds(20, 20, 110, 25);
        contentPane.add(lblDni);

        txtDni = new JTextField();
        txtDni.setBounds(140, 20, 110, 25);
        contentPane.add(txtDni);
        Validador.restringirSoloNumeros(txtDni, 8);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(270, 20, 80, 25);
        contentPane.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(340, 20, 150, 25);
        contentPane.add(txtNombre);
        Validador.restringirSoloLetras(txtNombre, 40);

        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(510, 20, 80, 25);
        contentPane.add(lblApellido);

        txtApellido = new JTextField();
        txtApellido.setBounds(580, 20, 140, 25);
        contentPane.add(txtApellido);
        Validador.restringirSoloLetras(txtApellido, 40);

        JLabel lblTelefono = new JLabel("Teléfono (9 dígitos):");
        lblTelefono.setBounds(20, 60, 130, 25);
        contentPane.add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(160, 60, 110, 25);
        contentPane.add(txtTelefono);
        Validador.restringirSoloNumeros(txtTelefono, 9);

        JLabel lblFecha = new JLabel("F. Nacimiento (yyyy-mm-dd):");
        lblFecha.setBounds(300, 60, 200, 25);
        contentPane.add(lblFecha);

        txtFechaNacimiento = new JTextField();
        txtFechaNacimiento.setBounds(500, 60, 120, 25);
        contentPane.add(txtFechaNacimiento);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 100, 110, 30);
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarPaciente();
            }
        });
        contentPane.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(140, 100, 110, 30);
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarPaciente();
            }
        });
        contentPane.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(260, 100, 110, 30);
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarPaciente();
            }
        });
        contentPane.add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(380, 100, 110, 30);
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
        contentPane.add(btnLimpiar);

        JButton btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setBounds(500, 100, 130, 30);
        btnExportarPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportarPacientesPDF();
            }
        });
        contentPane.add(btnExportarPDF);

        JLabel lblBuscar = new JLabel("Buscar (nombre o DNI):");
        lblBuscar.setBounds(20, 145, 150, 25);
        contentPane.add(lblBuscar);

        txtBuscar = new JTextField();
        txtBuscar.setBounds(180, 145, 250, 25);
        txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String criterio = txtBuscar.getText().trim();
                if (criterio.length() >= 3 || criterio.isEmpty()) {
                    cargarTabla(controller.buscarPacientes(criterio));
                }
            }
        });
        contentPane.add(txtBuscar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "DNI", "Nombre", "Apellido", "Teléfono", "F. Nacimiento"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPacientes = new JTable(modeloTabla);
        tablaPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaPacientes);
        scrollPane.setBounds(20, 185, 700, 300);
        contentPane.add(scrollPane);

        cargarTabla(controller.listarPacientes());
    }

    private void registrarPaciente() {
        if (!Validador.validarCampoObligatorio(txtDni.getText(), "DNI", this)) return;
        if (!Validador.validarDni(txtDni.getText().trim(), this)) return;
        if (!Validador.validarCampoObligatorio(txtNombre.getText(), "Nombre", this)) return;
        if (!Validador.validarCampoObligatorio(txtApellido.getText(), "Apellido", this)) return;
        if (!Validador.validarTelefono(txtTelefono.getText().trim(), this)) return;

        try {
            Paciente p = new Paciente();
            p.setDni(txtDni.getText().trim());
            p.setNombre(txtNombre.getText().trim());
            p.setApellido(txtApellido.getText().trim());
            p.setTelefono(txtTelefono.getText().trim());
            if (!txtFechaNacimiento.getText().trim().isEmpty()) {
                p.setFechaNacimiento(LocalDate.parse(txtFechaNacimiento.getText().trim()));
            }

            boolean resultado = controller.registrarPaciente(p);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Paciente registrado correctamente.");
                limpiarCampos();
                cargarTabla(controller.listarPacientes());
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el paciente. Verifique que el DNI no esté duplicado.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-mm-dd.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarPaciente() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Validador.validarCampoObligatorio(txtNombre.getText(), "Nombre", this)) return;
        if (!Validador.validarTelefono(txtTelefono.getText().trim(), this)) return;

        try {
            Paciente p = new Paciente();
            p.setId(idSeleccionado);
            p.setDni(txtDni.getText().trim());
            p.setNombre(txtNombre.getText().trim());
            p.setApellido(txtApellido.getText().trim());
            p.setTelefono(txtTelefono.getText().trim());
            if (!txtFechaNacimiento.getText().trim().isEmpty()) {
                p.setFechaNacimiento(LocalDate.parse(txtFechaNacimiento.getText().trim()));
            }

            boolean resultado = controller.editarPaciente(p);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Paciente actualizado correctamente.");
                limpiarCampos();
                cargarTabla(controller.listarPacientes());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-mm-dd.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPaciente() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este paciente?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            String resultado = controller.eliminarPaciente(idSeleccionado);
            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Paciente eliminado.");
                limpiarCampos();
                cargarTabla(controller.listarPacientes());
            } else {
                JOptionPane.showMessageDialog(this, resultado, "No se puede eliminar", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void exportarPacientesPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("listado_pacientes.pdf"));
        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            try {
                String ruta = fileChooser.getSelectedFile().getAbsolutePath();
                if (!ruta.toLowerCase().endsWith(".pdf")) {
                    ruta += ".pdf";
                }
                controller.exportarPacientesPDF(ruta);
                JOptionPane.showMessageDialog(this, "PDF generado correctamente.");
                abrirArchivo(ruta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Abre el PDF generado con el visor predeterminado del sistema operativo
    private void abrirArchivo(String ruta) {
        try {
            Desktop.getDesktop().open(new File(ruta));
        } catch (Exception ignored) {
            // Si no se puede abrir automáticamente, el usuario lo abre manualmente
        }
    }

    private void seleccionarFila() {
        int fila = tablaPacientes.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            txtDni.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtApellido.setText(modeloTabla.getValueAt(fila, 3).toString());
            txtTelefono.setText(modeloTabla.getValueAt(fila, 4).toString());
            Object fecha = modeloTabla.getValueAt(fila, 5);
            txtFechaNacimiento.setText(fecha != null ? fecha.toString() : "");
        }
    }

    private void limpiarCampos() {
        idSeleccionado = 0;
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtFechaNacimiento.setText("");
    }

    private void cargarTabla(List<Paciente> pacientes) {
        modeloTabla.setRowCount(0);
        for (Paciente p : pacientes) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getDni(), p.getNombre(), p.getApellido(),
                    p.getTelefono(), p.getFechaNacimiento()
            });
        }
    }
}