<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="../Fragments/Header.jsp" %>

<div class="container py-4">
  <h3 class="mb-3">La tua wishlist</h3>

  <div id="wlEmpty" class="alert alert-warning-nerdvana" style="display:none">La wishlist è vuota.</div>
  <div id="wlError" class="alert alert-error-nerdvana" style="display:none"></div>

  <div class="row g-3" id="wlGrid"></div>

  <div class="mt-3">
    <a href="../../index.jsp" class="btn btn-outline-secondary">Torna alla home</a>
  </div>
</div>

<!-- jQuery necessario per le chiamate AJAX della wishlist -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../Script/wishlist.js"></script>

<%@ include file="../Fragments/Footer.jsp" %>
