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
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.MedicoController;
import pe.upn.clinica.modelo.Medico;
import pe.upn.clinica.util.Validador;

public class VentanaMedicos extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtCmp;
    private JTextField txtEspecialidad;
    private JTextField txtFiltroEspecialidad;
    private JTable tablaMedicos;
    private DefaultTableModel modeloTabla;

    private final MedicoController controller = new MedicoController();
    private int idSeleccionado = 0;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaMedicos frame = new VentanaMedicos();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaMedicos() {
        setTitle("Gestión de Médicos");
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
        txtNombre.setBounds(340, 20, 130, 25);
        contentPane.add(txtNombre);
        Validador.restringirSoloLetras(txtNombre, 40);

        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(480, 20, 80, 25);
        contentPane.add(lblApellido);

        txtApellido = new JTextField();
        txtApellido.setBounds(550, 20, 150, 25);
        contentPane.add(txtApellido);
        Validador.restringirSoloLetras(txtApellido, 40);

        JLabel lblTelefono = new JLabel("Teléfono (9 dígitos):");
        lblTelefono.setBounds(20, 60, 130, 25);
        contentPane.add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(160, 60, 110, 25);
        contentPane.add(txtTelefono);
        Validador.restringirSoloNumeros(txtTelefono, 9);

        JLabel lblCmp = new JLabel("CMP (6 dígitos):");
        lblCmp.setBounds(290, 60, 100, 25);
        contentPane.add(lblCmp);

        txtCmp = new JTextField();
        txtCmp.setBounds(390, 60, 100, 25);
        contentPane.add(txtCmp);
        Validador.restringirSoloNumeros(txtCmp, 6);

        JLabel lblEspecialidad = new JLabel("Especialidad:");
        lblEspecialidad.setBounds(500, 60, 90, 25);
        contentPane.add(lblEspecialidad);

        txtEspecialidad = new JTextField();
        txtEspecialidad.setBounds(590, 60, 130, 25);
        contentPane.add(txtEspecialidad);
        Validador.restringirSoloLetras(txtEspecialidad, 40);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 100, 110, 30);
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarMedico();
            }
        });
        contentPane.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(140, 100, 110, 30);
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarMedico();
            }
        });
        contentPane.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(260, 100, 110, 30);
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarMedico();
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

        JLabel lblFiltro = new JLabel("Filtrar por especialidad:");
        lblFiltro.setBounds(20, 145, 160, 25);
        contentPane.add(lblFiltro);

        txtFiltroEspecialidad = new JTextField();
        txtFiltroEspecialidad.setBounds(190, 145, 200, 25);
        contentPane.add(txtFiltroEspecialidad);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBounds(400, 145, 100, 25);
        btnFiltrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarTabla(controller.filtrarPorEspecialidad(txtFiltroEspecialidad.getText().trim()));
            }
        });
        contentPane.add(btnFiltrar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "CMP", "Nombre", "Apellido", "Especialidad", "Teléfono"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMedicos = new JTable(modeloTabla);
        tablaMedicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarFila();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaMedicos);
        scrollPane.setBounds(20, 185, 700, 300);
        contentPane.add(scrollPane);

        cargarTabla(controller.listarMedicos());
    }

    private void registrarMedico() {
        if (!Validador.validarCampoObligatorio(txtDni.getText(), "DNI", this)) return;
        if (!Validador.validarDni(txtDni.getText().trim(), this)) return;
        if (!Validador.validarCampoObligatorio(txtNombre.getText(), "Nombre", this)) return;
        if (!Validador.validarCampoObligatorio(txtCmp.getText(), "CMP", this)) return;
        if (!Validador.validarCampoObligatorio(txtEspecialidad.getText(), "Especialidad", this)) return;
        if (!Validador.validarTelefono(txtTelefono.getText().trim(), this)) return;

        Medico m = new Medico();
        m.setDni(txtDni.getText().trim());
        m.setNombre(txtNombre.getText().trim());
        m.setApellido(txtApellido.getText().trim());
        m.setTelefono(txtTelefono.getText().trim());
        m.setCmp(txtCmp.getText().trim());
        m.setEspecialidad(txtEspecialidad.getText().trim());

        boolean resultado = controller.registrarMedico(m);
        if (resultado) {
            JOptionPane.showMessageDialog(this, "Médico registrado correctamente.");
            limpiarCampos();
            cargarTabla(controller.listarMedicos());
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar. Verifique que el CMP no esté duplicado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarMedico() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Validador.validarTelefono(txtTelefono.getText().trim(), this)) return;

        Medico m = new Medico();
        m.setId(idSeleccionado);
        m.setDni(txtDni.getText().trim());
        m.setNombre(txtNombre.getText().trim());
        m.setApellido(txtApellido.getText().trim());
        m.setTelefono(txtTelefono.getText().trim());
        m.setCmp(txtCmp.getText().trim());
        m.setEspecialidad(txtEspecialidad.getText().trim());

        boolean resultado = controller.editarMedico(m);
        if (resultado) {
            JOptionPane.showMessageDialog(this, "Médico actualizado correctamente.");
            limpiarCampos();
            cargarTabla(controller.listarMedicos());
        }
    }

    private void eliminarMedico() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (controller.tieneCitasPendientes(idSeleccionado)) {
            int continuar = JOptionPane.showConfirmDialog(this,
                    "Este médico tiene citas pendientes. ¿Desea eliminarlo de todas formas?",
                    "Advertencia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (continuar != JOptionPane.YES_OPTION) {
                return;
            }
        }
        boolean resultado = controller.eliminarMedico(idSeleccionado);
        if (resultado) {
            JOptionPane.showMessageDialog(this, "Médico eliminado.");
            limpiarCampos();
            cargarTabla(controller.listarMedicos());
        }
    }

    private void seleccionarFila() {
        int fila = tablaMedicos.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            txtCmp.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtApellido.setText(modeloTabla.getValueAt(fila, 3).toString());
            txtEspecialidad.setText(modeloTabla.getValueAt(fila, 4).toString());
            txtTelefono.setText(modeloTabla.getValueAt(fila, 5).toString());
        }
    }

    private void limpiarCampos() {
        idSeleccionado = 0;
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtCmp.setText("");
        txtEspecialidad.setText("");
    }

    private void cargarTabla(List<Medico> medicos) {
        modeloTabla.setRowCount(0);
        for (Medico m : medicos) {
            modeloTabla.addRow(new Object[]{
                    m.getId(), m.getCmp(), m.getNombre(), m.getApellido(),
                    m.getEspecialidad(), m.getTelefono()
            });
        }
    }
}