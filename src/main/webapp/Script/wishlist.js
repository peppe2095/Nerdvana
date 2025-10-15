// File: src/main/webapp/Script/wishlist.js
// Gestione pagina Wishlist: lettura articoli da /api/wishlist/items e rimozione/aggiunta al carrello.
// Nota: usiamo jQuery $.ajax (no fetch) come richiesto. Commenti in italiano per facilitare la comprensione.
(function(){
  const qs = (sel, root=document) => root.querySelector(sel);

  document.addEventListener('DOMContentLoaded', () => {
    loadWishlist();
  });

  // Carica la lista dei prodotti presenti in wishlist
  function loadWishlist() {
    $.ajax({
      url: '../../api/wishlist/items',
      method: 'GET',
      dataType: 'json'
    }).done(function(out){
      if (!out || out.success === false) return showError(out && out.message);
      const items = out.data || [];
      renderGrid(items);
    }).fail(function(jq){
      if (jq && jq.status === 401) {
        showError('Per vedere la wishlist devi effettuare il <a href="../Accessi/Login.jsp">login</a>.');
        const empty = qs('#wlEmpty');
        if (empty) empty.style.display = 'none';
        const grid = qs('#wlGrid');
        if (grid) grid.innerHTML = '';
        return;
      }
      const msg = (jq.responseJSON && jq.responseJSON.message) || 'Errore nel caricamento della wishlist';
      showError(msg);
    });
  }

  // Disegna le card dei prodotti in wishlist
  function renderGrid(items) {
    const grid = qs('#wlGrid');
    const empty = qs('#wlEmpty');
    if (!items || items.length === 0) {
      if (grid) grid.innerHTML = '';
      if (empty) empty.style.display = 'block';
      return;
    }
    if (empty) empty.style.display = 'none';

    grid.innerHTML = items.map(a => `
      <div class="col-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card h-100 shadow-sm">
          ${(() => { const src = normalizeUrl(a.url); return `<img src="${escapeHtml(src)}" class="card-img-top product-img" alt="${escapeHtml(a.nome || '')}">`; })()}
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">${escapeHtml(a.nome || '')}</h5>
            <h6 class="card-subtitle mb-2 text-muted">${escapeHtml(a.tipo || '')}</h6>
            <p class="card-text fw-semibold">€ ${Number(a.prezzo || 0).toFixed(2)}</p>
            <div class="mt-auto d-flex gap-2">
              <button class="btn btn-sm btn-nerdvana btn-add-cart" data-id="${escapeHtml(a.numeroSeriale || '')}">Carrello</button>
              <button class="btn btn-sm btn-outline-danger btn-remove-wl" data-id="${escapeHtml(a.numeroSeriale || '')}">Rimuovi</button>
            </div>
          </div>
        </div>
      </div>
    `).join('');

    // Aggiungi al carrello
    $('.btn-add-cart').off('click').on('click', function(){
      const id = $(this).data('id');
      $.ajax({ url: '../../api/cart/add', method: 'POST', data: { id, qty: 1 }, dataType: 'json' })
        .done(function(){ loadWishlist(); });
    });

    // Rimuovi dalla wishlist
    $('.btn-remove-wl').off('click').on('click', function(){
      const id = $(this).data('id');
      $.ajax({ url: '../../api/wishlist/remove', method: 'POST', data: { id }, dataType: 'json' })
        .done(function(){ loadWishlist(); });
    });
  }

  //è una vecchia funzione che ho fatto perché non mi funzionavano le URL.
  function normalizeUrl(raw){
    try{
      let u = (raw || '').trim();
      const base = '../../';
      if (!u) return base + 'Image/logo.png';
      const prefix = '/Nerdvana/src/main/webapp/';
      if (u.indexOf(prefix) === 0) u = u.substring(prefix.length);
      if (u.startsWith('/')) u = u.slice(1);
      return base + u;
    } catch(e){
      return '../../Image/logo.png';
    }
  }

  function showError(msg) {
    const box = qs('#wlError');
    if (box) { box.innerHTML = msg || 'Errore'; box.style.display = 'block'; }
    else { alert(msg || 'Errore'); }
  }

  function escapeHtml(str){
    return String(str)
      .replace(/&/g,'&amp;').replace(/</g,'&lt;')
      .replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;');
  }
})();
