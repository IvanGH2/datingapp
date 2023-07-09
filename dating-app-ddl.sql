CREATE TABLE public.ngd_user_profile_body_type
(
    body_type_id integer NOT NULL,
    body_type_desc character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT profile_bodytype_pkey PRIMARY KEY (body_type_id),
    CONSTRAINT ngd_user_profile_body_type_body_type_desc_key UNIQUE (body_type_desc)
)
CREATE TABLE public.ngd_user_profile_edu_level
(
    edu_level_id integer NOT NULL,
    edu_level_desc character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT profile_edulevel_pkey PRIMARY KEY (edu_level_id),
    CONSTRAINT ngd_user_profile_edu_level_edu_level_desc_key UNIQUE (edu_level_desc)
)
CREATE TABLE public.ngd_user_profile_rel
(
    rel_status_id integer NOT NULL,
    rel_status_desc character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT profile_rel_pkey PRIMARY KEY (rel_status_id),
    CONSTRAINT ngd_user_profile_rel_rel_status_desc_key UNIQUE (rel_status_desc)
)
CREATE TABLE public.ngd_role
(
    id integer NOT NULL DEFAULT nextval('ngd_role_id_seq'::regclass),
    role_name character varying(16) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id)
)
CREATE TABLE public.ngd_user_profile_personality_type
(
    personality_id integer NOT NULL,
    personality_desc character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT profile_perosonality_pkey PRIMARY KEY (personality_id),
    CONSTRAINT ngd_user_profile_personality_type_personality_desc_key UNIQUE (personality_desc)
)
CREATE TABLE public.ngd_user
(
    id integer NOT NULL DEFAULT nextval('ngd_user_id_seq'::regclass),
    username character varying(16) COLLATE pg_catalog."default" NOT NULL,
    psw character varying(64) COLLATE pg_catalog."default" NOT NULL,
    email character varying(128) COLLATE pg_catalog."default" NOT NULL,
    user_role integer NOT NULL,
    active boolean NOT NULL,
    age integer NOT NULL,
    gender character(1) COLLATE pg_catalog."default" NOT NULL,
    target_gender character(1) COLLATE pg_catalog."default" NOT NULL,
    date_created timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_changed timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity timestamp without time zone,
    ip character varying(16) COLLATE pg_catalog."default",
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT ngd_user_email_key UNIQUE (email),
    CONSTRAINT ngd_user_username_key UNIQUE (username),
    CONSTRAINT userrole_fkey FOREIGN KEY (user_role)
        REFERENCES public.ngd_role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE public.ngd_blocked_users
(
    user_id integer NOT NULL,
    blockage_reason character varying(128) COLLATE pg_catalog."default" NOT NULL,
    date_created timestamp with time zone NOT NULL,
    CONSTRAINT ngd_blocked_users_pkey PRIMARY KEY (user_id),
    CONSTRAINT blocked_user_fk FOREIGN KEY (user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
CREATE TABLE public.ngd_user_activation_link
(
    id integer NOT NULL DEFAULT nextval('ngd_user_activation_link_id_seq'::regclass),
    userid integer NOT NULL,
    activation_link character varying(64) COLLATE pg_catalog."default" NOT NULL,
    link_consumed boolean NOT NULL DEFAULT false,
    time_created timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    time_expires timestamp with time zone NOT NULL,
    CONSTRAINT ngd_user_activation_link_pkey PRIMARY KEY (id),
    CONSTRAINT user_id_fkey FOREIGN KEY (userid)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE public.ngd_user_profile
(
    user_id integer NOT NULL,
    rel_status integer NOT NULL,
    edu_level integer,
    body_type integer,
    country character varying(128) COLLATE pg_catalog."default" NOT NULL,
    city character varying(128) COLLATE pg_catalog."default" NOT NULL,
    profession character varying(128) COLLATE pg_catalog."default",
    profile_visible_to integer NOT NULL,
    personality character varying(128) COLLATE pg_catalog."default",
    photos_available boolean NOT NULL,
    hobbies character varying(256) COLLATE pg_catalog."default",
    current_status character varying(128) COLLATE pg_catalog."default",
    user_message character varying(512) COLLATE pg_catalog."default",
    date_created timestamp with time zone NOT NULL,
    date_changed timestamp with time zone NOT NULL,
    emp_status integer,
    CONSTRAINT profile_pkey PRIMARY KEY (user_id),
    CONSTRAINT userid_fkey FOREIGN KEY (user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE public.ngd_user_profile_photos
(
    id integer NOT NULL DEFAULT nextval('ngd_user_profile_photos_id_seq'::regclass),
    user_id integer NOT NULL,
    link_to_photo character varying(512) COLLATE pg_catalog."default" NOT NULL,
    date_created timestamp with time zone NOT NULL,
    date_changed timestamp with time zone NOT NULL,
    CONSTRAINT user_profile_photos_pkey PRIMARY KEY (id),
    CONSTRAINT userid_fkey FOREIGN KEY (user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
CREATE TABLE public.ngd_user_profile_views
(
    id integer NOT NULL DEFAULT nextval('ngd_user_profile_views_id_seq'::regclass),
    src_user_id integer NOT NULL,
    dst_user_id integer NOT NULL,
    date_viewed timestamp with time zone,
    CONSTRAINT ngd_user_profile_views_pkey PRIMARY KEY (id),
    CONSTRAINT pv_dstuser_id_fkey FOREIGN KEY (dst_user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT pv_srcuser_id_fkey FOREIGN KEY (src_user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
CREATE TABLE public.ngd_user_target_profile
(
    user_id integer NOT NULL,
    age_from integer,
    age_to integer,
    rel_status character varying(256) COLLATE pg_catalog."default",
    rel_status_include boolean,
    countries character varying(256) COLLATE pg_catalog."default",
    country_include boolean,
    children_status character varying(256) COLLATE pg_catalog."default",
    children_status_include boolean,
    edu_level character varying(256) COLLATE pg_catalog."default",
    edu_level_include boolean,
    body_type character varying(256) COLLATE pg_catalog."default",
    body_type_include boolean,
    personality character varying(256) COLLATE pg_catalog."default",
    personality_include boolean,
    employment_status character varying(256) COLLATE pg_catalog."default",
    employment_status_include boolean,
    date_created timestamp with time zone NOT NULL,
    date_changed timestamp with time zone NOT NULL,
    gender character(1) COLLATE pg_catalog."default",
    CONSTRAINT target_profile_pkey PRIMARY KEY (user_id)
)

CREATE TABLE public.ngd_user_messages
(
    id integer NOT NULL DEFAULT nextval('ngd_user_messages_id_seq'::regclass),
    src_user_id integer NOT NULL,
    dst_user_id integer NOT NULL,
    msg_txt character varying(4096) COLLATE pg_catalog."default" NOT NULL,
    msg_flag boolean NOT NULL,
    msg_viewed boolean NOT NULL,
    date_created timestamp with time zone NOT NULL,
    date_changed timestamp with time zone NOT NULL,
    reserved character varying(128) COLLATE pg_catalog."default",
    CONSTRAINT user_msg_pkey PRIMARY KEY (id),
    CONSTRAINT dtsuserid_fkey FOREIGN KEY (dst_user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT srcuserid_fkey FOREIGN KEY (src_user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
CREATE TABLE public.ngd_user_msg_last
(
    msg_id integer NOT NULL,
    src_user_id integer NOT NULL,
    dst_user_id integer NOT NULL,
    CONSTRAINT ngd_user_msg_last_pkey PRIMARY KEY (src_user_id, dst_user_id),
    CONSTRAINT ngd_user_msg_last_msg_id_key UNIQUE (msg_id),
    CONSTRAINT dest_user_id FOREIGN KEY (dst_user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT source_user_id FOREIGN KEY (src_user_id)
        REFERENCES public.ngd_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT usermsgid FOREIGN KEY (msg_id)
        REFERENCES public.ngd_user_messages (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

create index idx_user_userid on ngd_user (
	id desc
)

create index idx_user_username on ngd_user (
	username desc
)

create index idx_user_email on ngd_user (
	email desc
)

create index idx_userprofile_userid on ngd_user_profile (
	user_id desc
)

create index idx_targetprofile_userid on ngd_user_target_profile (
	user_id desc
)

create index idx_usermessage_id on ngd_user_messages (
	id desc
)

create index idx_usermessage_srcuserid on ngd_user_messages (
	src_user_id desc
)

create index idx_usermessage_dstuserid on ngd_user_messages (
	dst_user_id desc
)

create index idx_activation_id on ngd_user_activation_link (
	id desc
)

create index idx_activation_userid on ngd_user_activation_link (
	userid desc
)

create index idx_activation_actlink on ngd_user_activation_link (
	activation_link desc
)

create index idx_photos_userid on ngd_user_profile_photos (
	user_id desc
)

create index idx_photos_id on ngd_user_profile_photos (
	id desc
)

create index idx_photos_photolink on ngd_user_profile_photos (
	photo_link desc
)

create index idx_blockeduser_userid on ngd_blocked_users (
	user_id desc
)

create index idx_profileviews_srcuserid on ngd_user_profile_views (
	src_user_id desc
)

create index idx_profileviews_dstuserid on ngd_user_profile_views (
	dst_user_id desc
)

create index idx_msglast_dstuserid on ngd_user_msg_last (
	dst_user_id desc
)

create index idx_msglast_srcuserid on ngd_user_msg_last (
	src_user_id desc
)

create index idx_msglast_msgid on ngd_user_msg_last (
	msg_id desc
)

