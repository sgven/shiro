drop table if exists sys_users;
drop table if exists sys_roles;
drop table if exists sys_permissions;
drop table if exists sys_users_roles;
drop table if exists sys_roles_permissions;

create table sys_users (
  id bigint auto_increment,
  username varchar(100) COMMENT '用户名',
  password varchar(100) COMMENT '密码',
  salt varchar(100) COMMENT '盐',
  locked bool default false COMMENT '是否锁定，用于封禁用户使用',
  constraint pk_sys_users primary key(id)
) charset=utf8 ENGINE=InnoDB;
create unique index idx_sys_users_username on sys_users(username);

create table sys_roles (
  id bigint auto_increment,
  role varchar(100) COMMENT '角色标识符',
  description varchar(100) COMMENT '描述',
  available bool default false COMMENT '是否可用，表示角色当前是否激活',
  constraint pk_sys_roles primary key(id)
) charset=utf8 ENGINE=InnoDB;
create unique index idx_sys_roles_role on sys_roles(role);

create table sys_permissions (
  id bigint auto_increment,
  permission varchar(100) COMMENT '权限标识符',
  description varchar(100) COMMENT '描述',
  available bool default false COMMENT '是否可用，表示权限当前是否激活',
  constraint pk_sys_permissions primary key(id)
) charset=utf8 ENGINE=InnoDB;
create unique index idx_sys_permissions_permission on sys_permissions(permission);

create table sys_users_roles (
  user_id bigint,
  role_id bigint,
  constraint pk_sys_users_roles primary key(user_id, role_id)
) charset=utf8 ENGINE=InnoDB;

create table sys_roles_permissions (
  role_id bigint,
  permission_id bigint,
  constraint pk_sys_roles_permissions primary key(role_id, permission_id)
) charset=utf8 ENGINE=InnoDB;