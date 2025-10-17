package Control;

import Connessione.GestoreConnessioneDatabase;
import Dao.*;
import Dto.ApiResponse;
import Model.Articolo;
import Model.Ordine;
import Model.OrdineArticolo;
import Model.Utente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/*questa è una merda, va sistemata :D */
@WebServlet(name = "fatturaPdfServlet", urlPatterns = {"/api/invoice/pdf"})
public class fatturaPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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
            // Autorizzazione: l'ordine deve appartenere all'utente corrente (o admin)
            boolean isAdmin = utente.getRuolo() != null && "ADMIN".equalsIgnoreCase(utente.getRuolo().name());
            if (!isAdmin && ordine.getUtenteId() != utente.getId()) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(new com.google.gson.Gson().toJson(ApiResponse.error("Accesso negato alla fattura")));
                return;
            }

            Utente intestatario = utenteDao.getUtenteById(ordine.getUtenteId());
            List<OrdineArticolo> righe = ordineArticoloDao.getOrdineArticoliByOrdineId(orderId);

            // Prepara i dati per la tabella
            List<String[]> rows = new ArrayList<>();
            double totale = 0.0;
            for (OrdineArticolo oa : righe) {
                Articolo a = articoloDao.getArticoloById(oa.getArticoloId());
                if (a == null) continue;
                double sub = a.getPrezzo() * oa.getQuantita();
                totale += sub;
                rows.add(new String[]{safe(a.getNome()), String.format(Locale.ITALY, "%.2f", a.getPrezzo()), String.valueOf(oa.getQuantita()), String.format(Locale.ITALY, "%.2f", sub)});
            }

            // Metadati azienda (statici, modificabili)
            String aziendaNome = "Nerdvana S.r.l.";
            String aziendaIndirizzo = "Via degli Esempi 123, 00100 Roma (RM)";
            String aziendaPIVA = "P.IVA 01234567890";
            String sito = req.getServerName();
            String dataStr = new SimpleDateFormat("dd/MM/yyyy").format(ordine.getDataCreazione());

            // Genera PDF minimale
            byte[] pdf = buildSimplePdf(aziendaNome, aziendaIndirizzo, aziendaPIVA, sito, intestatario, ordine, rows, totale, dataStr);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/pdf");
            String filename = "fattura-ordine-" + orderId + ".pdf";
            resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
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

    private static int parseInt(String s, int def){ try { return Integer.parseInt(s); } catch(Exception e){ return def; } }

    private static String safe(String s){ return s == null ? "" : s; }

    // Costruisce un PDF olto semplice con testo e tabella
    private static byte[] buildSimplePdf(String aziendaNome, String aziendaIndirizzo, String aziendaPIVA,
                                         String sito, Utente u, Ordine o, List<String[]> rows, double totale, String dataStr) throws IOException {
        // Contenuto testuale (usa font base, layout monospazio simulato con spazi)
        StringBuilder content = new StringBuilder();
        content.append(aziendaNome).append("\n");
        content.append(aziendaIndirizzo).append("\n");
        content.append(aziendaPIVA).append("\n");
        content.append("Sito: ").append(sito).append("\n\n");
        content.append("Fattura Ordine #").append(o.getId()).append(" - Data: ").append(dataStr).append("\n");
        content.append("Intestatario: ").append(safe(u.getNome())).append(" ").append(safe(u.getCognome())).append("\n");
        content.append("Indirizzo: ").append(safe(u.getIndirizzo())).append(" ").append(safe(u.getNumeroCivico())).append(", ")
                .append(safe(u.getCittaResidenza())).append(" ").append(safe(u.getCap())).append("\n\n");

        String header = String.format("%-40s %10s %8s %12s", "Articolo", "Prezzo", "Qtà", "Subtotale");
        content.append(header).append("\n");
        content.append(repeat('-', 74)).append("\n");
        for (String[] r : rows) {
            String line = String.format("%-40.40s %10s %8s %12s", r[0], r[1], r[2], r[3]);
            content.append(line).append("\n");
        }
        content.append(repeat('-', 74)).append("\n");
        content.append(String.format(Locale.ITALY, "%62s %12.2f", "Totale:", totale)).append("\n");

        // Converte il testo in uno stream di comandi PDF con gestione a capo
        String[] lines = content.toString().split("\n");
        StringBuilder sb = new StringBuilder();
        sb.append("BT /F1 10 Tf 50 780 Td 14 TL ");
        for (int i = 0; i < lines.length; i++) {
            String line = escapePdfLine(lines[i]);
            if (line.isEmpty()) line = " ";
            sb.append("(").append(line).append(") Tj ");
            if (i < lines.length - 1) sb.append("T* "); // vai a capo
        }
        sb.append("ET");
        String streamText = sb.toString();
        byte[] streamBytes = streamText.getBytes(StandardCharsets.UTF_8);
        int len = streamBytes.length;

        String obj1 = "1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n";
        String obj2 = "2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n";
        String obj3 = "3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>endobj\n";
        String obj4 = "4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n";
        String obj5 = "5 0 obj<< /Length " + len + " >>stream\n" + streamText + "\nendstream endobj\n";

        // xref
        StringBuilder pdf = new StringBuilder();
        pdf.append("%PDF-1.4\n");
        int x1 = pdf.length(); pdf.append(obj1);
        int x2 = pdf.length(); pdf.append(obj2);
        int x3 = pdf.length(); pdf.append(obj3);
        int x4 = pdf.length(); pdf.append(obj4);
        int x5 = pdf.length(); pdf.append(obj5);
        int xref = pdf.length();
        pdf.append("xref\n0 6\n");
        pdf.append(String.format("%010d %05d f \n", 0, 65535));
        pdf.append(String.format("%010d %05d n \n", x1, 0));
        pdf.append(String.format("%010d %05d n \n", x2, 0));
        pdf.append(String.format("%010d %05d n \n", x3, 0));
        pdf.append(String.format("%010d %05d n \n", x4, 0));
        pdf.append(String.format("%010d %05d n \n", x5, 0));
        pdf.append("trailer<< /Size 6 /Root 1 0 R >>\nstartxref\n");
        pdf.append(xref).append("\n%%EOF");

        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private static String repeat(char c, int n){ char[] a = new char[n]; Arrays.fill(a, c); return new String(a); }

    private static String escapePdf(String s) {
        // Escape base per sequenza dentro stringa PDF (manteniamo \n per compatibilità con vecchie versioni non usate ora)
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("\r", "");
    }

    private static String escapePdfLine(String s) {
        if (s == null) return "";
        // Escape per una singola riga in una stringa PDF
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}