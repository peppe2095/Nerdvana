USE NerdvanaStore;

CREATE TABLE IF NOT EXISTS NerdvanaStore.UTENTE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    numeroCivico VARCHAR(10),
    indirizzo VARCHAR(200),
    cap VARCHAR(10),
    dataNascita DATE,
    ruolo ENUM('cliente', 'admin') DEFAULT 'cliente',
    telefono VARCHAR(20),
    passwordHash VARCHAR(255) NOT NULL,
    cittaResidenza VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS NerdvanaStore.CARTA_DI_CREDITO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utente_id INT NOT NULL,
    nomeTitolare VARCHAR(100) NOT NULL,
    numeroCarta VARCHAR(19) NOT NULL, -- Formato: XXXX-XXXX-XXXX-XXXX
    cvv VARCHAR(4) NOT NULL,
    scadenza DATE NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS NerdvanaStore.WISHLIST (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utente_id INT NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS NerdvanaStore.ARTICOLO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numeroSeriale VARCHAR(50) UNIQUE,
    nome VARCHAR(200) NOT NULL,
    tipo VARCHAR(100),
    prezzo DECIMAL(10,2) NOT NULL CHECK (prezzo >= 0),
    quantita INT NOT NULL DEFAULT 0 CHECK (quantita >= 0),
    descrizione TEXT,
    url VARCHAR(500) -- URL dell'immagine o pagina prodotto
    
);

