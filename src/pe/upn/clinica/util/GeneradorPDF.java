package pe.upn.clinica.util;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pe.upn.clinica.modelo.Boleta;
import pe.upn.clinica.modelo.CitaMedica;
import pe.upn.clinica.modelo.Factura;
import pe.upn.clinica.modelo.Paciente;

public class GeneradorPDF {

    // Paleta de colores institucional (tono salud: azul/verde)
    private static final java.awt.Color COLOR_PRIMARIO = new java.awt.Color(23, 92, 158);
    private static final java.awt.Color COLOR_SECUNDARIO = new java.awt.Color(235, 242, 250);
    private static final java.awt.Color COLOR_TEXTO_CLARO = java.awt.Color.WHITE;
    private static final java.awt.Color COLOR_GRIS = new java.awt.Color(90, 90, 90);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===================== RF21: BOLETA EN PDF =====================
    public static void generarBoletaPDF(Boleta boleta, String nombrePaciente, String ruta) throws Exception {
        Document doc = new Document(PageSize.A5);
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();

        agregarEncabezado(doc, "BOLETA DE VENTA", "N° B" + String.format("%06d", boleta.getId()));

        PdfPTable tablaDatos = crearTablaDatosCliente(nombrePaciente, boleta.getFecha().format(FORMATO_FECHA), null, null);
        doc.add(tablaDatos);
        doc.add(new Paragraph(" "));

        PdfPTable tablaDetalle = new PdfPTable(2);
        tablaDetalle.setWidthPercentage(100);
        tablaDetalle.setWidths(new float[]{3, 1});
        agregarFilaDetalle(tablaDetalle, "Atención médica - Consulta", boleta.getMonto());
        doc.add(tablaDetalle);
        doc.add(new Paragraph(" "));

        agregarTotal(doc, "TOTAL A PAGAR", boleta.getMonto());
        agregarPiePagina(doc, "Gracias por su visita. Conserve este comprobante.");

        doc.close();
    }

    // ===================== RF22 y RF23: FACTURA EN PDF (con IGV desglosado) =====================
    public static void generarFacturaPDF(Factura factura, String nombrePaciente, String ruta) throws Exception {
        Document doc = new Document(PageSize.A5);
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();

        agregarEncabezado(doc, "FACTURA ELECTRÓNICA", "N° F" + String.format("%06d", factura.getId()));

        PdfPTable tablaDatos = crearTablaDatosCliente(nombrePaciente, factura.getFecha().format(FORMATO_FECHA),
                factura.getRuc(), factura.getRazonSocial());
        doc.add(tablaDatos);
        doc.add(new Paragraph(" "));

        PdfPTable tablaDetalle = new PdfPTable(2);
        tablaDetalle.setWidthPercentage(100);
        tablaDetalle.setWidths(new float[]{3, 1});
        agregarFilaDetalle(tablaDetalle, "Atención médica - Consulta", factura.getMonto());
        doc.add(tablaDetalle);
        doc.add(new Paragraph(" "));

        // Desglose Subtotal + IGV + Total (lo distintivo de la factura)
        double subtotal = factura.getMonto();
        double igv = factura.getIgv();
        double total = subtotal + igv;

        PdfPTable tablaResumen = new PdfPTable(2);
        tablaResumen.setWidthPercentage(60);
        tablaResumen.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tablaResumen.setWidths(new float[]{2, 1});

        agregarFilaResumen(tablaResumen, "Subtotal:", subtotal, false);
        agregarFilaResumen(tablaResumen, "IGV (18%):", igv, false);
        agregarFilaResumen(tablaResumen, "TOTAL:", total, true);

        doc.add(tablaResumen);
        agregarPiePagina(doc, "Gracias por su preferencia. Comprobante válido para efectos tributarios.");

        doc.close();
    }

