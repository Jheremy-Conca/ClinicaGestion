package pe.upn.clinica.util;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class Validador {

    // ===================== VALIDACIONES DE FORMULARIO =====================

    public static boolean validarCampoObligatorio(String valor, String nombreCampo, java.awt.Component padre) {
        if (valor == null || valor.trim().isEmpty()) {
            JOptionPane.showMessageDialog(padre,
                    "El campo \"" + nombreCampo + "\" es obligatorio.",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // RF: el DNI debe tener exactamente 8 dígitos numéricos
    public static boolean validarDni(String dni, java.awt.Component padre) {
        if (dni == null || !dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(padre,
                    "El DNI debe contener exactamente 8 dígitos numéricos.",
                    "DNI inválido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // RF: el teléfono debe tener exactamente 9 dígitos numéricos
    public static boolean validarTelefono(String telefono, java.awt.Component padre) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return true; // teléfono es opcional; si se llena, debe cumplir el formato
        }
        if (!telefono.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(padre,
                    "El teléfono debe contener exactamente 9 dígitos numéricos.",
                    "Teléfono inválido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // RF: el RUC debe tener exactamente 11 dígitos numéricos
    public static boolean validarRuc(String ruc, java.awt.Component padre) {
        if (ruc == null || !ruc.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(padre,
                    "El RUC debe contener exactamente 11 dígitos numéricos.",
                    "RUC inválido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validarNumerico(String valor, String nombreCampo, java.awt.Component padre) {
        try {
            double v = Double.parseDouble(valor);
            if (v <= 0) {
                JOptionPane.showMessageDialog(padre,
                        "El campo \"" + nombreCampo + "\" debe ser mayor a cero.",
                        "Valor inválido", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(padre,
                    "El campo \"" + nombreCampo + "\" debe ser numérico.",
                    "Valor inválido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    // ===================== FORMATO DE TEXTO =====================

    // Convierte "juAn cARLOS" -> "Juan Carlos" (evita mezclar mayúsculas/minúsculas)
    public static String capitalizarNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }
        return resultado.toString().trim();
    }

    // ===================== RESTRICCIÓN DE TECLADO EN VIVO (JTextField) =====================

    // Limita un JTextField a solo dígitos, con una longitud máxima exacta (DNI=8, Teléfono=9, RUC=11)
    public static void restringirSoloNumeros(JTextField campo, int longitudMaxima) {
        ((PlainDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                String filtrado = string.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() + filtrado.length() <= longitudMaxima) {
                    super.insertString(fb, offset, filtrado, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) return;
                String filtrado = text.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() - length + filtrado.length() <= longitudMaxima) {
                    super.replace(fb, offset, length, filtrado, attrs);
                }
            }
        });
    }

    // Limita un JTextField a solo letras y espacios (para Nombre/Apellido), evita números/símbolos
    public static void restringirSoloLetras(JTextField campo, int longitudMaxima) {
        ((PlainDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                String filtrado = string.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
                if (fb.getDocument().getLength() + filtrado.length() <= longitudMaxima) {
                    super.insertString(fb, offset, filtrado, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) return;
                String filtrado = text.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
                if (fb.getDocument().getLength() - length + filtrado.length() <= longitudMaxima) {
                    super.replace(fb, offset, length, filtrado, attrs);
                }
            }
        });
    }
}