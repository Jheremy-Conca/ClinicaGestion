package pe.upn.clinica.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import pe.upn.clinica.modelo.CitaMedica;
import pe.upn.clinica.modelo.Paciente;

public class GestorArchivos {

    private static final String ARCHIVO_LOG = "log.txt";

    // RF28: exportar pacientes a PDF con diseño profesional
    public void exportarPacientesPDF(List<Paciente> pacientes, String ruta) throws Exception {
        GeneradorPDF.generarReportePacientesPDF(pacientes, ruta);
        registrarLog("Exportación de " + pacientes.size() + " pacientes a PDF: " + ruta);
    }

    // RF29: exportar historial de citas a PDF con diseño profesional
    public void exportarCitasPDF(List<CitaMedica> citas, String ruta) throws Exception {
        GeneradorPDF.generarReporteCitasPDF(citas, ruta);
        registrarLog("Exportación de " + citas.size() + " citas a PDF: " + ruta);
    }

    // RF40: registrar logs de operaciones (este sí se mantiene en texto plano, es interno del sistema)
    public void registrarLog(String descripcion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_LOG, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write("[" + timestamp + "] " + descripcion);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}