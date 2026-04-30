-- CREATE
CREATE TABLE
    categoria_alimento (
        id_categoria_alimento SERIAL PRIMARY KEY,
        nome_categoria VARCHAR(255) NOT NULL
    );

CREATE TABLE
    alimento (
        id_alimento SERIAL PRIMARY KEY,
        nome_alimento VARCHAR(255) NOT NULL,
        id_categoria INT NOT NULL,
        FOREIGN KEY (id_categoria) REFERENCES categoria_alimento (id_categoria_alimento) ON DELETE RESTRICT
    );

CREATE TABLE
    ambiente (
        id_ambiente SERIAL PRIMARY KEY,
        nome_ambiente VARCHAR(255) NOT NULL,
        tipo_ambiente CHAR(1) NOT NULL
    );

CREATE TABLE
    item_ambiente (
        id_item_ambiente SERIAL PRIMARY KEY,
        quantidade INT NOT NULL,
        data_cadastro DATE,
        data_vencimento DATE,
        id_alimento INT NOT NULL,
        id_ambiente INT NOT NULL,
        FOREIGN KEY (id_alimento) REFERENCES alimento (id_alimento) ON DELETE RESTRICT,
        FOREIGN KEY (id_ambiente) REFERENCES ambiente (id_ambiente) ON DELETE CASCADE
    );

CREATE TABLE
    lista_de_compra (
        id_lista SERIAL PRIMARY KEY,
        nome_lista VARCHAR(255) NOT NULL,
        data_compra DATE
    );

CREATE TABLE
    item_lista_compra (
        id_item_lista SERIAL PRIMARY KEY,
        quantidade INT NOT NULL,
        status_compra CHAR(1),
        id_alimento INT NOT NULL,
        id_lista INT NOT NULL,
        FOREIGN KEY (id_alimento) REFERENCES alimento (id_alimento) ON DELETE RESTRICT,
        FOREIGN KEY (id_lista) REFERENCES lista_de_compra (id_lista) ON DELETE CASCADE
    );