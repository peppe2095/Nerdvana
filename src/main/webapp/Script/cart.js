// File: src/main/webapp/Script/cart.js
// Gestione della pagina Carrello: lettura articoli dal backend e operazioni di rimozione/aggiornamento quantità.
// Nota: usiamo jQuery $.ajax come richiesto (no fetch). Tutto il codice è commentato in italiano.
(function(){
    // Selettore rapido: ritorna il primo elemento che matcha il selettore CSS
    const qs = (sel, root=document) => root.querySelector(sel);
    const qsa = (sel, root=document) => Array.from(root.querySelectorAll(sel));

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
        const btnCheckout = qs('#btnCheckout');

        if (!items || items.length === 0) {
            if (body) body.innerHTML = '';
            if (table) table.style.display = 'none';
            if (empty) empty.style.display = 'block';
            if (totalEl) totalEl.textContent = '€ 0,00';
            if (btnCheckout) btnCheckout.style.display = 'none';
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
        if (btnCheckout) {
            btnCheckout.style.display = '';
            btnCheckout.onclick = function(){ openCheckoutModal(); };
        }

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

    // Apertura modale e caricamento carte
    function openCheckoutModal(){
        const ckErr = qs('#ckError');
        if (ckErr) { ckErr.classList.add('d-none'); ckErr.textContent=''; }
        const cardsBox = qs('#cardsBox');
        const noCards = qs('#noCardsBox');
        const btnAdd = qs('#btnAddCard');
        const btnConf = qs('#btnConfermaOrdine');
        if (cardsBox) cardsBox.innerHTML = '<div class="text-muted">Caricamento metodi di pagamento...</div>';
        if (noCards) noCards.classList.add('d-none');
        if (btnAdd) btnAdd.classList.add('d-none');
        if (btnConf) btnConf.disabled = true;

        // Carica carte salvate
        $.ajax({ url: '../../areaRiservataServlet', method: 'GET', dataType: 'json' })
            .done(function(res){
                if (!res || res.success === false) return showCkErr((res && res.message) || 'Errore nel recupero dei metodi di pagamento');
                const data = res.data || res;
                const carte = data.carte || [];
                renderCards(carte);
            }).fail(function(jq){
            showCkErr((jq.responseJSON && jq.responseJSON.message) || 'Errore di rete');
        });

        // Mostra modale
        const modalEl = document.getElementById('checkoutModal');
        if (modalEl && window.bootstrap) new bootstrap.Modal(modalEl).show();

        // Bind pulsanti
        if (btnAdd) {
            btnAdd.onclick = function(){ addCardAndReload(); };
        }
        if (btnConf) {
            btnConf.onclick = function(){
                const selected = document.querySelector('input[name="payCardId"]:checked');
                if (!selected) return showCkErr('Seleziona un metodo di pagamento');
                const val = selected.value;
                let data = {};
                if (val === 'new') {
                    const nome = qs('#newCcNome').value.trim();
                    let numero = qs('#newCcNumero').value.replace(/\s+/g,'');
                    const cvv = qs('#newCcCvv').value.trim();
                    const scadenzaMonth = qs('#newCcScadenza').value;
                    if (!nome || !/^\d{16}$/.test(numero) || !/^\d{3}$/.test(cvv) || !scadenzaMonth) {
                        return showCkErr('Compila correttamente tutti i campi della nuova carta');
                    }
                    const scad = scadenzaMonth + '-01';
                    data = { nomeTitolare: nome, numeroCarta: numero, cvv: cvv, scadenza: scad };
                } else {
                    data = { cardId: val };
                }
                $.ajax({ url: '../../api/order/create', method: 'POST', dataType: 'json', data: data })
                    .done(function(out){
                        if (!out || out.success === false) return showCkErr((out && out.message) || 'Errore nella creazione dell\'ordine');
                        const d = out.data || out;
                        if (d.summaryUrl) window.location.href = d.summaryUrl; else showCkErr('Riepilogo non disponibile');
                    }).fail(function(jq){ showCkErr((jq.responseJSON && jq.responseJSON.message) || 'Errore di rete'); });
            };
        }
    }

    function renderCards(carte){
        const cardsBox = qs('#cardsBox');
        const noCards = qs('#noCardsBox');
        const btnAdd = qs('#btnAddCard');
        const btnConf = qs('#btnConfermaOrdine');
        const newBox = qs('#newCardBox');

        if (!carte || carte.length === 0) {
            if (cardsBox) cardsBox.innerHTML = '<div class="text-muted">Nessuna carta trovata.</div>';
            if (noCards) noCards.classList.remove('d-none');
            if (btnAdd) btnAdd.classList.remove('d-none');
            if (btnConf) btnConf.disabled = true;
            // Mostra anche la possibilità di pagare con nuova carta direttamente
            if (newBox) newBox.classList.remove('d-none');
            return;
        }

        // Liste radio delle carte
        let html = '<div class="list-group">';
        carte.forEach(function(c){
            const id = c.id || '';
            const label = escapeHtml((c.nomeTitolare||'') + ' - ' + (c.numeroCarta||''));
            html += '<label class="list-group-item d-flex align-items-center gap-2">\n' +
                '  <input type="radio" name="payCardId" value="'+id+'" class="form-check-input me-2"/>\n' +
                '  <span>'+label+'</span>\n' +
                '</label>';
        });
        html += '</div>';
        if (cardsBox) cardsBox.innerHTML = html;

        // Mostra la sezione per nuova carta anche quando esistono carte
        if (newBox) newBox.classList.remove('d-none');

        // Abilita conferma quando selezionata una carta
        document.querySelectorAll('input[name="payCardId"]').forEach(function(r){
            r.addEventListener('change', function(){ const btnConf = qs('#btnConfermaOrdine'); if (btnConf) btnConf.disabled = false; });
        });
    }

    function addCardAndReload(){
        const nome = qs('#ccNome').value.trim();
        let numero = qs('#ccNumero').value.replace(/\s+/g,'');
        const cvv = qs('#ccCvv').value.trim();
        const scadenzaMonth = qs('#ccScadenza').value; // yyyy-MM
        if (!nome || !/^[0-9]{16}$/.test(numero) || !/^[0-9]{3}$/.test(cvv) || !scadenzaMonth) {
            return showCkErr('Compila correttamente tutti i campi');
        }
        const scad = scadenzaMonth + '-01'; // server si aspetta yyyy-MM-dd
        $.ajax({ url: '../../areaRiservataServlet', method: 'POST', dataType: 'json', data: {
                nomeTitolare: nome, numeroCarta: numero, cvv: cvv, scadenza: scad
            }}).done(function(res){
            if (!res || res.success === false) return showCkErr((res && res.message) || 'Errore nel salvataggio della carta');
            const data = res.data || res; const carte = data.carte || [];
            renderCards(carte);
            // seleziona l'ultima carta
            setTimeout(function(){
                const radios = document.querySelectorAll('input[name="payCardId"]');
                if (radios.length>0) { radios[radios.length-1].checked = true; const btnConf = qs('#btnConfermaOrdine'); if (btnConf) btnConf.disabled = false; }
            }, 0);
            // Nascondi box inserimento
            const noCards = qs('#noCardsBox'); const btnAdd = qs('#btnAddCard');
            if (noCards) noCards.classList.add('d-none'); if (btnAdd) btnAdd.classList.add('d-none');
        }).fail(function(jq){ showCkErr((jq.responseJSON && jq.responseJSON.message) || 'Errore di rete'); });
    }

    function showCkErr(m){
        const e = qs('#ckError'); if (!e) return alert(m||'Errore');
        e.textContent = m || 'Errore'; e.classList.remove('d-none');
    }

    // Mostra un messaggio di errore in pagina (o alert di fallback)
    function showError(msg) {
        const box = qs('#cartError');
        if (box) { box.textContent = msg || 'Errore'; box.style.display = 'block'; }
        else { alert(msg || 'Errore'); }
    }

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

    // Escape basilare per evitare problemi XSS nelle stringhe iniettate in HTML
    function escapeHtml(str){
        return String(str)
            .replace(/&/g,'&amp;').replace(/</g,'&lt;')
            .replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;');
    }
})();
