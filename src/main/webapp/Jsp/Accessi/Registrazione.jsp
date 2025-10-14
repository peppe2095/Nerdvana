<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registrazione - Nerdvana</title>
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../../Stili/nerdvana-styles.css">
</head>
<body class="bg-light">
  <!-- Container centrale con il form di registrazione -->
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-12 col-md-6 col-lg-5">
        <div class="card shadow-sm">
          <div class="card-body">
            <h3 class="mb-3">Registrazione</h3>
            <!-- Box errori mostrato via JS in caso di problemi -->
            <div id="regError" class="alert alert-error-nerdvana" style="display:none"></div>

            
            
            <form id="registerForm">
              <div class="mb-3">
                <label for="nome" class="form-label-nerdvana">Nome</label>
                <input type="text" id="nome" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="cognome" class="form-label-nerdvana">Cognome</label>
                <input type="text" id="cognome" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="email" class="form-label-nerdvana">Email</label>
                <input type="email" id="email" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="cittaDiResidenza" class="form-label-nerdvana">città di residenza</label>
                <input type="text" id="cittaDiResidenza" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="indirizzo" class="form-label-nerdvana">indirizzo</label>
                <input type="text" id="indirizzo" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="numeroCivico" class="form-label-nerdvana">numero civico</label>
                <input type="number" id="numeroCivico" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="cap" class="form-label-nerdvana">CAP</label>
                <input type="number" id="cap" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="dataDiNascita" class="form-label-nerdvana">data di nascita</label>
                <input type="date" id="dataDiNascita" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="telefono" class="form-label-nerdvana">telefono</label>
                <input type="number" id="telefono" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label-nerdvana">Password</label>
                <input type="password" id="password" class="form-control-nerdvana" required>
              </div>
              <button type="submit" class="btn btn-nerdvana w-100">Crea account</button>
            </form>

            <hr>
            <div class="text-center">
              <a href="Login.jsp">Hai già un account? Accedi</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <!-- Script di autenticazione (usa AJAX jQuery) -->
  <script src="../../Script/autenticazione.js?v=1.0.1"></script>
</body>
</html>
