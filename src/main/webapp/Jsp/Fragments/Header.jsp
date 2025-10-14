<%@ page import="Model.Enum.Tipo" %>
<%@ page import="Model.Utente" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- CSS personalizzato -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Stili/nerdvana-styles.css">
</head>
<body>


<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
  <div class="container">
    <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/index.jsp">
      <img src="${pageContext.request.contextPath}/Image/logo.png" alt="Nerdvana" height="32" class="me-2">
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
          <a class="nav-link" href="${pageContext.request.contextPath}/Jsp/Condivisi/WishList.jsp" title="Wishlist">
            <img src="${pageContext.request.contextPath}/Image/wishList.jpg" alt="Wishlist" height="24">
          </a>
        </li>
        <li class="nav-item me-3 position-relative">
          <a class="nav-link" href="${pageContext.request.contextPath}/Jsp/Condivisi/Carrello.jsp" title="Carrello">
            <img src="${pageContext.request.contextPath}/Image/carrello.jpg" alt="Carrello" height="24">
          </a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userMenu" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            <img src="${pageContext.request.contextPath}/Image/utente.jpg" alt="Utente" height="28" class="rounded-circle me-2"> Account
          </a>
          <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userMenu">
            <%
              Object utente = session.getAttribute("utente");
              if (utente == null) {
            %>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/Jsp/Accessi/Login.jsp">Login</a></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/Jsp/Accessi/Registrazione.jsp">Registrazione</a></li>
            <%
              } else {
            %>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/Jsp/Logged/AreaRiservata.jsp">Area Riservata</a></li>
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



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

</body>
</html>