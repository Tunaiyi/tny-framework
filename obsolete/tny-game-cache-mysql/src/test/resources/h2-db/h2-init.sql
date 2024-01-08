/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
