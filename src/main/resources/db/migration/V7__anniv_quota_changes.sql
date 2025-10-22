CREATE TABLE IF NOT EXISTS anniv_certificate_open_day
(
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_no        VARCHAR(32)  NOT NULL,
    scs_code       VARCHAR(6)   NOT NULL,
    seq            INT          NOT NULL,
    name           LONGTEXT  NOT NULL,
    date_of_birth  DATE         NOT NULL,
    wishes         LONGTEXT NULL,
    ip             LONGTEXT  NULL,
    ua             LONGTEXT NULL,
    created_at     DATETIME     NULL,
    updated_at     DATETIME     NULL,
    UNIQUE KEY uk_full_no (full_no),
    KEY idx_scs_seq (scs_code, seq),
    KEY idx_created (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 1) 加列并把老数据标记为版本 1
ALTER TABLE anniv_quota
    ADD COLUMN version_code VARCHAR(16) NULL AFTER scs_code;

UPDATE anniv_quota
SET version_code = '1'
WHERE version_code IS NULL;

-- 2) 把 (version_code, scs_code) 设为新的主键
--    先确保 scs_code 非空
ALTER TABLE anniv_quota
    MODIFY scs_code VARCHAR(6) NOT NULL;

-- 2.1 删除旧主键（老主键大概率是 scs_code）
ALTER TABLE anniv_quota DROP PRIMARY KEY;

-- 2.2 建新的复合主键
ALTER TABLE anniv_quota
    ADD PRIMARY KEY (version_code, scs_code);
