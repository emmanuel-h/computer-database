drop schema if exists `computer-database-test`;
  create schema if not exists `computer-database-test`;
  use `computer-database-test`;

  drop table if exists computer;
  drop table if exists company;
  drop table if exists user_role;
  drop table if exists role;
  drop table if exists user;
  
  create table role (
   role                       varchar(255) NOT NULL,
   constraint pk_role primary key (role))
  ;
  
  create table user (
   username                   varchar(255) NOT NULL,
   password                   varchar(255) NOT NULL,
   enabled                    bit(1) NOT NULL,
   constraint pk_user primary key (username))
  ;
  
  CREATE TABLE user_role (
   User_username              varchar(255) NOT NULL,
   roles_role                 varchar(255) NOT NULL,
   constraint fk_role_user foreign key(User_username) references user(username),
   constraint fk_user_role foreign key(roles_role) references role(role))
  ;

  create table company (
    id                        bigint NOT NULL auto_increment,
    name                      varchar(255),
    number_of_computers       int NOT NULL,
    image_url                 varchar(255) NOT NULL,
    constraint pk_company primary key (id))
  ;

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
