--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.6
-- Dumped by pg_dump version 9.6.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: category_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE category_type (
    id integer NOT NULL,
    name text
);


ALTER TABLE category_type OWNER TO postgres;

--
-- Name: category_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE category_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE category_type_id_seq OWNER TO postgres;

--
-- Name: category_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE category_type_id_seq OWNED BY category_type.id;


--
-- Name: category_value; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE category_value (
    id integer NOT NULL,
    categorytypeid integer,
    name text
);


ALTER TABLE category_value OWNER TO postgres;

--
-- Name: category_value_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE category_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE category_value_id_seq OWNER TO postgres;

--
-- Name: category_value_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE category_value_id_seq OWNED BY category_value.id;


--
-- Name: flag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE flag (
    userid integer NOT NULL,
    videoid integer NOT NULL,
    message text
);


ALTER TABLE flag OWNER TO postgres;

--
-- Name: mediabox; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE mediabox (
    id integer NOT NULL,
    ip inet,
    ping integer,
    online boolean,
    userid integer
);


ALTER TABLE mediabox OWNER TO postgres;

--
-- Name: mediabox_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mediabox_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE mediabox_id_seq OWNER TO postgres;

--
-- Name: mediabox_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mediabox_id_seq OWNED BY mediabox.id;


--
-- Name: nottv_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE nottv_user (
    id integer NOT NULL,
    username text,
    email text,
    password text,
    userprofileurl text,
    profilepictureurl text
);


ALTER TABLE nottv_user OWNER TO postgres;

--
-- Name: nottv_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE nottv_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE nottv_user_id_seq OWNER TO postgres;

--
-- Name: nottv_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE nottv_user_id_seq OWNED BY nottv_user.id;


--
-- Name: playlist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE playlist (
    id integer NOT NULL,
    owner integer,
    title text,
    thumbnailurl text,
    downloadurl text
);


ALTER TABLE playlist OWNER TO postgres;

--
-- Name: playlist_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE playlist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playlist_id_seq OWNER TO postgres;

--
-- Name: playlist_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE playlist_id_seq OWNED BY playlist.id;


--
-- Name: playlist_video_join; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE playlist_video_join (
    playlistid integer NOT NULL,
    videoid integer NOT NULL
);


ALTER TABLE playlist_video_join OWNER TO postgres;

--
-- Name: settings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE settings (
    id integer NOT NULL,
    datalimit integer,
    alwaysdownload boolean,
    downloadschedule json,
    userid integer
);


ALTER TABLE settings OWNER TO postgres;

--
-- Name: settings_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE settings_id_seq OWNER TO postgres;

--
-- Name: settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE settings_id_seq OWNED BY settings.id;


--
-- Name: subscribe; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE subscribe (
    authorid integer NOT NULL,
    subscriberid integer NOT NULL
);


ALTER TABLE subscribe OWNER TO postgres;

--
-- Name: user_playlist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_playlist (
    userid integer NOT NULL,
    playlistid integer NOT NULL
);


ALTER TABLE user_playlist OWNER TO postgres;

--
-- Name: video; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE video (
    id integer NOT NULL,
    title text,
    description text,
    version integer,
    filetype text,
    license text,
    downloadurl text,
    thumbnailurl text,
    userid integer
);


ALTER TABLE video OWNER TO postgres;

--
-- Name: video_category_value_join; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE video_category_value_join (
    categoryvalueid integer NOT NULL,
    videoid integer NOT NULL
);


ALTER TABLE video_category_value_join OWNER TO postgres;

--
-- Name: video_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE video_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE video_id_seq OWNER TO postgres;

--
-- Name: video_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE video_id_seq OWNED BY video.id;


--
-- Name: category_type id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY category_type ALTER COLUMN id SET DEFAULT nextval('category_type_id_seq'::regclass);


--
-- Name: category_value id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY category_value ALTER COLUMN id SET DEFAULT nextval('category_value_id_seq'::regclass);


--
-- Name: mediabox id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mediabox ALTER COLUMN id SET DEFAULT nextval('mediabox_id_seq'::regclass);


