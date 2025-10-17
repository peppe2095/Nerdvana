<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="Model.Utente,Model.Enum.Ruolo" %>
<%
    // Pagina deprecata: reindirizza all'Area Riservata con tab Prodotti
    // TEORICAMENTE SI PUÒ CANCELLARE MA DEVO CONTROLLARE
    Utente u = (Utente) session.getAttribute("utente");
    if (u == null || u.getRuolo() != Ruolo.admin) {
        response.sendRedirect("../Accessi/Login.jsp");
        return;
    }
    response.sendRedirect("AreaRiservata.jsp");
%>
