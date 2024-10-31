# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists bill;

-- 切换库
use bill;
-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
    ) comment '用户' collate = utf8mb4_unicode_ci;


-- 账单本表
create table if not exists bill
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment '账单本名称',
    description     varchar(256)                       not null comment '账单本描述',
    userId          bigint                             not null comment '创建用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_billNameIdx (name)
    ) comment '账单本' collate = utf8mb4_unicode_ci;

-- 账单本-用户关联表
create table if not exists bill_user
(
    id              bigint auto_increment comment 'id' primary key,
    billId					bigint                             not null comment '关联账单本 id',
    userId          bigint                             not null comment '关联用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_billUserIdx (userId)
    ) comment '账单本用户关联' collate = utf8mb4_unicode_ci;

-- 账单明细表
create table if not exists bill_detail
(
    id              bigint auto_increment comment 'id' primary key,
    recordDate			date not null comment '账单日期',
    amount					decimal(10, 2) not null comment '金额',
    description     varchar(256) comment '描述',
    billId  				bigint not null comment '关联的账单本id',
    userId					bigint not null comment '记录的用户id',
    categoryId			bigint not null comment '账单明细类别id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_billDetailRecordDateIdx (recordDate),
    index idx_billIdIdx (billId),
    index idx_categoryIdIdx (categoryId)
    ) comment '账单明细' collate = utf8mb4_unicode_ci;

-- 账单明细类别表
create table if not exists bill_category
(
    id              bigint auto_increment comment 'id' primary key,
    billId					bigint                             not null comment '关联账单本 id',
    name          	varchar(128)                       not null comment '名称',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_billUserIdx (billId)
    ) comment '账单明细类别' collate = utf8mb4_unicode_ci;