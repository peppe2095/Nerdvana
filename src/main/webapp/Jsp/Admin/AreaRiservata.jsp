<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="Model.Utente,Model.Enum.Ruolo" %>
<%
    Utente u = (Utente) session.getAttribute("utente");
    if (u == null || u.getRuolo() != Ruolo.admin) {
        response.sendRedirect("../Accessi/Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Area Riservata Admin - Nerdvana</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../../Stili/nerdvana-styles.css">
</head>
<body>
<%@ include file="../Fragments/Header.jsp" %>
<div class="container py-4">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="mb-0">Area Riservata Admin</h1>
  </div>

  <ul class="nav nav-tabs" id="adminTabs" role="tablist">
    <li class="nav-item" role="presentation">
      <button class="nav-link active" id="prodotti-elenco-tab" data-bs-toggle="tab" data-bs-target="#prodotti-elenco" type="button" role="tab">Prodotti</button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="articoli-tab" data-bs-toggle="tab" data-bs-target="#articoli" type="button" role="tab">Aggiungi Articolo</button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="ordini-tab" data-bs-toggle="tab" data-bs-target="#ordini" type="button" role="tab">Ordini</button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="admin-tab" data-bs-toggle="tab" data-bs-target="#admin" type="button" role="tab">Registra Admin</button>
    </li>
  </ul>
  <div class="tab-content pt-3">
    <!-- TAB ELENCO PRODOTTI (DEFAULT) -->
    <div class="tab-pane fade show active" id="prodotti-elenco" role="tabpanel">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div class="btn-group" role="group">
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="">Tutti</button>
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="Manga">Manga</button>
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="Fumetti">Fumetti</button>
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="BoardGame">BoardGame</button>
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="Videogiochi">Videogiochi</button>
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="Funko">Funko</button>
          <button type="button" class="btn btn-sm btn-outline-primary tipo-link" data-tipo="ActionFigure">ActionFigure</button>
        </div>
      </div>
      <div class="row g-3" id="prodotti"></div>
      <div class="d-flex justify-content-between align-items-center mt-3">
        <button id="prevPage" class="btn btn-outline-secondary btn-sm">Precedente</button>
        <div>Pagina <span id="pageNum">1</span> di <span id="totalPages">1</span></div>
        <button id="nextPage" class="btn btn-outline-secondary btn-sm">Successiva</button>
      </div>

      <!-- Modal Modifica Articolo -->
      <div class="modal fade" id="editArticoloModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Modifica Articolo</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <form id="formEditArticolo">
                <input type="hidden" name="id" id="edit-id">
                <div class="row g-3">
                  <div class="col-md-4">
                    <label class="form-label">Numero Seriale</label>
                    <input type="text" class="form-control" name="numeroSeriale" id="edit-numeroSeriale">
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Nome</label>
                    <input type="text" class="form-control" name="nome" id="edit-nome">
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Tipo</label>
                    <select class="form-select" name="tipo" id="edit-tipo">
                      <option value="Manga">Manga</option>
                      <option value="Fumetti">Fumetti</option>
                      <option value="BoardGame">BoardGame</option>
                      <option value="Videogiochi">Videogiochi</option>
                      <option value="Funko">Funko</option>
                      <option value="ActionFigure">ActionFigure</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Prezzo</label>
                    <input type="number" step="0.01" class="form-control" name="prezzo" id="edit-prezzo">
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Quantità</label>
                    <input type="number" class="form-control" name="quantita" id="edit-quantita">
                  </div>
                  <div class="col-12">
                    <label class="form-label">Descrizione</label>
                    <textarea class="form-control" rows="3" name="descrizione" id="edit-descrizione"></textarea>
                  </div>
                  <div class="col-12">
                    <label class="form-label">URL immagine</label>
                    <input type="text" class="form-control" name="url" id="edit-url">
                  </div>
                </div>
              </form>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Chiudi</button>
              <button type="button" id="btnSaveArticolo" class="btn btn-primary">Salva</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TAB AGGIUNGI ARTICOLO -->
    <div class="tab-pane fade" id="articoli" role="tabpanel">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Nuovo Articolo</h5>
          <form id="formNuovoArticolo">
            <div class="row g-3">
              <div class="col-md-4">
                <label class="form-label">Numero Seriale</label>
                <input type="text" name="numeroSeriale" class="form-control" required>
              </div>
              <div class="col-md-4">
                <label class="form-label">Nome</label>
                <input type="text" name="nome" class="form-control" required>
              </div>
              <div class="col-md-4">
                <label class="form-label">Tipo</label>
                <select name="tipo" class="form-select" required>
                  <option value="Manga">Manga</option>
                  <option value="Fumetti">Fumetti</option>
                  <option value="BoardGame">BoardGame</option>
                  <option value="Videogiochi">Videogiochi</option>
                  <option value="Funko">Funko</option>
                  <option value="ActionFigure">ActionFigure</option>
                </select>
              </div>
              <div class="col-md-4">
                <label class="form-label">Prezzo</label>
                <input type="number" step="0.01" name="prezzo" class="form-control" required>
              </div>
              <div class="col-md-4">
                <label class="form-label">Quantità</label>
                <input type="number" name="quantita" class="form-control" required>
              </div>
              <div class="col-12">
                <label class="form-label">Descrizione</label>
                <textarea name="descrizione" class="form-control" rows="3"></textarea>
              </div>
              <div class="col-12">
                <label class="form-label">URL immagine</label>
                <input type="text" name="url" class="form-control">
              </div>
            </div>
            <div class="mt-3">
              <button type="submit" class="btn btn-primary">Aggiungi</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- TAB ORDINI -->
    <div class="tab-pane fade" id="ordini" role="tabpanel">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Ordini</h5>
          <div class="table-responsive">
            <table class="table table-striped" id="tabellaOrdini">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Utente</th>
                  <th>Data</th>
                  <th>Totale</th>
                  <th>Stato</th>
                </tr>
              </thead>
              <tbody></tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- TAB REGISTRA ADMIN -->
    <div class="tab-pane fade" id="admin" role="tabpanel">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Registra Nuovo Admin</h5>
          <form id="formRegistraAdmin">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Nome</label>
                <input type="text" name="nome" class="form-control" required>
              </div>
              <div class="col-md-6">
                <label class="form-label">Cognome</label>
                <input type="text" name="cognome" class="form-control" required>
              </div>
              <div class="col-md-6">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control" required>
              </div>
              <div class="col-md-6">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required>
              </div>
            </div>
            <div class="mt-3">
              <button type="submit" class="btn btn-primary">Registra</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../Script/admin.js?v=1.0.0"></script>
<script src="../../Script/admin-products.js?v=1.0.1"></script>
<%@ include file="../Fragments/Footer.jsp" %>
</body>
</html>
