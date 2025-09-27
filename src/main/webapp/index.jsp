<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Articolo" %>
<%@ page import="Model.Enum.Tipo" %>
<%@ page import="Model.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nerdvana - Home</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- CSS personalizzato -->
    <link rel="stylesheet" href="Stili/nerdvana-styles.css">
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
  <div class="container">
    <a class="navbar-brand d-flex align-items-center" href="index.jsp">
      <img src="Image/logo.png" alt="Nerdvana" height="32" class="me-2">
      <span class="fw-bold">Nerdvana</span>
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarsExample" aria-controls="navbarsExample" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarsExample">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <%
            for (Tipo t : Tipo.values()) {
        %>
            <li class="nav-item"><a href="#" class="nav-link tipo-link" data-tipo="<%= t.name() %>"><%= t.name() %></a></li>
        <%
            }
        %>
      </ul>

      <ul class="navbar-nav ms-auto align-items-center">
        <li class="nav-item me-2">
          <a class="nav-link" href="Jsp/Condivisi/Wishlist.jsp" title="Wishlist">
            <img src="Image/wishList.jpg" alt="Wishlist" height="24">
          </a>
        </li>
        <li class="nav-item me-3 position-relative">
          <a class="nav-link" href="Jsp/Condivisi/Carrello.jsp" title="Carrello">
            <img src="Image/carrello.jpg" alt="Carrello" height="24">
          </a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userMenu" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            <img src="Image/utente.jpg" alt="Utente" height="28" class="rounded-circle me-2"> Account
          </a>
          <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
            <%
              Object utente = session.getAttribute("utente");
              if (utente == null) {
            %>
              <li><a class="dropdown-item" href="Jsp/Accessi/Login.jsp">Login</a></li>
              <li><a class="dropdown-item" href="Jsp/Accessi/Registrazione.jsp">Registrazione</a></li>
            <%
              } else {
            %>
              <li><a class="dropdown-item" href="Jsp/Logged/AreaRiservata.jsp">Area Riservata</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a id="logoutLink" class="dropdown-item" href="#">Logout</a></li>
            <%
              }
            %>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>

<!-- Hero con slider -->
<section class="bg-light py-3">
  <div class="container">
    <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
      <div class="carousel-inner rounded shadow-sm">
        <div class="carousel-item active">
          <img src="Database/ImageDynamic/onePiece1.jpg" class="d-block w-100" alt="Slide 1">
        </div>
        <div class="carousel-item">
          <img src="Database/ImageDynamic/catanBordGame.jpg" class="d-block w-100" alt="Slide 2">
        </div>
        <div class="carousel-item">
          <img src="Database/ImageDynamic/The Last of Us Part II.jpg" class="d-block w-100" alt="Slide 3">
        </div>
      </div>
      <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Precedente</span>
      </button>
      <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Successivo</span>
      </button>
    </div>
  </div>
</section>

<!-- Sezione Prodotti con paginazione -->
<main class="py-4">
  <div class="container">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="m-0">Prodotti</h2>
      <div>
        <button id="btn-aggiorna" class="btn btn-sm btn-outline-secondary">Aggiorna</button>
      </div>
    </div>

    <div id="prodotti" class="row g-3">
      <!-- Cards prodotti caricate via JS -->
      <div class="col-12 text-center text-muted" id="placeholder">Caricamento prodotti...</div>
    </div>

    <div class="d-flex justify-content-between align-items-center mt-3">
      <button id="prevPage" class="btn btn-outline-secondary btn-sm">&laquo; Precedente</button>
      <div>
        Pagina <span id="pageNum">1</span> di <span id="totalPages">1</span>
      </div>
      <button id="nextPage" class="btn btn-outline-secondary btn-sm">Successiva &raquo;</button>
    </div>
  </div>
</main>

<footer class="py-4 border-top">
  <div class="container text-center text-muted small">
    © <%= java.time.Year.now() %> Nerdvana
  </div>
</footer>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<!-- Script per la index-->
<script src="Script/main.js"></script>
</body>
</html>