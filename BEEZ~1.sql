CREATE OR REPLACE PROCEDURE generate_project_id(
    p_id OUT VARCHAR2
) AS
    v_prefix VARCHAR2(6);
    v_seq    NUMBER;
BEGIN
    -- 접두사 + 연월
    v_prefix := 'PROJ' || TO_CHAR(SYSDATE, 'YYMM');

    -- 이번 달 max 시퀀스 조회 후 증가
    SELECT NVL(MAX(TO_NUMBER(SUBSTR(id, 7, 3))), 0) + 1
      INTO v_seq
      FROM project
     WHERE TO_CHAR(created_on, 'YYMM') = TO_CHAR(SYSDATE, 'YYMM');

    -- 최종 ID 생성
    p_id := v_prefix || LPAD(v_seq, 3, '0');
END;
/

DECLARE
    v_id VARCHAR2(20);
BEGIN
    generate_project_id(v_id);
    DBMS_OUTPUT.PUT_LINE(v_id);
END;
/

CREATE OR REPLACE FUNCTION BEEZ.generate_pk_auto(
  p_tbl_name VARCHAR2)
RETURN VARCHAR2
IS
  PRAGMA AUTONOMOUS_TRANSACTION; -- 핵심: 메인 트랜잭션과 분리
  v_prefix VARCHAR2(10);
  v_seq_time VARCHAR2(20);
  v_seq NUMBER;
  v_format VARCHAR2(20);
  v_seq_size NUMBER;
  v_now VARCHAR2(20);
  v_result VARCHAR2(20);
BEGIN
  SELECT prefix, seq_time, seq, format, seq_size
  INTO v_prefix, v_seq_time, v_seq, v_format, v_seq_size
  FROM pk_manage
  WHERE tbl_name = p_tbl_name
  FOR UPDATE;

  v_result := v_prefix;

  IF v_format IS NOT NULL THEN
    v_now := TO_CHAR(SYSTIMESTAMP, v_format);
    IF v_seq_time != v_now THEN
      v_seq := 1;
    END IF;

    UPDATE pk_manage SET seq_time = v_now WHERE tbl_name = p_tbl_name;
    v_result := v_result || v_now;
  END IF;
  
  v_result := v_result || LPAD(v_seq, v_seq_size, '0');
  v_seq := v_seq + 1;
  UPDATE pk_manage SET seq = v_seq WHERE tbl_name = p_tbl_name;

  COMMIT; -- 자율 트랜잭션은 종료 전 반드시 COMMIT 또는 ROLLBACK이 필요합니다.

  RETURN v_result;
END;
/

SELECT id, title, is_lock, status FROM project;

SELECT p.id, p.title, p.user_id, u.name, p.end_date, p.is_lock, p.status
FROM project p
LEFT JOIN users u ON u.id = p.user_id
WHERE p.status = 'K1'