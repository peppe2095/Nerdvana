// File: src/main/webapp/Script/cart.js
// Gestione della pagina Carrello: lettura articoli dal backend e operazioni di rimozione/aggiornamento quantità.
// Nota: usiamo jQuery $.ajax come richiesto (no fetch). Tutto il codice è commentato in italiano.
(function(){
  // Selettore rapido: ritorna il primo elemento che matcha il selettore CSS
  const qs = (sel, root=document) => root.querySelector(sel);

  // Al caricamento della pagina, recuperiamo il contenuto del carrello
  document.addEventListener('DOMContentLoaded', () => {
    loadCart();
  });

  // Carica i dettagli degli articoli nel carrello da /api/cart/items
  function loadCart() {
    $.ajax({
      url: '../../api/cart/items',
      method: 'GET',
      dataType: 'json'
    }).done(function(out){
      if (!out || out.success === false) return showError(out && out.message);
      const items = out.data || [];
      renderCart(items);
    }).fail(function(jq){
      const msg = (jq.responseJSON && jq.responseJSON.message) || 'Errore nel caricamento del carrello';
      showError(msg);
    });
  }

  // Renderizza la tabella del carrello partendo dalla lista di righe [{articolo: ArticoloDto, qty: number}, ...]
  function renderCart(items) {
    const body = qs('#cartBody');
    const table = qs('#cartTable');
    const empty = qs('#cartEmpty');
    const totalEl = qs('#cartGrandTotal');

    if (!items || items.length === 0) {
      if (body) body.innerHTML = '';
      if (table) table.style.display = 'none';
      if (empty) empty.style.display = 'block';
      if (totalEl) totalEl.textContent = '€ 0,00';
      return;
    }

    if (empty) empty.style.display = 'none';
    if (table) table.style.display = '';

    let grand = 0;
    body.innerHTML = items.map(row => {
      const a = row.articolo || {};
      const qty = Number(row.qty || 1);
      const prezzo = Number(a.prezzo || 0);
      const sub = prezzo * qty;
      grand += sub;
      return `
        <tr>
          <td>
            <div class="d-flex align-items-center gap-2">
              ${(() => { const src = normalizeUrl(a.url); return `<img src="${escapeHtml(src)}" alt="" style="width:64px; height:64px; object-fit:cover" />`; })()}
              <div>
                <div class="fw-semibold">${escapeHtml(a.nome || '')}</div>
                <div class="text-muted small">${escapeHtml(a.tipo || '')}</div>
              </div>
            </div>
          </td>
          <td class="text-end">€ ${prezzo.toFixed(2)}</td>
          <td class="text-center">
            <div class="btn-group btn-group-sm" role="group">
              <button class="btn btn-outline-secondary btn-dec" data-id="${escapeHtml(a.numeroSeriale || '')}">-</button>
              <span class="px-2">${qty}</span>
              <button class="btn btn-outline-secondary btn-inc" data-id="${escapeHtml(a.numeroSeriale || '')}">+</button>
            </div>
          </td>
          <td class="text-end">€ ${sub.toFixed(2)}</td>
          <td class="text-end">
            <button class="btn btn-sm btn-outline-danger btn-remove" data-id="${escapeHtml(a.numeroSeriale || '')}">Rimuovi</button>
          </td>
        </tr>
      `;
    }).join('');

    if (totalEl) totalEl.textContent = '€ ' + grand.toFixed(2);

    // Eventi per incrementare/decrementare quantità e rimuovere l'articolo
    $('.btn-inc').off('click').on('click', function(){
      const id = $(this).data('id');
      $.ajax({ url: '../../api/cart/add', method: 'POST', data: { id, qty: 1 }, dataType: 'json' })
        .done(loadCart);
    });
    $('.btn-dec').off('click').on('click', function(){
      const id = $(this).data('id');
      $.ajax({ url: '../../api/cart/remove', method: 'POST', data: { id, qty: 1 }, dataType: 'json' })
        .done(loadCart);
    });
    $('.btn-remove').off('click').on('click', function(){
      const id = $(this).data('id');
      // Rimuove completamente l'articolo (qty molto grande)
      $.ajax({ url: '../../api/cart/remove', method: 'POST', data: { id, qty: 9999 }, dataType: 'json' })
        .done(loadCart);
    });
  }

  // Mostra un messaggio di errore in pagina (o alert di fallback)
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
    const box = qs('#cartError');
    if (box) { box.textContent = msg || 'Errore'; box.style.display = 'block'; }
    else { alert(msg || 'Errore'); }
  }

  // Escape basilare per evitare problemi XSS nelle stringhe iniettate in HTML
  function escapeHtml(str){
    return String(str)
      .replace(/&/g,'&amp;').replace(/</g,'&lt;')
      .replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;');
  }
})();