--
-- Name: nottv_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY nottv_user ALTER COLUMN id SET DEFAULT nextval('nottv_user_id_seq'::regclass);


--
-- Name: playlist id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY playlist ALTER COLUMN id SET DEFAULT nextval('playlist_id_seq'::regclass);


--
-- Name: settings id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY settings ALTER COLUMN id SET DEFAULT nextval('settings_id_seq'::regclass);


--
-- Name: video id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY video ALTER COLUMN id SET DEFAULT nextval('video_id_seq'::regclass);

-- Daniel's contraint changes --

ALTER TABLE video_category_value_join
DROP CONSTRAINT video_category_value_join_categoryvalueid_fkey;

ALTER TABLE video_category_value_join
ADD FOREIGN KEY (categoryValueId) REFERENCES category_value(id)
ON DELETE CASCADE;

--
-- Data for Name: category_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY category_type (id, name) FROM stdin;
2	Genre
3	Media Type
4	Language
1	Misc2
\.


--
-- Name: category_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('category_type_id_seq', 82, true);


--
-- Data for Name: category_value; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY category_value (id, categorytypeid, name) FROM stdin;
1	3	Music Video
2	3	Web Series
3	3	News
4	3	Short Film
5	2	Action
6	2	Sci Fi
7	2	Documentary
8	2	Sports
9	2	Romance
10	1	In Library
11	1	Random
12	1	Popular
13	1	Trending
14	3	Radio
15	4	English
17	4	Spanish
18	1	Awesome Videos
22	1	New Tag
23	2	Infomercial
16	4	Alien language
\.


--
-- Name: category_value_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('category_value_id_seq', 98, true);


--
-- Data for Name: flag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY flag (userid, videoid, message) FROM stdin;
1	7	khuih
1	81	cdscds
\.


--
-- Data for Name: mediabox; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY mediabox (id, ip, ping, online, userid) FROM stdin;
\.


--
-- Name: mediabox_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('mediabox_id_seq', 1, false);


--
-- Data for Name: nottv_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY nottv_user (id, username, email, password, userprofileurl, profilepictureurl) FROM stdin;
1	testUser	\N	$2a$10$hWp8bZ0XvXx52eEbkCaSz.KgNHG1lL1MOoAldJRi5owIAJVvS5mj2	/userProfile/testUser	/img/user.png
2	newUser	\N	$2a$10$hWp8bZ0XvXx52eEbkCaSz.KgNHG1lL1MOoAldJRi5owIAJVvS5mj2	/userProfile/newUser	/img/user.png
-1	default_user	hello@yahoo.com	$2a$10$euHZC3aYiCm.WmSJ8Kv1z.IXND8txsmcjLZWUo41OP7EeK9GXdQuy	/userProfile/default_user	/img/user.png
\.


--
-- Name: nottv_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('nottv_user_id_seq', 1, true);


--
-- Data for Name: playlist; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY playlist (id, owner, title, thumbnailurl, downloadurl) FROM stdin;
1	-1	Funniest Videos	/img/playlist.png	/process/download?playlist=1
2	-1	Norberts Playlist	/img/playlist.png	/process/download?playlist=2
3	-1	Levis Playlist	/img/playlist.png	/process/download?playlist=3
4	-1	Nicks Playlist	/img/playlist.png	/process/download?playlist=4
5	1	Daniels playlist	/img/playlist.png	/process/download?playlist=5
6	1	NOTTV playlist	/img/playlist.png	/process/download?playlist=6
7	1	Funny videos	/img/playlist.png	/process/download?playlist=7
\.


--
-- Name: playlist_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('playlist_id_seq', 1, false);


--
-- Data for Name: playlist_video_join; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY playlist_video_join (playlistid, videoid) FROM stdin;
2	3
2	4
2	6
3	7
\.


--
-- Data for Name: settings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY settings (id, datalimit, alwaysdownload, downloadschedule, userid) FROM stdin;
1	\N	f	\N	-1
\.


