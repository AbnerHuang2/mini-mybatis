create database skitii_mybatis;

-- skitii_mybatis.t_user definition

CREATE TABLE `t_user` (
                          `id` bigint(20) DEFAULT NULL,
                          `name` varchar(64) DEFAULT NULL,
                          `age` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into t_user values(1,'skitii',18);

alter table t_user modify column id bigint(20) unsigned primary key auto_increment;
alter table t_user add column user_email varchar(32);