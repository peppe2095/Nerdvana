(function() {
  const state = { page: 1, size: 8, totalPages: 1, tipo: null };
  const qs = (sel, root = document) => root.querySelector(sel);
  const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  const productsById = new Map();

  function toast(msg) { alert(msg); }

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
      const parts = (window.location.pathname || '').split('/');
      const ctx = parts.length > 1 && parts[1] ? '/' + parts[1] : '';
      if (!u) return ctx + '/Image/logo.png';
      const devPrefix = '/Nerdvana/src/main/webapp/';
      if (u.indexOf(devPrefix) === 0) u = u.substring(devPrefix.length);
      if (u.startsWith('/')) u = u.slice(1);
      const finalUrl = ctx + '/' + u;
      return encodeURI(finalUrl);
    } catch (e) {
      const parts = (window.location.pathname || '').split('/');
      const ctx = parts.length > 1 && parts[1] ? '/' + parts[1] : '';
      return ctx + '/Image/logo.png';
    }
  }

  function loadArticoli() {
    const container = qs('#prodotti');
    if (container) container.innerHTML = '<div class="col-12 text-center text-muted">Caricamento...</div>';
    const params = new URLSearchParams({ format: 'json', page: String(state.page), size: String(state.size) });
    if (state.tipo) params.append('tipo', state.tipo);
    // Costruisce URL relativo al context path per evitare /Jsp/Admin/getArticoli
    const parts = (window.location.pathname || '').split('/');
    const ctx = parts.length > 1 && parts[1] ? '/' + parts[1] : '';
    $.ajax({ url: ctx + '/getArticoli?' + params.toString(), method: 'GET', dataType: 'json' })
      .done(renderArticoli)
      .fail(function() { if (container) container.innerHTML = '<div class="col-12 text-danger">Errore nel caricamento</div>'; });
  }

  function renderArticoli(data) {
    const container = qs('#prodotti');
    if (!container) return;
    const items = Array.isArray(data.items) ? data.items : [];
    state.totalPages = data.totalPages || 1;
    state.page = data.page || 1;

    productsById.clear();
    items.forEach(p => { if (p && typeof p.id === 'number') productsById.set(p.id, p); });

    if (items.length === 0) {
      container.innerHTML = '<div class="col-12 text-center text-muted">Nessun prodotto trovato.</div>';
    } else {
      container.innerHTML = items.map((p, idx) => cardHtml(p, idx)).join('');
    }

    const pageNum = qs('#pageNum');
    const totalPages = qs('#totalPages');
    if (pageNum) pageNum.textContent = String(state.page);
    if (totalPages) totalPages.textContent = String(state.totalPages);

    const prevBtn = qs('#prevPage');
    const nextBtn = qs('#nextPage');
    if (prevBtn) prevBtn.disabled = state.page <= 1;
    if (nextBtn) nextBtn.disabled = state.page >= state.totalPages;

    // (admin) niente binding per carrello e wishlist

    // binding edit
    qsa('.btn-edit-articolo').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = Number(btn.getAttribute('data-id'));
        const p = productsById.get(id);
        if (!p) return;
        qs('#edit-id').value = p.id;
        qs('#edit-numeroSeriale').value = p.numeroSeriale || '';
        qs('#edit-nome').value = p.nome || '';
        qs('#edit-tipo').value = p.tipo || '';
        qs('#edit-prezzo').value = p.prezzo != null ? p.prezzo : '';
        qs('#edit-quantita').value = p.quantita != null ? p.quantita : '';
        qs('#edit-descrizione').value = p.descrizione || '';
        qs('#edit-url').value = p.url || '';
        const modalEl = document.getElementById('editArticoloModal');
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
      });
    });

    // binding delete
    qsa('.btn-delete-articolo').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-id');
        if (!confirm('Sei sicuro di voler eliminare questo articolo?')) return;
        $.ajax({ url: '../../API/admin/articoli/delete', method: 'POST', data: { id: id }, dataType: 'json' })
          .done(function(res){ if (res && res.success) { toast('Articolo eliminato'); loadArticoli(); } else { alert(res.message || 'Errore eliminazione'); } })
          .fail(function(){ alert('Errore comunicazione server'); });
      });
    });
  }

  function cardHtml(p, idx) {
    const img = normalizeUrl(p.url);
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
            <div class="mt-auto d-flex flex-wrap gap-2">
              <button class="btn btn-sm btn-outline-primary btn-edit-articolo" data-id="${p.id}">Modifica</button>
              <button class="btn btn-sm btn-outline-danger btn-delete-articolo" data-id="${p.id}">Elimina</button>
            </div>
          </div>
        </div>
      </div>`;
  }

  function bindFilters() {
    qsa('.tipo-link').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const t = btn.getAttribute('data-tipo');
        state.tipo = t || null;
        state.page = 1;
        loadArticoli();
      });
    });
  }

  function bindPagination() {
    const prevBtn = qs('#prevPage');
    const nextBtn = qs('#nextPage');
    if (prevBtn) prevBtn.addEventListener('click', () => { if (state.page > 1) { state.page--; loadArticoli(); } });
    if (nextBtn) nextBtn.addEventListener('click', () => { if (state.page < state.totalPages) { state.page++; loadArticoli(); } });
  }

  function bindEditSave() {
    const btnSave = qs('#btnSaveArticolo');
    if (!btnSave) return;
    btnSave.addEventListener('click', function() {
      const form = qs('#formEditArticolo');
      const data = Object.fromEntries(new FormData(form).entries());
      $.ajax({ url: '../../API/admin/articoli/update', method: 'POST', data: data, dataType: 'json' })
        .done(function(res){ if (res && res.success) { toast('Articolo aggiornato'); const modalEl = document.getElementById('editArticoloModal'); const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl); modal.hide(); loadArticoli(); } else { alert(res.message || 'Errore aggiornamento'); } })
        .fail(function(){ alert('Errore comunicazione server'); });
    });
  }

  document.addEventListener('DOMContentLoaded', function() {
    bindFilters();
    bindPagination();
    bindEditSave();
    loadArticoli();
  });
})();
