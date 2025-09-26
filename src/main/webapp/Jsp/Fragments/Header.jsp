<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.Utente" %>
<%
    Utente utente = (Utente) session.getAttribute("utente");
    Integer wishlistCount = (Integer) session.getAttribute("wishlistCount");
    Integer cartCount = (Integer) session.getAttribute("cartCount");
%>

<style>
    .navbar-custom {
        background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
        box-shadow: 0 2px 15px rgba(0,0,0,0.1);
        padding: 15px 0;
        min-height: 80px;
    }
    
    .navbar-brand {
        font-weight: bold;
        font-size: 1.8rem;
        color: #ff6b35 !important;
        text-decoration: none;
        transition: all 0.3s ease;
    }
    
    .navbar-brand:hover {
        color: #f7931e !important;
        transform: scale(1.05);
    }
    
    .navbar-brand img {
        height: 50px;
        width: auto;
        transition: transform 0.3s ease;
    }
    
    .navbar-brand:hover img {
        transform: scale(1.1);
    }
    
    .search-container {
        position: relative;
        flex-grow: 1;
        max-width: 600px;
        margin: 0 2rem;
    }
    
    .search-form {
        position: relative;
        width: 100%;
    }
    
    .search-input {
        width: 100%;
        padding: 12px 50px 12px 20px;
        border: none;
        border-radius: 50px;
        font-size: 1rem;
        background: rgba(255,255,255,0.95);
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        transition: all 0.3s ease;
    }
    
    .search-input:focus {
        outline: none;
        background: white;
        box-shadow: 0 6px 25px rgba(0,0,0,0.15);
        transform: translateY(-2px);
    }
    
    .search-btn {
        position: absolute;
        right: 5px;
        top: 50%;
        transform: translateY(-50%);
        background: #ff6b35;
        border: none;
        border-radius: 50%;
        width: 40px;
        height: 40px;
        color: white;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .search-btn:hover {
        background: #f7931e;
        transform: translateY(-50%) scale(1.1);
    }
    
    .navbar-icons {
        display: flex;
        align-items: center;
        gap: 1rem;
    }
    
    .icon-link {
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 45px;
        height: 45px;
        border-radius: 50%;
        background: rgba(255,255,255,0.1);
        color: white;
        text-decoration: none;
        transition: all 0.3s ease;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255,255,255,0.2);
    }
    
    .icon-link:hover {
        background: #ff6b35;
        color: white;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(255,107,53,0.3);
    }
    
    .icon-link img {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        transition: transform 0.3s ease;
    }
    
    .icon-link:hover img {
        transform: scale(1.1);
    }
    
    .badge-notification {
        position: absolute;
        top: -5px;
        right: -5px;
        background: #e74c3c;
        color: white;
        border-radius: 50%;
        width: 20px;
        height: 20px;
        font-size: 0.75rem;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        border: 2px solid white;
    }
    
    .mobile-toggle {
        display: none;
        background: rgba(255,255,255,0.1);
        border: 1px solid rgba(255,255,255,0.2);
        color: white;
        padding: 10px;
        border-radius: 8px;
        transition: all 0.3s ease;
    }
    
    .mobile-toggle:hover {
        background: #ff6b35;
        color: white;
    }
    
    .dropdown-menu {
        background: #2c3e50;
        border: 1px solid rgba(255,255,255,0.2);
        border-radius: 10px;
    }
    
    .dropdown-item {
        color: white;
        transition: all 0.3s ease;
    }
    
    .dropdown-item:hover {
        background: #ff6b35;
        color: white;
    }
    
    @media (max-width: 992px) {
        .search-container {
            margin: 1rem 0;
            order: 3;
            flex-basis: 100%;
        }
        
        .navbar-icons {
            gap: 0.5rem;
        }
        
        .icon-link {
            width: 40px;
            height: 40px;
        }
        
        .mobile-toggle {
            display: block;
        }
    }
    
    @media (max-width: 576px) {
        .navbar-custom {
            padding: 10px 0;
        }
        
        .navbar-brand img {
            height: 40px;
        }
        
        .icon-link {
            width: 35px;
            height: 35px;
        }
        
        .icon-link img {
            width: 20px;
            height: 20px;
        }
        
        .search-input {
            padding: 10px 45px 10px 15px;
            font-size: 0.9rem;
        }
    }
