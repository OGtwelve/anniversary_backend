-- 问卷主表
CREATE TABLE IF NOT EXISTS anniv_quiz
(
    id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quiz_code        VARCHAR(32)  NOT NULL UNIQUE,    -- 例如 ANNIV25QZ-0001
    title            VARCHAR(128) NOT NULL,
    pass_min_correct INT          NOT NULL DEFAULT 1, -- 通过所需最少答对题数（或可换成分数）
    is_active        TINYINT(1)   NOT NULL DEFAULT 0,
    created_at       DATETIME     NULL,
    updated_at       DATETIME     NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 题目
CREATE TABLE IF NOT EXISTS anniv_quiz_question
(
    id      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT       NOT NULL,
    idx_no  INT          NOT NULL, -- 展示顺序
    content VARCHAR(255) NOT NULL,
    CONSTRAINT fk_qzq_quiz FOREIGN KEY (quiz_id) REFERENCES anniv_quiz (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 选项（包含正确答案标记）
CREATE TABLE IF NOT EXISTS anniv_quiz_option
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT       NOT NULL,
    idx_no      INT          NOT NULL,
    content     VARCHAR(255) NOT NULL,
    is_correct  TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT fk_qzo_question FOREIGN KEY (question_id) REFERENCES anniv_quiz_question (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
