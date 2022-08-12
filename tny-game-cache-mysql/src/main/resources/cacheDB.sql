DROP PROCEDURE IF EXISTS `createTables`;

DELIMITER
$$

CREATE PROCEDURE `createTables`(total int)
BEGIN
    declare
i int;
    set
i = 0;
    while
i<total do
    
        set @delSql = concat('DROP TABLE IF EXISTS Cache_', i, ';');
PREPARE delTable from @delSql;
EXECUTE delTable;

set
@crtSql = concat(
            'create table Cache_', i,
	        ' (`key` varchar(200) NOT NULL,',
	        '`flags` int NOT NULL,',
	        '`data` mediumblob,',
	        '`expire` bigint(20) NOT NULL,',
	        '`version` bigint(20) NOT NULL DEFAULT "0",',
	        '`saveAt` bigint(20) NOT NULL,',
	        'PRIMARY KEY (`key`),',
	 		 'INDEX `index_flags` (`flags` ASC)',
	        ') ENGINE=InnoDB DEFAULT CHARSET=utf8');
PREPARE crtTable from @crtSql;
EXECUTE crtTable;

set
i = i + 1;
end while;
END;$$

CALL createTables(10);


DROP PROCEDURE IF EXISTS `createTablesByName`;
DELIMITER
$$

CREATE PROCEDURE `createTablesByName`(tableName varchar (45))
BEGIN
/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

--    set @delSql = concat('DROP TABLE IF EXISTS `', tableName, '`;');
--    PREPARE delTable from  @delSql;
--    EXECUTE delTable;
    
    set
@crtSql = concat(
        'create table `', tableName, '`',
        ' (`key` varchar(200) NOT NULL,',
        '`flags` int NOT NULL,',
        '`data` mediumblob,',
        '`expire` bigint(20) NOT NULL,',
        '`version` bigint(20) NOT NULL DEFAULT "0",',
        '`saveAt` bigint(20) NOT NULL,',
        'PRIMARY KEY (`key`),',
 		 'INDEX `index_flags` (`flags` ASC)',
        ') ENGINE=InnoDB DEFAULT CHARSET=utf8');
PREPARE crtTable from @crtSql;
EXECUTE crtTable;
END;

$$
