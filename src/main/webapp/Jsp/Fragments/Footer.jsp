<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .footer-custom {
        background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
        color: white;
        padding: 40px 0 20px;
        margin-top: 50px;
        position: relative;
        overflow: hidden;
    }
    
    .footer-custom::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 2px;
        background: linear-gradient(90deg, #ff6b35, #f7931e, #ff6b35);
    }
    
    .footer-content {
        position: relative;
        z-index: 2;
    }
    
    .footer-logo {
        display: flex;
        align-items: center;
        margin-bottom: 1rem;
    }
    
    .footer-logo img {
        height: 60px;
        width: auto;
        margin-right: 15px;
        filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
    }
    
    .footer-logo-text {
        font-size: 1.8rem;
        font-weight: bold;
        color: #ff6b35;
    }
    
    .footer-description {
        color: #bdc3c7;
        font-size: 1rem;
        line-height: 1.6;
        margin-bottom: 2rem;
        max-width: 400px;
    }
    
    .social-links {
        display: flex;
        gap: 1rem;
        margin-bottom: 2rem;
    }
    
    .social-link {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: rgba(255,255,255,0.1);
        color: white;
        text-decoration: none;
        transition: all 0.3s ease;
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255,255,255,0.2);
    }
    
    .social-link:hover {
        background: #ff6b35;
        color: white;
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(255,107,53,0.3);
    }
    
    .social-link img {
        width: 24px;
        height: 24px;
        transition: transform 0.3s ease;
    }
    
    .social-link:hover img {
        transform: scale(1.2);
    }
    
    .footer-links {
        margin-bottom: 2rem;
    }
    
    .footer-links h5 {
        color: #ff6b35;
        font-weight: bold;
        margin-bottom: 1rem;
        font-size: 1.1rem;
    }
    
    .footer-links ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }
    
    .footer-links li {
        margin-bottom: 0.5rem;
    }
    
    .footer-links a {
        color: #bdc3c7;
        text-decoration: none;
        transition: all 0.3s ease;
        font-size: 0.9rem;
    }
    
    .footer-links a:hover {
        color: #ff6b35;
        padding-left: 5px;
    }
    
    .footer-bottom {
        border-top: 1px solid rgba(255,255,255,0.1);
        padding-top: 20px;
        text-align: center;
        color: #95a5a6;
        font-size: 0.9rem;
    }
    
    .footer-bottom p {
        margin: 0;
    }
    
    .contact-info {
        margin-bottom: 2rem;
    }
    
    .contact-info h5 {
        color: #ff6b35;
        font-weight: bold;
        margin-bottom: 1rem;
        font-size: 1.1rem;
    }
    
    .contact-item {
        display: flex;
        align-items: center;
        margin-bottom: 0.5rem;
        color: #bdc3c7;
        font-size: 0.9rem;
    }
    
    .contact-item i {
        margin-right: 10px;
        color: #ff6b35;
        width: 16px;
    }
    
    @media (max-width: 768px) {
        .footer-custom {
            padding: 30px 0 15px;
            text-align: center;
        }
        
        .footer-logo {
            justify-content: center;
        }
        
        .footer-logo img {
            height: 50px;
        }
        
        .footer-logo-text {
            font-size: 1.5rem;
        }
        
        .footer-description {
            text-align: center;
            margin: 0 auto 2rem;
        }
        
        .social-links {
            justify-content: center;
        }
        
        .social-link {
            width: 45px;
            height: 45px;
        }
        
        .footer-links h5,
        .contact-info h5 {
            margin-top: 2rem;
            margin-bottom: 1rem;
        }
    }
</style>

<footer class="footer-custom">
    <div class="container">
        <div class="footer-content">
            <div class="row">
                <!-- Logo e Descrizione -->
                <div class="col-lg-4 col-md-6 mb-4">
                    <div class="footer-logo">
                        <img src="Image/logo.png" alt="Nerdvana Logo">
                        <div class="footer-logo-text">Nerdvana</div>
                    </div>
                    
                    <div class="footer-description">
                        Qua va la descrizione del sito e della sua mission nel mondo nerd. 
                        Il tuo universo di manga, fumetti, giochi e collectibles ti aspetta.
                    </div>
                    
                    <div class="social-links">
                        <a href="https://www.facebook.com/nerdvana" 
                           target="_blank" 
                           rel="noopener noreferrer" 
                           class="social-link"
                           title="Seguici su Facebook">
                            <img src="Image/Facebook.png" alt="Facebook">
                        </a>
                        <a href="https://www.instagram.com/nerdvana" 
                           target="_blank" 
                           rel="noopener noreferrer" 
                           class="social-link"
                           title="Seguici su Instagram">
                            <img src="Image/Instagram.png" alt="Instagram">
                        </a>
                    </div>
                </div>
                
                <!-- Link Utili -->
                <div class="col-lg-2 col-md-6 mb-4">
                    <div class="footer-links">
                        <h5>Categorie</h5>
                        <ul>
                            <li><a href="categoria?tipo=Manga">Manga</a></li>
                            <li><a href="categoria?tipo=Fumetti">Fumetti</a></li>
                            <li><a href="categoria?tipo=BoardGame">Board Game</a></li>
                            <li><a href="categoria?tipo=Videogiochi">Videogiochi</a></li>
                            <li><a href="categoria?tipo=Funko">Funko Pop</a></li>
                            <li><a href="categoria?tipo=ActionFigure">Action Figure</a></li>
                        </ul>
                    </div>
                </div>
                
                <!-- Servizi -->
                <div class="col-lg-2 col-md-6 mb-4">
                    <div class="footer-links">
                        <h5>Servizi</h5>
                        <ul>
                            <li><a href="account">Il mio Account</a></li>
                            <li><a href="ordini">I miei Ordini</a></li>
                            <li><a href="wishlist">Wishlist</a></li>
                            <li><a href="carrello">Carrello</a></li>
                            <li><a href="tracking">Tracking Ordini</a></li>
                        </ul>
                    </div>
                </div>
                
                <!-- Supporto -->
                <div class="col-lg-2 col-md-6 mb-4">
                    <div class="footer-links">
                        <h5>Supporto</h5>
                        <ul>
                            <li><a href="contatti">Contattaci</a></li>
                            <li><a href="faq">FAQ</a></li>
                            <li><a href="spedizioni">Spedizioni</a></li>
                            <li><a href="resi">Resi</a></li>
                            <li><a href="privacy">Privacy Policy</a></li>
                            <li><a href="termini">Termini d'uso</a></li>
                        </ul>
                    </div>
                </div>
                
                <!-- Contatti -->
                <div class="col-lg-2 col-md-6 mb-4">
                    <div class="contact-info">
                        <h5>Contatti</h5>
                        <div class="contact-item">
                            <i class="fas fa-envelope"></i>
                            <span>info@nerdvana.it</span>
                        </div>
                        <div class="contact-item">
                            <i class="fas fa-phone"></i>
                            <span>+39 123 456 789</span>
                        </div>
                        <div class="contact-item">
                            <i class="fas fa-map-marker-alt"></i>
                            <span>Via Nerd 123, Milano</span>
                        </div>
                        <div class="contact-item">
                            <i class="fas fa-clock"></i>
                            <span>Lun-Ven 9:00-18:00</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Footer Bottom -->
        <div class="footer-bottom">
            <p>&copy; 2025 Nerdvana. Tutti i diritti riservati. | Sviluppato con passione per la cultura nerd</p>
        </div>
    </div>
</footer>