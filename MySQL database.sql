
--
-- Banco de dados: `dbvendas`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_caixa`
--

CREATE TABLE `tab_caixa` (
  `id` int(11) NOT NULL,
  `nome` text NOT NULL,
  `data` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
--
-- Estrutura para tabela `tab_carinho`
--

CREATE TABLE `tab_carinho` (
  `id` int(11) NOT NULL,
  `numero` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

---------------------------------------------

--
-- Estrutura para tabela `tab_dados`
--

CREATE TABLE `tab_dados` (
  `id` int(11) NOT NULL,
  `id_venda` int(11) NOT NULL,
  `hora` time NOT NULL,
  `cash` double NOT NULL,
  `card` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE
  `tab_customer` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `empresa` text NOT NULL,
    `nuit` int DEFAULT NULL,
    `endereco` text,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-
--
-- Estrutura para tabela `tab_dadosclientecard`
--

CREATE TABLE `tab_dadosclientecard` (
  `id_venda` int(11) NOT NULL,
  `data` datetime NOT NULL,
  `idRecibo` int(11) NOT NULL,
  `nrpos` int(20) DEFAULT NULL,
  `expdate` int(20) DEFAULT NULL,
  `card` int(20) DEFAULT NULL,
  `valor` int(120) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-
-- --------------------------------------------------------

--
-- Estrutura para tabela `tab_fecho`
--

CREATE TABLE `tab_fecho` (
  `id` int(11) NOT NULL,
  `nome` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `data` datetime NOT NULL,
  `valor_total` double NOT NULL,
  `valor_falta` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--

--
-- Estrutura para tabela `tab_log_de_login`
--

CREATE TABLE `tab_log_de_login` (
  `id` int(11) NOT NULL,
  `nome_completo` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `data_hora_login` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--

--
-- Estrutura para tabela `tab_produtos`
--

CREATE TABLE `tab_produtos` (
  `id` int(11) NOT NULL,
  `id_codigo` int(45) NOT NULL,
  `descricao` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `stock_loja` int(200) DEFAULT NULL,
  `stock_armazem` int(200) NOT NULL,
  `preco` double NOT NULL,
  `preco_venda` double NOT NULL,
  `imagem` longblob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Estrutura para tabela `tab_produtos_devolvidos`
--

CREATE TABLE `tab_produtos_devolvidos` (
  `id_produto` int(11) NOT NULL,
  `id_venda` int(11) NOT NULL,
  `codigo` int(11) NOT NULL,
  `descricao` text NOT NULL,
  `quantidade` int(11) NOT NULL,
  `preco` double NOT NULL,
  `subtotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--

--
-- Estrutura para tabela `tab_produtos_salvos`
--