</style>

<nav class="navbar navbar-expand-lg navbar-custom">
    <div class="container">
        <!-- Logo -->
        <a class="navbar-brand d-flex align-items-center" href="index.jsp">
            <img src="Image/logo.png" alt="Nerdvana Logo" class="me-2">
            <span>Nerdvana</span>
        </a>
        
        <!-- Mobile Toggle -->
        <button class="navbar-toggler mobile-toggle" type="button" data-bs-toggle="collapse" 
                data-bs-target="#navbarContent" aria-controls="navbarContent" 
                aria-expanded="false" aria-label="Toggle navigation">
            <i class="fas fa-bars"></i>
        </button>
        
        <!-- Navbar Content -->
        <div class="collapse navbar-collapse" id="navbarContent">
            <!-- Search Bar -->
            <div class="search-container">
                <form class="search-form" action="ricerca" method="GET">
                    <input type="text" 
                           name="q" 
                           class="search-input" 
                           placeholder="Cerca manga, fumetti, giochi..." 
                           autocomplete="off">
                    <button type="submit" class="search-btn">
                        <i class="fas fa-search"></i>
                    </button>
                </form>
            </div>
            
            <!-- Icons -->
            <div class="navbar-icons">
                <!-- User Icon -->
                <%
                if (utente != null) {
                    // Utente loggato
                %>
                    <a href="profilo" class="icon-link" title="Il mio profilo">
                        <img src="Image/utente.jpg" alt="Profilo">
                    </a>
                <%
                } else {
                    // Utente non loggato
                %>
                    <div class="dropdown">
                        <a href="#" class="icon-link dropdown-toggle" data-bs-toggle="dropdown" 
                           title="Accedi o Registrati">
                            <img src="Image/utente.jpg" alt="Utente">
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="login.jsp">
                                <i class="fas fa-sign-in-alt me-2"></i>Accedi
                            </a></li>
                            <li><a class="dropdown-item" href="registrazione.jsp">
                                <i class="fas fa-user-plus me-2"></i>Registrati
                            </a></li>
                        </ul>
                    </div>
                <%
                }
                %>
                
                <!-- Wishlist Icon -->
                <a href="wishlist" class="icon-link" title="La mia Wishlist">
                    <img src="Image/wishList.jpg" alt="Wishlist">
                    <%
                    if (wishlistCount != null && wishlistCount > 0) {
                    %>
                        <span class="badge-notification"><%=wishlistCount%></span>
                    <%
                    }
                    %>
                </a>
                
                <!-- Cart Icon -->
                <a href="carrello" class="icon-link" title="Il mio Carrello">
                    <img src="Image/carrello.jpg" alt="Carrello">
                    <%
                    if (cartCount != null && cartCount > 0) {
                    %>
                        <span class="badge-notification"><%=cartCount%></span>
                    <%
                    }
                    %>
                </a>
            </div>
        </div>
    </div>
</nav>

<script>
    // Auto-complete per la ricerca (opzionale)
    $(document).ready(function() {
        $('.search-input').on('input', function() {
            var query = $(this).val();
            if (query.length > 2) {
                // Qui puoi implementare l'auto-complete
                // $.ajax per suggerimenti di ricerca
            }
        });
        
        // Gestione mobile search
        $('.search-form').on('submit', function(e) {
            var query = $('.search-input').val().trim();
            if (query === '') {
                e.preventDefault();
                $('.search-input').focus();
            }
        });
    });
</script>