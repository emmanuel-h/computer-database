drop schema if exists `computer-database-db`;
  create schema if not exists `computer-database-db`;
  use `computer-database-db`;

  drop table if exists computer;
  drop table if exists company;
  drop table if exists user;
  drop table if exists role;

  create table company (
    id                        bigint not null auto_increment,
    name                      varchar(255),
    constraint pk_company primary key (id))
  ;
  
  create table user(
	  username  varchar(255) not null primary key,
	  password  varchar(255) not null,
	  enabled   boolean not null);
	  
  create table role (
	  username varchar(255) not null,
	  role varchar(255) not null,
	  constraint fk_role_user foreign key(username) references user(username)
  );

  create unique index ix_auth_username on role (username,role);


  create table computer (
    id                        bigint not null auto_increment,
    name                      varchar(255),
    introduced                timestamp NULL,
    discontinued              timestamp NULL,
    company_id                bigint default NULL,
    constraint pk_computer primary key (id))
  ;

  alter table computer add constraint fk_computer_company_1 foreign key (company_id) references company (id) on delete restrict on update restrict;
  create index ix_computer_company_1 on computer (company_id);
