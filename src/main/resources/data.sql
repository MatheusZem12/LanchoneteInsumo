-- Seeds para tb_insumos (6 registros)
INSERT INTO tb_insumos (id, codigo, nome, descricao, quantidade_critica) VALUES (1, 'INS-001', 'Farinha de Trigo', 'Farinha para preparo de massas', 10);
INSERT INTO tb_insumos (id, codigo, nome, descricao, quantidade_critica) VALUES (2, 'INS-002', 'Açúcar', 'Açúcar refinado', 15);
INSERT INTO tb_insumos (id, codigo, nome, descricao, quantidade_critica) VALUES (3, 'INS-003', 'Leite Longa Vida', 'Leite integral 1L', 8);
INSERT INTO tb_insumos (id, codigo, nome, descricao, quantidade_critica) VALUES (4, 'INS-004', 'Ovos', 'Bandeja com 30 unidades', 5);
INSERT INTO tb_insumos (id, codigo, nome, descricao, quantidade_critica) VALUES (5, 'INS-005', 'Chocolate em Pó', 'Chocolate em pó para bebidas', 7);
INSERT INTO tb_insumos (id, codigo, nome, descricao, quantidade_critica) VALUES (6, 'INS-006', 'Manteiga', 'Manteiga sem sal 200g', 6);

-- Seeds para tb_usuarios (5 registros) - ids numéricos (Long)
INSERT INTO tb_usuarios (id, nome, email, senha, telefone) VALUES (1, 'Ana Silva', 'ana.silva@example.com', '$2a$12$YIHqAnLBwoBfKgiC.U7ssOiylMqQkvpgF6rz2bR1LmV3nIOHHE.Dq', '11999990001');
INSERT INTO tb_usuarios (id, nome, email, senha, telefone) VALUES (2, 'Bruno Costa', 'bruno.costa@example.com', '$2a$12$YIHqAnLBwoBfKgiC.U7ssOiylMqQkvpgF6rz2bR1LmV3nIOHHE.Dq', '11999990002');
INSERT INTO tb_usuarios (id, nome, email, senha, telefone) VALUES (3, 'Carla Pereira', 'carla.pereira@example.com', '$2a$12$YIHqAnLBwoBfKgiC.U7ssOiylMqQkvpgF6rz2bR1LmV3nIOHHE.Dq', '11999990003');
INSERT INTO tb_usuarios (id, nome, email, senha, telefone) VALUES (4, 'Diego Alves', 'diego.alves@example.com', '$2a$12$YIHqAnLBwoBfKgiC.U7ssOiylMqQkvpgF6rz2bR1LmV3nIOHHE.Dq', '11999990004');
INSERT INTO tb_usuarios (id, nome, email, senha, telefone) VALUES (5, 'Elisa Rocha', 'elisa.rocha@example.com', '$2a$12$YIHqAnLBwoBfKgiC.U7ssOiylMqQkvpgF6rz2bR1LmV3nIOHHE.Dq', '11999990005');

-- Seeds para tb_roles
INSERT INTO tb_roles (id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_roles (id, nome) VALUES (2, 'ROLE_USER');

-- Seeds para tb_usuarios_roles (mapeamento usuário -> role)
INSERT INTO tb_usuarios_roles (id, user_id, role_id) VALUES (1, 1, 1); -- Ana Silva = ADMIN
INSERT INTO tb_usuarios_roles (id, user_id, role_id) VALUES (2, 2, 2); -- Bruno = USER
INSERT INTO tb_usuarios_roles (id, user_id, role_id) VALUES (3, 3, 2); -- Carla = USER
INSERT INTO tb_usuarios_roles (id, user_id, role_id) VALUES (4, 4, 2); -- Diego = USER
INSERT INTO tb_usuarios_roles (id, user_id, role_id) VALUES (5, 5, 2); -- Elisa = USER

-- Seeds para tb_movimentacoes_insumos (>=20 registros)
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (1, 50, 'ENTRADA', '2025-09-01 08:30:00', 1, 1);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (2, 20, 'SAIDA', '2025-09-02 10:15:00', 2, 1);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (3, 100, 'ENTRADA', '2025-09-03 09:00:00', 3, 2);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (4, 30, 'SAIDA', '2025-09-04 12:45:00', 4, 2);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (5, 25, 'ENTRADA', '2025-09-05 14:20:00', 5, 3);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (6, 10, 'SAIDA', '2025-09-06 16:10:00', 1, 3);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (7, 40, 'ENTRADA', '2025-09-07 08:00:00', 2, 4);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (8, 12, 'SAIDA', '2025-09-08 11:30:00', 3, 4);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (9, 60, 'ENTRADA', '2025-09-09 09:45:00', 4, 5);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (10, 15, 'SAIDA', '2025-09-10 15:00:00', 5, 5);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (11, 20, 'ENTRADA', '2025-09-11 10:00:00', 1, 6);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (12, 5, 'SAIDA', '2025-09-12 13:30:00', 2, 6);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (13, 10, 'SAIDA', '2025-09-13 09:15:00', 3, 1);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (14, 30, 'ENTRADA', '2025-09-14 08:50:00', 4, 2);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (15, 8, 'SAIDA', '2025-09-15 17:20:00', 5, 3);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (16, 22, 'ENTRADA', '2025-09-16 07:40:00', 1, 4);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (17, 18, 'SAIDA', '2025-09-17 14:55:00', 2, 5);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (18, 45, 'ENTRADA', '2025-09-18 09:05:00', 3, 6);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (19, 3, 'SAIDA', '2025-09-19 19:30:00', 4, 6);
INSERT INTO tb_movimentacoes_insumos (id, quantidade, tipo_movimentacao, data, usuario_id, insumo_id) VALUES (20, 7, 'SAIDA', '2025-09-20 12:10:00', 5, 1);

-- Fim dos seeds
