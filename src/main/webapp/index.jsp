<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="Model.Articolo" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nerdvana - Il tuo Store di Cultura Nerd</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --primary-color: #ff6b35;
            --secondary-color: #f7931e;
            --dark-bg: #2c3e50;
            --light-bg: #ecf0f1;
        }

        body {
            font-family: 'Arial', sans-serif;
            margin: 0;
            padding: 0;
        }

        .hero-section {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            padding: 80px 0;
            position: relative;
            overflow: hidden;
        }

        .hero-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="%23ffffff" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="%23ffffff" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="%23ffffff" opacity="0.15"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
            animation: float 20s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-10px); }
        }

        .hero-content {
            position: relative;
            z-index: 2;
        }

        .hero-title {
            font-size: 3.5rem;
            font-weight: bold;
            margin-bottom: 1rem;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }

        .hero-subtitle {
            font-size: 1.3rem;
            margin-bottom: 2rem;
            opacity: 0.95;
        }

        .cta-buttons {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }

        .btn-cta {
            padding: 12px 30px;
            border-radius: 50px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            border: none;
        }

        .btn-primary-cta {
            background: var(--dark-bg);
            color: white;
        }

        .btn-primary-cta:hover {
            background: #34495e;
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
            color: white;
        }

        .btn-secondary-cta {
            background: transparent;
            color: white;
            border: 2px solid white;
        }

        .btn-secondary-cta:hover {
            background: white;
            color: var(--primary-color);
            transform: translateY(-2px);
        }

        .category-nav {
            background: var(--dark-bg);
            padding: 15px 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .category-nav .nav-link {
            color: white;
            font-weight: 500;
            padding: 10px 20px;
            border-radius: 25px;
            transition: all 0.3s ease;
            margin: 0 5px;
        }

        .category-nav .nav-link:hover {
            background: var(--primary-color);
            transform: translateY(-2px);
            color: white;
        }

        .slider-section {
            padding: 60px 0;
            background: var(--light-bg);
        }

        .section-title {
            text-align: center;
            font-size: 2.5rem;
            font-weight: bold;
            color: var(--dark-bg);
            margin-bottom: 1rem;
            position: relative;
        }

        .section-title::after {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            width: 80px;
            height: 4px;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            border-radius: 2px;
        }

        .slider-card {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            cursor: pointer;
            height: 400px;
        }

        .slider-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(0,0,0,0.15);
        }

        .slider-card img {
            width: 100%;
            height: 250px;
            object-fit: cover;
        }

        .slider-card-body {
            padding: 20px;
        }

        .slider-card-title {
            font-size: 1.3rem;
            font-weight: bold;
            color: var(--dark-bg);
            margin-bottom: 0.5rem;
        }

        .slider-card-price {
            font-size: 1.5rem;
            color: var(--primary-color);
            font-weight: bold;
        }

        .products-section {
            padding: 60px 0;
        }

        .loading-spinner {
            text-align: center;
            padding: 40px 0;
        }

        .spinner-border {
            color: var(--primary-color);
        }

        @media (max-width: 768px) {
            .hero-title {
                font-size: 2.5rem;
            }
            
            .hero-subtitle {
                font-size: 1.1rem;
            }
            
            .cta-buttons {
                justify-content: center;
            }
            
            .category-nav .nav-link {
                margin: 2px;
                padding: 8px 15px;
                font-size: 0.9rem;
            }
        }
    </style>
