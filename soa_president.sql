-- phpMyAdmin SQL Dump
-- version 3.2.1
-- http://www.phpmyadmin.net
--
-- Serveur: localhost
-- Généré le : Jeu 20 Février 2014 à 21:10
-- Version du serveur: 5.1.37
-- Version de PHP: 5.3.0

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Base de données: `soa_president`
--

-- --------------------------------------------------------

--
-- Structure de la table `carte`
--

CREATE TABLE IF NOT EXISTS `carte` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `numero` int(11) NOT NULL,
  `couleur` varchar(100) NOT NULL,
  `libelle` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=53 ;

--
-- Contenu de la table `carte`
--

INSERT INTO `carte` (`id`, `numero`, `couleur`, `libelle`) VALUES
(1, 1, 'carreau', '1Cca'),
(2, 2, 'carreau', '2Cca'),
(3, 3, 'carreau', '3Cca'),
(4, 4, 'carreau', '4Cca'),
(5, 5, 'carreau', '5Cca'),
(6, 6, 'carreau', '6Cca'),
(7, 7, 'carreau', '7Cca'),
(8, 8, 'carreau', '8Cca'),
(9, 9, 'carreau', '9Cca'),
(10, 10, 'carreau', '10Cca'),
(11, 11, 'carreau', '11Cca'),
(12, 12, 'carreau', '12Cca'),
(13, 13, 'carreau', '13Cca'),
(14, 1, 'coeur', '1Cco'),
(15, 2, 'coeur', '2Cco'),
(16, 3, 'coeur', '3Cco'),
(17, 4, 'coeur', '4Cco'),
(18, 5, 'coeur', '5Cco'),
(19, 6, 'coeur', '6Cco'),
(20, 7, 'coeur', '7Cco'),
(21, 8, 'coeur', '8Cco'),
(22, 9, 'coeur', '9Cco'),
(23, 10, 'coeur', '10Cco'),
(24, 11, 'coeur', '11Cco'),
(25, 12, 'coeur', '12Cco'),
(26, 13, 'coeur', '13Cco'),
(27, 1, 'trefle', '1Ctr'),
(28, 2, 'trefle', '2Ctr'),
(29, 3, 'trefle', '3Ctr'),
(30, 4, 'trefle', '4Ctr'),
(31, 5, 'trefle', '5Ctr'),
(32, 6, 'trefle', '6Ctr'),
(33, 7, 'trefle', '7Ctr'),
(34, 8, 'trefle', '8Ctr'),
(35, 9, 'trefle', '9Ctr'),
(36, 10, 'trefle', '10Ctr'),
(37, 11, 'trefle', '11Ctr'),
(38, 12, 'trefle', '12Ctr'),
(39, 13, 'trefle', '13Ctr'),
(40, 1, 'pique', '1Cpi'),
(41, 2, 'pique', '2Cpi'),
(42, 3, 'pique', '3Cpi'),
(43, 4, 'pique', '4Cpi'),
(44, 5, 'pique', '5Cpi'),
(45, 6, 'pique', '6Cpi'),
(46, 7, 'pique', '7Cpi'),
(47, 8, 'pique', '8Cpi'),
(48, 9, 'pique', '9Cpi'),
(49, 10, 'pique', '10Cpi'),
(50, 11, 'pique', '11Cpi'),
(51, 12, 'pique', '12Cpi'),
(52, 13, 'pique', '13Cpi');

-- --------------------------------------------------------

--
-- Structure de la table `jouer`
--

CREATE TABLE IF NOT EXISTS `jouer` (
  `id_partie` int(11) NOT NULL,
  `id_joueur` int(11) NOT NULL,
  `scorepartie` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `jouer`
--


-- --------------------------------------------------------

--
-- Structure de la table `joueur`
--

CREATE TABLE IF NOT EXISTS `joueur` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `prenom` varchar(100) NOT NULL,
  `mail` varchar(100) NOT NULL,
  `login` varchar(100) NOT NULL,
  `mdp` varchar(100) NOT NULL,
  `score` int(11) NOT NULL,
  `en_attente_partie` tinyint(1) NOT NULL DEFAULT '0',
  `nom` varchar(100) NOT NULL,
  `id_statistique` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `joueur`
--

INSERT INTO `joueur` (`id`, `prenom`, `mail`, `login`, `mdp`, `score`, `en_attente_partie`, `nom`, `id_statistique`) VALUES
(1, 'charlotte', 'charlotte.saintpierre@gmail.com', 'cha', 'cha', 0, 0, 'saintpierre', 0),
(2, 'Amélie', 'amelie.lefevre19@gmail.com', 'Ame', 'lie', 0, 0, 'Lefèvre', 0);

-- --------------------------------------------------------

--
-- Structure de la table `partie`
--

CREATE TABLE IF NOT EXISTS `partie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nbjoueur` int(11) NOT NULL,
  `etat` varchar(100) NOT NULL,
  `datedebut` date NOT NULL,
  `nbtour` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Contenu de la table `partie`
--


-- --------------------------------------------------------

--
-- Structure de la table `sauvegarde`
--

CREATE TABLE IF NOT EXISTS `sauvegarde` (
  `id_partie` int(11) NOT NULL,
  `id_carte` int(11) NOT NULL,
  `id_joueur` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `sauvegarde`
--


-- --------------------------------------------------------

--
-- Structure de la table `statistique`
--

CREATE TABLE IF NOT EXISTS `statistique` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `nbpartiejouee` int(11) NOT NULL,
  `nbpartiegagnee` int(11) NOT NULL,
  `nbpartieperdue` int(11) NOT NULL,
  `nbjoueurmoyenparpartie` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Contenu de la table `statistique`
--

