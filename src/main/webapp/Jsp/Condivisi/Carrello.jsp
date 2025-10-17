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
    <div id="cartEmpty" class="alert alert-warning-nerdvana" style="display:none">Il carrello è vuoto.</div>
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

    <div class="mt-3 d-flex gap-2">
      <a href="../../index.jsp" class="btn btn-outline-secondary">Continua lo shopping</a>
      <button id="btnCheckout" type="button" class="btn btn-primary" style="display:none">Procedi all'ordine</button>
    </div>
  </div>

  <!-- Modal Checkout -->
  <div class="modal fade" id="checkoutModal" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Seleziona metodo di pagamento</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div id="ckError" class="alert alert-danger d-none"></div>
          <div id="cardsBox" class="mb-3"></div>
          <div id="noCardsBox" class="d-none">
            <div class="alert alert-info">Nessuna carta salvata. Inseriscine una nuova:</div>
            <div class="row g-2">
              <div class="col-12"><input id="ccNome" class="form-control" placeholder="Nome Titolare"/></div>
              <div class="col-12"><input id="ccNumero" class="form-control" placeholder="Numero Carta (16 cifre)" maxlength="19"/></div>
              <div class="col-6"><input id="ccScadenza" type="month" class="form-control" placeholder="Scadenza (YYYY-MM)"/></div>
              <div class="col-6"><input id="ccCvv" class="form-control" placeholder="CVV (3 cifre)" maxlength="3"/></div>
            </div>
          </div>
          <div id="newCardBox" class="border rounded p-3 d-none">
            <div class="form-check mb-2">
              <input class="form-check-input" type="radio" name="payCardId" id="radioNewCard" value="new">
              <label class="form-check-label" for="radioNewCard">Paga con nuova carta (senza salvare)</label>
            </div>
            <div class="row g-2 mt-1">
              <div class="col-12"><input id="newCcNome" class="form-control" placeholder="Nome Titolare"/></div>
              <div class="col-12"><input id="newCcNumero" class="form-control" placeholder="Numero Carta (16 cifre)" maxlength="19"/></div>
              <div class="col-6"><input id="newCcScadenza" type="month" class="form-control" placeholder="Scadenza (YYYY-MM)"/></div>
              <div class="col-6"><input id="newCcCvv" class="form-control" placeholder="CVV (3 cifre)" maxlength="3"/></div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Annulla</button>
          <button id="btnAddCard" type="button" class="btn btn-outline-primary d-none">Aggiungi carta</button>
          <button id="btnConfermaOrdine" type="button" class="btn btn-primary" disabled>Conferma Ordine</button>
        </div>
      </div>
    </div>
  </div>

  <!-- jQuery necessario per le chiamate AJAX del carrello -->
  <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="../../Script/cart.js"></script>
</body>
</html>