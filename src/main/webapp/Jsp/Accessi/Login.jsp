<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login - Nerdvana</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../../Stili/nerdvana-styles.css">
</head>
<body class="bg-light">
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-12 col-md-6 col-lg-4">
        <div class="card shadow-sm">
          <div class="card-body">
            <h3 class="mb-3">Login</h3>
            <div id="loginError" class="alert alert-error-nerdvana" style="display:none"></div>
            <form id="loginForm">
              <div class="mb-3">
                <label for="loginEmail" class="form-label-nerdvana">Email</label>
                <input type="email" id="loginEmail" class="form-control-nerdvana" required>
              </div>
              <div class="mb-3">
                <label for="loginPassword" class="form-label-nerdvana">Password</label>
                <input type="password" id="loginPassword" class="form-control-nerdvana" required>
              </div>
              <button type="submit" class="btn btn-nerdvana w-100">Entra</button>
            </form>
            <hr>
            <div class="text-center">
              <a href="Registrazione.jsp">Non hai un account? Registrati</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- jQuery PER LE CHIAMATE AJAX-->
  <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="../../Script/autenticazione.js?v=1.0.1"></script>
</body>
</html>