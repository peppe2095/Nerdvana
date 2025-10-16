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
  <title>Dashboard Admin - Nerdvana</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../../Stili/nerdvana-styles.css">
</head>
<body>
<div class="container py-4">
  <h1 class="mb-4">Dashboard Admin</h1>

  <ul class="nav nav-tabs" id="adminTabs" role="tablist">
    <li class="nav-item" role="presentation">
      <button class="nav-link active" id="articoli-tab" data-bs-toggle="tab" data-bs-target="#articoli" type="button" role="tab">Aggiungi Articolo</button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="ordini-tab" data-bs-toggle="tab" data-bs-target="#ordini" type="button" role="tab">Ordini</button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="admin-tab" data-bs-toggle="tab" data-bs-target="#admin" type="button" role="tab">Registra Admin</button>
    </li>
  </ul>
  <div class="tab-content pt-3">
    <div class="tab-pane fade show active" id="articoli" role="tabpanel">
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
</body>
</html>
