INSERT INTO anniv_quiz(quiz_code, title, pass_min_correct, is_active, created_at)
VALUES ('ANNIV25QZ-0001', '2025周年资格问卷', 2, 1, NOW());

-- 题目
INSERT INTO anniv_quiz_question(quiz_id, idx_no, content)
SELECT id, 1, '公司周年日是哪一天？'
FROM anniv_quiz
WHERE quiz_code = 'ANNIV25QZ-0001';
INSERT INTO anniv_quiz_question(quiz_id, idx_no, content)
SELECT id, 2, '证书编号的组别前缀是哪个？'
FROM anniv_quiz
WHERE quiz_code = 'ANNIV25QZ-0001';

-- 选项（示例单选）
-- Q1 选项
INSERT INTO anniv_quiz_option(question_id, idx_no, content, is_correct)
SELECT q1.id, 1, '2025-09-06', 1
FROM anniv_quiz_question q1
         JOIN anniv_quiz q ON q1.quiz_id = q.id AND q.quiz_code = 'ANNIV25QZ-0001' AND q1.idx_no = 1;
INSERT INTO anniv_quiz_option(question_id, idx_no, content, is_correct)
SELECT q1.id, 2, '2025-09-01', 0
FROM anniv_quiz_question q1
         JOIN anniv_quiz q ON q1.quiz_id = q.id AND q.quiz_code = 'ANNIV25QZ-0001' AND q1.idx_no = 1;

-- Q2 选项
INSERT INTO anniv_quiz_option(question_id, idx_no, content, is_correct)
SELECT q2.id, 1, 'SCSxx', 1
FROM anniv_quiz_question q2
         JOIN anniv_quiz q ON q2.quiz_id = q.id AND q.quiz_code = 'ANNIV25QZ-0001' AND q2.idx_no = 2;
INSERT INTO anniv_quiz_option(question_id, idx_no, content, is_correct)
SELECT q2.id, 2, 'ABCxx', 0
FROM anniv_quiz_question q2
         JOIN anniv_quiz q ON q2.quiz_id = q.id AND q.quiz_code = 'ANNIV25QZ-0001' AND q2.idx_no = 2;
