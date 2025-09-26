<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="Model.Articolo" %>
<%@ page import="Model.Utente" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    // Recupera gli attributi dalla servlet
    List<Articolo> articoli = (List<Articolo>) request.getAttribute("articoli");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalArticoli = (Integer) request.getAttribute("totalArticoli");
    Integer pageSize = (Integer) request.getAttribute("pageSize");
    Integer startPage = (Integer) request.getAttribute("startPage");
    Integer endPage = (Integer) request.getAttribute("endPage");
    String categoria = (String) request.getAttribute("categoria");
    Utente utente = (Utente) session.getAttribute("utente");
    
    // Inizializza valori di default se nulli
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalArticoli == null) totalArticoli = 0;
    if (pageSize == null) pageSize = 12;
    if (startPage == null) startPage = 1;
    if (endPage == null) endPage = 1;
    if (articoli == null) articoli = new ArrayList<Articolo>();
    
    // Formattatore per i prezzi
    DecimalFormat df = new DecimalFormat("#,##0.00");
%>

<style>
    .product-card {
        background: white;
        border-radius: 15px;
        overflow: hidden;
        box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        transition: all 0.3s ease;
        height: 100%;
        position: relative;
        border: 1px solid #f0f0f0;
    }
    
    .product-card:hover {
        transform: translateY(-8px);
        box-shadow: 0 15px 35px rgba(0,0,0,0.15);
        border-color: #ff6b35;
    }
    
    .product-image {
        position: relative;
        overflow: hidden;
        height: 250px;
        background: #f8f9fa;
    }
    
    .product-image img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
    }
    
    .product-card:hover .product-image img {
        transform: scale(1.1);
    }
    
    .product-badge {
        position: absolute;
        top: 10px;
        right: 10px;
        background: #ff6b35;
        color: white;
        padding: 4px 8px;
        border-radius: 15px;
        font-size: 0.75rem;
        font-weight: bold;
        text-transform: uppercase;
        z-index: 2;
    }
    
    .product-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, rgba(255,107,53,0.9), rgba(247,147,30,0.9));
        opacity: 0;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 3;
    }
    
    .product-card:hover .product-overlay {
        opacity: 1;
    }
    
    .overlay-buttons {
        display: flex;
        gap: 10px;
    }
    
    .overlay-btn {
        background: white;
        color: #ff6b35;
        border: none;
        border-radius: 50%;
        width: 45px;
        height: 45px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;
        cursor: pointer;
        font-size: 1.1rem;
    }
    
    .overlay-btn:hover {
        background: #2c3e50;
        color: white;
        transform: scale(1.1);
    }
    
    .product-body {
        padding: 20px;
        display: flex;
        flex-direction: column;
        height: calc(100% - 250px);
    }
    
    .product-category {
        color: #95a5a6;
        font-size: 0.8rem;
        text-transform: uppercase;
        font-weight: 600;
        letter-spacing: 0.5px;
        margin-bottom: 5px;
    }
    
    .product-title {
        font-size: 1.1rem;
        font-weight: bold;
        color: #2c3e50;
        margin-bottom: 10px;
        line-height: 1.3;
        height: 2.6em;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    
    .product-description {
        color: #7f8c8d;
        font-size: 0.85rem;
        line-height: 1.4;
        margin-bottom: 15px;
        flex-grow: 1;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    
    .product-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: auto;
    }
    
    .product-price {
        font-size: 1.3rem;
        font-weight: bold;
        color: #ff6b35;
    }
    
    .product-stock {
        font-size: 0.8rem;
        color: #27ae60;
        font-weight: 500;
    }
    
    .stock-low {
        color: #f39c12;
    }
    
    .stock-out {
        color: #e74c3c;
    }
    
    .pagination-custom {
        margin-top: 3rem;
        display: flex;
        justify-content: center;
    }
    
    .pagination-custom .page-link {
        color: #2c3e50;
        border: 1px solid #dee2e6;
        padding: 10px 15px;
        margin: 0 2px;
        border-radius: 8px;
        transition: all 0.3s ease;
        text-decoration: none;
    }
    
    .pagination-custom .page-item.active .page-link {
        background: #ff6b35;
        border-color: #ff6b35;
        color: white;
    }
    
    .pagination-custom .page-link:hover {
        background: #f7931e;
        border-color: #f7931e;
        color: white;
        transform: translateY(-2px);
    }
    
    .no-products {
        text-align: center;
        padding: 60px 20px;
        color: #7f8c8d;
    }
    
    .no-products i {
        font-size: 4rem;
        margin-bottom: 20px;
        color: #bdc3c7;
    }
    
    @media (max-width: 768px) {
        .product-image {
            height: 200px;
        }
        
        .product-body {
            padding: 15px;
            height: calc(100% - 200px);
        }
        
        .product-title {
            font-size: 1rem;
        }
        
        .overlay-btn {
            width: 40px;
            height: 40px;
            font-size: 1rem;
        }
        
        .pagination-custom .page-link {
            padding: 8px 12px;
            font-size: 0.9rem;
        }
    }
</style>

<!-- Prodotti Grid -->
<%
if (articoli != null && !articoli.isEmpty()) {
%>
    <div class="row">
        <%
        for (Articolo articolo : articoli) {
        %>
            <div class="col-xl-3 col-lg-4 col-md-6 col-sm-6 mb-4">
                <div class="product-card">
                    <div class="product-image">
                        <img src="<%=articolo.getUrl()%>" alt="<%=articolo.getNome()%>" 
                             onerror="this.src='/Nerdvana/src/main/webapp/Image/placeholder.jpg'">
                        
                        <!-- Badge Categoria -->
                        <div class="product-badge"><%=articolo.getTipo().name()%></div>
                        
                        <!-- Overlay con pulsanti -->
                        <div class="product-overlay">
                            <div class="overlay-buttons">
                                <button class="overlay-btn" 
                                        onclick="viewProduct(<%=articolo.getId()%>)" 
                                        title="Visualizza Dettagli">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <%
                                if (articolo.getQuantita() > 0) {
                                %>
                                    <button class="overlay-btn" 
                                            onclick="addToCart(<%=articolo.getId()%>)" 
                                            title="Aggiungi al Carrello">
                                        <i class="fas fa-shopping-cart"></i>
                                    </button>
                                <%
                                }
                                %>
                                <button class="overlay-btn" 
                                        onclick="addToWishlist(<%=articolo.getId()%>)" 
                                        title="Aggiungi alla Wishlist">
                                    <i class="fas fa-heart"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="product-body">
                        <div class="product-category"><%=articolo.getTipo().name()%></div>
                        <h5 class="product-title"><%=articolo.getNome()%></h5>
                        <p class="product-description">
                            <%
                            if (articolo.getDescrizione() != null && !articolo.getDescrizione().trim().isEmpty()) {
                                out.print(articolo.getDescrizione());
                            } else {
                                out.print("Scopri questo fantastico prodotto della categoria " + articolo.getTipo().name() + ".");
                            }
                            %>
                        </p>
                        
                        <div class="product-footer">
                            <div class="product-price">
                                €<%=df.format(articolo.getPrezzo())%>
                            </div>
                            <div class="product-stock <%=(articolo.getQuantita() <= 5 && articolo.getQuantita() > 0) ? "stock-low" : ""%><%=(articolo.getQuantita() == 0) ? "stock-out" : ""%>">
                                <%
                                if (articolo.getQuantita() == 0) {
                                    out.print("Non disponibile");
                                } else if (articolo.getQuantita() <= 5) {
                                    out.print("Solo " + articolo.getQuantita() + " rimasti");
                                } else {
                                    out.print("Disponibile");
                                }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        <%
        }
        %>
    </div>
    
    <!-- Paginazione -->
    <%
    if (totalPages > 1) {
    %>
        <nav class="pagination-custom">
            <ul class="pagination">
                <!-- Previous -->
                <%
                if (currentPage > 1) {
                %>
                    <li class="page-item">
                        <a class="page-link" href="javascript:changePage(<%=currentPage - 1%>)">
                            <i class="fas fa-chevron-left"></i>
                        </a>
                    </li>
                <%
                }
                %>
                
                <!-- Numeri di pagina -->
                <%
                for (int pageNum = startPage; pageNum <= endPage; pageNum++) {
                %>
                    <li class="page-item <%=(pageNum == currentPage) ? "active" : ""%>">
                        <a class="page-link" href="javascript:changePage(<%=pageNum%>)"><%=pageNum%></a>
                    </li>
                <%
                }
                %>
                
                <!-- Next -->
                <%
                if (currentPage < totalPages) {
                %>
                    <li class="page-item">
                        <a class="page-link" href="javascript:changePage(<%=currentPage + 1%>)">
                            <i class="fas fa-chevron-right"></i>
                        </a>
                    </li>
                <%
                }
                %>
            </ul>
        </nav>
    <%
    }
    %>
<%
} else {
%>
    <div class="no-products">
        <i class="fas fa-box-open"></i>
        <h3>Nessun prodotto trovato</h3>
        <p>Non ci sono prodotti disponibili al momento.</p>
    </div>
<%
}
%>

<script>
    function viewProduct(articleId) {
        window.location.href = 'articolo?id=' + articleId;
    }
    
    function addToCart(articleId) {
        <%
        if (utente != null) {
        %>
            $.ajax({
                url: 'carrello',
                type: 'POST',
                data: {
                    action: 'add',
                    articleId: articleId,
                    quantity: 1
                },
                success: function(response) {
                    if (response.success) {
                        showNotification('Prodotto aggiunto al carrello!', 'success');
                        updateCartCount(response.cartCount);
                    } else {
                        showNotification(response.message || 'Errore nell\'aggiunta al carrello', 'error');
                    }
                },
                error: function() {
                    showNotification('Errore di connessione', 'error');
                }
            });
        <%
        } else {
        %>
            showNotification('Devi effettuare il login per aggiungere prodotti al carrello', 'warning');
            setTimeout(function() {
                window.location.href = 'login.jsp';
            }, 2000);
        <%
        }
        %>
    }
    
    function addToWishlist(articleId) {
        <%
        if (utente != null) {
        %>
            $.ajax({
                url: 'wishlist',
                type: 'POST',
                data: {
                    action: 'add',
                    articleId: articleId
                },
                success: function(response) {
                    if (response.success) {
                        showNotification('Prodotto aggiunto alla wishlist!', 'success');
                        updateWishlistCount(response.wishlistCount);
                    } else {
                        showNotification(response.message || 'Errore nell\'aggiunta alla wishlist', 'error');
                    }
                },
                error: function() {
                    showNotification('Errore di connessione', 'error');
                }
            });
        <%
        } else {
        %>
            showNotification('Devi effettuare il login per aggiungere prodotti alla wishlist', 'warning');
            setTimeout(function() {
                window.location.href = 'login.jsp';
            }, 2000);
        <%
        }
        %>
    }
    
    function showNotification(message, type) {
        // Rimuovi notifiche esistenti
        $('.notification').remove();
        
        var alertClass = 'alert-info';
        var icon = 'fas fa-info-circle';
        
        switch(type) {
            case 'success':
                alertClass = 'alert-success';
                icon = 'fas fa-check-circle';
                break;
            case 'error':
                alertClass = 'alert-danger';
                icon = 'fas fa-exclamation-circle';
                break;
            case 'warning':
                alertClass = 'alert-warning';
                icon = 'fas fa-exclamation-triangle';
                break;
        }
        
        var notification = $('<div class="notification alert ' + alertClass + ' alert-dismissible fade show position-fixed" style="top: 20px; right: 20px; z-index: 9999; max-width: 350px;">' +
            '<i class="' + icon + ' me-2"></i>' + message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>');
            
        $('body').append(notification);
        
        // Auto-remove dopo 5 secondi
        setTimeout(function() {
            notification.alert('close');
        }, 5000);
    }
    
    function updateCartCount(count) {
        // Aggiorna il contatore del carrello nell'header
        var badge = $('.icon-link[href="carrello"] .badge-notification');
        if (count > 0) {
            if (badge.length) {
                badge.text(count);
            } else {
                $('.icon-link[href="carrello"]').append('<span class="badge-notification">' + count + '</span>');
            }
        } else {
            badge.remove();
        }
    }
    
    function updateWishlistCount(count) {
        // Aggiorna il contatore della wishlist nell'header
        var badge = $('.icon-link[href="wishlist"] .badge-notification');
        if (count > 0) {
            if (badge.length) {
                badge.text(count);
            } else {
                $('.icon-link[href="wishlist"]').append('<span class="badge-notification">' + count + '</span>');
            }
        } else {
            badge.remove();
        }
    }
</script>