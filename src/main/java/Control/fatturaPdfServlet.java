package Control;

import Connessione.GestoreConnessioneDatabase;
import Dao.*;
import Dto.ApiResponse;
import Model.Articolo;
import Model.Ordine;
import Model.OrdineArticolo;
import Model.Utente;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@WebServlet(name = "fatturaPdfServlet", urlPatterns = {"/api/invoice/pdf"})
public class fatturaPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final double IVA_RATE = 0.22; // 22%

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(new com.google.gson.Gson().toJson(ApiResponse.error("Non autorizzato")));
            return;
        }
        Utente utente = (Utente) session.getAttribute("utente");

        int orderId = parseInt(req.getParameter("orderId"), -1);
        if (orderId <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(new com.google.gson.Gson().toJson(ApiResponse.error("Parametro orderId mancante")));
            return;
        }

        try (Connection conn = GestoreConnessioneDatabase.getConnection()) {
            OrdineDao ordineDao = new OrdineDao(conn);
            OrdineArticoloDao ordineArticoloDao = new OrdineArticoloDao(conn);
            ArticoloDao articoloDao = new ArticoloDao(conn);
            UtenteDao utenteDao = new UtenteDao(conn);

            Ordine ordine = ordineDao.getOrdineById(orderId);
            if (ordine == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(new com.google.gson.Gson().toJson(ApiResponse.error("Ordine non trovato")));
                return;
            }

            // Autorizzazione: solo admin o proprietario dell'ordine
            boolean isAdmin = utente.getRuolo() != null && "ADMIN".equalsIgnoreCase(utente.getRuolo().name());
            if (!isAdmin && ordine.getUtenteId() != utente.getId()) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(new com.google.gson.Gson().toJson(ApiResponse.error("Accesso negato alla fattura")));
                return;
            }

            Utente intestatario = utenteDao.getUtenteById(ordine.getUtenteId());
            List<OrdineArticolo> righe = ordineArticoloDao.getOrdineArticoliByOrdineId(orderId);

            // Prepara i dati per il PDF
            List<InvoiceRow> rows = new ArrayList<>();
            double totaleImponibile = 0.0;

            for (OrdineArticolo oa : righe) {
                Articolo a = articoloDao.getArticoloById(oa.getArticoloId());
                if (a == null) continue;

                double totaleRiga = a.getPrezzo() * oa.getQuantita();
                double ivaRiga = totaleRiga * IVA_RATE;
                double totaleConIva = totaleRiga + ivaRiga;

                totaleImponibile += totaleRiga;
                rows.add(new InvoiceRow(a.getNome(), a.getPrezzo(), oa.getQuantita(), totaleRiga, ivaRiga, totaleConIva));
            }

            double totaleIva = totaleImponibile * IVA_RATE;
            double totaleFattura = totaleImponibile + totaleIva;
            String dataStr = new SimpleDateFormat("dd/MM/yyyy").format(ordine.getDataCreazione());

            // Genera il PDF in memoria
            byte[] pdf = buildProfessionalPdf(intestatario, ordine, rows, totaleImponibile, totaleIva, totaleFattura, dataStr);

            // Invia il PDF al client
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; filename=\"fattura-ordine-" + orderId + ".pdf\"");
            resp.setContentLength(pdf.length);
            try (OutputStream os = resp.getOutputStream()) {
                os.write(pdf);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(new com.google.gson.Gson().toJson(ApiResponse.error("Errore generazione PDF: " + e.getMessage())));
        }
    }

    public static byte[] buildProfessionalPdf(Utente cliente, Ordine ordine, List<InvoiceRow> rows,
                                              double totaleImponibile, double totaleIva, double totaleFattura,
                                              String dataStr) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Font
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(50, 50, 50));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            // Colori
            Color darkGray = new Color(50, 50, 50);
            Color lightGray = new Color(240, 240, 240);
            Color borderGray = new Color(200, 200, 200);

            // === INTESTAZIONE DITTA ===
            Paragraph companyName = new Paragraph("Nerdvana S.r.l.", titleFont);
            companyName.setAlignment(Element.ALIGN_LEFT);
            companyName.setSpacingAfter(3);
            document.add(companyName);

            Paragraph companyAddress = new Paragraph("Via degli Esempi, 123", normalFont);
            companyAddress.setSpacingAfter(2);
            document.add(companyAddress);

            Paragraph companyCity = new Paragraph("00100 Roma (RM) - Italia", normalFont);
            companyCity.setSpacingAfter(2);
            document.add(companyCity);

            Paragraph companyPiva = new Paragraph("P.IVA: 01234567890", normalFont);
            companyPiva.setSpacingAfter(2);
            document.add(companyPiva);

            Paragraph companyContact = new Paragraph("Tel: +39 06 1234567 - Email: info@nerdvana.it", normalFont);
            companyContact.setSpacingAfter(20);
            document.add(companyContact);

            // Linea separatore
            PdfPTable separatorLine = new PdfPTable(1);
            separatorLine.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderColorBottom(darkGray);
            lineCell.setBorderWidthBottom(1);
            lineCell.setFixedHeight(3);
            separatorLine.addCell(lineCell);
            separatorLine.setSpacingAfter(20);
            document.add(separatorLine);

            // === TITOLO FATTURA ===
            Paragraph invoiceTitle = new Paragraph("FATTURA N° " + ordine.getId(), titleFont);
            invoiceTitle.setAlignment(Element.ALIGN_LEFT);
            invoiceTitle.setSpacingAfter(15);
            document.add(invoiceTitle);

            // === DATA ===
            Paragraph invoiceDate = new Paragraph("Data: " + dataStr, boldFont);
            invoiceDate.setSpacingAfter(15);
            document.add(invoiceDate);

            // === CLIENTE ===
            Paragraph clienteLabel = new Paragraph("Cliente:", boldFont);
            clienteLabel.setSpacingAfter(5);
            document.add(clienteLabel);

            Paragraph clienteNome = new Paragraph(safe(cliente.getNome()) + " " + safe(cliente.getCognome()), normalFont);
            clienteNome.setSpacingAfter(2);
            document.add(clienteNome);

            Paragraph clienteIndirizzo = new Paragraph(safe(cliente.getIndirizzo()) + " " + safe(cliente.getNumeroCivico()), normalFont);
            clienteIndirizzo.setSpacingAfter(2);
            document.add(clienteIndirizzo);

            Paragraph clienteCitta = new Paragraph(safe(cliente.getCap()) + " " + safe(cliente.getCittaResidenza()), normalFont);
            clienteCitta.setSpacingAfter(2);
            document.add(clienteCitta);

            // === TABELLA ARTICOLI ===
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{8f, 30f, 12f, 10f, 12f, 12f, 16f});
            table.setSpacingBefore(10);

            // Header tabella
            addTableHeader(table, "#", tableHeaderFont, lightGray, borderGray, Element.ALIGN_CENTER);
            addTableHeader(table, "Descrizione", tableHeaderFont, lightGray, borderGray, Element.ALIGN_LEFT);
            addTableHeader(table, "Prezzo Unitario (€)", tableHeaderFont, lightGray, borderGray, Element.ALIGN_RIGHT);
            addTableHeader(table, "Quantità", tableHeaderFont, lightGray, borderGray, Element.ALIGN_CENTER);
            addTableHeader(table, "IVA %", tableHeaderFont, lightGray, borderGray, Element.ALIGN_CENTER);
            addTableHeader(table, "Totale (€)", tableHeaderFont, lightGray, borderGray, Element.ALIGN_RIGHT);
            addTableHeader(table, "Totale IVA Inclusa (€)", tableHeaderFont, lightGray, borderGray, Element.ALIGN_RIGHT);

            // Righe articoli
            int rowNum = 1;
            for (InvoiceRow row : rows) {
                addTableCell(table, String.valueOf(rowNum++), tableFont, borderGray, Element.ALIGN_CENTER);
                addTableCell(table, row.nome, tableFont, borderGray, Element.ALIGN_LEFT);
                addTableCell(table, String.format(Locale.ITALY, "%.2f", row.prezzo), tableFont, borderGray, Element.ALIGN_RIGHT);
                addTableCell(table, String.valueOf(row.quantita), tableFont, borderGray, Element.ALIGN_CENTER);
                addTableCell(table, "22%", tableFont, borderGray, Element.ALIGN_CENTER);
                addTableCell(table, String.format(Locale.ITALY, "%.2f", row.totale), tableFont, borderGray, Element.ALIGN_RIGHT);
                addTableCell(table, String.format(Locale.ITALY, "%.2f", row.totaleConIva), tableFont, borderGray, Element.ALIGN_RIGHT);
            }

            document.add(table);

            // === RIEPILOGO TOTALE ===
            Paragraph riepilogoTitle = new Paragraph("\nRiepilogo Totale:", boldFont);
            riepilogoTitle.setSpacingBefore(20);
            riepilogoTitle.setSpacingAfter(10);
            document.add(riepilogoTitle);

            Font riepilogoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font riepilogoBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            Paragraph totImponibile = new Paragraph("• Totale imponibile (IVA esclusa): " + String.format(Locale.ITALY, "%.2f €", totaleImponibile), riepilogoFont);
            totImponibile.setSpacingAfter(5);
            document.add(totImponibile);

            Paragraph totIva = new Paragraph("• Totale IVA: " + String.format(Locale.ITALY, "%.2f €", totaleIva), riepilogoFont);
            totIva.setSpacingAfter(5);
            document.add(totIva);

            Paragraph totFattura = new Paragraph("• Totale fattura (IVA inclusa): " + String.format(Locale.ITALY, "%.2f €", totaleFattura), riepilogoBoldFont);
            totFattura.setSpacingAfter(20);
            document.add(totFattura);

            document.close();

        } catch (DocumentException e) {
            throw new IOException("Errore durante la generazione del PDF: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    private static void addTableHeader(PdfPTable table, String text, Font font, Color bgColor, Color borderColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(6);
        cell.setBorderColor(borderColor);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font, Color borderColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        cell.setBorderColor(borderColor);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return def;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    // Classe helper per le righe della fattura
    public static class InvoiceRow {
        public String nome;
        public double prezzo;
        public int quantita;
        public double totale;
        public double iva;
        public double totaleConIva;

        public InvoiceRow(String nome, double prezzo, int quantita, double totale, double iva, double totaleConIva) {
            this.nome = nome;
            this.prezzo = prezzo;
            this.quantita = quantita;
            this.totale = totale;
            this.iva = iva;
            this.totaleConIva = totaleConIva;
        }
    }
}