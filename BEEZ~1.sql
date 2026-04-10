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
        AND rm.is_delete = 'F0'
        AND rm.role_id NOT IN ('ROLE0001', 'ROLE0002')
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
        AND rm.is_delete = 'F0'
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
    AND rm.is_delete = 'F0'
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
        
        -- 해당 멤버의 role_mapping도 소프트 딜리트
        UPDATE role_mapping
        SET is_delete = 'F1'
        WHERE member_id = p_project_member_id;
    ELSE
        -- 그룹 소프트 딜리트
        UPDATE project_member
        SET is_delete = 'F1'
        WHERE id = p_project_member_id;
        
        -- 해당 그룹의 role_mapping도 소프트 딜리트
        UPDATE role_mapping
        SET is_delete = 'F1'
        WHERE member_id = p_project_member_id;
        
        -- 1. 개별 역할 있는 멤버의 상속 역할만 소프트 딜리트
        UPDATE role_mapping
        SET is_delete = 'F1'
        WHERE member_id IN (
            SELECT id FROM project_member
            WHERE group_id = v_group_id
            AND project_id = v_project_id
            AND is_delete = 'F0'
        )
        AND is_inherited = 'P1';
        
        -- 2. 개별 역할 있는 멤버 -> 개별 사용자로 전환
        UPDATE project_member
        SET group_id = NULL
        WHERE group_id = v_group_id
        AND project_id = v_project_id
        AND id IN (
            SELECT member_id FROM role_mapping
            WHERE is_inherited = 'P0'
            AND is_delete = 'F0'
        );
        
        -- 3. 나머지 그룹 멤버 role_mapping 소프트 딜리트 (개별 역할도 포함)
        UPDATE role_mapping
        SET is_delete = 'F1'
        WHERE member_id IN (
            SELECT id FROM project_member
            WHERE group_id = v_group_id
            AND project_id = v_project_id
            AND is_delete = 'F0'
        );
        
        -- 4. 나머지 그룹 멤버 소프트 딜리트
        UPDATE project_member
        SET is_delete = 'F1'
        WHERE group_id = v_group_id
        AND project_id = v_project_id
        AND is_delete = 'F0';
 
    END IF;
    
    COMMIT;
END;
/

ALTER TABLE role_mapping ADD (is_delete VARCHAR2(3) DEFAULT 'F0' NOT NULL);
COMMENT ON COLUMN role_mapping.is_delete IS '삭제여부';

COMMIT;

SELECT id, name
FROM roles
WHERE id >= 'ROLE0004';

SELECT id, name 
FROM users u
WHERE status = 'H1'
AND role = 'ROLE0003'
AND NOT EXISTS (SELECT 1 FROM project_member pm
                WHERE pm.user_id = u.id
                AND pm.project_id = 'PROJ2603007');

-- 프로젝트 구성원 역할 수정 프로시저
CREATE OR REPLACE PROCEDURE update_project_member_role (
    p_member_id IN VARCHAR2,
    p_role_ids  IN T_VARCHAR_LIST  -- 최종 선택된 역할 배열
)
AS
BEGIN
    -- 1. 기존 직접 부여 역할 전부 삭제 처리
    UPDATE role_mapping
    SET is_delete = 'F1'
    WHERE member_id = p_member_id
    AND is_inherited = 'P0';
    
    -- 2. 선택된 역할들 MERGE INTO
    FOR i IN 1 .. p_role_ids.COUNT LOOP
        MERGE INTO role_mapping rm
        USING DUAL
        ON (rm.member_id = p_member_id 
            AND rm.role_id = p_role_ids(i) 
            AND rm.is_inherited = 'P0')
        WHEN MATCHED THEN
            UPDATE SET rm.is_delete ='F0'
        WHEN NOT MATCHED THEN
            INSERT (member_id, role_id, is_inherited, is_delete)
            VALUES (p_member_id,p_role_ids(i), 'P0', 'F0');
    END LOOP;
END;
/

-- 구성원 추가 프로시저
CREATE OR REPLACE PROCEDURE add_project_member_role (
    p_project_id IN VARCHAR2,
    p_user_ids IN T_VARCHAR_LIST,
    p_group_ids IN T_VARCHAR_LIST,
    p_role_ids IN T_VARCHAR_LIST
)
AS
    v_member_id project_member.id%TYPE;
