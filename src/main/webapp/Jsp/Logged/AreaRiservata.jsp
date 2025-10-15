<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Model.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Area Riservata - Nerdvana</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- CSS personalizzato -->
    <link rel="stylesheet" href="../../Stili/nerdvana-styles.css">
</head>
<body>

<%@ include file="../Fragments/Header.jsp" %>

<div class="container my-5">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 mb-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title mb-3">Menu</h5>
                    <nav class="nav flex-column">
                        <a class="nav-link active" href="#" data-section="profilo">
                            <i class="bi bi-person"></i> Il mio profilo
                        </a>
                        <a class="nav-link" href="#" data-section="ordini">
                            <i class="bi bi-box"></i> I miei ordini
                        </a>
                        <a class="nav-link" href="#" data-section="impostazioni">
                            <i class="bi bi-gear"></i> Impostazioni
                        </a>
                    </nav>
                </div>
            </div>
        </div>

        <!-- Contenuto principale -->
        <div class="col-md-9">
            <!-- Sezione Profilo -->
            <div id="section-profilo" class="content-section">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">I miei dati</h4>
                    </div>
                    <div class="card-body">
                        <!-- Loading placeholder -->
                        <div id="loading-profilo" class="text-center py-5">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Caricamento...</span>
                            </div>
                            <p class="mt-3 text-muted">Caricamento dati in corso...</p>
                        </div>

                        <!-- Contenuto profilo (nascosto inizialmente) -->
                        <div id="profilo-content" style="display: none;">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Nome</label>
                                    <p class="form-control-plaintext" id="user-nome">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Cognome</label>
                                    <p class="form-control-plaintext" id="user-cognome">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Email</label>
                                    <p class="form-control-plaintext" id="user-email">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Telefono</label>
                                    <p class="form-control-plaintext" id="user-telefono">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Data di nascita</label>
                                    <p class="form-control-plaintext" id="user-dataNascita">-</p>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Ruolo</label>
                                    <p class="form-control-plaintext" id="user-ruolo">-</p>
                                </div>
                                <div class="col-12">
                                    <hr>
                                    <h5 class="mb-3">Indirizzo</h5>
                                </div>
                                <div class="col-md-8">
                                    <label class="form-label fw-bold">Via</label>
                                    <p class="form-control-plaintext" id="user-indirizzo">-</p>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">Civico</label>
                                    <p class="form-control-plaintext" id="user-civico">-</p>
                                </div>
                                <div class="col-md-8">
                                    <label class="form-label fw-bold">Città</label>
                                    <p class="form-control-plaintext" id="user-citta">-</p>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">CAP</label>
                                    <p class="form-control-plaintext" id="user-cap">-</p>
                                </div>
                            </div>
                            
                            <div class="mt-4">
                                <button class="btn btn-primary" id="btn-modifica-profilo">
                                    Modifica profilo
                                </button>
                            </div>
                        </div>

                        <!-- Messaggio errore -->
                        <div id="error-profilo" class="alert alert-danger" style="display: none;" role="alert">
                            <strong>Errore!</strong> <span id="error-message"></span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Sezione Ordini -->
            <div id="section-ordini" class="content-section" style="display: none;">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">I miei ordini</h4>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle"></i> Sezione in fase di sviluppo
                        </div>
                        <p class="text-muted">Qui visualizzerai lo storico dei tuoi ordini.</p>
                    </div>
                </div>
            </div>

            <!-- Sezione Impostazioni -->
            <div id="section-impostazioni" class="content-section" style="display: none;">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Impostazioni account</h4>
                    </div>
                    <div class="card-body">
                        <h5 class="mb-3">Modifica password</h5>
                        <form id="form-change-password">
                            <div class="mb-3">
                                <label for="current-password" class="form-label">Password attuale</label>
                                <input type="password" class="form-control" id="current-password" required>
                            </div>
                            <div class="mb-3">
                                <label for="new-password" class="form-label">Nuova password</label>
                                <input type="password" class="form-control" id="new-password" required>
                            </div>
                            <div class="mb-3">
                                <label for="confirm-password" class="form-label">Conferma nuova password</label>
                                <input type="password" class="form-control" id="confirm-password" required>
                            </div>
                            <button type="submit" class="btn btn-primary">Cambia password</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../Fragments/Footer.jsp" %>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- Main JS -->
<script src="../../Script/main.js"></script>
<!-- Area Riservata JS -->
<script src="../../Script/areaRiservata.js"></script>

</body>
</html>