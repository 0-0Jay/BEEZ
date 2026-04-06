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

-- 프로젝트 구성원 조회 프로시저
CREATE OR REPLACE PROCEDURE get_project_member (
    p_project_id IN VARCHAR2,
    p_users OUT SYS_REFCURSOR,
    p_groups OUT SYS_REFCURSOR,
    p_group_members OUT SYS_REFCURSOR
)
AS 
BEGIN
    OPEN p_users FOR
        SELECT pm.id AS project_member_id,
               pm.user_id,
               u.name AS user_name,
               rm.role_id,
               r.name AS role_name
        FROM project_member pm
        LEFT JOIN users u ON u.id = pm.user_id
        LEFT JOIN role_mapping rm ON rm.member_id = pm.id
        LEFT JOIN roles r ON r.id = rm.role_id
        WHERE pm.project_id = p_project_id
        AND pm.group_id IS NULL
        AND pm.is_delete = 'F0'
        ORDER BY pm.id;
        
    OPEN p_groups FOR
        SELECT pm.id AS project_member_id,
               pm.group_id,
               g.name AS group_name,
               rm.role_id,
               r.name AS role_name
        FROM project_member pm
        LEFT JOIN groups g ON g.id = pm.group_id
        LEFT JOIN role_mapping rm ON rm.member_id = pm.id
        LEFT JOIN roles r ON r.id = rm.role_id
        WHERE pm.project_id = p_project_id
        AND pm.user_id IS NULL
        AND pm.is_delete = 'F0'
        ORDER BY pm.id;
    
    OPEN p_group_members FOR
    SELECT pm.id AS project_member_id,
           pm.user_id,
           pm.group_id,
           u.name AS user_name,
           rm.role_id,
           r.name AS role_name,
           rm.is_inherited
    FROM project_member pm 
    LEFT JOIN users u ON u.id = pm.user_id
    LEFT JOIN role_mapping rm ON rm.member_id = pm.id
    LEFT JOIN roles r ON r.id = rm.role_id
    WHERE pm.project_id = p_project_id
    AND pm.user_id IS NOT NULL
    AND pm.group_id IS NOT NULL
    AND pm.is_delete = 'F0'
    ORDER BY pm.id;
END;
/

SET SERVEROUTPUT ON;

DECLARE
    v_users         SYS_REFCURSOR;
    v_groups        SYS_REFCURSOR;
    v_group_members SYS_REFCURSOR;
    
    v_id        VARCHAR2(100);
    v_group_id  VARCHAR2(100);
    v_user_id   VARCHAR2(100);
    v_name      VARCHAR2(100);
    v_role_id   VARCHAR2(100);
    v_role_name VARCHAR2(100);
    v_inherited VARCHAR2(10);
BEGIN
    get_project_member('PROJ2603007', v_users, v_groups, v_group_members);
    
    DBMS_OUTPUT.PUT_LINE('=== 개별 사용자 ===');
    LOOP
        FETCH v_users INTO v_id, v_user_id, v_name, v_role_id, v_role_name;
        EXIT WHEN v_users%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_id || ' | ' || v_name || ' | ' || v_role_name);
    END LOOP;
    CLOSE v_users;
    
    DBMS_OUTPUT.PUT_LINE('=== 그룹 ===');
    LOOP
        FETCH v_groups INTO v_id, v_group_id, v_name, v_role_id, v_role_name;
        EXIT WHEN v_groups%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_id || ' | ' || v_name || ' | ' || v_role_name);
    END LOOP;
    CLOSE v_groups;
    
    DBMS_OUTPUT.PUT_LINE('=== 그룹 멤버 ===');
    LOOP
        FETCH v_group_members INTO v_id, v_group_id, v_user_id, v_name, v_role_id, v_role_name, v_inherited;
        EXIT WHEN v_group_members%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_id || ' | ' || v_name || ' | ' || v_role_name || ' | 상속: ' || v_inherited);
    END LOOP;
    CLOSE v_group_members;
END;
/

ALTER TABLE project_member ADD (is_delete VARCHAR2(3) DEFAULT 'F0' NOT NULL);
COMMENT ON COLUMN project_member.is_delete IS '삭제여부';
COMMIT;

CREATE OR REPLACE PROCEDURE delete_project_member (
    p_project_member_id IN VARCHAR2
)
AS 
    v_group_id project_member.group_id%TYPE;
    v_project_id project_member.project_id%TYPE;
BEGIN
    -- group_id, project_id 조회
    SELECT group_id, project_id
    INTO v_group_id, v_project_id
    FROM project_member
    WHERE id = p_project_member_id;
    
    IF v_group_id IS NULL THEN
        -- 개별 사용자 소프트 딜리트
        UPDATE project_member
        SET is_delete = 'F1'
        WHERE id = p_project_member_id;
    ELSE
        -- 그룹 소프트 딜리트
        UPDATE project_member
        SET is_delete = 'F1'
        WHERE id = p_project_member_id;
        
        -- 개별 역할 있는 멤버 -> 개별 사용자로 전환
        UPDATE project_member
        SET group_id = NULL
        WHERE group_id = v_group_id
        AND project_id = v_project_id
        AND id IN (
            SELECT member_id FROM role_mapping
            WHERE is_inherited = 'P0'
        );
        
        -- 나머지 그룹 멤버 소프트 딜리트
        UPDATE project_member
        SET is_delete = 'F1'
        WHERE group_id = v_group_id
        AND project_id = v_project_id
        AND is_delete = 'F0';
    END IF;
    
    COMMIT;
END;
/

COMMIT;