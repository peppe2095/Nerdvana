// Carica gli articoli in modo paginato via AJAX
// Mostra cards Bootstrap per gli articoli con paginazione

(function() {
    /*valori default per la paginazione*/
  const state = {
    page: 1,
    size: 5,
    totalPages: 1,
    tipo: null
  };

  const qs = (sel, root=document) => root.querySelector(sel);
  const qsa = (sel, root=document) => Array.from(root.querySelectorAll(sel));

  document.addEventListener('DOMContentLoaded', () => {
    // Eventi navbar per filtro Tipo
    qsa('.tipo-link').forEach(a => {
      a.addEventListener('click', (e) => {
        e.preventDefault();
        const t = a.getAttribute('data-tipo');
        state.tipo = t || null;
        state.page = 1;
        loadArticoli();
      });
    });

    // Pulsanti paginazione
    const prevBtn = qs('#prevPage');
    const nextBtn = qs('#nextPage');
    if (prevBtn) prevBtn.addEventListener('click', () => { if (state.page > 1) { state.page--; loadArticoli(); } });
    if (nextBtn) nextBtn.addEventListener('click', () => { if (state.page < state.totalPages) { state.page++; loadArticoli(); } });

    // Pulsante aggiorna (inutile, da togliere)
    const refreshBtn = qs('#btn-aggiorna');
    if (refreshBtn) refreshBtn.addEventListener('click', () => loadArticoli());


    const logoutLink = qs('#logoutLink');
    if (logoutLink) logoutLink.addEventListener('click', (e) => {
      e.preventDefault();

      $.ajax({
        url: 'api/auth/logout',
        method: 'POST'
      }).done(function(){
        location.reload();
      }).fail(function(){
        alert('Errore durante il logout');
      });
    });

    // Avvio: carica la prima pagina
    loadArticoli();
  });

  // Carica gli articoli via AJAX (jQuery) rispettando il requisito: niente fetch
  function loadArticoli() {
    const container = qs('#prodotti');
    const placeholder = qs('#placeholder');
    if (placeholder) placeholder.textContent = 'Caricamento prodotti...';

    const params = new URLSearchParams({ format: 'json', page: String(state.page), size: String(state.size) });
    if (state.tipo) params.append('tipo', state.tipo);

    $.ajax({
      url: 'getArticoli?' + params.toString(),
      method: 'GET',
      dataType: 'json'
    }).done(function(data){
      renderArticoli(data);
    }).fail(function(jq){
      if (container) container.innerHTML = '<div class="col-12 text-danger">Errore nel caricamento</div>';
    });
  }

  function renderArticoli(data) {
    const container = qs('#prodotti');
    if (!container) return;

    const items = Array.isArray(data.items) ? data.items : [];
    state.totalPages = data.totalPages || 1;
    state.page = data.page || 1;

    if (items.length === 0) {
      container.innerHTML = '<div class="col-12 text-center text-muted">Nessun prodotto trovato.</div>';
    } else {
      container.innerHTML = items.map((p, idx) => cardHtml(p, idx)).join('');
    }

    // Aggiorna paginazione
    const pageNum = qs('#pageNum');
    const totalPages = qs('#totalPages');
    if (pageNum) pageNum.textContent = String(state.page);
    if (totalPages) totalPages.textContent = String(state.totalPages);

    const prevBtn = qs('#prevPage');
    const nextBtn = qs('#nextPage');
    if (prevBtn) prevBtn.disabled = state.page <= 1;
    if (nextBtn) nextBtn.disabled = state.page >= state.totalPages;

    // Collega i bottoni "Dettagli"
    qsa('.btn-toggle').forEach(btn => {
      btn.addEventListener('click', () => {
        const targetId = btn.getAttribute('data-target');
        const el = qs(`#${targetId}`);
        if (el) el.classList.toggle('show');
      });
    });

    // Aggiungi al carrello (AJAX con jQuery)
    qsa('.btn-add-cart').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-id');
        $.ajax({
          url: 'api/cart/add',
          method: 'POST',
          data: { id: id },
          dataType: 'json'
        }).done(function(out){
          if (out && out.success === false) {
            alert('Errore carrello: ' + (out.message || 'Errore'));
          } else {
            toast('Aggiunto al carrello');
          }
        }).fail(function(jq){
          alert('Errore carrello');
        });
      });
    });

    // Aggiungi alla wishlist (AJAX con jQuery)
    qsa('.btn-add-wishlist').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-id');
        $.ajax({
          url: 'api/wishlist/add',
          method: 'POST',
          data: { id: id },
          dataType: 'json'
        }).done(function(out){
          if (out && out.success === false) {
            alert('Errore wishlist: ' + (out.message || 'Errore'));
          } else {
            toast('Aggiunto alla wishlist');
          }
        }).fail(function(jq){
          if (jq && jq.status === 401) {
            if (confirm('Per usare la wishlist devi effettuare il login. Vuoi accedere ora?')) {
              window.location.href = 'Jsp/Accessi/Login.jsp';
            }
            return;
          }
          const msg = (jq && jq.responseJSON && jq.responseJSON.message) || 'Errore wishlist';
          alert(msg);
        });
      });
    });
  }

  function cardHtml(p, idx) {
    const img = normalizeUrl(p.url);
    const cid = `collapse-${idx}-${Math.random().toString(36).slice(2,7)}`;
    const prezzo = typeof p.prezzo === 'number' ? p.prezzo.toFixed(2) : p.prezzo;
    const tipo = p.tipo || '-';
    return `
      <div class="col-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card h-100 shadow-sm">
          <img src="${escapeHtml(img)}" class="card-img-top product-img" alt="${escapeHtml(p.nome || '')}">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">${escapeHtml(p.nome || '')}</h5>
            <h6 class="card-subtitle mb-2 text-muted">${escapeHtml(tipo)}</h6>
            <p class="card-text fw-semibold">€ ${prezzo}</p>
            <div class="mt-auto d-flex gap-2">
              <button class="btn btn-sm btn-nerdvana btn-add-cart" data-id="${escapeHtml(p.numeroSeriale || '')}" title="Aggiungi al carrello">Carrello</button>
              <button class="btn btn-sm btn-outline-nerdvana btn-add-wishlist" data-id="${escapeHtml(p.numeroSeriale || '')}" title="Aggiungi alla wishlist">Wishlist</button>
            </div>
            <div class="mt-2">
              <button class="btn btn-outline-primary btn-sm btn-toggle" data-target="${cid}">Dettagli</button>
            </div>
            <div id="${cid}" class="collapse mt-2">
              <p class="small mb-2">${escapeHtml(p.descrizione || 'Nessuna descrizione')}</p>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function normalizeUrl(raw) {
    try {
      let u = (raw || '').trim();
      if (!u) return 'Image/logo.png';
      const prefix = '/Nerdvana/src/main/webapp/';
      if (u.indexOf(prefix) === 0) u = u.substring(prefix.length);
      if (u.startsWith('/')) u = u.slice(1);
      return u || 'Image/logo.png';
    } catch (e) {
      return 'Image/logo.png';
    }
  }

  function toast(message) {
    try {
      const n = document.createElement('div');
      n.textContent = message;
      n.style.position = 'fixed';
      n.style.top = '16px';
      n.style.right = '16px';
      n.style.padding = '10px 14px';
      n.style.background = '#2c3e50';
      n.style.color = '#fff';
      n.style.borderRadius = '8px';
      n.style.boxShadow = '0 2px 10px rgba(0,0,0,0.2)';
      n.style.zIndex = '9999';
      document.body.appendChild(n);
      setTimeout(() => { n.remove(); }, 1500);
    } catch (_) { /* fallback */ alert(message); }
  }
})();
