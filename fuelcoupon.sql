--
-- Déchargement des données de la table `hibernate_sequence`
--

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
(1);

-- --------------------------------------------------------

--
-- Déchargement des données de la table `revinfo`
--

INSERT INTO `revinfo` (`rev`, `revtstmp`) VALUES
(1, 1671432122632)
-- --------------------------------------------------------

--
-- Déchargement des données de la table `role_user`
--

INSERT INTO `role_user` (`id`, `description`, `name`) VALUES
(1, '', 'ROLE_ADMIN'),
(2, '', 'ROLE_SUPERADMIN'),
(3, '', 'ROLE_AGENT'),
(4, '', 'ROLE_PRE_VERIFICATION_USER'),
(5, '', 'ROLE_USER');

-- --------------------------------------------------------

--
-- Déchargement des données de la table `status`
--

INSERT INTO `status` (`id`, `description`, `name`) VALUES
(1, '', 'CREATED'),
(2, '', 'MODIFIED'),
(3, '', 'DELETED'),
(4, '', 'CLOSED'),
(5, '', 'CANCELED'),
(6, '', 'ACCEPTED'),
(7, '', 'REFUSED'),
(8, '', 'PAID'),
(9, '', 'IN_PROCESS_OF_DELIVERY'),
(11, '', 'DELIVERED'),
(12, '', 'ACTIVATED'),
(13, '', 'DISABLED'),
(14, '', 'SUSPENDED'),
(15, '', 'AVAILABLE'),
(19, NULL, 'USED');


--
-- Déchargement des données de la table `status_order`
--

INSERT INTO `status_order` (`id`, `description`, `name`) VALUES
(1, '', 'CREATED'),
(2, '', 'MODIFIED'),
(3, '', 'DELETED'),
(4, '', 'CLOSED'),
(5, '', 'CANCELED'),
(6, '', 'ACCEPTED'),
(7, '', 'REFUSED'),
(8, '', 'PAID'),
(9, '', 'IN_PROCESS_OF_DELIVERY'),
(11, '', 'DELIVERED');

-- --------------------------------------------------------

--
-- Déchargement des données de la table `status_user`
--

INSERT INTO `status_user` (`id`, `description`, `name`) VALUES
(1, '', 'USER_ENABLED'),
(2, '', 'USER_DISABLED');


-- --------------------------------------------------------

--
-- Déchargement des données de la table `store`
--

INSERT INTO `store` (`id`, `created_at`, `internal_reference`, `localization`, `update_at`, `status_id`) VALUES
(1, '2022-12-19 06:36:22', 123456789, 'Douala', '2022-12-19 06:36:22', 1),
(2, '2022-12-19 06:36:22', 987654321, 'Yaounde', '2022-12-19 06:36:22', 1);

-- --------------------------------------------------------

--
-- Déchargement des données de la table `storehouse`
--

INSERT INTO `storehouse` (`id`, `create_at`, `id_store`, `internal_reference`, `name`, `type`, `update_at`, `status_id`) VALUES
(1, '2022-12-19', 123456789, 445799816, 'Entrepôt de Vente 1', 'vente', NULL, 1),
(2, '2022-12-19', 123456789, 241908726, 'Entrepôt de Stockage 1', 'stockage', NULL, 1);


-- --------------------------------------------------------

--
-- Déchargement des données de la table `type_account`
--

INSERT INTO `type_account` (`id`, `name`) VALUES
(1, 'STORE_KEEPER'),
(2, 'MANAGER_COUPON'),
(3, 'MANAGER_STORE'),
(8, 'MANAGER_ORDER'),
(3, 'MANAGER_STORE'),
(4, 'TREASURY'),
(5, 'CUSTOMER_SERVICE'),
(6, 'MANAGER_STATION'),
(7, 'POMPIST');


-- --------------------------------------------------------

--
-- Déchargement des données de la table `type_client`
--

INSERT INTO `type_client` (`id`, `name`) VALUES
(1, 'ENTREPRISE'),
(2, 'PARTICULAR'),
(3, 'INSTITUTION');

-- --------------------------------------------------------

--
-- Déchargement des données de la table `type_document`
--

