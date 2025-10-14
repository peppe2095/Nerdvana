(function() {
    const qs = (sel, root = document) => root.querySelector(sel);

    document.addEventListener('DOMContentLoaded', () => {
        
        // GESTIONE REGISTRAZIONE
        const registrazione = qs('#registerForm');
        if(registrazione) {
            registrazione.addEventListener('submit', function(e) {
                e.preventDefault();
                const nome = qs('#nome').value;
                const cognome = qs('#cognome').value;
                const email = qs('#email').value;
                const password = qs('#password').value;
                const cittaDiResidenza = qs('#cittaDiResidenza').value;
                const indirizzo = qs('#indirizzo').value;
                const numeroCivico = qs('#numeroCivico').value;
                const cap = qs('#cap').value;
                const dataDiNascita = qs('#dataDiNascita').value;
                const telefono = qs('#telefono').value;

                $.ajax({
                    url: '../../API/autenticazione/registrazione',
                    method: 'POST',
                    data: {
                        nome: nome,
                        cognome: cognome,
                        email: email,
                        password: password,
                        cittaResidenza: cittaDiResidenza,
                        indirizzo: indirizzo,
                        numeroCivico: numeroCivico,
                        cap: cap,
                        dataNascita: dataDiNascita,
                        telefono: telefono
                    },
                    dataType: 'json'
                }).done(function(response) {
                    if (response.success) {
                        alert('Registrazione avvenuta con successo!');
                        window.location.href = '../../index.jsp';
                    } else {
                        alert('Errore nella registrazione: ' + response.message);
                    }
                }).fail(function() {
                    alert('Errore di comunicazione con il server.');
                });
            });
        }

        // GESTIONE LOGIN
        const login = qs('#loginForm');
        if(login) {
            login.addEventListener('submit', function(e) {
                e.preventDefault();
                const email = qs('#loginEmail').value;
                const password = qs('#loginPassword').value;

                $.ajax({
                    url: '../../API/autenticazione/login',
                    method: 'POST',
                    data: {
                        email: email,
                        password: password
                    },
                    dataType: 'json'
                }).done(function(response) {
                    if (response.success) {
                        alert('Login effettuato con successo!');
                        window.location.href = '../../index.jsp';
                    } else {
                        alert('Errore nel login: ' + response.message);
                    }
                }).fail(function() {
                    alert('Errore di comunicazione con il server.');
                });
            });
        }
    });
})();