--
-- Name: settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('settings_id_seq', 1, true);


--
-- Data for Name: subscribe; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY subscribe (authorid, subscriberid) FROM stdin;
-1	-1
-1	1
\.


--
-- Data for Name: user_playlist; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY user_playlist (userid, playlistid) FROM stdin;
\.


--
-- Data for Name: video; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY video (id, title, description, version, filetype, license, downloadurl, thumbnailurl, userid) FROM stdin;
1	TestVideo	\N	\N	\N	\N	test.torrent	/img/index.png	1
3	TestVideo2	\N	\N	\N	\N	test.torrent	/img/index.png	1
7	Toaster	My toaster video	1	video/mp4	Y	toaster.torrent	/img/index.png	1
6	MyTestVideo		0	video/mp4		toaster.torrent	/img/index.png	-1
66	TestingUpload	This is just for testing the upload function.	0	\N		toaster.torrent	\N	-1
67	NewVideo	hello World	0	\N		toaster.torrent	\N	-1
68	NewVideo	hello World	0	\N		toaster.torrent	\N	-1
4	Example2	\N	\N	\N	\N	test.torrent	/img/index.png	1
35	Example3		0	video/mp4		toaster.torrent	/img/default-placeholder-300x300.png	-1
69	Unique	hello	0	\N		toaster.torrent	\N	-1
70	Test	Desc	0	\N		toaster.torrent	\N	-1
71	MyToasterVideo2	hvdc	0	\N		toaster.torrent	\N	-1
72	Testing uploads	A video that shows you how to upload a video on NOTTV	0	\N		toaster.torrent	\N	-1
73	Testing2	something	0	\N		toaster.torrent	\N	-1
74	video	hmm	0	\N		toaster.torrent	\N	-1
75	ToasterVat		0	\N		\N	\N	-1
76	hr;gh		0	\N		\N	\N	-1
77	hr;gh		0	\N		\N	\N	-1
78	vdfds		0	\N		\N	\N	-1
79	vsd		0	\N		\N	\N	-1
80	efd		0	\N		\N	\N	-1
81	dvweh	crfvd	0	\N		\N	\N	-1
82	cdsc	cds	0	\N		\N	\N	-1
83	csd	cds	0	\N		\N	\N	-1
65	helloooooo		1	video/mp4	N	toaster.torrent	/img/index.png	1
\.


--
-- Data for Name: video_category_value_join; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY video_category_value_join (categoryvalueid, videoid) FROM stdin;
1	1
2	1
3	1
4	3
4	4
10	4
10	3
11	3
11	7
14	66
4	66
6	66
7	66
10	67
3	67
4	67
10	68
3	68
4	68
12	69
11	69
4	69
3	69
10	70
11	70
13	70
6	70
8	70
1	70
2	70
17	70
6	71
7	71
16	71
7	72
7	73
6	74
7	74
8	74
15	74
11	74
22	74
3	74
7	75
8	75
2	75
3	75
10	75
7	76
8	76
3	76
2	76
7	77
8	77
3	77
2	77
7	78
8	78
23	79
8	79
7	79
7	80
8	80
23	80
8	81
6	81
7	81
6	82
8	82
9	82
5	83
7	83
9	83
3	83
2	83
\.


--
-- Name: video_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('video_id_seq', 83, true);


--
-- Name: category_type category_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY category_type
    ADD CONSTRAINT category_type_pkey PRIMARY KEY (id);


--
-- Name: category_value category_value_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY category_value
    ADD CONSTRAINT category_value_pkey PRIMARY KEY (id);


--
-- Name: flag flag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY flag
    ADD CONSTRAINT flag_pkey PRIMARY KEY (userid, videoid);


--
-- Name: mediabox mediabox_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mediabox
    ADD CONSTRAINT mediabox_pkey PRIMARY KEY (id);


--
-- Name: nottv_user nottv_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY nottv_user
    ADD CONSTRAINT nottv_user_pkey PRIMARY KEY (id);


