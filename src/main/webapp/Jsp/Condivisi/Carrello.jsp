<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Carrello - Nerdvana</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../../Stili/nerdvana-styles.css">
</head>
<body class="bg-light">
  <div class="container py-4">
    <h3 class="mb-3">Il tuo carrello</h3>

    <!-- Contenitori popolati via AJAX da Script/cart.js -->
    <div id="cartEmpty" class="alert alert-warning-nerdvana" style="display:none">
      Il carrello è vuoto.
    </div>
    <div id="cartError" class="alert alert-error-nerdvana" style="display:none"></div>

    <div class="table-responsive">
      <table class="table align-middle" id="cartTable" style="display:none">
        <thead>
          <tr>
            <th>Prodotto</th>
            <th class="text-end">Prezzo</th>
            <th class="text-center">Quantità</th>
            <th class="text-end">Totale</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="cartBody"></tbody>
        <tfoot>
          <tr>
            <th colspan="3" class="text-end">Totale carrello:</th>
            <th class="text-end" id="cartGrandTotal">€ 0,00</th>
            <th></th>
          </tr>
        </tfoot>
      </table>
    </div>

    <!-- Pulsanti azione -->
    <div class="d-flex justify-content-between align-items-center mt-4">
      <a href="../../index.jsp" class="btn btn-outline-secondary">
        Continua lo shopping
      </a>

      <!-- 🔹 Nuovo pulsante per completare l’ordine -->
      <form action="../../Jsp/Ordine/Checkout.jsp" method="GET" class="m-0">
        <button type="submit" class="btn btn-success">
          <i class="bi bi-credit-card"></i> Completa ordine
        </button>
      </form>
    </div>
  </div>

  <!-- jQuery necessario per le chiamate AJAX del carrello -->
  <script src="https://code.jquery.com/jquery-3.7.1.min.js"
          integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
          crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="../../Script/cart.js"></script>
</body>
</html>
