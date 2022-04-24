create table CPlayer1
(
    `key`     varchar(50) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    PRIMARY KEY (`key`)
);
create table CPlayer2
(
    `key`     varchar(50) NOT NULL,
    `data`    mediumblob,
    `expire`  bigint(20) NOT NULL,
    `version` bigint(20) NOT NULL DEFAULT '0',
    PRIMARY KEY (`key`)
);
