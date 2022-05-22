--
-- PostgreSQL database dump
--

-- Dumped from database version 14.1
-- Dumped by pg_dump version 14.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: detection_history; Type: TABLE; Schema: public; Owner: dtire
--

CREATE TABLE public.detection_history (
    detection_id character varying NOT NULL,
    user_id character varying NOT NULL,
    date_of_check timestamp with time zone NOT NULL,
    condition_title character varying NOT NULL,
    recommendation character varying NOT NULL
);


ALTER TABLE public.detection_history OWNER TO dtire;

--
-- Name: user; Type: TABLE; Schema: public; Owner: dtire
--

CREATE TABLE public."user" (
    user_id character varying NOT NULL,
    email character varying NOT NULL,
    password character varying NOT NULL,
    name character varying
);


ALTER TABLE public."user" OWNER TO dtire;

--
-- Name: user_details; Type: TABLE; Schema: public; Owner: dtire
--

CREATE TABLE public.user_details (
    user_id character varying NOT NULL,
    address character varying,
    phone text
);


ALTER TABLE public.user_details OWNER TO dtire;

--
-- Data for Name: detection_history; Type: TABLE DATA; Schema: public; Owner: dtire
--

COPY public.detection_history (detection_id, user_id, date_of_check, condition_title, recommendation) FROM stdin;
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: dtire
--

COPY public."user" (user_id, email, password, name) FROM stdin;
\.


--
-- Data for Name: user_details; Type: TABLE DATA; Schema: public; Owner: dtire
--

COPY public.user_details (user_id, address, phone) FROM stdin;
\.


--
-- Name: detection_history detection_history_pkey; Type: CONSTRAINT; Schema: public; Owner: dtire
--

ALTER TABLE ONLY public.detection_history
    ADD CONSTRAINT detection_history_pkey PRIMARY KEY (detection_id);


--
-- Name: user user_email_email1_key; Type: CONSTRAINT; Schema: public; Owner: dtire
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_email_email1_key UNIQUE (email) INCLUDE (email);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: dtire
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (user_id);


--
-- Name: detection_history detection_history_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dtire
--

ALTER TABLE ONLY public.detection_history
    ADD CONSTRAINT detection_history_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(user_id) ON DELETE CASCADE;


--
-- Name: user_details user_details_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dtire
--

ALTER TABLE ONLY public.user_details
    ADD CONSTRAINT user_details_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(user_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

