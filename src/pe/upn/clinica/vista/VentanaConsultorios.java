package pe.upn.clinica.vista;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.ConsultorioController;
import pe.upn.clinica.controlador.MedicoController;
import pe.upn.clinica.modelo.Consultorio;
import pe.upn.clinica.modelo.Medico;
import pe.upn.clinica.util.Validador;

public class VentanaConsultorios extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNumero;
    private JTextField txtPiso;
    private JComboBox<Medico> comboMedicos;
    private JTable tablaConsultorios;
    private DefaultTableModel modeloTabla;
    private JTable tablaDisponibilidad;
    private DefaultTableModel modeloDisponibilidad;

    private final ConsultorioController controller = new ConsultorioController();
    private final MedicoController medicoController = new MedicoController();
    private int idSeleccionado = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaConsultorios frame = new VentanaConsultorios();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaConsultorios() {
        setTitle("Gestión de Consultorios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNumero = new JLabel("Número:");
        lblNumero.setBounds(20, 20, 80, 25);
        contentPane.add(lblNumero);

        txtNumero = new JTextField();
        txtNumero.setBounds(100, 20, 100, 25);
        contentPane.add(txtNumero);

        JLabel lblPiso = new JLabel("Piso:");
        lblPiso.setBounds(220, 20, 60, 25);
        contentPane.add(lblPiso);

        txtPiso = new JTextField();
        txtPiso.setBounds(280, 20, 80, 25);
        contentPane.add(txtPiso);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 60, 110, 30);
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarConsultorio();
            }
        });
        contentPane.add(btnRegistrar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(140, 60, 110, 30);
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarConsultorio();
            }
        });
        contentPane.add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(260, 60, 110, 30);
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
        contentPane.add(btnLimpiar);

        JLabel lblAsignar = new JLabel("Asignar médico al consultorio seleccionado:");
        lblAsignar.setBounds(20, 105, 280, 25);
        contentPane.add(lblAsignar);

        comboMedicos = new JComboBox<>();
        comboMedicos.setBounds(20, 135, 250, 25);
        cargarComboMedicos();
        contentPane.add(comboMedicos);

        JButton btnAsignar = new JButton("Asignar Médico");
        btnAsignar.setBounds(280, 135, 150, 25);
        btnAsignar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                asignarMedico();
            }
        });
        contentPane.add(btnAsignar);

        JLabel lblListado = new JLabel("Listado de Consultorios:");
        lblListado.setBounds(20, 175, 200, 20);
        contentPane.add(lblListado);

        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Número", "Piso", "Disponible"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaConsultorios = new JTable(modeloTabla);
        tablaConsultorios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaConsultorios);
        scrollPane.setBounds(20, 200, 730, 150);
        contentPane.add(scrollPane);

        JLabel lblDisponibilidad = new JLabel("Disponibilidad (RF20):");
        lblDisponibilidad.setBounds(20, 365, 200, 20);
        contentPane.add(lblDisponibilidad);

        modeloDisponibilidad = new DefaultTableModel(new Object[]{"Número", "Piso", "Estado", "Médico Asignado"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDisponibilidad = new JTable(modeloDisponibilidad);
        JScrollPane scrollDisponibilidad = new JScrollPane(tablaDisponibilidad);
        scrollDisponibilidad.setBounds(20, 390, 730, 150);
        contentPane.add(scrollDisponibilidad);

        cargarTabla();
        cargarDisponibilidad();
    }

    private void registrarConsultorio() {
        if (!Validador.validarCampoObligatorio(txtNumero.getText(), "Número", this)) return;
        if (!Validador.validarNumerico(txtPiso.getText(), "Piso", this)) return;

        Consultorio c = new Consultorio();
        c.setNumero(txtNumero.getText().trim());
        c.setPiso(Integer.parseInt(txtPiso.getText().trim()));

        boolean resultado = controller.registrarConsultorio(c);
        if (resultado) {
            JOptionPane.showMessageDialog(this, "Consultorio registrado correctamente.");
            limpiarCampos();
            cargarTabla();
            cargarDisponibilidad();
        }
    }

    private void eliminarConsultorio() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un consultorio de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar este consultorio?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            String resultado = controller.eliminarConsultorio(idSeleccionado);
            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Consultorio eliminado.");
                limpiarCampos();
                cargarTabla();
                cargarDisponibilidad();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "No se puede eliminar", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void asignarMedico() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un consultorio de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Medico medico = (Medico) comboMedicos.getSelectedItem();
        if (medico == null) {
            JOptionPane.showMessageDialog(this, "No hay médicos registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String resultado = controller.asignarMedico(medico.getId(), idSeleccionado);
        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this, "Médico asignado correctamente al consultorio.");
            cargarTabla();
            cargarDisponibilidad();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo asignar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarComboMedicos() {
        comboMedicos.removeAllItems();
        List<Medico> medicos = medicoController.listarMedicos();
        for (Medico m : medicos) {
            comboMedicos.addItem(m);
        }
    }

    private void seleccionarFila() {
        int fila = tablaConsultorios.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            txtNumero.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtPiso.setText(modeloTabla.getValueAt(fila, 2).toString());
        }
    }

    private void limpiarCampos() {
        idSeleccionado = 0;
        txtNumero.setText("");
        txtPiso.setText("");
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Consultorio> lista = controller.listarConsultorios();
        for (Consultorio c : lista) {
            modeloTabla.addRow(new Object[]{
                    c.getId(), c.getNumero(), c.getPiso(), c.isDisponible() ? "Sí" : "No"
            });
        }
    }

    private void cargarDisponibilidad() {
        modeloDisponibilidad.setRowCount(0);
        List<String[]> lista = controller.listarDisponibilidad();
        for (String[] fila : lista) {
            modeloDisponibilidad.addRow(fila);
        }
    }
}