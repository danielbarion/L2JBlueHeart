CREATE TABLE IF NOT EXISTS `clan_wars` (
  `clan1` varchar(35) NOT NULL DEFAULT '',
  `clan2` varchar(35) NOT NULL DEFAULT '',
  `wantspeace1` decimal(1,0) NOT NULL DEFAULT '0',
  `wantspeace2` decimal(1,0) NOT NULL DEFAULT '0',
  KEY `clan1` (`clan1`),
  KEY `clan2` (`clan2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;