--
-- Name: playlist playlist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY playlist
    ADD CONSTRAINT playlist_pkey PRIMARY KEY (id);


--
-- Name: playlist_video_join playlist_video_join_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY playlist_video_join
    ADD CONSTRAINT playlist_video_join_pkey PRIMARY KEY (playlistid, videoid);


--
-- Name: settings settings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


--
-- Name: subscribe subscribe_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subscribe
    ADD CONSTRAINT subscribe_pkey PRIMARY KEY (authorid, subscriberid);


--
-- Name: user_playlist user_playlist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_playlist
    ADD CONSTRAINT user_playlist_pkey PRIMARY KEY (userid, playlistid);


--
-- Name: video_category_value_join video_category_value_join_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY video_category_value_join
    ADD CONSTRAINT video_category_value_join_pkey PRIMARY KEY (categoryvalueid, videoid);


--
-- Name: video video_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY video
    ADD CONSTRAINT video_pkey PRIMARY KEY (id);


--
-- Name: category_value category_value_categorytypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY category_value
    ADD CONSTRAINT category_value_categorytypeid_fkey FOREIGN KEY (categorytypeid) REFERENCES category_type(id);


--
-- Name: flag flag_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY flag
    ADD CONSTRAINT flag_userid_fkey FOREIGN KEY (userid) REFERENCES nottv_user(id);


--
-- Name: flag flag_videoid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY flag
    ADD CONSTRAINT flag_videoid_fkey FOREIGN KEY (videoid) REFERENCES video(id);


--
-- Name: mediabox mediabox_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mediabox
    ADD CONSTRAINT mediabox_userid_fkey FOREIGN KEY (userid) REFERENCES nottv_user(id);


--
-- Name: playlist playlist_owner_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY playlist
    ADD CONSTRAINT playlist_owner_fkey FOREIGN KEY (owner) REFERENCES nottv_user(id);


--
-- Name: playlist_video_join playlist_video_join_playlistid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY playlist_video_join
    ADD CONSTRAINT playlist_video_join_playlistid_fkey FOREIGN KEY (playlistid) REFERENCES playlist(id);


--
-- Name: playlist_video_join playlist_video_join_videoid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY playlist_video_join
    ADD CONSTRAINT playlist_video_join_videoid_fkey FOREIGN KEY (videoid) REFERENCES video(id);


--
-- Name: settings settings_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY settings
    ADD CONSTRAINT settings_userid_fkey FOREIGN KEY (userid) REFERENCES nottv_user(id);


--
-- Name: subscribe subscribe_authorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subscribe
    ADD CONSTRAINT subscribe_authorid_fkey FOREIGN KEY (authorid) REFERENCES nottv_user(id);


--
-- Name: subscribe subscribe_subscriberid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subscribe
    ADD CONSTRAINT subscribe_subscriberid_fkey FOREIGN KEY (subscriberid) REFERENCES nottv_user(id);


--
-- Name: user_playlist user_playlist_playlistid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_playlist
    ADD CONSTRAINT user_playlist_playlistid_fkey FOREIGN KEY (playlistid) REFERENCES playlist(id);


--
-- Name: user_playlist user_playlist_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_playlist
    ADD CONSTRAINT user_playlist_userid_fkey FOREIGN KEY (userid) REFERENCES nottv_user(id);


--
-- Name: video_category_value_join video_category_value_join_categoryvalueid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY video_category_value_join
    ADD CONSTRAINT video_category_value_join_categoryvalueid_fkey FOREIGN KEY (categoryvalueid) REFERENCES category_value(id);


--
-- Name: video_category_value_join video_category_value_join_videoid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY video_category_value_join
    ADD CONSTRAINT video_category_value_join_videoid_fkey FOREIGN KEY (videoid) REFERENCES video(id);


--
-- Name: video video_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY video
    ADD CONSTRAINT video_userid_fkey FOREIGN KEY (userid) REFERENCES nottv_user(id);


--
-- PostgreSQL database dump complete
--

