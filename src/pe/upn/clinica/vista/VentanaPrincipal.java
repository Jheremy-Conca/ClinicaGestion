package pe.upn.clinica.vista;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    // Paleta de marca (coherente con GeneradorPDF: mismo azul institucional)
    private static final Color COLOR_PRIMARIO = new Color(23, 92, 158);
    private static final Color COLOR_FONDO_HEADER = new Color(23, 92, 158);
    private static final Color COLOR_TEXTO_CLARO = Color.WHITE;
    private static final Color COLOR_GRIS_ETIQUETA = new Color(110, 110, 110);
    private static final Color COLOR_FONDO_VENTANA = new Color(245, 247, 250);

    private JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaPrincipal frame = new VentanaPrincipal();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public VentanaPrincipal() {
        setTitle("Sistema de Gestión Clínica Ambulatoria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 620, 560);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBackground(COLOR_FONDO_VENTANA);
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        construirEncabezado();
        construirSeccionPersonas();
        construirSeccionOperacion();
        construirSeccionAdministracion();
        construirPiePagina();
    }

    // ===================== ENCABEZADO =====================
    private void construirEncabezado() {
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(COLOR_FONDO_HEADER);
        panelHeader.setBounds(0, 0, 620, 80);
        panelHeader.setLayout(null);
        contentPane.add(panelHeader);

        JLabel lblTitulo = new JLabel("CLÍNICA AMBULATORIA");
        lblTitulo.setForeground(COLOR_TEXTO_CLARO);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 14, 620, 28);
        panelHeader.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión Ambulatoria — Menú Principal");
        lblSubtitulo.setForeground(new Color(210, 226, 245));
        lblSubtitulo.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setBounds(0, 44, 620, 18);
        panelHeader.add(lblSubtitulo);
    }

    // ===================== SECCIÓN 1: GESTIÓN DE PERSONAS =====================
    private void construirSeccionPersonas() {
        agregarEtiquetaSeccion("REGISTROS", 30, 105);

        JButton btnPacientes = crearBotonMenu("Pacientes", 30, 130, 270, 60);
        btnPacientes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaPacientes().setVisible(true);
            }
        });
        contentPane.add(btnPacientes);

        JButton btnMedicos = crearBotonMenu("Médicos", 320, 130, 270, 60);
        btnMedicos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaMedicos().setVisible(true);
            }
        });
        contentPane.add(btnMedicos);

        JButton btnConsultorios = crearBotonMenu("Consultorios", 30, 200, 560, 50);
        btnConsultorios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaConsultorios().setVisible(true);
            }
        });
        contentPane.add(btnConsultorios);
    }

    // ===================== SECCIÓN 2: OPERACIÓN CLÍNICA (flujo natural: citar -> atender -> cobrar) =====================
    private void construirSeccionOperacion() {
        agregarEtiquetaSeccion("ATENCIÓN MÉDICA", 30, 270);

        JButton btnCitas = crearBotonPaso("1. Citas Médicas", "Agendar y consultar", 30, 295, 270, 65);
        btnCitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaCitas().setVisible(true);
            }
        });
        contentPane.add(btnCitas);

        JButton btnDiagnosticos = crearBotonPaso("2. Diagnósticos", "Atender citas pendientes", 320, 295, 270, 65);
        btnDiagnosticos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaDiagnosticos().setVisible(true);
            }
        });
        contentPane.add(btnDiagnosticos);

        JButton btnComprobantes = crearBotonPaso("3. Comprobantes de Pago", "Generar boleta o factura", 30, 365, 560, 55);
        btnComprobantes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaComprobantes().setVisible(true);
            }
        });
        contentPane.add(btnComprobantes);
    }

    // ===================== SECCIÓN 3: ADMINISTRACIÓN =====================
    private void construirSeccionAdministracion() {
        agregarEtiquetaSeccion("REPORTES Y ARCHIVOS", 30, 435);

        JButton btnArchivos = crearBotonMenu("Manejo de Archivos (PDF / Log)", 30, 460, 560, 45);
        btnArchivos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaArchivos().setVisible(true);
            }
        });
        contentPane.add(btnArchivos);
    }

    // ===================== PIE DE PÁGINA =====================
    private void construirPiePagina() {
        JSeparator separador = new JSeparator();
        separador.setBounds(30, 515, 560, 2);
        contentPane.add(separador);

        JLabel lblFooter = new JLabel("Clínica Ambulatoria · Sistema de Gestión v1.0");
        lblFooter.setForeground(COLOR_GRIS_ETIQUETA);
        lblFooter.setFont(new Font("Tahoma", Font.PLAIN, 10));
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBounds(30, 525, 560, 18);
        contentPane.add(lblFooter);
    }

    // ===================== COMPONENTES REUTILIZABLES =====================

    private void agregarEtiquetaSeccion(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
        lbl.setForeground(COLOR_GRIS_ETIQUETA);
        lbl.setBounds(x, y, 400, 16);
        contentPane.add(lbl);
    }

    // Botón simple de una sola línea, para acciones administrativas
    private JButton crearBotonMenu(String texto, int x, int y, int ancho, int alto) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, ancho, alto);
        boton.setFont(new Font("Tahoma", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setBackground(Color.WHITE);
        boton.setForeground(COLOR_PRIMARIO);
        return boton;
    }

    // Botón de dos líneas (título + descripción), para resaltar el flujo de atención paso a paso
    private JButton crearBotonPaso(String titulo, String descripcion, int x, int y, int ancho, int alto) {
        JButton boton = new JButton("<html><div style='text-align:center;'>"
                + "<span style='font-size:12px; font-weight:bold;'>" + titulo + "</span><br>"
                + "<span style='font-size:10px; font-weight:normal;'>" + descripcion + "</span>"
                + "</div></html>");
        boton.setBounds(x, y, ancho, alto);
        boton.setFocusPainted(false);
        boton.setBackground(COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        return boton;
    }
}