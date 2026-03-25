DROP TABLE file_detail;

CREATE TABLE file_detail (
	id	VARCHAR2(20)		NOT NULL,
	file_id	VARCHAR2(20)		NOT NULL,
	original_name	VARCHAR2(2000)		NOT NULL,
	stored_name	VARCHAR2(2000)		NOT NULL,
	extension	VARCHAR2(10)		NOT NULL,
	file_size	NUMBER		NOT NULL
);

COMMENT ON COLUMN file_detail.id IS '파일상세번호';

COMMENT ON COLUMN file_detail.file_id IS '파일묶음번호';

COMMENT ON COLUMN file_detail.original_name IS '업로드명';

COMMENT ON COLUMN file_detail.stored_name IS '저장명';

COMMENT ON COLUMN file_detail.extension IS '확장자';

COMMENT ON COLUMN file_detail.file_size IS '파일크기';

DROP TABLE board_type;

CREATE TABLE board_type (
	id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(100)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN board_type.id IS '게시글분류번호';

COMMENT ON COLUMN board_type.name IS '분류명';

COMMENT ON COLUMN board_type.project_id IS '프로젝트번호';

DROP TABLE chat;

CREATE TABLE chat (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	content	VARCHAR2(2000)		NOT NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	parent_id	VARCHAR2(20)		NULL
);

COMMENT ON COLUMN chat.id IS '채팅번호';

COMMENT ON COLUMN chat.project_id IS '프로젝트번호';

COMMENT ON COLUMN chat.user_id IS '작성자';

COMMENT ON COLUMN chat.content IS '내용';

COMMENT ON COLUMN chat.created_on IS '작성일시';

COMMENT ON COLUMN chat.parent_id IS '상위채팅번호';

DROP TABLE reply;

CREATE TABLE reply (
	id	VARCHAR2(20)		NOT NULL,
	board_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NULL,
	content	VARCHAR2(2000)		NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	edited_on	TIMESTAMP		NULL,
	parent_id	VARCHAR2(20)		NULL
);

COMMENT ON COLUMN reply.id IS '댓글번호';

COMMENT ON COLUMN reply.board_id IS '게시글번호';

COMMENT ON COLUMN reply.user_id IS '작성자';

COMMENT ON COLUMN reply.content IS '내용';

COMMENT ON COLUMN reply.created_on IS '작성일시';

COMMENT ON COLUMN reply.edited_on IS '수정일시';

COMMENT ON COLUMN reply.parent_id IS '상위댓글번호';

DROP TABLE notice;

CREATE TABLE notice (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	title	VARCHAR2(2000)		NOT NULL,
	content	CLOB		NOT NULL,
	user_id	VARCHAR2(20)		NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	edited_on	TIMESTAMP		NULL,
	is_important	VARCHAR2(3)		NULL,
	file_id	VARCHAR2(100)		NULL
);

COMMENT ON COLUMN notice.id IS '공지사항번호';

COMMENT ON COLUMN notice.project_id IS '프로젝트번호';

COMMENT ON COLUMN notice.title IS '제목';

COMMENT ON COLUMN notice.content IS '내용';

COMMENT ON COLUMN notice.user_id IS '작성자';

COMMENT ON COLUMN notice.created_on IS '작성일';

COMMENT ON COLUMN notice.edited_on IS '수정일';

COMMENT ON COLUMN notice.is_important IS '중요사항코드';

COMMENT ON COLUMN notice.file_id IS '파일';

DROP TABLE task_relations;

CREATE TABLE task_relations (
	id	VARCHAR2(20)		NOT NULL,
	task_id	VARCHAR2(20)		NOT NULL,
	related_task_id	VARCHAR2(20)		NOT NULL,
	relation_type	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN task_relations.id IS '일감연결번호';

COMMENT ON COLUMN task_relations.task_id IS '기준일감번호';

COMMENT ON COLUMN task_relations.related_task_id IS '연결된일감번호';

COMMENT ON COLUMN task_relations.relation_type IS '관계유형코드';

DROP TABLE time;

CREATE TABLE time (
	id	VARCHAR2(20)		NOT NULL,
	task_id	VARCHAR2(20)		NOT NULL,
	task_start	TIMESTAMP		NOT NULL,
	spent	NUMBER	DEFAULT 0	NOT NULL,
	description	VARCHAR2(1000)		NULL,
	progress	NUMBER	DEFAULT 0	NULL,
	activity_type	VARCHAR2(3)		NULL
);

COMMENT ON COLUMN time.id IS '작업번호';

COMMENT ON COLUMN time.task_id IS '일감번호';

COMMENT ON COLUMN time.task_start IS '작업일시';

COMMENT ON COLUMN time.spent IS '소요시간';

COMMENT ON COLUMN time.description IS '설명';

COMMENT ON COLUMN time.progress IS '진척도';

COMMENT ON COLUMN time.activity_type IS '작업종류코드';

DROP TABLE commom_code;

CREATE TABLE commom_code (
	com_value	VARCHAR2(3)		NOT NULL,
	group_value	VARCHAR2(3)		NOT NULL,
	com_name	VARCHAR2(30)		NOT NULL,
	is_use	NUMBER		NOT NULL
);

COMMENT ON COLUMN commom_code.com_value IS '공통코드값';

COMMENT ON COLUMN commom_code.group_value IS '그룹코드값';

COMMENT ON COLUMN commom_code.com_name IS '코드명';

COMMENT ON COLUMN commom_code.is_use IS '0:미사용/1:사용';

DROP TABLE docs_mark;

CREATE TABLE docs_mark (
	mark_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	document_id	VARCHAR2(20)		NOT NULL,
	created_on	DATE		NOT NULL
);

COMMENT ON COLUMN docs_mark.mark_id IS '문서 즐겨찾기 번호';

COMMENT ON COLUMN docs_mark.user_id IS '사원번호';

COMMENT ON COLUMN docs_mark.document_id IS '문서번호';

COMMENT ON COLUMN docs_mark.created_on IS '등록일';

DROP TABLE task_category;

CREATE TABLE task_category (
	id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(255)		NOT NULL
);

COMMENT ON COLUMN task_category.id IS '일감범주번호';

COMMENT ON COLUMN task_category.name IS '범주명';

DROP TABLE wiki;

CREATE TABLE wiki (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	version_id	VARCHAR2(20)		NULL
);

COMMENT ON COLUMN wiki.id IS '위키번호';

COMMENT ON COLUMN wiki.project_id IS '프로젝트번호';

COMMENT ON COLUMN wiki.version_id IS '위키버전번호';

DROP TABLE boards;

CREATE TABLE boards (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	title	VARCHAR2(500)		NOT NULL,
	content	CLOB		NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	edited_on	TIMESTAMP		NULL,
	file_id	VARCHAR2(100)		NULL,
	type_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN boards.id IS '게시글번호';

COMMENT ON COLUMN boards.project_id IS '프로젝트번호';

COMMENT ON COLUMN boards.user_id IS '작성자';

COMMENT ON COLUMN boards.title IS '제목';

COMMENT ON COLUMN boards.content IS '내용';

COMMENT ON COLUMN boards.created_on IS '작성일';

COMMENT ON COLUMN boards.edited_on IS '수정일';

COMMENT ON COLUMN boards.file_id IS '파일';

COMMENT ON COLUMN boards.type_id IS '게시글분류번호';

DROP TABLE groups;

CREATE TABLE groups (
	id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(100)		NOT NULL,
	description	VARCHAR2(1000)		NULL
);

COMMENT ON COLUMN groups.id IS '그룹번호';

COMMENT ON COLUMN groups.name IS '그룹명';

COMMENT ON COLUMN groups.description IS '그룹설명';

DROP TABLE journal_details;

CREATE TABLE journal_details (
	id	VARCHAR2(20)		NOT NULL,
	journal_id	VARCHAR2(20)		NOT NULL,
	field_name	VARCHAR2(100)		NOT NULL,
	old_value	VARCHAR2(100)		NOT NULL,
	new_value	VARCHAR2(100)		NOT NULL
);

COMMENT ON COLUMN journal_details.id IS '변경상세번호';

COMMENT ON COLUMN journal_details.journal_id IS '변경번호';

COMMENT ON COLUMN journal_details.field_name IS '변경컬럼명';

COMMENT ON COLUMN journal_details.old_value IS '이전값';

COMMENT ON COLUMN journal_details.new_value IS '변경값';

DROP TABLE calendar;

CREATE TABLE calendar (
	id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	type	VARCHAR2(3)		NOT NULL,
	title	VARCHAR2(255)		NOT NULL,
	content	VARCHAR2(2000)		NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	start_date	DATE		NOT NULL,
	end_date	DATE		NOT NULL
);

COMMENT ON COLUMN calendar.id IS '일정번호';

COMMENT ON COLUMN calendar.user_id IS '작성자';

COMMENT ON COLUMN calendar.type IS '일정유형코드';

COMMENT ON COLUMN calendar.title IS '제목';

COMMENT ON COLUMN calendar.content IS '내용';

COMMENT ON COLUMN calendar.project_id IS '프로젝트번호';

COMMENT ON COLUMN calendar.start_date IS '시작일';

COMMENT ON COLUMN calendar.end_date IS '종료일';

DROP TABLE docs;

CREATE TABLE docs (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	title	VARCHAR2(500)		NOT NULL,
	content	CLOB		NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	edited_on	TIMESTAMP		NULL,
	file_id	VARCHAR2(20)		NULL,
	doc_type	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN docs.id IS '문서번호';

COMMENT ON COLUMN docs.project_id IS '프로젝트번호';

COMMENT ON COLUMN docs.user_id IS '작성자';

COMMENT ON COLUMN docs.title IS '제목';

COMMENT ON COLUMN docs.content IS '내용';

COMMENT ON COLUMN docs.created_on IS '작성일';

COMMENT ON COLUMN docs.edited_on IS '수정일';

COMMENT ON COLUMN docs.file_id IS '파일번호';

COMMENT ON COLUMN docs.doc_type IS '문서유형코드';

DROP TABLE wiki_version;

CREATE TABLE wiki_version (
	version_id	VARCHAR2(20)		NOT NULL,
	content	CLOB		NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	wiki_id	VARCHAR2(20)		NOT NULL,
	description	VARCHAR2(1000)		NOT NULL,
	version_name	VARCHAR2(100)		NOT NULL,
	links	VARCHAR2(2000)		NULL
);

COMMENT ON COLUMN wiki_version.version_id IS '위키버전번호';

COMMENT ON COLUMN wiki_version.content IS '위키내용';

COMMENT ON COLUMN wiki_version.user_id IS '작성자';

COMMENT ON COLUMN wiki_version.created_on IS '작성일시';

COMMENT ON COLUMN wiki_version.wiki_id IS '위키번호';

COMMENT ON COLUMN wiki_version.description IS '버전설명';

COMMENT ON COLUMN wiki_version.version_name IS '버전이름';

COMMENT ON COLUMN wiki_version.links IS '관련링크';

DROP TABLE permission;

CREATE TABLE permission (
	id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(100)		NOT NULL,
	method	VARCHAR2(10)		NOT NULL,
	URL	VARCHAR2(255)		NOT NULL
);

COMMENT ON COLUMN permission.id IS '권한번호';

COMMENT ON COLUMN permission.name IS '권한명';

COMMENT ON COLUMN permission.method IS '메소드';

COMMENT ON COLUMN permission.URL IS 'URL';

DROP TABLE group_member;

CREATE TABLE group_member (
	user_id	VARCHAR2(20)		NOT NULL,
	group_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN group_member.user_id IS '사원번호';

COMMENT ON COLUMN group_member.group_id IS '그룹번호';

DROP TABLE workflow;

CREATE TABLE workflow (
	id	VARCHAR2(20)		NOT NULL,
	role_id	VARCHAR2(20)		NOT NULL,
	type_id	VARCHAR2(20)		NOT NULL,
	before	VARCHAR2(3)		NOT NULL,
	after	VARCHAR2(3)		NOT NULL,
	is_allow	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN workflow.before IS '변경전상태코드';

COMMENT ON COLUMN workflow.after IS '변경후상태코드';

COMMENT ON COLUMN workflow.is_allow IS '변경허용여부코드';

DROP TABLE journals;

CREATE TABLE journals (
	id	VARCHAR2(20)		NOT NULL,
	task_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	created_on  DATE         DEFAULT SYSDATE NOT NULL
);

COMMENT ON COLUMN journals.id IS '변경번호';

COMMENT ON COLUMN journals.task_id IS '일감번호';

COMMENT ON COLUMN journals.user_id IS '수정자';

COMMENT ON COLUMN journals.created_on IS '수정시간';

DROP TABLE project_member;

CREATE TABLE project_member (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NULL,
	group_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN project_member.id IS '참여자번호';

COMMENT ON COLUMN project_member.project_id IS '프로젝트번호';

COMMENT ON COLUMN project_member.user_id IS '사원번호';

COMMENT ON COLUMN project_member.group_id IS '그룹번호';

DROP TABLE task_watcher;

CREATE TABLE task_watcher (
	task_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN task_watcher.task_id IS '일감번호';

COMMENT ON COLUMN task_watcher.user_id IS '사원번호';

DROP TABLE task_reply;

CREATE TABLE task_reply (
	id	VARCHAR2(20)		NOT NULL,
	task_id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	content	VARCHAR2(2000)		NOT NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL
);

COMMENT ON COLUMN task_reply.id IS '일감댓글번호';

COMMENT ON COLUMN task_reply.task_id IS '일감번호';

COMMENT ON COLUMN task_reply.user_id IS '작성자';

COMMENT ON COLUMN task_reply.content IS '내용';

COMMENT ON COLUMN task_reply.created_on IS '작성일';

DROP TABLE users;

CREATE TABLE users (
	id	VARCHAR2(20)		NOT NULL,
	email	VARCHAR2(100)		NOT NULL,
	password	VARCHAR2(100)		NOT NULL,
	name	VARCHAR2(50)		NOT NULL,
	status	VARCHAR2(3)		NOT NULL,
	created_on	DATE	DEFAULT SYSDATE	NOT NULL,
	role	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN users.id IS '사원번호';

COMMENT ON COLUMN users.email IS '이메일';

COMMENT ON COLUMN users.password IS '비밀번호';

COMMENT ON COLUMN users.name IS '이름';

COMMENT ON COLUMN users.status IS '사용자상태값코드';

COMMENT ON COLUMN users.created_on IS '등록일';

COMMENT ON COLUMN users.role IS '역할유형코드';

DROP TABLE version;

CREATE TABLE version (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(100)		NOT NULL,
	description	VARCHAR2(1000)		NULL,
	created_on	DATE	DEFAULT SYSDATE	NOT NULL,
	start_date	DATE		NOT NULL,
	end_date	DATE		NOT NULL,
	status	VARCHAR2(3)		NOT NULL,
	is_share	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN version.id IS '버전번호';

COMMENT ON COLUMN version.project_id IS '프로젝트번호';

COMMENT ON COLUMN version.name IS '버전명';

COMMENT ON COLUMN version.description IS '버전설명';

COMMENT ON COLUMN version.created_on IS '생성일';

COMMENT ON COLUMN version.start_date IS '시작일';

COMMENT ON COLUMN version.end_date IS '마감일';

COMMENT ON COLUMN version.status IS '버전상태값코드';

COMMENT ON COLUMN version.is_share IS '프로젝트공유여부코드';

DROP TABLE roles;

CREATE TABLE roles (
	id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(100)		NOT NULL
);

COMMENT ON COLUMN roles.id IS '역할번호';

COMMENT ON COLUMN roles.name IS '역할명';

DROP TABLE project;

CREATE TABLE project (
	id	VARCHAR2(20)		NOT NULL,
	identifier	VARCHAR2(255)		NOT NULL,
	title	VARCHAR2(255)		NOT NULL,
	description	VARCHAR2(1000)		NULL,
	is_public	VARCHAR2(3)		NOT NULL,
	parent_id	VARCHAR2(20)		NULL,
	created_on	DATE	DEFAULT SYSDATE	NOT NULL,
	start_date	DATE		NOT NULL,
	end_date	DATE		NOT NULL,
	status	VARCHAR2(3)		NOT NULL,
	is_lock	VARCHAR2(3)		NOT NULL,
	default_version	VARCHAR2(3)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN project.id IS '프로젝트번호';

COMMENT ON COLUMN project.identifier IS '프로젝트 식별자';

COMMENT ON COLUMN project.title IS '프로젝트명';

COMMENT ON COLUMN project.description IS '프로젝트설명';

COMMENT ON COLUMN project.is_public IS '포로젝트공개여부코드';

COMMENT ON COLUMN project.parent_id IS '상위프로젝트';

COMMENT ON COLUMN project.created_on IS '생성일';

COMMENT ON COLUMN project.start_date IS '시작일';

COMMENT ON COLUMN project.end_date IS '마감일';

COMMENT ON COLUMN project.status IS '프로젝트상태값코드';

COMMENT ON COLUMN project.is_lock IS '프로젝트잠금여부';

COMMENT ON COLUMN project.default_version IS '기본 버전여부코드';

COMMENT ON COLUMN project.user_id IS '프로젝트생성자';

DROP TABLE files;

CREATE TABLE files (
	id	VARCHAR2(20)		NOT NULL,
	created_on	DATE	DEFAULT SYSTIMESTAMP	NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	is_delete	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN files.id IS '파일묶음번호';

COMMENT ON COLUMN files.created_on IS '등록일자';

COMMENT ON COLUMN files.user_id IS '등록자';

COMMENT ON COLUMN files.is_delete IS '삭제여부코드';

DROP TABLE task_type;

CREATE TABLE task_type (
	id	VARCHAR2(20)		NOT NULL,
	name	VARCHAR2(255)		NOT NULL,
	default_status	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN task_type.id IS '일감유형번호';

COMMENT ON COLUMN task_type.name IS '유형명';

COMMENT ON COLUMN task_type.default_status IS '초기상태코드';

DROP TABLE task;

CREATE TABLE task (
	id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	version_id	VARCHAR2(20)		NULL,
	title	VARCHAR2(255)		NOT NULL,
	description	VARCHAR2(2000)		NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	type	VARCHAR2(20)		NOT NULL,
	category	VARCHAR2(20)		NOT NULL,
	workflow	NUMBER(1)	DEFAULT 0	NOT NULL,
	reject	VARCHAR2(2000)		NULL,
	priority	VARCHAR2(3)		NOT NULL,
	planned_start	DATE		NOT NULL,
	planned_end	DATE		NOT NULL,
	actual_start	DATE		NULL,
	actual_end	DATE		NULL,
	estimated_time	NUMBER		NULL,
	progress	NUMBER	DEFAULT 0	NULL,
	status	VARCHAR2(3)		NOT NULL,
	parent_id	VARCHAR2(20)		NOT NULL,
	is_public	VARCHAR2(3)		NOT NULL,
	creator	VARCHAR2(20)		NOT NULL,
	file_id	VARCHAR2(20)		NULL
);

COMMENT ON COLUMN task.id IS '일감번호';

COMMENT ON COLUMN task.project_id IS '프로젝트번호';

COMMENT ON COLUMN task.version_id IS '목표버전';

COMMENT ON COLUMN task.title IS '일감명';

COMMENT ON COLUMN task.description IS '일감설명';

COMMENT ON COLUMN task.user_id IS '담당자';

COMMENT ON COLUMN task.type IS '일감유형';

COMMENT ON COLUMN task.category IS '일감범주';

COMMENT ON COLUMN task.workflow IS '일감상태코드';

COMMENT ON COLUMN task.reject IS '반려사유';

COMMENT ON COLUMN task.priority IS '우선순위코드';

COMMENT ON COLUMN task.planned_start IS '시작예정일';

COMMENT ON COLUMN task.planned_end IS '마감예정일';

COMMENT ON COLUMN task.actual_start IS '실제시작일';

COMMENT ON COLUMN task.actual_end IS '실제마감일';

COMMENT ON COLUMN task.estimated_time IS '추정시간';

COMMENT ON COLUMN task.progress IS '진척도';

COMMENT ON COLUMN task.status IS '0 : 비활성화 / 1 : 활성화';

COMMENT ON COLUMN task.parent_id IS '상위일감';

COMMENT ON COLUMN task.is_public IS '0 : 비공개 / 1 : 전체공개';

COMMENT ON COLUMN task.creator IS '생성자';

COMMENT ON COLUMN task.file_id IS '파일번호';

DROP TABLE permission_mapping;

CREATE TABLE permission_mapping (
	role_id	VARCHAR2(20)		NOT NULL,
	perm_id	VARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN permission_mapping.role_id IS '역할번호';

COMMENT ON COLUMN permission_mapping.perm_id IS '권한번호';

DROP TABLE role_mapping;

CREATE TABLE role_mapping (
	member_id	VARCHAR2(20)		NOT NULL,
	role_id	VARCHAR2(20)		NOT NULL,
	is_inherited	VARCHAR2(3)		NOT NULL
);

COMMENT ON COLUMN role_mapping.member_id IS '참여자번호';

COMMENT ON COLUMN role_mapping.role_id IS '역할번호';

COMMENT ON COLUMN role_mapping.is_inherited IS '상속여부코드';

DROP TABLE notifications;

CREATE TABLE notifications (
	id	VARCHAR2(20)		NOT NULL,
	user_id	VARCHAR2(20)		NOT NULL,
	content	VARCHAR2(2000)		NOT NULL,
	time	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	status	VARCHAR2(3)		NOT NULL,
	link	VARCHAR2(1000)		NOT NULL
);

COMMENT ON COLUMN notifications.id IS '알림번호';

COMMENT ON COLUMN notifications.user_id IS '알림대상';

COMMENT ON COLUMN notifications.content IS '알림내용';

COMMENT ON COLUMN notifications.time IS '발생일시';

COMMENT ON COLUMN notifications.status IS '확인여부코드';

COMMENT ON COLUMN notifications.link IS '알림 발생지 링크';

DROP TABLE logs;

CREATE TABLE logs (
	log_id	VARCHAR2(20)		NOT NULL,
	project_id	VARCHAR2(20)		NOT NULL,
	type	VARCHAR2(3)		NOT NULL,
	category	VARCHAR2(3)		NULL,
	content	VARCHAR2(2000)		NOT NULL,
	created_on	TIMESTAMP	DEFAULT SYSTIMESTAMP	NOT NULL,
	link	VARCHAR2(1000)		NULL
);

COMMENT ON COLUMN logs.log_id IS '로그번호';

COMMENT ON COLUMN logs.project_id IS '프로젝트번호';

COMMENT ON COLUMN logs.type IS '로그유형코드';

COMMENT ON COLUMN logs.category IS '로그구분코드';

COMMENT ON COLUMN logs.content IS '로그내용';

COMMENT ON COLUMN logs.created_on IS '발생일시';

COMMENT ON COLUMN logs.link IS '로그 발생지 링크';

ALTER TABLE file_detail ADD CONSTRAINT PK_FILE_DETAIL PRIMARY KEY (
	id
);

ALTER TABLE board_type ADD CONSTRAINT PK_BOARD_TYPE PRIMARY KEY (
	id
);

ALTER TABLE chat ADD CONSTRAINT PK_CHAT PRIMARY KEY (
	id
);

ALTER TABLE reply ADD CONSTRAINT PK_REPLY PRIMARY KEY (
	id
);

ALTER TABLE notice ADD CONSTRAINT PK_NOTICE PRIMARY KEY (
	id
);

ALTER TABLE task_relations ADD CONSTRAINT PK_TASK_RELATIONS PRIMARY KEY (
	id
);

ALTER TABLE time ADD CONSTRAINT PK_TIME PRIMARY KEY (
	id
);

ALTER TABLE commom_code ADD CONSTRAINT PK_COMMOM_CODE PRIMARY KEY (
	com_value
);

ALTER TABLE docs_mark ADD CONSTRAINT PK_DOCS_MARK PRIMARY KEY (
	mark_id
);

ALTER TABLE task_category ADD CONSTRAINT PK_TASK_CATEGORY PRIMARY KEY (
	id
);

ALTER TABLE wiki ADD CONSTRAINT PK_WIKI PRIMARY KEY (
	id
);

ALTER TABLE boards ADD CONSTRAINT PK_BOARDS PRIMARY KEY (
	id
);

ALTER TABLE groups ADD CONSTRAINT PK_GROUPS PRIMARY KEY (
	id
);

ALTER TABLE journal_details ADD CONSTRAINT PK_JOURNAL_DETAILS PRIMARY KEY (
	id
);

ALTER TABLE calendar ADD CONSTRAINT PK_CALENDAR PRIMARY KEY (
	id
);

ALTER TABLE docs ADD CONSTRAINT PK_DOCS PRIMARY KEY (
	id
);

ALTER TABLE wiki_version ADD CONSTRAINT PK_WIKI_VERSION PRIMARY KEY (
	version_id
);

ALTER TABLE permission ADD CONSTRAINT PK_PERMISSION PRIMARY KEY (
	id
);

ALTER TABLE group_member ADD CONSTRAINT PK_GROUP_MEMBER PRIMARY KEY (
	user_id,
	group_id
);

ALTER TABLE workflow ADD CONSTRAINT PK_WORKFLOW PRIMARY KEY (
	id
);

ALTER TABLE journals ADD CONSTRAINT PK_JOURNALS PRIMARY KEY (
	id
);

ALTER TABLE project_member ADD CONSTRAINT PK_PROJECT_MEMBER PRIMARY KEY (
	id
);

ALTER TABLE task_watcher ADD CONSTRAINT PK_TASK_WATCHER PRIMARY KEY (
	task_id,
	user_id
);

ALTER TABLE task_reply ADD CONSTRAINT PK_TASK_REPLY PRIMARY KEY (
	id
);

ALTER TABLE users ADD CONSTRAINT PK_USERS PRIMARY KEY (
	id
);

ALTER TABLE version ADD CONSTRAINT PK_VERSION PRIMARY KEY (
	id
);

ALTER TABLE roles ADD CONSTRAINT PK_ROLES PRIMARY KEY (
	id
);

ALTER TABLE project ADD CONSTRAINT PK_PROJECT PRIMARY KEY (
	id
);

ALTER TABLE files ADD CONSTRAINT PK_FILES PRIMARY KEY (
	id
);

ALTER TABLE task_type ADD CONSTRAINT PK_TASK_TYPE PRIMARY KEY (
	id
);

ALTER TABLE task ADD CONSTRAINT PK_TASK PRIMARY KEY (
	id
);

ALTER TABLE permission_mapping ADD CONSTRAINT PK_PERMISSION_MAPPING PRIMARY KEY (
	role_id,
	perm_id
);

ALTER TABLE role_mapping ADD CONSTRAINT PK_ROLE_MAPPING PRIMARY KEY (
	member_id,
	role_id
);

ALTER TABLE notifications ADD CONSTRAINT PK_NOTIFICATIONS PRIMARY KEY (
	id
);

ALTER TABLE logs ADD CONSTRAINT PK_LOGS PRIMARY KEY (
	log_id
);

ALTER TABLE group_member ADD CONSTRAINT FK_users_TO_group_member_1 FOREIGN KEY (
	user_id
)
REFERENCES users (
	id
);

ALTER TABLE group_member ADD CONSTRAINT FKgroupsgroupmember FOREIGN KEY (
	group_id
)
REFERENCES groups (
	id
);

ALTER TABLE task_watcher ADD CONSTRAINT FKtasktaskwatcher FOREIGN KEY (
	task_id
)
REFERENCES task (
	id
);

ALTER TABLE task_watcher ADD CONSTRAINT FKuserstaskwatcher FOREIGN KEY (
	user_id
)
REFERENCES users (
	id
);

ALTER TABLE permission_mapping ADD CONSTRAINT FKrolespermmapping FOREIGN KEY (
	role_id
)
REFERENCES roles (
	id
);

ALTER TABLE permission_mapping ADD CONSTRAINT FKpermpermmapping FOREIGN KEY (
	perm_id
)
REFERENCES permission (
	id
);

ALTER TABLE role_mapping ADD CONSTRAINT FKprojectmemberrolemapping FOREIGN KEY (
	member_id
)
REFERENCES project_member (
	id
);

ALTER TABLE role_mapping ADD CONSTRAINT FKrolesrolemapping FOREIGN KEY (
	role_id
)
REFERENCES roles (
	id
);

