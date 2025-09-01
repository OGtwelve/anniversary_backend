CREATE TABLE IF NOT EXISTS anniv_quiz_pass_ticket
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quiz_id       BIGINT       NOT NULL,
    token         VARCHAR(64)  NOT NULL UNIQUE, -- 一次性通行令牌（UUID）
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at    DATETIME     NOT NULL,
    ip            VARCHAR(45)  NULL,
    ua            VARCHAR(255) NULL,
    used_for_cert TINYINT(1)   NOT NULL DEFAULT 0,
    -- 统计信息（便于审计）
    answer_json   JSON         NULL,            -- 本次答案（questionId->optionId）
    correct_count INT          NOT NULL DEFAULT 0,
    all_correct   TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT fk_pass_quiz FOREIGN KEY (quiz_id) REFERENCES anniv_quiz (id),
    KEY idx_expire (expires_at),
    KEY idx_used (used_for_cert)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
