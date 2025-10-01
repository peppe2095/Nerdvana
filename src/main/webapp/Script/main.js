// Mettiamo tutto lo script dentro una function IIFE (Immediately Invoked Function Expression)
// Questo per far sì che tutto quello che è dentro non è accessibile dall'esterno
// E che lo script venga eseguito una volta sola.
(function() {

    // Oggetto che mantiene lo stato corrente della paginazione e filtri
    // È come una "memoria" che ricorda dove siamo e cosa stiamo visualizzando
    const state = {
        page: 1,        // Pagina corrente (inizia dalla prima)
        size: 5,        // Quanti prodotti mostrare per pagina (verrà sovrascritto dal server)
        totalPages: 1,  // Totale pagine disponibili (calcolato dalla servlet, in realtà dal DAO che lo passa alla servlet)
        tipo: null      // Filtro categoria di articolo (null = mostra tutti)
    };
    
    // qs = querySelector - trova UN elemento nel DOM
    // sel = selettore CSS (es: '#prodotti', '.btn-cart')
    // root = dove cercare (default = document = tutta la pagina)
    const qs = (sel, root = document) => root.querySelector(sel);

    // qsa = querySelectorAll - trova TUTTI gli elementi che corrispondono al selettore
    // Array.from() converte la lista di elementi in Array per usare for, map ecc.
    const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

    // addEventListener() - aggiunge un "ascoltatore" per un evento
    // 'DOMContentLoaded' - evento che scatta quando il DOM è completamente caricato
    // La arrow function () => è la funzione che viene eseguita quando l'evento scatta
    document.addEventListener('DOMContentLoaded', () => {

        // qsa('.tipo-link') - trova tutti i link sulla navbar (MANGA, VIDEOGIOCO, ecc.)
        // forEach() - esegue una funzione per ogni elemento trovato
        qsa('.tipo-link').forEach(a => {

            //aspettiamo che il link venga cliccato
            a.addEventListener('click', (e) => {
                // preventDefault() - impedisce al link di navigare (blocca href="#")
                // perché vogliamo caricare i prodotti "manualmente" con ajax
                e.preventDefault();

                // getAttribute() - legge il valore dell'attributo HTML data-tipo
                // attributo ovviamente personale, cioè definito da noi dentro la index.jsp
                // Esempio: se data-tipo="MANGA", restituisce "MANGA"
                const t = a.getAttribute('data-tipo');

                // Operatore OR (||) - se t è null/undefined, usa null
                // Aggiorna lo stato con il tipo selezionato
                state.tipo = t || null;

                // Quando cambi filtro, torna sempre alla prima pagina
                state.page = 1;

                // Ricarica gli articoli con il nuovo filtro
                loadArticoli();
            });
        });


        // qs() - trova il pulsante "Precedente" o il "Successivo"
        const prevBtn = qs('#prevPage');
        const nextBtn = qs('#nextPage');

        // if (prevBtn) - verifica che l'elemento sia stato trovato
        if (prevBtn) prevBtn.addEventListener('click', () => {
            // vai indietro solo se non siamo già alla prima pagina
            if (state.page > 1) {
                state.page--; // Decrementa il numero di pagina
                loadArticoli(); // Ricarica con la nuova pagina
            }
        });

        if (nextBtn) nextBtn.addEventListener('click', () => {
            // vai avanti solo se non siamo all'ultima pagina
            if (state.page < state.totalPages) {
                state.page++; // Incrementa il numero di pagina
                loadArticoli(); // Ricarica con la nuova pagina
            }
        });


        const refreshBtn = qs('#btn-aggiorna');
        if (refreshBtn) refreshBtn.addEventListener('click', () => loadArticoli());

        // GESTION DEL LOGOUT, IGNORA AL MOMENTO.
        const logoutLink = qs('#logoutLink');
        if (logoutLink) logoutLink.addEventListener('click', (e) => {
            e.preventDefault(); // Impedisce navigazione del link

            $.ajax({
                url: 'api/auth/logout',
                method: 'POST'
            })
                // .done() - cosa fare se la richiesta ha successo
                .done(function() {
                    location.reload(); // Ricarica la pagina per aggiornare l'interfaccia
                })
                // .fail() - cosa fare se la richiesta fallisce
                .fail(function() {
                    alert('Errore durante il logout'); // Mostra messaggio di errore
                });
        });

        // --------------------------------------------------
        //               AVVIO INIZIALE
        // --------------------------------------------------

        // Al caricamento della pagina, carica subito gli articoli
        loadArticoli();
    });

    // FUNZIONE CHE CARICA GLI ARTICOLI
    function loadArticoli() {
        // Trova il contenitore dove mostrare i prodotti
        const container = qs('#prodotti');
        const placeholder = qs('#placeholder');

        // Mostra messaggio di caricamento mentre aspettiamo la risposta dal server
        if (placeholder) placeholder.textContent = 'Caricamento prodotti...';

        // URLSearchParams - Classe per costruire l'url
        // con i parametri per la richiesta HTTP
        const params = new URLSearchParams({
            format: 'json',                    // Vogliamo risposta JSON
            page: String(state.page),          // Converti numero in stringa
            size: String(state.size)           // Quanti articoli per pagina
        });

        // Se c'è un filtro categoria attivo, aggiungilo ai parametri
        if (state.tipo) params.append('tipo', state.tipo);

        // $.ajax() - richiesta AJAX
        $.ajax({
            // toString() converte i parametri in stringa tipo: "format=json&page=1&size=5&tipo=MANGA"
            url: 'getArticoli?' + params.toString(),
            method: 'GET',              // Richiesta GET (lettura dati)
            dataType: 'json'            
        })
            // .done() - funzione eseguita se la richiesta ha successo
            // data = oggetto JavaScript con i dati ricevuti dal server
            .done(function(data) {
                renderArticoli(data); // Mostra i prodotti ricevuti
            })
            // .fail() - callback eseguita se la richiesta fallisce
            // jq = oggetto jqXHR con info sull'errore
            .fail(function(jq) {
                // Mostra messaggio di errore nel contenitore
                if (container) container.innerHTML = '<div class="col-12 text-danger">Errore nel caricamento</div>';
            });
    }


    // FUNZIONE RENDERING: MOSTRA PRODOTTI NELLA PAGINA
    function renderArticoli(data) {
        // Trova il contenitore div dove inserire le card prodotti
        const container = qs('#prodotti');
        if (!container) return; // Sicurezza: esce se non trova il contenitore

        // Array.isArray() - verifica se data.items è un array valido
        // Se non è array, usa array vuoto [] come alternativa
        const items = Array.isArray(data.items) ? data.items : [];

        // Sincronizza lo stato della paginazione con quello ricevuto dal server
        state.totalPages = data.totalPages || 1;  // || 1 = default se è null/undefined
        state.page = data.page || 1;

        // Controllo se non ci sono prodotti da mostrare
        if (items.length === 0) {
            // innerHTML - sostituisce tutto il contenuto HTML dell'elemento con un messaggio di errore
            container.innerHTML = '<div class="col-12 text-center text-muted">Nessun prodotto trovato.</div>';
        } else {
            // SE CI SONO PRODOTTI DA MOSTRARE, ALLORA:
            // items.map() - per ogni prodotto (p) con indice (idx) chiama cardHTML
            // cardHtml(p, idx) - funzione che crea l'HTML che mostra ogni articolo una singola card 
            // ( per card intendo componente boostrap)
            // .join('') - unisce tutti i pezzi HTML in una stringa unica, serve perché alla funzione innerhtml serve una
            // unica grande stringa per poter creare il codice html
            container.innerHTML = items.map((p, idx) => cardHtml(p, idx)).join('');
        }

        // Aggiorna i numeri di pagina mostrati all'utente
        const pageNum = qs('#pageNum');        // Span che mostra pagina corrente
        const totalPages = qs('#totalPages');  // Span che mostra totale pagine

        // Aggiorna il testo degli elementi che mostrano la paginazione corrente (pageNum) e totale (totalPages)
        // textContent è una proprietà che permette di modificare il contenuto testuale di un elemento HTML
        if (pageNum) pageNum.textContent = String(state.page);
        if (totalPages) totalPages.textContent = String(state.totalPages);

        // Gestione stato dei pulsanti paginazione
        const prevBtn = qs('#prevPage');
        const nextBtn = qs('#nextPage');

        // disabled = true/false - abilita/disabilita il pulsante
        if (prevBtn) prevBtn.disabled = state.page <= 1;                    // Disabilita se siamo alla prima pagina
        if (nextBtn) nextBtn.disabled = state.page >= state.totalPages;     // Disabilita se siamo all'ultima pagina


        // RE-BINDING EVENTI (IMPORTANTE!)
        // Quando usiamo innerHTML, tutti i vecchi event listener vengono distrutti
        // Dobbiamo riattaccare gli eventi ai nuovi elementi HTML

        // Gestione pulsanti "Dettagli"
        qsa('.btn-toggle').forEach(btn => {
            btn.addEventListener('click', () => {
                // getAttribute() - legge l'ID dell'elemento da mostrare/nascondere
                const targetId = btn.getAttribute('data-target');

                // Seleziona l'elemento con quell'id
                const el = qs(`#${targetId}`);

                if (el) el.classList.toggle('show'); // toggle() - aggiunge/rimuove classe CSS 'show'
            });
        });


        // GESTIONE CARRELLO IGNORA AL MOMENTO
        qsa('.btn-add-cart').forEach(btn => {
            btn.addEventListener('click', () => {
                // Estrae l'ID prodotto dal data attribute
                const id = btn.getAttribute('data-id');

                // Richiesta AJAX per aggiungere al carrello
                $.ajax({
                    url: 'api/cart/add',
                    method: 'POST',                 // POST per modificare dati
                    data: { id: id },              // Invia ID prodotto nel body
                    dataType: 'json'
                })
                    .done(function(out) {
                        // Verifica se il server ha restituito un errore
                        if (out && out.success === false) {
                            // out.message || 'Errore' - usa messaggio server o fallback
                            alert('Errore carrello: ' + (out.message || 'Errore'));
                        } else {
                            // Successo: mostra notifica toast
                            toast('Aggiunto al carrello');
                        }
                    })
                    .fail(function(jq) {
                        alert('Errore carrello');
                    });
            });
        });


        // GESTIONE WISHLIST IGNORA AL MOMENTO
        qsa('.btn-add-wishlist').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');

                $.ajax({
                    url: 'api/wishlist/add',
                    method: 'POST',
                    data: { id: id },
                    dataType: 'json'
                })
                    .done(function(out) {
                        if (out && out.success === false) {
                            alert('Errore wishlist: ' + (out.message || 'Errore'));
                        } else {
                            toast('Aggiunto alla wishlist');
                        }
                    })
                    .fail(function(jq) {
                        // Gestione speciale per errore 401 (Unauthorized = non loggato)
                        if (jq && jq.status === 401) {
                            // confirm() - mostra popup con OK/Annulla
                            if (confirm('Per usare la wishlist devi effettuare il login. Vuoi accedere ora?')) {
                                // window.location.href - naviga a nuova pagina
                                window.location.href = 'Jsp/Accessi/Login.jsp';
                            }
                            return; // Esce dalla funzione
                        }

                        // Estrazione messaggio di errore con fallback multipli
                        // jq?.responseJSON?.message - messaggio dal server con optional chaining
                        const msg = jq?.responseJSON?.message || 'Errore wishlist';
                        alert(msg);
                    });
            });
        });
    }


    // GENERAZIONE HTML PER SINGOLA CARD PRODOTTO
    function cardHtml(p, idx) {
        // Normalizza l'URL dell'immagine, rimuove eventuali prefissi inutili
        // Essenzialmente ormai inutile.
        const img = normalizeUrl(p.url);

        // Genera ID univoco per il collapse della descrizione
        // Math.random().toString(36) - numero random in base 36 (0-9, a-z)
        // .slice(2,7) - prende 5 caratteri dopo "0."
        // Risultato esempio: "collapse-0-k3x9p"
        // Ho usato copilot per questa funzione
        const cid = `collapse-${idx}-${Math.random().toString(36).slice(2,7)}`;

        // Formattazione prezzo: se è numero, mostra 2 decimali
        // typeof - verifica il tipo della variabile
        // toFixed(2) - arrotonda a 2 cifre decimali
        const prezzo = typeof p.prezzo === 'number' ? p.prezzo.toFixed(2) : p.prezzo;

        // Fallback per tipo: se null/undefined, mostra "-"
        const tipo = p.tipo || '-';

        // Uso della backtick ` necessario per permette stringhe multilinea e interpolazione
        // ${nomevariabile} - inserisce valore della variabile nel codice html
        // escapeHtml() - previene attacchi XSS encodando caratteri pericolosi
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


    // Funzione che "pulisce" le stringhe per prevenire attacchi XSS
    // Converte caratteri pericolosi in entità HTML sicure
    // Praticamente se l'utente nel campo nome ci mette qualcosa come
    // <script>codice malevolo </script> questa funzione fa in modo che
    // quello script viene trasformato in testo e quindi non eseguito
    function escapeHtml(str) {
        return String(str)                                                // Converte in stringa
            .replace(/&/g, '&amp;')                // & diventa &amp;
            .replace(/</g, '&lt;')                 // < diventa &lt;
            .replace(/>/g, '&gt;')                 // > diventa &gt;
            .replace(/"/g, '&quot;')               // " diventa &quot;
            .replace(/'/g, '&#039;');              // ' diventa &#039;
    }


    // NORMALIZZAZIONE URL IMMAGINI (NON C'È PIU BISOGNO ORA CHE SISTEMIAMO NEL DB)
    function normalizeUrl(raw) {
        try {
            // (raw || '') - se raw è null, usa stringa vuota
            // .trim() - rimuove spazi all'inizio e fine
            let u = (raw || '').trim();

            // Se URL vuoto, usa immagine di default
            if (!u) return 'Image/logo.png';

            // Rimuove prefisso del path assoluto del progetto (se presente)
            const prefix = '/Nerdvana/src/main/webapp/';
            if (u.indexOf(prefix) === 0) u = u.substring(prefix.length);

            // Rimuove slash iniziale se presente
            if (u.startsWith('/')) u = u.slice(1);

            // Doppio controllo: se risultato vuoto, usa default
            return u || 'Image/logo.png';
        } catch (e) {
            // try-catch: se qualcosa va storto, usa immagine di default
            return 'Image/logo.png';
        }
    }


    // SISTEMA DI NOTIFICHE TOAST
    // Crea notifica temporanea
    function toast(message) {
        try {
            // createElement() - crea nuovo elemento HTML in memoria
            const n = document.createElement('div');

            // textContent - inserisce testo
            n.textContent = message;

            // Stile CSS applicato via JavaScript
            n.style.position = 'fixed';                        // Posizione fissa rispetto alla finestra
            n.style.top = '16px';                              // 16px dall'alto
            n.style.right = '16px';                            // 16px da destra
            n.style.padding = '10px 14px';                     // Spazio interno
            n.style.background = '#2c3e50';                    // Colore sfondo
            n.style.color = '#fff';                            // Colore testo
            n.style.borderRadius = '8px';                      // Angoli arrotondati
            n.style.boxShadow = '0 2px 10px rgba(0,0,0,0.2)';  // Ombra
            n.style.zIndex = '9999';                           // Sopra tutto il resto

            // appendChild() - aggiunge l'elemento alla fine del body
            document.body.appendChild(n);

            // setTimeout() - esegue funzione dopo un ritardo
            // () => { n.remove(); } - arrow function che rimuove l'elemento
            // 1500 - millisecondi (1.5 secondi)
            setTimeout(() => { n.remove(); }, 1500);
        } catch (_) {
            // Fallback: se la creazione DOM fallisce, usa alert nativo
            alert(message);
        }
    }
})();