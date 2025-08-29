-- 数据库建议 UTF8MB4、InnoDB
CREATE TABLE IF NOT EXISTS anniv_seq_counter (
                                                 name      VARCHAR(16)  NOT NULL PRIMARY KEY,
                                                 last_seq  INT          NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS anniv_quota (
                                           scs_code  VARCHAR(6)   NOT NULL PRIMARY KEY,
                                           issued    INT          NOT NULL,
                                           limit_cnt INT          NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS anniv_certificate (
                                                 id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                 full_no         VARCHAR(32)  NOT NULL,
                                                 scs_code        VARCHAR(6)   NOT NULL,
                                                 seq             INT          NOT NULL,
                                                 days_to_target  INT          NOT NULL,
                                                 name            VARCHAR(64)  NOT NULL,
                                                 start_date      DATE         NOT NULL,
                                                 work_no         VARCHAR(32)  NOT NULL,
                                                 ip              VARCHAR(45)  NULL,
                                                 ua              VARCHAR(255) NULL,
                                                 created_at      DATETIME     NULL,
                                                 updated_at      DATETIME     NULL,
                                                 UNIQUE KEY uk_full_no (full_no),
                                                 KEY idx_scs_seq (scs_code, seq),
                                                 KEY idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
