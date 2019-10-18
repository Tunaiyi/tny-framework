CREATE TABLE Account (
  `uid`            BIGINT(20) NOT NULL,
  `account`        VARCHAR(50),
  `account`      VARCHAR(50),
  `device`         VARCHAR(128),
  `deviceId`       VARCHAR(128),
  `pf`             VARCHAR(45),
  `zone`           INT(11),
  `entry`          INT(11),
  `ad`             VARCHAR(45),
  `createSid`      INT(11),
  `createDate`     INT(11),
  `createAt`       BIGINT(20),
  `createRoleDate` INT(11),
  `createRoleAt`   BIGINT(20),
  `level`          INT(11),
  `name`           VARCHAR(45),
  `totalPower`     INT(11),
  `activeDate`     INT(11),
  `activeAt`       BIGINT(20),
  `onlineTime`       BIGINT(20),
  `offlineTime`      BIGINT(20),
  `noLevels`       INT(11),
  `elLevels`       INT(11),
  `guideStep`      INT(11),
  `payDate`        INT(11),
  `payAt`          BIGINT(20),
  PRIMARY KEY (`uid`)
);


CREATE TABLE CTaskReceiver (
  `key`     VARCHAR(100) NOT NULL,
  `flags`   TINYINT(3)   NOT NULL,
  `data`    MEDIUMBLOB,
  `expire`  BIGINT(20)   NOT NULL,
  `version` BIGINT(20)   NOT NULL DEFAULT 0,
  `saveAt`  BIGINT(20)   NOT NULL,
  `UID`     BIGINT(20),
  `itemId`  INT(20),
  `number`  INT(20),
  PRIMARY KEY (`key`)
);


CREATE TABLE CGameServer (
  `key`     VARCHAR(100) NOT NULL,
  `flags`   TINYINT(3)   NOT NULL,
  `data`    MEDIUMBLOB,
  `expire`  BIGINT(20)   NOT NULL,
  `version` BIGINT(20)   NOT NULL DEFAULT 0,
  `saveAt`  BIGINT(20)   NOT NULL,
  `UID`     BIGINT(20),
  `itemId`  INT(20),
  `number`  INT(20),
  PRIMARY KEY (`key`)
);