CREATE TABLE `tab_produtos_salvos` (
  `id` int(11) NOT NULL,
  `id_carrinho` int(11) NOT NULL,
  `codigo` int(11) NOT NULL,
  `descricao` text NOT NULL,
  `quantidade` int(11) NOT NULL,
  `preco` float NOT NULL,
  `subtotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Despejando dados para a tabela `tab_produtos_salvos`
--


--
-- Estrutura para tabela `tab_produtos_vendidos`
--

CREATE TABLE `tab_produtos_vendidos` (
  `id_produto` int(11) NOT NULL,
  `id_venda` int(11) NOT NULL,
  `codigo` int(20) NOT NULL,
  `descricao` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `quantidade` int(11) NOT NULL,
  `preco` double NOT NULL,
  `subtotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
---
-- Estrutura para tabela `tab_return`
--

CREATE TABLE `tab_return` (
  `id` int(11) NOT NULL,
  `idDaTroca` int(11) NOT NULL,
  `dataHora` datetime NOT NULL,
  `autorizadoPor` text NOT NULL,
  `operador` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;



--
-- Estrutura para tabela `tab_users`
--

CREATE TABLE `tab_users` (
  `id_user` int(11) NOT NULL,
  `username` text CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `nome_utilizador` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `senha` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `acesso_caixa` tinyint(1) DEFAULT NULL,
  `acesso_gestao` tinyint(1) DEFAULT NULL,
  `acesso_contabilista` tinyint(1) DEFAULT NULL,
  `acesso_admin` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `tab_vendas` (
  `id_venda` int(11) NOT NULL,
  `id_recibo` int(11) NOT NULL,
  `data_venda` datetime NOT NULL,
  `operador` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `valor_total` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE
  `tab_autorizacao` (
    `id` int NOT NULL AUTO_INCREMENT,
    `autorizedby` varchar(100) NOT NULL,
    `operacao` varchar(100) NOT NULL,
    `datahora` datetime NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
  `tab_entrada_stock` (
    `id` int NOT NULL AUTO_INCREMENT,
    `autorizedby` varchar(255) NOT NULL,
    `operacao` varchar(255) NOT NULL,
    `datahora` datetime NOT NULL,
    `produto` int NOT NULL,
    `qtdanterior` double NOT NULL,
    `qtdatual` double NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
  
CREATE TABLE
  `tab_extrato_caixa` (
    `id` int NOT NULL AUTO_INCREMENT,
    `id_venda` int NOT NULL,
    `hora` time NOT NULL,
    `cash` double NOT NULL,
    `card` double NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
  
CREATE TABLE
  `tab_notas` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `id_fecho` int NOT NULL,
    `n1000` int DEFAULT NULL,
    `n500` int DEFAULT NULL,
    `n200` int DEFAULT NULL,
    `n100` int DEFAULT NULL,
    `n50` int DEFAULT NULL,
    `n20` int DEFAULT NULL,
    `moedas` float DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `id_fecho` (`id_fecho`),
    CONSTRAINT `fk_fecho_id` FOREIGN KEY (`id_fecho`) REFERENCES `tab_fecho` (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
  
CREATE TABLE
  `tab_produtos_promocao` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `codigo_do_produto` int DEFAULT NULL,
    `preco_normal` double DEFAULT NULL,
    `preco_promocional` double DEFAULT NULL,
    `data_inicio_promocao` date DEFAULT NULL,
    `data_fim_promocao` date DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `codigo_do_produto` (`codigo_do_produto`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

--
-- Índices de tabela `tab_caixa`
--
ALTER TABLE `tab_caixa`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_carinho`
--
ALTER TABLE `tab_carinho`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_dados`
--
ALTER TABLE `tab_dados`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_valor` (`id_venda`);

--
-- Índices de tabela `tab_dadosclientecard`
--
ALTER TABLE `tab_dadosclientecard`
  ADD PRIMARY KEY (`id_venda`);

--
-- Índices de tabela `tab_fecho`
--
ALTER TABLE `tab_fecho`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_log_de_login`
--
ALTER TABLE `tab_log_de_login`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_produtos`
--
ALTER TABLE `tab_produtos`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_produtos_devolvidos`
--
ALTER TABLE `tab_produtos_devolvidos`
  ADD PRIMARY KEY (`id_produto`),
  ADD KEY `id_venda` (`id_venda`);

--
-- Índices de tabela `tab_produtos_salvos`
--
ALTER TABLE `tab_produtos_salvos`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_produtos_vendidos`
--
ALTER TABLE `tab_produtos_vendidos`
  ADD PRIMARY KEY (`id_produto`),
  ADD KEY `fk_venda_id` (`id_venda`);

--
-- Índices de tabela `tab_return`
--
ALTER TABLE `tab_return`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `tab_users`
--
ALTER TABLE `tab_users`
  ADD PRIMARY KEY (`id_user`);

--
-- Índices de tabela `tab_vendas`
--
ALTER TABLE `tab_vendas`
  ADD PRIMARY KEY (`id_venda`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `tab_caixa`
--
ALTER TABLE `tab_caixa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de tabela `tab_carinho`
--
ALTER TABLE `tab_carinho`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de tabela `tab_dados`
--
ALTER TABLE `tab_dados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;

--
-- AUTO_INCREMENT de tabela `tab_dadosclientecard`
--
ALTER TABLE `tab_dadosclientecard`
  MODIFY `id_venda` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT de tabela `tab_fecho`
--
ALTER TABLE `tab_fecho`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT de tabela `tab_log_de_login`
--
ALTER TABLE `tab_log_de_login`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=480;

--
-- AUTO_INCREMENT de tabela `tab_produtos`
--
ALTER TABLE `tab_produtos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de tabela `tab_produtos_devolvidos`
--
ALTER TABLE `tab_produtos_devolvidos`
  MODIFY `id_produto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de tabela `tab_produtos_salvos`
--
ALTER TABLE `tab_produtos_salvos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT de tabela `tab_produtos_vendidos`
--
ALTER TABLE `tab_produtos_vendidos`
  MODIFY `id_produto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=196;

--
-- AUTO_INCREMENT de tabela `tab_return`
--
ALTER TABLE `tab_return`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de tabela `tab_users`
--
ALTER TABLE `tab_users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=65;

--
-- AUTO_INCREMENT de tabela `tab_vendas`
--
ALTER TABLE `tab_vendas`
  MODIFY `id_venda` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=145;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `tab_produtos_vendidos`
--
ALTER TABLE `tab_produtos_vendidos`
  ADD CONSTRAINT `fk_venda_id` FOREIGN KEY (`id_venda`) REFERENCES `tab_vendas` (`id_venda`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