BEGIN
    -- [1] 역할(Role) 필수 체크: NULL이거나 비어있으면 에러
    IF p_role_ids IS NULL OR p_role_ids.COUNT = 0 THEN
        raise_application_error(-20001, '최소 하나 이상의 역할을 선택해야 합니다.');
    END IF;

    -- [2] 대상(사용자/그룹) 필수 체크: 둘 다 비어있으면 에러
    IF (p_user_ids IS NULL OR p_user_ids.COUNT = 0) AND 
       (p_group_ids IS NULL OR p_group_ids.COUNT = 0) THEN
        raise_application_error(-20002, '등록할 사용자 또는 그룹을 선택해야 합니다.');
    END IF;

    --  [3] 개별 사용자 추가
    FOR i IN 1 .. p_user_ids.COUNT LOOP
        v_member_id := generate_pk_auto('project_member');
        INSERT INTO project_member (id, project_id, user_id, group_id)
        VALUES(v_member_id, p_project_id, p_user_ids(i), NULL);
        
        FOR j IN 1 .. p_role_ids.COUNT LOOP
            INSERT INTO role_mapping (member_id, role_id, is_inherited)
            VALUES (v_member_id, p_role_ids(j), 'P0');
        END LOOP;
    END LOOP;
    
    -- [4] 그룹추가
    FOR i IN 1 .. p_group_ids.COUNT LOOP
        v_member_id := generate_pk_auto('project_member');
        INSERT INTO project_member (id, project_id, user_id, group_id)
        VALUES (v_member_id, p_project_id, NULL, p_group_ids(i));
        
        FOR j IN 1 .. p_role_ids.COUNT LOOP
            INSERT INTO role_mapping (member_id, role_id, is_inherited)
            VALUES (v_member_id, p_role_ids(j), 'P0');
        END LOOP;
        
        -- 그룹 소속 사원 루프
        FOR k IN (SELECT user_id FROM group_member WHERE group_id = p_group_ids(i)) LOOP
            v_member_id := generate_pk_auto('project_member');
            INSERT INTO project_member (id, project_id, user_id, group_id)
            VALUES (v_member_id, p_project_id, k.user_id, p_group_ids(i));
            
            FOR j IN 1 .. p_role_ids.COUNT LOOP
                INSERT INTO role_mapping (member_id, role_id, is_inherited)
                VALUES (v_member_id, p_role_ids(j), 'P1');
            END LOOP;
        END LOOP;
    END LOOP;
    
END;
/

COMMIT;

SELECT * FROM USER_OBJECTS 
 WHERE OBJECT_TYPE = 'PROCEDURE' 
   AND OBJECT_NAME = 'ADD_PROJECT_MEMBER_ROLE';

BEGIN
    add_project_member_role(
        p_project_id => 'PROJ2512002',
        p_user_ids   => T_VARCHAR_LIST('20260022', '20261111'),
        p_group_ids  => T_VARCHAR_LIST('GR0001'),
        p_role_ids   => T_VARCHAR_LIST('ROLE0004', 'ROLE0005')
    );
    COMMIT;
END;
/

-- 프로젝트 생성 프로시저
CREATE OR REPLACE PROCEDURE proc_create_project (
    p_identifier         IN VARCHAR2,
    p_title              IN VARCHAR2,
    p_description        IN VARCHAR2,
    p_is_public          IN VARCHAR2,
    p_parent_id          IN VARCHAR2,
    p_start_date         IN DATE,
    p_end_date           IN DATE,
    p_user_id            IN VARCHAR2,
    p_project_id         OUT VARCHAR2
) AS
    v_member_id          project_member.id%TYPE;
    v_count              NUMBER;
BEGIN
    -- 1. 식별자 중복 체크
    SELECT COUNT(*) INTO v_count FROM project 
    WHERE identifier = p_identifier AND status = 'K1';
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, '이미 사용중인 식별자입니다.');
    END IF;
    
    -- 2. 타이틀 중복 체크
    SELECT COUNT(*) INTO v_count FROM project 
    WHERE title = p_title AND status = 'K1';
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, '이미 사용중인 프로젝트명입니다.');
    END IF;
    
    -- 3. project PK 생성
    SELECT BEEZ.generate_pk_auto('project') 
    INTO p_project_id 
    FROM dual;
    
    -- 4. project 삽입
    INSERT INTO project (
                         id, 
                         identifier, 
                         title, 
                         description, 
                         is_public,
                         parent_id, 
                         created_on, 
                         start_date, 
                         end_date,
                         status, 
                         is_lock, 
                         user_id
    ) VALUES (
                p_project_id, 
                p_identifier, 
                p_title, 
                p_description, 
                p_is_public,
                p_parent_id, 
                SYSDATE, 
                p_start_date, 
                p_end_date,
                'K1', 
                'L0',
                p_user_id
    );
    
    -- 5. project_member 삽입 (생성자 자동 등록)
    SELECT BEEZ.generate_pk_auto('project_member') 
    INTO v_member_id 
    FROM dual;
    
    INSERT INTO project_member (
                                id,
                                project_id,
                                user_id,
                                group_id
                                )
    VALUES ( 
            v_member_id,
            p_project_id,
            p_user_id,
            NULL
            );
            
    -- 6. role_mapping 삽입 (생성자 = 관리자 역할)
    INSERT INTO role_mapping (
                              member_id,
                              role_id,
                              is_inherited
                              )
    VALUES (
            v_member_id,
            'ROLE0002',
            'P0'
            );
END proc_create_project;
/

ALTER TABLE project ADD CONSTRAINT  uq_project_identifier UNIQUE (identifier);
ALTER TABLE project ADD CONSTRAINT uq_project_title UNIQUE (title);

COMMIT;

SELECT SYSDATE, CURRENT_DATE, SESSIONTIMEZONE FROM DUAL;

SELECT 
    p.id,
    p.title,
    p.parent_id,
    LEVEL - 1 AS "lv",
    LPAD(' ', (LEVEL - 1) * 4) || p.title AS "hierarchy_title"
FROM project p
WHERE p.status = 'K1'
START WITH p.parent_id IS NULL
CONNECT BY PRIOR p.id = p.parent_id
ORDER SIBLINGS BY p.id DESC;