</head>
<body>
    <!-- Header Include -->
    <%@ include file="Jsp/Fragments/Header.jsp" %>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <div class="row align-items-center hero-content">
                <div class="col-lg-6">
                    <h1 class="hero-title">Scopri le novità</h1>
                    <p class="hero-subtitle">
                        Dal un'occhiata alle ultime novità, come il set Ghirlanda di Halloween LEGO®.<br>
                        Il tuo universo nerd ti aspetta!
                    </p>
                    <div class="cta-buttons">
                        <a href="#novita-slider" class="btn btn-cta btn-primary-cta">
                            <i class="fas fa-star me-2"></i>Scopri le Novità
                        </a>
                        <a href="#products-section" class="btn btn-cta btn-secondary-cta">
                            <i class="fas fa-shopping-bag me-2"></i>Tutti i Prodotti
                        </a>
                    </div>
                </div>
                <div class="col-lg-6 text-center">
                    <div style="font-size: 15rem; opacity: 0.1;">
                        <i class="fas fa-gamepad"></i>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Category Navigation -->
    <nav class="category-nav">
        <div class="container">
            <ul class="nav justify-content-center flex-wrap">
                <li class="nav-item">
                    <a class="nav-link" href="categoria?tipo=Manga">
                        <i class="fas fa-book me-2"></i>Manga
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="categoria?tipo=Fumetti">
                        <i class="fas fa-mask me-2"></i>Fumetti
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="categoria?tipo=BoardGame">
                        <i class="fas fa-chess me-2"></i>Board Game
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="categoria?tipo=Videogiochi">
                        <i class="fas fa-gamepad me-2"></i>Videogiochi
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="categoria?tipo=Funko">
                        <i class="fas fa-child me-2"></i>Funko
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="categoria?tipo=ActionFigure">
                        <i class="fas fa-robot me-2"></i>Action Figure
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- Novità della Settimana Slider -->
    <section class="slider-section" id="novita-slider">
        <div class="container">
            <h2 class="section-title">Novità della Settimana</h2>
            <p class="text-center text-muted mb-5">Le ultime novità selezionate per te</p>
            
            <div id="novitaCarousel" class="carousel slide" data-bs-ride="carousel">
                <div class="carousel-indicators">
                    <button type="button" data-bs-target="#novitaCarousel" data-bs-slide-to="0" class="active"></button>
                    <button type="button" data-bs-target="#novitaCarousel" data-bs-slide-to="1"></button>
                    <button type="button" data-bs-target="#novitaCarousel" data-bs-slide-to="2"></button>
                </div>
                
                <div class="carousel-inner">
                    <div class="carousel-item active">
                        <div class="row">
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=1'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/onePiece1.jpg" alt="One Piece Volume 1">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">One Piece Volume 1</h5>
                                        <p class="text-muted">Manga</p>
                                        <div class="slider-card-price">€7,99</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=2'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/batmanYeatOne.jpg" alt="Batman Year One">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">Batman: Year One</h5>
                                        <p class="text-muted">Fumetti</p>
                                        <div class="slider-card-price">€15,50</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=3'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/catanBordGame.jpg" alt="Catan">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">Catan</h5>
                                        <p class="text-muted">Board Game</p>
                                        <div class="slider-card-price">€39,99</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="carousel-item">
                        <div class="row">
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=4'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/FunkoPop!Pikachu.jpg" alt="Funko Pop Pikachu">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">Funko Pop! Pikachu</h5>
                                        <p class="text-muted">Funko</p>
                                        <div class="slider-card-price">€12,99</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=5'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/The Last of Us Part II.jpg" alt="The Last of Us Part II">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">The Last of Us Part II</h5>
                                        <p class="text-muted">Videogiochi</p>
                                        <div class="slider-card-price">€59,90</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=1'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/onePiece1.jpg" alt="One Piece Volume 1">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">One Piece Volume 1</h5>
                                        <p class="text-muted">Manga</p>
                                        <div class="slider-card-price">€7,99</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="carousel-item">
                        <div class="row">
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=2'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/batmanYeatOne.jpg" alt="Batman Year One">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">Batman: Year One</h5>
                                        <p class="text-muted">Fumetti</p>
                                        <div class="slider-card-price">€15,50</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=3'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/catanBordGame.jpg" alt="Catan">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">Catan</h5>
                                        <p class="text-muted">Board Game</p>
                                        <div class="slider-card-price">€39,99</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 mb-4">
                                <div class="slider-card" onclick="window.location.href='articolo?id=4'">
                                    <img src="/Nerdvana/src/main/webapp/Database/ImageDynamic/FunkoPop!Pikachu.jpg" alt="Funko Pop Pikachu">
                                    <div class="slider-card-body">
                                        <h5 class="slider-card-title">Funko Pop! Pikachu</h5>
                                        <p class="text-muted">Funko</p>
                                        <div class="slider-card-price">€12,99</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <button class="carousel-control-prev" type="button" data-bs-target="#novitaCarousel" data-bs-slide="prev">
                    <span class="carousel-control-prev-icon"></span>
                    <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#novitaCarousel" data-bs-slide="next">
                    <span class="carousel-control-next-icon"></span>
                    <span class="visually-hidden">Next</span>
                </button>
            </div>
        </div>
    </section>

    <!-- Products Section -->
    <section class="products-section" id="products-section">
        <div class="container">
            <h2 class="section-title">Tutti i Prodotti</h2>
            <p class="text-center text-muted mb-5">Esplora il nostro vasto catalogo</p>
            
            <!-- Loading Spinner -->
            <div id="loading-spinner" class="loading-spinner">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Caricamento...</span>
                </div>
                <p class="mt-3">Caricamento prodotti...</p>
            </div>
            
            <!-- Products Container -->
            <div id="products-container">
                <!-- I prodotti verranno caricati qui via AJAX -->
            </div>
            
            <!-- Pagination Container -->
            <div id="pagination-container" class="mt-4">
                <!-- La paginazione verrà caricata qui via AJAX -->
            </div>
        </div>
    </section>

    <!-- Footer Include -->
    <%@ include file="Jsp/Fragments/Footer.jsp" %>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
        $(document).ready(function() {
            // Carica i prodotti al caricamento della pagina
            loadProducts(1);
            
            // Smooth scroll per i link interni
            $('a[href^="#"]').on('click', function(event) {
                var target = $(this.getAttribute('href'));
                if( target.length ) {
                    event.preventDefault();
                    $('html, body').stop().animate({
                        scrollTop: target.offset().top - 100
                    }, 1000);
                }
            });
        });
        
        function loadProducts(page) {
            $('#loading-spinner').show();
            $('#products-container').hide();
            
            $.ajax({
                url: 'prodotti',
                type: 'GET',
                data: { 
                    page: page,
                    size: 12
                },
                success: function(response) {
                    $('#products-container').html(response);
                    $('#loading-spinner').hide();
                    $('#products-container').fadeIn();
                },
                error: function() {
                    $('#loading-spinner').hide();
                    $('#products-container').html('<div class="alert alert-danger">Errore nel caricamento dei prodotti.</div>');
                    $('#products-container').show();
                }
            });
        }
        
        // Funzione per gestire la paginazione (da chiamare dal file JSP dei prodotti)
        function changePage(page) {
            loadProducts(page);
            $('html, body').animate({
                scrollTop: $('#products-section').offset().top - 100
            }, 500);
        }
    </script>
</body>
</html>