    // ===================== RF28: EXPORTAR LISTADO DE PACIENTES A PDF =====================
    public static void generarReportePacientesPDF(List<Paciente> pacientes, String ruta) throws Exception {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();

        agregarEncabezado(doc, "LISTADO DE PACIENTES", "Total: " + pacientes.size());

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1, 2, 2.5f, 2.5f, 1.8f});
        tabla.setSpacingBefore(10);

        agregarCabeceraTabla(tabla, new String[]{"DNI", "Nombre", "Apellido", "Teléfono", "F. Nacimiento"});

        boolean filaClara = true;
        for (Paciente p : pacientes) {
            java.awt.Color fondoFila = filaClara ? java.awt.Color.WHITE : COLOR_SECUNDARIO;
            agregarCeldaCuerpo(tabla, p.getDni(), fondoFila);
            agregarCeldaCuerpo(tabla, p.getNombre(), fondoFila);
            agregarCeldaCuerpo(tabla, p.getApellido(), fondoFila);
            agregarCeldaCuerpo(tabla, p.getTelefono() != null ? p.getTelefono() : "-", fondoFila);
            agregarCeldaCuerpo(tabla, p.getFechaNacimiento() != null ? p.getFechaNacimiento().format(FORMATO_FECHA) : "-", fondoFila);
            filaClara = !filaClara;
        }

        doc.add(tabla);
        agregarPiePagina(doc, "Reporte generado por el Sistema de Gestión Clínica Ambulatoria.");
        doc.close();
    }

    // ===================== RF29: EXPORTAR HISTORIAL DE CITAS A PDF =====================
    public static void generarReporteCitasPDF(List<CitaMedica> citas, String ruta) throws Exception {
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();

        agregarEncabezado(doc, "HISTORIAL DE CITAS MÉDICAS", "Total: " + citas.size());

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.3f, 1, 2.5f, 2.5f, 1.8f, 1.3f});
        tabla.setSpacingBefore(10);

        agregarCabeceraTabla(tabla, new String[]{"Fecha", "Hora", "Paciente", "Médico", "Consultorio", "Estado"});

        boolean filaClara = true;
        for (CitaMedica c : citas) {
            java.awt.Color fondoFila = filaClara ? java.awt.Color.WHITE : COLOR_SECUNDARIO;
            agregarCeldaCuerpo(tabla, c.getFecha().format(FORMATO_FECHA), fondoFila);
            agregarCeldaCuerpo(tabla, c.getHora().toString(), fondoFila);
            agregarCeldaCuerpo(tabla, c.getNombrePaciente(), fondoFila);
            agregarCeldaCuerpo(tabla, c.getNombreMedico(), fondoFila);
            agregarCeldaCuerpo(tabla, c.getNumeroConsultorio(), fondoFila);
            agregarCeldaCuerpo(tabla, c.getEstado(), fondoFila);
            filaClara = !filaClara;
        }

        doc.add(tabla);
        agregarPiePagina(doc, "Reporte generado por el Sistema de Gestión Clínica Ambulatoria.");
        doc.close();
    }

    // ===================== MÉTODOS AUXILIARES DE DISEÑO =====================

    private static void agregarEncabezado(Document doc, String titulo, String referencia) throws Exception {
        PdfPTable cabecera = new PdfPTable(1);
        cabecera.setWidthPercentage(100);

        PdfPCell celdaTitulo = new PdfPCell();
        celdaTitulo.setBackgroundColor(COLOR_PRIMARIO);
        celdaTitulo.setBorder(Rectangle.NO_BORDER);
        celdaTitulo.setPadding(14);

        Font fuenteClinica = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_TEXTO_CLARO);
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOR_TEXTO_CLARO);
        Font fuenteRef = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_TEXTO_CLARO);

        Paragraph pClinica = new Paragraph("CLÍNICA AMBULATORIA", fuenteClinica);
        Paragraph pTitulo = new Paragraph(titulo, fuenteTitulo);
        Paragraph pRef = new Paragraph(referencia, fuenteRef);
        pTitulo.setSpacingBefore(4);

        celdaTitulo.addElement(pClinica);
        celdaTitulo.addElement(pTitulo);
        celdaTitulo.addElement(pRef);
        cabecera.addCell(celdaTitulo);

        doc.add(cabecera);
        doc.add(new Paragraph(" "));
    }

    private static PdfPTable crearTablaDatosCliente(String nombrePaciente, String fecha, String ruc, String razonSocial) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);

        Font fuenteEtiqueta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_GRIS);
        Font fuenteValor = FontFactory.getFont(FontFactory.HELVETICA, 10, java.awt.Color.BLACK);

        agregarFilaSimple(tabla, "Paciente:", nombrePaciente, fuenteEtiqueta, fuenteValor);
        agregarFilaSimple(tabla, "Fecha:", fecha, fuenteEtiqueta, fuenteValor);

        if (ruc != null) {
            agregarFilaSimple(tabla, "RUC:", ruc, fuenteEtiqueta, fuenteValor);
            agregarFilaSimple(tabla, "Razón Social:", razonSocial, fuenteEtiqueta, fuenteValor);
        }
        return tabla;
    }

    private static void agregarFilaSimple(PdfPTable tabla, String etiqueta, String valor, Font fEtiqueta, Font fValor) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, fEtiqueta));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPaddingBottom(4);
        PdfPCell c2 = new PdfPCell(new Phrase(valor != null ? valor : "-", fValor));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPaddingBottom(4);
        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    private static void agregarFilaDetalle(PdfPTable tabla, String concepto, double monto) {
        Font fuenteCabecera = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_TEXTO_CLARO);
        Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 10, java.awt.Color.BLACK);

        PdfPCell h1 = new PdfPCell(new Phrase("Concepto", fuenteCabecera));
        h1.setBackgroundColor(COLOR_PRIMARIO);
        h1.setPadding(8);
        PdfPCell h2 = new PdfPCell(new Phrase("Monto (S/)", fuenteCabecera));
        h2.setBackgroundColor(COLOR_PRIMARIO);
        h2.setPadding(8);
        h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(h1);
        tabla.addCell(h2);

        PdfPCell d1 = new PdfPCell(new Phrase(concepto, fuenteCuerpo));
        d1.setPadding(8);
        PdfPCell d2 = new PdfPCell(new Phrase(String.format("%.2f", monto), fuenteCuerpo));
        d2.setPadding(8);
        d2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(d1);
        tabla.addCell(d2);
    }

    private static void agregarFilaResumen(PdfPTable tabla, String etiqueta, double monto, boolean esTotal) {
        Font fuente = esTotal
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_PRIMARIO)
                : FontFactory.getFont(FontFactory.HELVETICA, 10, java.awt.Color.BLACK);

        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, fuente));
        c1.setBorder(esTotal ? Rectangle.TOP : Rectangle.NO_BORDER);
        c1.setPadding(6);
        PdfPCell c2 = new PdfPCell(new Phrase("S/ " + String.format("%.2f", monto), fuente));
        c2.setBorder(esTotal ? Rectangle.TOP : Rectangle.NO_BORDER);
        c2.setPadding(6);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    private static void agregarTotal(Document doc, String etiqueta, double monto) throws Exception {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(60);
        tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_PRIMARIO);
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, fuente));
        c1.setBorder(Rectangle.TOP);
        c1.setPadding(6);
        PdfPCell c2 = new PdfPCell(new Phrase("S/ " + String.format("%.2f", monto), fuente));
        c2.setBorder(Rectangle.TOP);
        c2.setPadding(6);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(c1);
        tabla.addCell(c2);

        doc.add(tabla);
    }

    private static void agregarCabeceraTabla(PdfPTable tabla, String[] columnas) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_TEXTO_CLARO);
        for (String col : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(col, fuente));
            celda.setBackgroundColor(COLOR_PRIMARIO);
            celda.setPadding(8);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }
    }

    private static void agregarCeldaCuerpo(PdfPTable tabla, String texto, java.awt.Color fondo) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 9, java.awt.Color.BLACK);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(fondo);
        celda.setPadding(6);
        tabla.addCell(celda);
    }

    private static void agregarPiePagina(Document doc, String texto) throws Exception {
        doc.add(new Paragraph(" "));
        Font fuentePie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, COLOR_GRIS);
        Paragraph pie = new Paragraph(texto, fuentePie);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(20);
        doc.add(pie);
    }
}