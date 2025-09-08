-- =====================================================================
-- V6: Admin 用户与角色的基础表结构（MySQL 8+）
-- - 表：admin_user / admin_role / admin_user_role
-- - 初始化角色：ADMIN / USER
-- - 初始化用户：admin / 密码：zjlab@dxjh@superadmin  （BCrypt 已加密）
--   👉 上线后请尽快修改密码！
-- =====================================================================

-- 1) 用户表
CREATE TABLE IF NOT EXISTS admin_user
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    display_name  VARCHAR(64)  NULL,
    email         VARCHAR(128) NULL,
    phone         VARCHAR(32)  NULL,
    password_hash VARCHAR(100) NOT NULL, -- 存 BCrypt
    is_enabled    TINYINT(1)   NOT NULL DEFAULT 1,
    is_locked     TINYINT(1)   NOT NULL DEFAULT 0,
    last_login_at DATETIME     NULL,
    remark        VARCHAR(255) NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admin_user_email (email),
    UNIQUE KEY uk_admin_user_phone (phone),
    KEY idx_admin_user_enabled (is_enabled),
    KEY idx_admin_user_locked (is_locked)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 2) 角色表
CREATE TABLE IF NOT EXISTS admin_role
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code       VARCHAR(64)  NOT NULL UNIQUE, -- 机器可读：ADMIN / USER
    name       VARCHAR(64)  NOT NULL,        -- 中文名：管理员 / 普通用户
    remark     VARCHAR(255) NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 3) 用户-角色关联表（多对多）
CREATE TABLE IF NOT EXISTS admin_user_role
(
    user_id    BIGINT   NOT NULL,
    role_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_aur_user FOREIGN KEY (user_id) REFERENCES admin_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_aur_role FOREIGN KEY (role_id) REFERENCES admin_role (id) ON DELETE CASCADE,
    KEY idx_aur_role (role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 4) 初始化角色（若不存在则插入）
INSERT INTO admin_role (code, name, remark)
SELECT 'ADMIN', '管理员', '系统超管'
WHERE NOT EXISTS (SELECT 1 FROM admin_role WHERE code = 'ADMIN');

INSERT INTO admin_role (code, name, remark)
SELECT 'USER', '普通用户', '默认角色'
WHERE NOT EXISTS (SELECT 1 FROM admin_role WHERE code = 'USER');

-- 5) 初始化管理员账号（若不存在则插入）
-- 下面这个 BCrypt 哈希对应明文：zjlab@dxjh@superadmin
-- $2a$10$fV1Ng3bcL7e1ssNJk7Q0juxSBThr0Rex9NGs94PhuHeaudkpYhl52
INSERT INTO admin_user (username, display_name, email, password_hash, is_enabled, is_locked, remark)
SELECT 'admin',
       '系统管理员',
       'admin@example.com',
       '$2a$10$fV1Ng3bcL7e1ssNJk7Q0juxSBThr0Rex9NGs94PhuHeaudkpYhl52',
       1,
       0,
       '初始超管账号'
WHERE NOT EXISTS (SELECT 1 FROM admin_user WHERE username = 'admin');

-- 6) 赋予 admin -> ADMIN 角色
INSERT INTO admin_user_role (user_id, role_id)
SELECT u.id, r.id
FROM admin_user u
         JOIN admin_role r ON r.code = 'ADMIN'
         LEFT JOIN admin_user_role ur ON ur.user_id = u.id AND ur.role_id = r.id
WHERE u.username = 'admin'
  AND ur.user_id IS NULL;