INSERT INTO `type_document` (`id`, `name`) VALUES
(1, 'INVOICE'),
(2, 'DELIVERY');

-- --------------------------------------------------------

--
-- Déchargement des données de la table `type_voucher`
--

INSERT INTO `type_voucher` (`id`, `amount`, `created_at`, `designation`, `internal_reference`, `update_at`, `status_id`) VALUES
(1, 3000, '2022-12-19 07:32:14', 'Coupon de 3000', 123456781, '2022-12-19 07:32:14', 1),
(2, 10000, '2022-12-19 07:32:14', 'Coupon de 10000', 987654329, '2022-12-19 07:32:14', 1),
(3, 5000, '2022-12-19 07:33:25', 'Coupon de 5000', 654789321, '2022-12-19 07:33:25', 1);


-- --------------------------------------------------------

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`user_id`, `created_at`, `update_at`, `created_date`, `date_last_login`, `email`, `emailverify`, `first_name`, `id_store`, `internal_reference`, `is_delete`, `is_first_connection`, `last_name`, `notification_key`, `otp_code`, `otp_code_createdat`, `password`, `phoneverify`, `pin_code`, `position`, `telephone`, `token_auth`, `using_2fa`, `created_by_user_id`, `update_by_user_id`, `status_id`, `type_account_id`) VALUES
(1, NULL, NULL, '2022-12-21 20:04:23', '2022-12-28 17:38:55', 'arnoldkim4@gmail.com', b'0', 'THERRY', 123456789, 799898371, b'0', b'1', 'KIM', NULL, NULL, NULL, '$2a$10$ftHP0e4tf7YSraum0CxPouqIeVSG1v2GAHnO1R.Ige6ZkkVU7/5IG', b'0', 12345, 'Développeur FULL STACK JAVA', '690362808', NULL, b'0', NULL, NULL, 1, 3);

-- --------------------------------------------------------

--
-- Déchargement des données de la table `users_aud`
--

INSERT INTO `users_aud` (`user_id`, `rev`, `revtype`, `created_date`, `created_date_mod`, `date_last_login`, `date_last_login_mod`, `email`, `email_mod`, `emailverify`, `emailverify_mod`, `first_name`, `first_name_mod`, `id_store`, `id_store_mod`, `internal_reference`, `internal_reference_mod`, `is_delete`, `is_delete_mod`, `is_first_connection`, `is_first_connection_mod`, `last_name`, `last_name_mod`, `notification_key`, `notification_key_mod`, `otp_code`, `otp_code_mod`, `otp_code_createdat`, `otp_code_createdat_mod`, `password`, `password_mod`, `phoneverify`, `phoneverify_mod`, `pin_code`, `pin_code_mod`, `position`, `position_mod`, `telephone`, `telephone_mod`, `token_auth`, `token_auth_mod`, `using_2fa`, `using2fa_mod`, `roles_mod`, `status_id`, `status_mod`, `type_account_id`, `type_account_mod`) VALUES
(1, 1, 0, '2022-12-19 06:42:03', b'1', NULL, b'0', 'arnoldkim4@gmail.com', b'1', b'0', b'1', 'Arnold', b'1', 123456789, b'1', 799898371, b'1', b'0', b'1', b'0', b'1', 'KOM', b'1', NULL, b'0', NULL, b'0', NULL, b'0', '$2a$10$ftHP0e4tf7YSraum0CxPouqIeVSG1v2GAHnO1R.Ige6ZkkVU7/5IG', b'1', b'0', b'1', 1234, b'1', 'Développeur FULL STACK JAVAEE', b'1', '690362808', b'1', NULL, b'0', b'0', b'1', b'1', 1, b'1', 3, b'1'),


-- --------------------------------------------------------

--
-- Déchargement des données de la table `users_roles`
--

INSERT INTO `users_roles` (`users_user_id`, `roles_id`) VALUES
(1, 5);

-- --------------------------------------------------------

--
-- Déchargement des données de la table `users_roles_aud`
--

INSERT INTO `users_roles_aud` (`rev`, `users_user_id`, `roles_id`, `revtype`) VALUES
(1, 1, 5, 0);
