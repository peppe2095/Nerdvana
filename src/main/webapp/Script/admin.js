(function() {
  const qs = (sel, root = document) => root.querySelector(sel);
  const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  function showToast(msg, type = 'success') {
    alert(msg); // semplice feedback; si può sostituire con toast bootstrap
  }

  document.addEventListener('DOMContentLoaded', () => {
    // Aggiungi Articolo
    const formArticolo = qs('#formNuovoArticolo');
    if (formArticolo) {
      formArticolo.addEventListener('submit', function(e) {
        e.preventDefault();
        const data = Object.fromEntries(new FormData(formArticolo).entries());
        $.ajax({
          url: '../../API/admin/articoli/add',
          method: 'POST',
          data: data,
          dataType: 'json'
        }).done(function(res) {
          if (res.success) {
            showToast('Articolo aggiunto con successo');
            formArticolo.reset();
          } else {
            showToast(res.message || 'Errore durante l\'aggiunta', 'danger');
          }
        }).fail(function() {
          showToast('Errore di comunicazione con il server', 'danger');
        });
      });
    }

    // Carica Ordini quando si apre la tab
    const ordiniTabButton = qs('#ordini-tab');
    const tbody = qs('#tabellaOrdini tbody');

    function caricaOrdini() {
      if (!tbody) return;
      $.ajax({
        url: '../../API/admin/ordini/list',
        method: 'GET',
        dataType: 'json'
      }).done(function(res) {
        if (res.success) {
          tbody.innerHTML = '';
          (res.data || []).forEach(o => {
            const tr = document.createElement('tr');
            //const dataStr = o.dataCreazione ? new Date(o.dataCre).toLocaleString() : '';
            tr.innerHTML = `
              <td>${o.id || ''}</td>
              <td>${o.utenteId || ''}</td>
              <td>${o.dataCreazione}</td>
              <td>${o.importo || ''}</td>
              <td>${o.stato || ''}</td>
            `;
            tbody.appendChild(tr);
          });
        } else {
          showToast(res.message || 'Errore nel caricamento ordini', 'danger');
        }
      }).fail(function() {
        showToast('Errore di comunicazione con il server', 'danger');
      });
    }

    if (ordiniTabButton) {
      ordiniTabButton.addEventListener('shown.bs.tab', caricaOrdini);
    }

    // Registra Admin
    const formAdmin = qs('#formRegistraAdmin');
    if (formAdmin) {
      formAdmin.addEventListener('submit', function(e) {
        e.preventDefault();
        const data = Object.fromEntries(new FormData(formAdmin).entries());
        $.ajax({
          url: '../../API/admin/utenti/register-admin',
          method: 'POST',
          data: data,
          dataType: 'json'
        }).done(function(res) {
          if (res.success) {
            showToast('Admin registrato con successo');
            formAdmin.reset();
          } else {
            showToast(res.message || 'Errore nella registrazione admin', 'danger');
          }
        }).fail(function() {
          showToast('Errore di comunicazione con il server', 'danger');
        });
      });
    }
  });
})();
