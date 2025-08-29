-- 12 组，每组 125
INSERT INTO anniv_quota (scs_code, issued, limit_cnt) VALUES
                                                          ('SCS01',0,125),('SCS02',0,125),('SCS03',0,125),('SCS04',0,125),
                                                          ('SCS05',0,125),('SCS06',0,125),('SCS07',0,125),('SCS08',0,125),
                                                          ('SCS09',0,125),('SCS10',0,125),('SCS11',0,125),('SCS12',0,125)
ON DUPLICATE KEY UPDATE issued=VALUES(issued), limit_cnt=VALUES(limit_cnt);

-- 全局序号计数器（如需预留 0001，把 last_seq 改为 1，并预插那条证书）
INSERT INTO anniv_seq_counter (name, last_seq) VALUES ('ANNIV_CERT', 0)
ON DUPLICATE KEY UPDATE last_seq=last_seq;
