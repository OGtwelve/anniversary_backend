-- 确保问卷存在（若不存在则插入一条）
INSERT INTO anniv_quiz (quiz_code, title, pass_min_correct, is_active, created_at)
SELECT 'ANNIV25QZ-0001', '2025周年资格问卷', 2, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM anniv_quiz WHERE quiz_code='ANNIV25QZ-0001');

-- 统一设置为激活 & 通过需答对2题
UPDATE anniv_quiz
SET pass_min_correct = 2,
    is_active        = 1
WHERE quiz_code = 'ANNIV25QZ-0001';

-- 删除该问卷下旧的选项
DELETE o
FROM anniv_quiz_option o
         JOIN anniv_quiz_question q ON o.question_id = q.id
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code = 'ANNIV25QZ-0001';

-- 删除该问卷下旧的题目
DELETE q
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code = 'ANNIV25QZ-0001';

-- 题目1：实验室成立日是哪一天？
INSERT INTO anniv_quiz_question (quiz_id, idx_no, content)
SELECT id, 1, '实验室成立日是哪一天？'
FROM anniv_quiz
WHERE quiz_code = 'ANNIV25QZ-0001';

-- 题目2：三体计算星座首发总共有多少颗星？
INSERT INTO anniv_quiz_question (quiz_id, idx_no, content)
SELECT id, 2, '三体计算星座首发总共有多少颗星？'
FROM anniv_quiz
WHERE quiz_code = 'ANNIV25QZ-0001';

-- Q1 选项（正确：2017.9.6）
INSERT INTO anniv_quiz_option (question_id, idx_no, content, is_correct)
SELECT q.id, 1, '2017.9.6', 1
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code='ANNIV25QZ-0001' AND q.idx_no=1;

INSERT INTO anniv_quiz_option (question_id, idx_no, content, is_correct)
SELECT q.id, 2, '2017.9.5', 0
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code='ANNIV25QZ-0001' AND q.idx_no=1;

INSERT INTO anniv_quiz_option (question_id, idx_no, content, is_correct)
SELECT q.id, 3, '2017.10.1', 0
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code='ANNIV25QZ-0001' AND q.idx_no=1;

-- Q2 选项（正确：12颗）
INSERT INTO anniv_quiz_option (question_id, idx_no, content, is_correct)
SELECT q.id, 1, '12颗', 1
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code='ANNIV25QZ-0001' AND q.idx_no=2;

INSERT INTO anniv_quiz_option (question_id, idx_no, content, is_correct)
SELECT q.id, 2, '9颗', 0
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code='ANNIV25QZ-0001' AND q.idx_no=2;

INSERT INTO anniv_quiz_option (question_id, idx_no, content, is_correct)
SELECT q.id, 3, '10颗', 0
FROM anniv_quiz_question q
         JOIN anniv_quiz z ON q.quiz_id = z.id
WHERE z.quiz_code='ANNIV25QZ-0001' AND q.idx_no=2;
