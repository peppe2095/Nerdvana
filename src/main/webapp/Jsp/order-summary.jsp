<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Riepilogo Ordine - Nerdvana</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="../Stili/nerdvana-styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="bg-light">
<%@ include file="Fragments/Header.jsp" %>
<div class="container py-4">
    <div class="d-flex align-items-center justify-content-between mb-3">
        <h1 class="h3 mb-0">Riepilogo Ordine</h1>
        <a href="../Jsp/Logged/AreaRiservata.jsp" class="btn btn-outline-secondary btn-sm"><i class="bi bi-person"></i> Area Riservata</a>
    </div>

    <div id="alert" class="alert alert-danger d-none"></div>

    <div id="orderInfo" class="card shadow-sm mb-3 d-none">
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <div><span class="text-muted">Numero Ordine:</span> <strong>#<span id="ord-id"></span></strong></div>
                    <div><span class="text-muted">Stato:</span> <span class="badge bg-info" id="ord-stato"></span></div>
                    <div><span class="text-muted">Data:</span> <strong id="ord-data"></strong></div>
                </div>
                <div class="col-md-6 text-md-end mt-3 mt-md-0">
                    <div><span class="text-muted">Articoli:</span> <strong id="ord-num"></strong></div>
                    <div><span class="text-muted">Totale:</span> <strong>€ <span id="ord-tot"></span></strong></div>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm mb-3 d-none" id="orderItems">
        <div class="card-header">Articoli</div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-striped mb-0">
                    <thead>
                    <tr>
                        <th>Articolo</th>
                        <th class="text-end">Prezzo</th>
                        <th class="text-center">Quantità</th>
                        <th class="text-end">Subtotale</th>
                    </tr>
                    </thead>
                    <tbody id="itemsBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="d-flex gap-2">
        <a id="fatturaLink" href="#" target="_blank" class="btn btn-primary d-none"><i class="bi bi-file-earmark-pdf"></i> Visualizza PDF Fattura</a>
        <a href="../index.jsp" class="btn btn-outline-secondary">Torna alla Home</a>
    </div>
</div>
<%@ include file="Fragments/Footer.jsp" %>

<script>
(function(){
  const qs = (s, r=document)=>r.querySelector(s);
  function fmtDate(iso){ try { var d = new Date(iso); return d.toLocaleDateString('it-IT'); } catch(e){ return iso||''; } }

  $(function(){
    var p = new URLSearchParams(window.location.search);
    var orderId = p.get('orderId');
    if (!orderId) return showErr('ID ordine mancante');

    $.ajax({
      url: '../api/order/summary',
      method: 'GET',
      data: { orderId: orderId },
      dataType: 'json'
    }).done(function(res){
      if (!res || res.success === false) return showErr((res && res.message) || 'Errore nel caricamento del riepilogo');
      var data = res.data || {};
      var o = data.ordine || {};
      var items = data.items || [];
      qs('#ord-id').textContent = orderId;
      qs('#ord-stato').textContent = o.stato || 'in_attesa';
      qs('#ord-data').textContent = fmtDate(o.dataCreazione);
      qs('#ord-num').textContent = o.numeroArticoli || items.reduce(function(a,it){return a + Number(it.qty||0);},0);
      qs('#ord-tot').textContent = ((o.importo || 0) && Number(o.importo||0).toFixed(2));
      qs('#orderInfo').classList.remove('d-none');

      var body = qs('#itemsBody');
      var rows = '';
      items.forEach(function(it){
        var a = it.articolo || {}; var qty = Number(it.qty||1); var prezzo = Number(a.prezzo||0); var sub = qty*prezzo;
        var imgSrc = normalizeUrl(a.url);
        rows += '<tr>'+
          '<td>'+
            '<div class="d-flex align-items-center gap-2">'+
              '<img src="'+escapeHtml(imgSrc)+'" alt="" style="width:48px;height:48px;object-fit:cover"/>'+
              '<div>'+
                '<div class="fw-semibold">'+escapeHtml(a.nome||'')+'</div>'+
                '<div class="text-muted small">'+escapeHtml(a.tipo||'')+'</div>'+
              '</div>'+
            '</div>'+
          '</td>'+
          '<td class="text-end">€ '+prezzo.toFixed(2)+'</td>'+
          '<td class="text-center">'+qty+'</td>'+
          '<td class="text-end">€ '+sub.toFixed(2)+'</td>'+
        '</tr>';
      });
      body.innerHTML = rows;
      qs('#orderItems').classList.remove('d-none');

      if (data.fatturaUrl) {
        var link = qs('#fatturaLink');
        link.href = data.fatturaUrl;
        link.classList.remove('d-none');
      }
    }).fail(function(jq){
      showErr((jq.responseJSON && jq.responseJSON.message) || 'Errore nel recupero riepilogo');
    });
  });

  function showErr(m){ var a = qs('#alert'); a.textContent = m||'Errore'; a.classList.remove('d-none'); }
  function escapeHtml(str){ return String(str||'').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#039;'); }
  function normalizeUrl(raw){ try{ var u = (raw||'').trim(); var base = '../'; var prefix = '/Nerdvana/src/main/webapp/'; if (u.indexOf(prefix)===0) u = u.substring(prefix.length); if (u.startsWith('/')) u=u.slice(1); return base + u; }catch(e){ return '../Image/logo.png'; } }
})();
</script>
</body>
</html>