CREATE TABLE IF NOT EXISTS NerdvanaStore.ORDINE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utente_id INT NOT NULL,
    carta_credito_id INT,
    numeroArticoli INT NOT NULL DEFAULT 0,
    importo DECIMAL(10,2) NOT NULL CHECK (importo >= 0),
    dataSpedizione DATE,
    dataArrivo DATE,
    stato ENUM('in_attesa', 'confermato', 'spedito', 'consegnato', 'annullato') DEFAULT 'in_attesa',
    dataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dataAggiornamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (utente_id) REFERENCES UTENTE(id) ON DELETE CASCADE,
    FOREIGN KEY (carta_credito_id) REFERENCES CARTA_DI_CREDITO(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS NerdvanaStore.FATTURA (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ordine_id INT NOT NULL UNIQUE,
    urlFattura VARCHAR(500) NOT NULL,
    FOREIGN KEY (ordine_id) REFERENCES ORDINE(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS NerdvanaStore.ORDINE_ARTICOLO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ordine_id INT NOT NULL,
    articolo_id INT NOT NULL,
    quantita INT NOT NULL DEFAULT 1 CHECK (quantita > 0),
    FOREIGN KEY (ordine_id) REFERENCES ORDINE(id) ON DELETE CASCADE,
    FOREIGN KEY (articolo_id) REFERENCES ARTICOLO(id) ON DELETE CASCADE,
    UNIQUE KEY unique_ordine_articolo (ordine_id, articolo_id)
);

CREATE TABLE IF NOT EXISTS NerdvanaStore.WISHLIST_ARTICOLO (
    id INT PRIMARY KEY AUTO_INCREMENT,
    wishlist_id INT NOT NULL,
    articolo_id INT NOT NULL,
    FOREIGN KEY (wishlist_id) REFERENCES WISHLIST(id) ON DELETE CASCADE,
    FOREIGN KEY (articolo_id) REFERENCES ARTICOLO(id) ON DELETE CASCADE,
    UNIQUE KEY unique_wishlist_articolo (wishlist_id, articolo_id)
);


DROP INDEX idx_utente_email ON UTENTE;
CREATE INDEX  idx_utente_email  ON UTENTE(email);
DROP INDEX idx_articolo_tipo ON ARTICOLO;
CREATE INDEX idx_articolo_tipo ON ARTICOLO(tipo);
DROP INDEX idx_articolo_prezzo ON ARTICOLO;
CREATE INDEX idx_articolo_prezzo ON ARTICOLO(prezzo);
DROP INDEX idx_ordine_stato ON ORDINE;
CREATE INDEX idx_ordine_stato ON ORDINE(stato);
DROP INDEX idx_ordine_data ON ORDINE;
CREATE INDEX idx_ordine_data ON ORDINE(dataCreazione);

DELIMITER //

-- ========================================
-- TRIGGER NUMERO ARTICOLI ORDINE
-- ========================================
DROP TRIGGER IF EXISTS update_numero_articoli_after_insert //
CREATE TRIGGER update_numero_articoli_after_insert
    AFTER INSERT ON ORDINE_ARTICOLO
    FOR EACH ROW
BEGIN
    UPDATE ORDINE 
    SET numeroArticoli = (
        SELECT SUM(quantita) 
        FROM ORDINE_ARTICOLO 
        WHERE ordine_id = NEW.ordine_id
    )
    WHERE id = NEW.ordine_id;
END //

DROP TRIGGER IF EXISTS update_numero_articoli_after_update //
CREATE TRIGGER update_numero_articoli_after_update
    AFTER UPDATE ON ORDINE_ARTICOLO
    FOR EACH ROW
BEGIN
    UPDATE ORDINE 
    SET numeroArticoli = (
        SELECT SUM(quantita) 
        FROM ORDINE_ARTICOLO 
        WHERE ordine_id = NEW.ordine_id
    )
    WHERE id = NEW.ordine_id;
END //

DROP TRIGGER IF EXISTS update_numero_articoli_after_delete //
CREATE TRIGGER update_numero_articoli_after_delete
    AFTER DELETE ON ORDINE_ARTICOLO
    FOR EACH ROW
BEGIN
    UPDATE ORDINE 
    SET numeroArticoli = COALESCE((
        SELECT SUM(quantita) 
        FROM ORDINE_ARTICOLO 
        WHERE ordine_id = OLD.ordine_id
    ), 0)
    WHERE id = OLD.ordine_id;
END //

-- ========================================
-- TRIGGER VALIDAZIONE UTENTE
-- ========================================
DROP TRIGGER IF EXISTS validate_utente_before_insert //
CREATE TRIGGER validate_utente_before_insert
BEFORE INSERT ON NerdvanaStore.UTENTE
FOR EACH ROW
BEGIN
  -- Validazioni campi...
  IF NOT (NEW.nome REGEXP '^[a-zA-Z[:space:]]{2,}$') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Il campo Nome deve contenere almeno 2 lettere.';
  END IF;
  IF NOT (NEW.cognome REGEXP '^[a-zA-Z[:space:]]{2,}$') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Il campo Cognome deve contenere almeno 2 lettere.';
  END IF;
  IF NOT (NEW.email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Il campo Email deve contenere un indirizzo email valido.';
  END IF;
  -- (continua con le altre validazioni come avevi già scritto)
END //

DROP TRIGGER IF EXISTS validate_utente_before_update //
CREATE TRIGGER validate_utente_before_update
BEFORE UPDATE ON NerdvanaStore.UTENTE
FOR EACH ROW
BEGIN
  -- Stesse validazioni di sopra
  IF NOT (NEW.nome REGEXP '^[a-zA-Z[:space:]]{2,}$') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Il campo Nome deve contenere almeno 2 lettere.';
  END IF;
  -- (continua come avevi già scritto)
END //

-- ========================================
-- TRIGGER VALIDAZIONE ARTICOLO
-- ========================================
DROP TRIGGER IF EXISTS validate_articolo_before_insert //
CREATE TRIGGER validate_articolo_before_insert
BEFORE INSERT ON NerdvanaStore.ARTICOLO
FOR EACH ROW
BEGIN
    IF NEW.quantita < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La quantità non può essere inferiore a 0.';
    END IF;
    IF NEW.tipo IS NOT NULL THEN
        IF NEW.tipo NOT IN ('Manga', 'Fumetti', 'BoardGame', 'Videogiochi', 'Funko', 'Action Figure') THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tipo articolo non valido.';
        END IF;
    END IF;
    IF NEW.prezzo <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Il prezzo deve essere maggiore di 0.';
    END IF;
END //

DROP TRIGGER IF EXISTS validate_articolo_before_update //
CREATE TRIGGER validate_articolo_before_update
BEFORE UPDATE ON NerdvanaStore.ARTICOLO
FOR EACH ROW
BEGIN
    IF NEW.quantita < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La quantità non può essere inferiore a 0.';
    END IF;
    IF NEW.tipo IS NOT NULL THEN
        IF NEW.tipo NOT IN ('Manga', 'Fumetti', 'BoardGame', 'Videogiochi', 'Funko', 'Action Figure') THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tipo articolo non valido.';
        END IF;
    END IF;
    IF NEW.prezzo <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Il prezzo deve essere maggiore di 0.';
    END IF;
END //

-- ========================================
-- TRIGGER IMPORTO ORDINE
-- ========================================
DROP TRIGGER IF EXISTS update_importo_ordine_after_insert //
CREATE TRIGGER update_importo_ordine_after_insert
    AFTER INSERT ON NerdvanaStore.ORDINE_ARTICOLO
    FOR EACH ROW
BEGIN
    UPDATE NerdvanaStore.ORDINE 
    SET importo = (
        SELECT SUM(oa.quantita * a.prezzo) 
        FROM NerdvanaStore.ORDINE_ARTICOLO oa
        JOIN NerdvanaStore.ARTICOLO a ON oa.articolo_id = a.id
        WHERE oa.ordine_id = NEW.ordine_id
    )
    WHERE id = NEW.ordine_id;
END //

DROP TRIGGER IF EXISTS update_importo_ordine_after_update //
CREATE TRIGGER update_importo_ordine_after_update
    AFTER UPDATE ON NerdvanaStore.ORDINE_ARTICOLO
    FOR EACH ROW
BEGIN
    UPDATE NerdvanaStore.ORDINE 
    SET importo = (
        SELECT SUM(oa.quantita * a.prezzo) 
        FROM NerdvanaStore.ORDINE_ARTICOLO oa
        JOIN NerdvanaStore.ARTICOLO a ON oa.articolo_id = a.id
        WHERE oa.ordine_id = NEW.ordine_id
    )
    WHERE id = NEW.ordine_id;
END //

DROP TRIGGER IF EXISTS update_importo_ordine_after_delete //
CREATE TRIGGER update_importo_ordine_after_delete
    AFTER DELETE ON NerdvanaStore.ORDINE_ARTICOLO
    FOR EACH ROW
BEGIN
    UPDATE NerdvanaStore.ORDINE 
    SET importo = COALESCE((
        SELECT SUM(oa.quantita * a.prezzo) 
        FROM NerdvanaStore.ORDINE_ARTICOLO oa
        JOIN NerdvanaStore.ARTICOLO a ON oa.articolo_id = a.id
        WHERE oa.ordine_id = OLD.ordine_id
    ), 0)
    WHERE id = OLD.ordine_id;
END //

-- ========================================
-- TRIGGER DATE ORDINE
-- ========================================
DROP TRIGGER IF EXISTS set_dates_ordine_before_update //
CREATE TRIGGER set_dates_ordine_before_update
BEFORE UPDATE ON NerdvanaStore.ORDINE
FOR EACH ROW
BEGIN
    IF OLD.stato = 'in_attesa' AND NEW.stato = 'confermato' AND NEW.dataSpedizione IS NULL THEN
        SET NEW.dataSpedizione = CURDATE();
    END IF;
    IF NEW.dataSpedizione IS NOT NULL AND OLD.dataSpedizione IS NULL THEN
        SET NEW.dataArrivo = DATE_ADD(NEW.dataSpedizione, INTERVAL 5 DAY);
    END IF;
    IF NEW.stato = 'spedito' AND NEW.dataSpedizione IS NULL THEN
        SET NEW.dataSpedizione = CURDATE();
        SET NEW.dataArrivo = DATE_ADD(NEW.dataSpedizione, INTERVAL 5 DAY);
    END IF;
END //

-- ========================================
-- TRIGGER CONTROLLO DISPONIBILITÀ
-- ========================================
DROP TRIGGER IF EXISTS check_disponibilita_before_insert //
CREATE TRIGGER check_disponibilita_before_insert
BEFORE INSERT ON NerdvanaStore.ORDINE_ARTICOLO
FOR EACH ROW
BEGIN
    DECLARE disponibile INT;
    SELECT quantita INTO disponibile
    FROM NerdvanaStore.ARTICOLO
    WHERE id = NEW.articolo_id;
    
    IF disponibile < NEW.quantita THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Quantità richiesta non disponibile in magazzino.';
    END IF;
END //

DROP TRIGGER IF EXISTS update_stock_after_ordine_confirmed //
CREATE TRIGGER update_stock_after_ordine_confirmed
AFTER UPDATE ON NerdvanaStore.ORDINE
FOR EACH ROW
BEGIN
    IF OLD.stato = 'in_attesa' AND NEW.stato = 'confermato' THEN
        UPDATE NerdvanaStore.ARTICOLO a
        JOIN NerdvanaStore.ORDINE_ARTICOLO oa ON a.id = oa.articolo_id
        SET a.quantita = a.quantita - oa.quantita
        WHERE oa.ordine_id = NEW.id;
    END IF;
    IF OLD.stato = 'confermato' AND NEW.stato = 'annullato' THEN
        UPDATE NerdvanaStore.ARTICOLO a
        JOIN NerdvanaStore.ORDINE_ARTICOLO oa ON a.id = oa.articolo_id
        SET a.quantita = a.quantita + oa.quantita
        WHERE oa.ordine_id = NEW.id;
    END IF;
END //

DELIMITER ;


INSERT INTO UTENTE (nome, cognome, email, numeroCivico, indirizzo, cap, dataNascita, ruolo, telefono, passwordHash, cittaResidenza)
VALUES
('Mario', 'Rossi', 'mario.rossi@example.com', '12', 'Via Roma', '20100', '1990-05-15', 'cliente', '3331234567', 'hash1', 'Milano'),
('Luigi', 'Bianchi', 'luigi.bianchi@example.com', '8', 'Corso Italia', '50100', '1985-08-20', 'cliente', '3209876543', 'hash2', 'Firenze'),
('Anna', 'Verdi', 'anna.verdi@example.com', '25', 'Piazza Garibaldi', '10100', '1993-03-10', 'cliente', '3401122334', 'hash3', 'Torino'),
('Giulia', 'Neri', 'giulia.neri@example.com', '5B', 'Via Dante', '80100', '1998-12-01', 'admin', '3805566778', 'hash4', 'Napoli'),
('Carlo', 'Romano', 'carlo.romano@example.com', '44', 'Viale Europa', '70100', '1991-07-22', 'cliente', '3394455667', 'hash5', 'Bari');

-- ========================================
-- CARTA DI CREDITO
-- ========================================
INSERT INTO CARTA_DI_CREDITO (utente_id, nomeTitolare, numeroCarta, cvv, scadenza)
VALUES
(1, 'Mario Rossi', '1111-2222-3333-4444', '123', '2026-05-01'),
(2, 'Luigi Bianchi', '5555-6666-7777-8888', '456', '2027-09-01'),
(3, 'Anna Verdi', '9999-0000-1111-2222', '789', '2028-03-01'),
(4, 'Giulia Neri', '3333-4444-5555-6666', '321', '2025-12-01'),
(5, 'Carlo Romano', '7777-8888-9999-0000', '654', '2029-07-01');

-- ========================================
-- WISHLIST
-- ========================================
INSERT INTO WISHLIST (utente_id)
VALUES
(1),
(2),
(3),
(4),
(5);

-- ========================================
-- ARTICOLO
-- ========================================
INSERT INTO ARTICOLO (numeroSeriale, nome, tipo, prezzo, quantita, descrizione, url)
VALUES
('A1001', 'One Piece Volume 1', 'Manga', 7.99, 50, 'Primo volume del manga One Piece.', 'http://example.com/onepiece1.jpg'),
('A1002', 'Batman: Year One', 'Fumetti', 15.50, 30, 'Graphic novel di Frank Miller.', 'http://example.com/batman-yearone.jpg'),
('A1003', 'Catan', 'BoardGame', 39.99, 20, 'Gioco da tavolo strategico.', 'http://example.com/catan.jpg'),
('A1004', 'Funko Pop! Pikachu', 'Funko', 12.99, 40, 'Figure da collezione Pokémon.', 'http://example.com/funko-pikachu.jpg'),
('A1005', 'The Last of Us Part II', 'Videogiochi', 59.90, 15, 'Videogioco per PS4.', 'http://example.com/tlou2.jpg');

-- ========================================
-- ORDINE
-- ========================================
INSERT INTO ORDINE (utente_id, carta_credito_id, numeroArticoli, importo, dataSpedizione, dataArrivo, stato)
VALUES
(1, 1, 2, 23.49, '2025-09-10', '2025-09-15', 'consegnato'),
(2, 2, 1, 15.50, '2025-09-12', '2025-09-17', 'spedito'),
(3, 3, 3, 107.88, '2025-09-13', '2025-09-18', 'confermato'),
(4, 4, 1, 59.90, NULL, NULL, 'in_attesa'),
(5, 5, 2, 72.89, '2025-09-14', '2025-09-19', 'spedito');

-- ========================================
-- FATTURA
-- ========================================
INSERT INTO FATTURA (ordine_id, urlFattura)
VALUES
(1, 'http://example.com/fatture/fattura1.pdf'),
(2, 'http://example.com/fatture/fattura2.pdf'),
(3, 'http://example.com/fatture/fattura3.pdf'),
(4, 'http://example.com/fatture/fattura4.pdf'),
(5, 'http://example.com/fatture/fattura5.pdf');

-- ========================================
-- ORDINE_ARTICOLO
-- ========================================
INSERT INTO ORDINE_ARTICOLO (ordine_id, articolo_id, quantita)
VALUES
(1, 1, 1), -- One Piece
(1, 4, 1), -- Funko Pikachu
(2, 2, 1), -- Batman
(3, 3, 2), -- Catan
(3, 5, 1), -- Last of Us
(4, 5, 1), -- Last of Us
(5, 1, 2); -- One Piece

-- ========================================
-- WISHLIST_ARTICOLO
-- ========================================
INSERT INTO WISHLIST_ARTICOLO (wishlist_id, articolo_id)
VALUES
(1, 5), -- Mario vuole Last of Us
(2, 3), -- Luigi vuole Catan
(3, 4), -- Anna vuole Funko Pikachu
(4, 2), -- Giulia vuole Batman
(5, 1); -- Carlo vuole One Piece

