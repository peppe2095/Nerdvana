// Script per gestire l'Area Riservata
(function() {
    'use strict';

    // Helper functions
    const qs = (sel, root = document) => root.querySelector(sel);
    const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

    // Funzione per escape HTML (previene XSS)
    function escapeHtml(str) {
        if (str === null || str === undefined) return '-';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // Funzione per formattare la data di scadenza
    function formatScadenza(dateString) {
        if (!dateString) return '-';
        try {
            const date = new Date(dateString);
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            return `${month}/${year}`;
        } catch (e) {
            return dateString;
        }
    }

    // Funzione per mostrare notifiche toast
    function showToast(message, type = 'success') {
        const toastContainer = qs('#toast-container');
        if (!toastContainer) {
            // Crea container se non esiste
            const container = document.createElement('div');
            container.id = 'toast-container';
            container.style.position = 'fixed';
            container.style.top = '20px';
            container.style.right = '20px';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `alert alert-${type} alert-dismissible fade show`;
        toast.role = 'alert';
        toast.innerHTML = `
            ${escapeHtml(message)}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        const container = qs('#toast-container');
        container.appendChild(toast);

        // Rimuovi dopo 3 secondi
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 150);
        }, 3000);
    }

    // Carica i dati dell'utente e le carte
    function loadUserData() {
        const loadingEl = qs('#loading-profilo');
        const contentEl = qs('#profilo-content');
        const errorEl = qs('#error-profilo');
        const errorMsg = qs('#error-message');

        // Mostra loading
        if (loadingEl) loadingEl.style.display = 'block';
        if (contentEl) contentEl.style.display = 'none';
        if (errorEl) errorEl.style.display = 'none';

        $.ajax({
            url: '../../areaRiservataServlet',
            method: 'GET',
            dataType: 'json'
        })
        .done(function(response) {
            // Nascondi loading
            if (loadingEl) loadingEl.style.display = 'none';

            // Verifica se la risposta è valida
            if (!response || response.success === false) {
                showError(response?.message || 'Errore nel caricamento dei dati');
                return;
            }

            // Estrai i dati dal response
            const data = response.data || response;
            const utente = data.utente;
            const carte = data.carte || [];

            // Popola i campi con i dati utente
            if (qs('#user-nome')) qs('#user-nome').textContent = escapeHtml(utente.nome);
            if (qs('#user-cognome')) qs('#user-cognome').textContent = escapeHtml(utente.cognome);
            if (qs('#user-email')) qs('#user-email').textContent = escapeHtml(utente.email);
            if (qs('#user-telefono')) qs('#user-telefono').textContent = escapeHtml(utente.telefono);
            if (qs('#user-dataNascita')) qs('#user-dataNascita').textContent = utente.dataNascita;
            if (qs('#user-ruolo')) qs('#user-ruolo').textContent = escapeHtml(utente.ruolo);
            if (qs('#user-indirizzo')) qs('#user-indirizzo').textContent = escapeHtml(utente.indirizzo);
            if (qs('#user-civico')) qs('#user-civico').textContent = escapeHtml(utente.numeroCivico);
            if (qs('#user-citta')) qs('#user-citta').textContent = escapeHtml(utente.cittaResidenza);
            if (qs('#user-cap')) qs('#user-cap').textContent = escapeHtml(utente.cap);

            // Popola le carte di credito
            displayCarte(carte);

            // Mostra il contenuto
            if (contentEl) contentEl.style.display = 'block';
        })
        .fail(function(jqXHR) {
            // Nascondi loading
            if (loadingEl) loadingEl.style.display = 'none';

            // Gestione errori specifici
            if (jqXHR.status === 401) {
                showError('Sessione scaduta. Effettua nuovamente il login.');
                setTimeout(() => {
                    window.location.href = '../Accessi/Login.jsp';
                }, 2000);
                return;
            }

            if (jqXHR.status === 404) {
                showError('Utente non trovato');
                return;
            }

            // Errore generico
            const errorMessage = jqXHR.responseJSON?.message || 'Errore nel caricamento dei dati';
            showError(errorMessage);
        });
    }

    // Visualizza le carte di credito
    function displayCarte(carte) {
        const carteContainer = qs('#carte-container');
        if (!carteContainer) return;

        if (!carte || carte.length === 0) {
            carteContainer.innerHTML = `
                <div class="alert alert-info">
                    <i class="bi bi-info-circle"></i> Non hai ancora aggiunto metodi di pagamento
                </div>
            `;
            return;
        }

        let html = '<div class="row g-3">';
        carte.forEach(carta => {
            html += `
                <div class="col-md-6">
                    <div class="card h-100 border-primary">
                        <div class="card-body">
                            <h6 class="card-title">
                                <i class="bi bi-credit-card"></i> ${escapeHtml(carta.nomeTitolare)}
                            </h6>
                            <p class="card-text mb-2">
                                <strong>Numero:</strong> ${escapeHtml(carta.numeroCarta)}
                            </p>
                            <p class="card-text mb-0">
                                <strong>Scadenza:</strong> ${formatScadenza(carta.scadenza)}
                            </p>
                        </div>
                    </div>
                </div>
            `;
        });
        html += '</div>';
        
        carteContainer.innerHTML = html;
    }

    // Mostra messaggio di errore
    function showError(message) {
        const errorEl = qs('#error-profilo');
        const errorMsg = qs('#error-message');
        
        if (errorMsg) errorMsg.textContent = message;
        if (errorEl) errorEl.style.display = 'block';
    }

    // Gestione cambio sezione
    function initSectionNavigation() {
        qsa('.nav-link[data-section]').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                
                const sectionName = this.getAttribute('data-section');
                
                // Rimuovi classe active da tutti i link
                qsa('.nav-link').forEach(l => l.classList.remove('active'));
                
                // Aggiungi active al link cliccato
                this.classList.add('active');
                
                // Nascondi tutte le sezioni
                qsa('.content-section').forEach(section => {
                    section.style.display = 'none';
                });
                
                // Mostra la sezione selezionata
                const targetSection = qs(`#section-${sectionName}`);
                if (targetSection) {
                    targetSection.style.display = 'block';
                }
                if (sectionName === 'ordini') {
                    loadUserOrders();
                }
            });
        });
    }

    // Gestione modifica profilo
    function initProfileEdit() {
        const btnModifica = qs('#btn-modifica-profilo');
        if (btnModifica) {
            btnModifica.addEventListener('click', function() {
                showToast('Funzionalità in fase di sviluppo', 'info');
                // Qui implementerai la logica per modificare il profilo
            });
        }
    }

    // Gestione cambio password
    function initPasswordChange() {
        const form = qs('#form-change-password');
        if (form) {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const currentPassword = qs('#current-password').value;
                const newPassword = qs('#new-password').value;
                const confirmPassword = qs('#confirm-password').value;

                // Validazione
                if (newPassword !== confirmPassword) {
                    showToast('Le password non corrispondono', 'danger');
                    return;
                }

                if (newPassword.length < 8) {
                    showToast('La password deve essere di almeno 8 caratteri', 'warning');
                    return;
                }

                // Qui faresti la chiamata AJAX per cambiare la password
                showToast('Funzionalità in fase di sviluppo', 'info');
                
                // Reset form
                form.reset();
            });
        }
    }

    // Gestione aggiunta carta di credito
    function initAddCarta() {
        const form = qs('#form-add-carta');
        if (form) {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const nomeTitolare = qs('#nomeTitolare').value;
                const numeroCarta = qs('#numeroCarta').value;
                const cvv = qs('#cvv').value;
                const scadenza = qs('#scadenza').value;

                // Validazione client-side
                const numCartaPulito = numeroCarta.replace(/\s+/g, '');
                if (!/^\d{16}$/.test(numCartaPulito)) {
                    showToast('Il numero carta deve contenere 16 cifre', 'warning');
                    return;
                }

                if (!/^\d{3}$/.test(cvv)) {
                    showToast('Il CVV deve contenere 3 cifre', 'warning');
                    return;
                }

                // Invia richiesta
                $.ajax({
                    url: '../../areaRiservataServlet',
                    method: 'POST',
                    data: {
                        nomeTitolare: nomeTitolare,
                        numeroCarta: numCartaPulito,
                        cvv: cvv,
                        scadenza: scadenza
                    },
                    dataType: 'json'
                })
                .done(function(response) {
                    if (response && response.success !== false) {
                        showToast('Carta di credito aggiunta con successo', 'success');
                        form.reset();
                        
                        // Chiudi il modal
                        const modalEl = qs('#modalAddCarta');
                        const modal = bootstrap.Modal.getInstance(modalEl);
                        if (modal) modal.hide();
                        const backdrops = qsa('.modal-backdrop');
                        backdrops.forEach(backdrop => backdrop.remove());
                        document.body.classList.remove('modal-open');
                        document.body.style.overflow = '';
                        document.body.style.paddingRight = '';
                        
                        // Aggiorna la visualizzazione delle carte
                        const data = response.data || response;
                        const carte = data.carte || [];
                        displayCarte(carte);
                    } else {
                        showToast(response.message || 'Errore nell\'aggiunta della carta', 'danger');
                    }
                })
                .fail(function(jqXHR) {
                    const errorMessage = jqXHR.responseJSON?.message || 'Errore nell\'aggiunta della carta';
                    showToast(errorMessage, 'danger');
                });
            });
        }

        // Formattazione automatica numero carta
        const numeroCartaInput = qs('#numeroCarta');
        if (numeroCartaInput) {
            numeroCartaInput.addEventListener('input', function(e) {
                let value = e.target.value.replace(/\s+/g, '');
                let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
                e.target.value = formattedValue;
            });
        }
    }

    // Carica ordini utente (Sezione Ordini)
    function loadUserOrders() {
        const err = qs('#ordini-error');
        const empty = qs('#ordini-empty');
        const table = qs('#ordini-table');
        const tbody = qs('#ordini-tbody');
        if (err) { err.classList.add('d-none'); err.textContent=''; }
        if (empty) empty.classList.add('d-none');
        if (table) table.style.display = 'none';
        if (tbody) tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Caricamento...</td></tr>';
        $.ajax({ url: '../../api/order/user-list', method: 'GET', dataType: 'json' })
          .done(function(res){
            if (!res || res.success === false) {
                if (err) { err.textContent = (res && res.message) || 'Errore nel recupero ordini'; err.classList.remove('d-none'); }
                return;
            }
            const list = res.data || [];
            renderUserOrders(list);
          }).fail(function(jq){ if (err) { err.textContent = (jq.responseJSON && jq.responseJSON.message) || 'Errore di rete'; err.classList.remove('d-none'); } });
    }

    function renderUserOrders(list) {
        const empty = qs('#ordini-empty');
        const table = qs('#ordini-table');
        const tbody = qs('#ordini-tbody');
        if (!tbody) return;
        if (!list || list.length === 0) {
            if (tbody) tbody.innerHTML = '';
            if (table) table.style.display = 'none';
            if (empty) empty.classList.remove('d-none');
            return;
        }
        let rows = '';
        list.forEach(function(o){
            const id = o.id;
            const data = formatDate(o.dataCreazione);
            const tot = Number(o.importo || 0).toFixed(2);
            const stato = o.stato || 'in_attesa';
            const fattUrl = o.fatturaUrl || '#';
            rows += '<tr>'+
                    '<td>#'+escapeHtml(id)+'</td>'+
                    '<td>'+escapeHtml(data)+'</td>'+
                    '<td class="text-end">€ '+tot+'</td>'+
                    '<td>'+escapeHtml(stato)+'</td>'+
                    '<td class="text-end">'+
                      '<a class="btn btn-sm btn-outline-primary me-2" href="'+escapeHtml('../../Jsp/order-summary.jsp?orderId='+id)+'">Dettagli</a>'+
                      (fattUrl && fattUrl !== '#' ? '<a class="btn btn-sm btn-primary" target="_blank" href="'+escapeHtml(fattUrl)+'">Fattura PDF</a>' : '')+
                    '</td>'+
                    '</tr>';
        });
        tbody.innerHTML = rows;
        if (table) table.style.display = '';
    }

    function formatDate(iso) { try { const d = new Date(iso); return d.toLocaleDateString('it-IT'); } catch(e) { return iso || ''; } }

    // Inizializzazione quando il DOM è caricato
    document.addEventListener('DOMContentLoaded', function() {
        // Verifica se siamo nella pagina Area Riservata
        if (!qs('#section-profilo')) return;

        // Inizializza navigazione sezioni
        initSectionNavigation();

        // Inizializza gestione profilo
        initProfileEdit();

        // Inizializza cambio password
        initPasswordChange();

        // Inizializza aggiunta carta
        initAddCarta();

        // Carica i dati dell'utente
        loadUserData();
        // Carica subito anche gli ordini, così sono visibili nella tab "I miei ordini"
        loadUserOrders();

        const btnRefresh = qs('#btn-refresh-ordini');
        if (btnRefresh) {
            btnRefresh.addEventListener('click', function(){ loadUserOrders(); });
        }
    });

})();