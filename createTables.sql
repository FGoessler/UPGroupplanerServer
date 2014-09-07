CREATE TABLE `acceptedDates` (
  `id`      INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `start`   INT(11) DEFAULT NULL,
  `end`     INT(11) DEFAULT NULL,
  `room`    VARCHAR(50) DEFAULT NULL,
  `groupId` INT(11) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `groupId` (`groupId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

CREATE TABLE `blockedDates` (
  `id`     INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user`   VARCHAR(255)     NOT NULL DEFAULT '',
  `start`  INT(11) DEFAULT NULL,
  `end`    INT(11) DEFAULT NULL,
  `source` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

CREATE TABLE `groups` (
  `id`       INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`     VARCHAR(255)     NOT NULL DEFAULT '',
  `semester` VARCHAR(6) DEFAULT '',
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

CREATE TABLE `invites` (
  `id`           INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `invitee`      VARCHAR(255)     NOT NULL DEFAULT '',
  `invitor`      VARCHAR(25)      NOT NULL DEFAULT '',
  `groupId`      INT(11) UNSIGNED NOT NULL,
  `status`       VARCHAR(50) DEFAULT NULL,
  `lastModified` TIMESTAMP        NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `invitee` (`invitee`),
  KEY `invitor` (`invitor`),
  KEY `groupId` (`groupId`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;

CREATE TABLE `user` (
  `email` VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`email`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;