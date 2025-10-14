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
/*
    // Funzione per formattare la data
    function formatDate(dateString) {
        if (!dateString) return '-';
        try {
            const date = new Date(dateString);
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            return `${day}/${month}/${year}`;
        } catch (e) {
            return dateString;
        }
    }
*/
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

    // Carica i dati dell'utente
    function loadUserData() {
        const loadingEl = qs('#loading-profilo');
        const contentEl = qs('#profilo-content');
        const errorEl = qs('#error-profilo');
        const errorMsg = qs('#error-message');

        // Mostra loading
        if (loadingEl) loadingEl.style.display = 'block';
        if (contentEl) contentEl.style.display = 'none';
        if (errorEl) errorEl.style.display = 'none';

        // Recupera l'ID utente dalla sessione (se disponibile)
        // In alternativa, potresti passarlo come parametro URL
        // Per questo esempio, assumiamo che la servlet lo recuperi dalla sessione

        $.ajax({
            url: '../../areaRiservataServlet',
            method: 'GET',
            dataType: 'json',
            data: {
                // Se hai l'ID utente, passalo qui
                // idUtente: userId
            }
        })
        .done(function(response) {
            // Nascondi loading
            if (loadingEl) loadingEl.style.display = 'none';

            // Verifica se la risposta è valida
            if (!response || response.success === false) {
                showError(response?.message || 'Errore nel caricamento dei dati');
                return;
            }

            // Popola i campi con i dati utente
            const utente = response.data || response;
            
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

        // Carica i dati dell'utente
        loadUserData();
    });

})();