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
WHERE p.status = 'K1';

ALTER TABLE logs ADD (user_id VARCHAR2(20) DEFAULT '' NOT NULL);

DROP TABLE commom_code;

-- 뒤에서부터 순서대로 밀기
UPDATE BEEZ.COMMON_CODE SET COM_VALUE = 'B6' WHERE GROUP_VALUE = '0B' AND COM_VALUE = 'B5';
UPDATE BEEZ.COMMON_CODE SET COM_VALUE = 'B5' WHERE GROUP_VALUE = '0B' AND COM_VALUE = 'B4';
UPDATE BEEZ.COMMON_CODE SET COM_VALUE = 'B4' WHERE GROUP_VALUE = '0B' AND COM_VALUE = 'B3';
UPDATE BEEZ.COMMON_CODE SET COM_VALUE = 'B3' WHERE GROUP_VALUE = '0B' AND COM_VALUE = 'B2';
UPDATE BEEZ.COMMON_CODE SET COM_VALUE = 'B2' WHERE GROUP_VALUE = '0B' AND COM_VALUE = 'B1';

-- B1 버전 신규 추가
INSERT INTO BEEZ.COMMON_CODE (COM_VALUE, GROUP_VALUE, COM_NAME, IS_USE)
VALUES ('B1', '0B', '버전', '1');

ALTER TABLE version ADD (is_delete VARCHAR2(3) DEFAULT 'F0' NOT NULL);
COMMENT ON COLUMN version.is_delete IS '삭제여부';
COMMIT;

SELECT identifier,
       title,
       description,
       is_public,
       parent_id,
       start_date,
       end_date,
       default_version_id
FROM project
WHERE id = 'PROJ2603007';
    

CREATE OR REPLACE FUNCTION alter_common_code
    (p_com_value common_code.com_value%TYPE)
    RETURN VARCHAR2
IS
    v_com_name VARCHAR2(30);
BEGIN
    SELECT com_name
    INTO v_com_name
    FROM common_code
    WHERE com_value = p_com_value;
RETURN v_com_name;
END;
/

SELECT alter_common_code('D0') FROM DUAL;