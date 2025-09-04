-- 表结构（如果你使用的是 JPA 自动建表可省略）
CREATE TABLE IF NOT EXISTS anniv_gate_config
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    gate_code  VARCHAR(64) NOT NULL,
    is_active  TINYINT(1)  NOT NULL DEFAULT 1,
    open_at    DATETIME    NOT NULL,
    close_at   DATETIME    NULL,
    remark     VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_gate_code (gate_code),
    KEY idx_active_openat (is_active, open_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 初始化一条开启记录（示例时间自行改）
INSERT INTO anniv_gate_config(gate_code, is_active, open_at, close_at, remark, created_at)
VALUES ('ANNIV25_GATE', 1, '2025-09-05 09:00:00', NULL, '证书H5开启时间', NOW())
ON DUPLICATE KEY UPDATE open_at=VALUES(open_at),
                        is_active=VALUES(is_active),
                        remark=VALUES(remark);
