-- Criação das tabelas para o ERP

CREATE TABLE IF NOT EXISTS cliente (
    codcadastroclientes SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nascimento DATE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    endereco VARCHAR(255),
    numero VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255),
    complemento VARCHAR(255),
    cep VARCHAR(255),
    email VARCHAR(255),
    pontoreferencia VARCHAR(255),
    telefone VARCHAR(255),
    celular VARCHAR(255),
    fax VARCHAR(255),
    fisica BOOLEAN NOT NULL DEFAULT TRUE,
    juridica BOOLEAN NOT NULL DEFAULT FALSE,
    cpf VARCHAR(255) UNIQUE,
    rg VARCHAR(255),
    cnpj VARCHAR(255),
    inscricaoestadual VARCHAR(255),
    observacoes VARCHAR(1000),
    datacadastro DATE NOT NULL DEFAULT CURRENT_DATE,
    nomefantasia VARCHAR(255),
    permiteconveniovinculado BOOLEAN DEFAULT FALSE,
    pais VARCHAR(255),
    estrangeiro BOOLEAN DEFAULT FALSE,
    consumidorfinal BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL CHECK (preco > 0),
    estoque INTEGER NOT NULL DEFAULT 0,
    estoque_minimo INTEGER,
    estoque_maximo INTEGER,
    codigobarras VARCHAR(255),
    ncm VARCHAR(8),
    descricaoresumida VARCHAR(60),
    unidademedida VARCHAR(2),
    precocompra DOUBLE PRECISION DEFAULT 0,
    margemlucro DOUBLE PRECISION DEFAULT 0,
    permitirdesconto BOOLEAN DEFAULT FALSE,
    desconto DOUBLE PRECISION DEFAULT 0,
    ativo BOOLEAN DEFAULT TRUE,
    validade BIGINT,
    cfop VARCHAR(4),
    codigointerno VARCHAR(255),
    datacadastro DATE DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS fornecedor (
    id SERIAL PRIMARY KEY,
    fisica BOOLEAN NOT NULL DEFAULT TRUE,
    juridica BOOLEAN NOT NULL DEFAULT FALSE,
    cpf_cnpj VARCHAR(255) UNIQUE NOT NULL,
    nome_razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255),
    rg_ie VARCHAR(255),
    estrangeiro BOOLEAN DEFAULT FALSE,
    cep VARCHAR(20),
    endereco VARCHAR(255),
    numero VARCHAR(20),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(2),
    complemento VARCHAR(255),
    telefone VARCHAR(50),
    email VARCHAR(255),
    nome_contato VARCHAR(255),
    observacoes TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE produto ADD COLUMN IF NOT EXISTS fornecedor_id INTEGER REFERENCES fornecedor(id);

ALTER TABLE produto ADD COLUMN IF NOT EXISTS estoque_minimo INTEGER;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS estoque_maximo INTEGER;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS codigobarras VARCHAR(255);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS ncm VARCHAR(8);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS descricaoresumida VARCHAR(60);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS unidademedida VARCHAR(2);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS precocompra DOUBLE PRECISION DEFAULT 0;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS margemlucro DOUBLE PRECISION DEFAULT 0;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS permitirdesconto BOOLEAN DEFAULT FALSE;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS desconto DOUBLE PRECISION DEFAULT 0;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS validade BIGINT;
ALTER TABLE produto ADD COLUMN IF NOT EXISTS cfop VARCHAR(4);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS codigointerno VARCHAR(255);
ALTER TABLE produto ADD COLUMN IF NOT EXISTS datacadastro DATE DEFAULT CURRENT_DATE;

CREATE TABLE IF NOT EXISTS pedido (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES cliente(codcadastroclientes),
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS pedido_item (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedido(id) ON DELETE CASCADE,
    produto_id INTEGER REFERENCES produto(id),
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS forma_pagamento (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL UNIQUE,
    permite_desconto BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS pedido_pagamento (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedido(id) ON DELETE CASCADE,
    forma_pagamento_id INTEGER REFERENCES forma_pagamento(id),
    valor_pago DECIMAL(10, 2) NOT NULL,
    desconto DECIMAL(10, 2),
    acrescimo DECIMAL(10, 2)
);
