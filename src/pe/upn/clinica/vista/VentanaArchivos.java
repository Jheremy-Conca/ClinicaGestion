package pe.upn.clinica.vista;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;

import pe.upn.clinica.controlador.CitaController;
import pe.upn.clinica.controlador.PacienteController;

public class VentanaArchivos extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea txtAreaLog;

    private final PacienteController pacienteController = new PacienteController();
    private final CitaController citaController = new CitaController();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaArchivos frame = new VentanaArchivos();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaArchivos() {
        setTitle("Manejo de Archivos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 700, 550);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Generación de Reportes en PDF y Registro de Operaciones");
        lblTitulo.setBounds(20, 10, 450, 25);
        contentPane.add(lblTitulo);

        JButton btnExportarPac = new JButton("Exportar Pacientes a PDF");
        btnExportarPac.setBounds(20, 50, 220, 35);
        btnExportarPac.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportarPacientesPDF();
            }
        });
        contentPane.add(btnExportarPac);

        JButton btnExportarCitas = new JButton("Exportar Citas a PDF");
        btnExportarCitas.setBounds(260, 50, 220, 35);
        btnExportarCitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportarCitasPDF();
            }
        });
        contentPane.add(btnExportarCitas);

        JButton btnVerLog = new JButton("Ver Log de Operaciones");
        btnVerLog.setBounds(20, 100, 220, 35);
        btnVerLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verLog();
            }
        });
        contentPane.add(btnVerLog);

        JLabel lblLog = new JLabel("Contenido del log.txt:");
        lblLog.setBounds(20, 150, 200, 20);
        contentPane.add(lblLog);

        txtAreaLog = new JTextArea();
        txtAreaLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        scrollPane.setBounds(20, 175, 640, 330);
        contentPane.add(scrollPane);
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
                pacienteController.exportarPacientesPDF(ruta);
                JOptionPane.showMessageDialog(this, "PDF generado correctamente.");
                Desktop.getDesktop().open(new File(ruta));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
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

    private void verLog() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("log.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
            txtAreaLog.setText(sb.toString());
        } catch (IOException ex) {
            txtAreaLog.setText("No se encontró el archivo log.txt todavía. Realice alguna operación primero.");
        }
    }
}