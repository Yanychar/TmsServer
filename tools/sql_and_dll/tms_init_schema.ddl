--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: tms
--
SET default_tablespace = '';

SET default_with_oids = false;



CREATE TABLE accessrights
(
  id bigint NOT NULL,
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  read boolean NOT NULL,
  write boolean NOT NULL,
  add boolean NOT NULL,
  del boolean NOT NULL,
  owneruser_id bigint,
  ownergroup_id bigint,
  function integer,
  CONSTRAINT accessrights_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE accessrights
  OWNER TO tms;


CREATE TABLE check_in_out
(
  id bigint NOT NULL,
  consistencyversion bigint NOT NULL,
  checkoutbyclient boolean,
  deleted boolean NOT NULL,
  datecheckedin timestamp without time zone,
  datecheckedout timestamp without time zone,
  inlatitude double precision,
  inlongitude double precision,
  outlatitude double precision,
  outlongitude double precision,
  project_id bigint,
  userstate_id bigint,
  inaccuracy double precision,
  outaccuracy double precision,
  CONSTRAINT check_in_out_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE check_in_out
  OWNER TO tms;

CREATE TABLE configuration
(
  id bigint NOT NULL,
  db_version bigint,
  CONSTRAINT configuration_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE configuration
  OWNER TO tms;

CREATE TABLE organisation
(
  id bigint NOT NULL,
  code character varying(255),
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  name character varying(255),
  serviceowner_id bigint,
  propstring character varying(4096),
  address character varying(255),
  tunnus character varying(255),
  info character varying(4096),
  phone character varying(255),
  email character varying(255),
  CONSTRAINT organisation_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organisation
  OWNER TO tms;


CREATE TABLE project
(
  id bigint NOT NULL,
  code character varying(255),
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  name character varying(255),
  mgrcode character varying(255),
  mgrname character varying(255),
  organisation_id bigint,
  projectmanager_id bigint,
  start date,
  endplanned date,
  endreal date,
  address character varying(255),
  latitude double precision,
  longitude double precision,
  accuracy double precision,
  closed boolean,
  CONSTRAINT project_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE project
  OWNER TO tms;


CREATE TABLE projecttask
(
  id bigint NOT NULL,
  codeinproject character varying(255),
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  project_id bigint,
  task_id bigint,
  projecttasks_key character varying(255),
  CONSTRAINT projecttask_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE projecttask
  OWNER TO tms;


CREATE TABLE securitygroup
(
  id bigint NOT NULL,
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  organisation_id bigint,
  code character varying(255),
  defname character varying(255),
  CONSTRAINT securitygroup_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE securitygroup
  OWNER TO tms;


CREATE TABLE sequence
(
  seq_name character varying(50) NOT NULL,
  seq_count numeric(38,0),
  CONSTRAINT sequence_pkey PRIMARY KEY (seq_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sequence
  OWNER TO tms;


CREATE TABLE task
(
  id bigint NOT NULL,
  code character varying(255),
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  name character varying(255),
  organisation_id bigint,
  CONSTRAINT task_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE task
  OWNER TO tms;


CREATE TABLE taskreport
(
  id bigint NOT NULL,
  comment character varying(1024),
  consistencyversion bigint NOT NULL,
  date date,
  deleted boolean NOT NULL,
  hours double precision,
  uniquereportid character varying(255),
  projecttask_id bigint,
  user_id bigint,
  intapprovaltype integer,
  org_id bigint,
  CONSTRAINT taskreport_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE taskreport
  OWNER TO tms;


CREATE TABLE taxreport
(
  id bigint NOT NULL,
  code character varying(1048576),
  consistencyversion bigint NOT NULL,
  date date,
  deleted boolean NOT NULL,
  lastmodidate date,
  status integer,
  xml_data character varying(4096),
  type integer,
  organisation_id bigint,
  CONSTRAINT taxreport_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE taxreport
  OWNER TO tms;


CREATE TABLE tmsaccount
(
  id bigint NOT NULL,
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  pwd character varying(255),
  usrname character varying(255),
  tmsuser_id bigint,
  pwdmustbechanged boolean NOT NULL DEFAULT false,
  CONSTRAINT tmsaccount_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tmsaccount
  OWNER TO tms;


CREATE TABLE tmstransactions
(
  id bigint NOT NULL,
  type character varying(20),
  consistencyversion bigint NOT NULL,
  date timestamp without time zone,
  deleted boolean NOT NULL,
  transactiondata character varying(2048),
  tmsuser_id bigint,
  CONSTRAINT tmstransactions_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tmstransactions
  OWNER TO tms;


CREATE TABLE tmsuser
(
  id bigint NOT NULL,
  code character varying(255),
  consistencyversion bigint NOT NULL,
  deleted boolean NOT NULL,
  firstname character varying(255),
  lastname character varying(255),
  midname character varying(255),
  resource character varying(255),
  department_id bigint,
  manager_id bigint,
  userstate_id bigint,
  countgroup character varying(10),
  organisation_id bigint,
  project_manager_flag boolean NOT NULL DEFAULT false,
  line_manager_flag boolean NOT NULL DEFAULT false,
  secgroup_id bigint,
  address character varying(255),
  kelacode character varying(255),
  taxnumber character varying(255),
  email character varying(255),
  mobile character varying(255),
  CONSTRAINT tmsuser_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tmsuser
  OWNER TO tms;


CREATE TABLE tmsuserstate
(
  id bigint NOT NULL,
  checkedin boolean,
  consistencyversion bigint NOT NULL,
  datesessionended timestamp without time zone,
  datesessionstarted timestamp without time zone,
  datesessiontouched timestamp without time zone,
  deleted boolean NOT NULL,
  uniquesessionid character varying(255),
  check_in_out_id bigint,
  CONSTRAINT tmsuserstate_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tmsuserstate
  OWNER TO tms;


CREATE TABLE travelreport
(
  id bigint NOT NULL,
  consistencyversion bigint NOT NULL,
  date date,
  deleted boolean NOT NULL,
  distance integer,
  enddate timestamp without time zone,
  inttraveltype integer,
  route character varying(512),
  startdate timestamp without time zone,
  uniquereportid character varying(255),
  user_id bigint,
  intapprovaltype integer,
  org_id bigint,
  project_id bigint,
  CONSTRAINT travelreport_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE travelreport
  OWNER TO tms;

--
-- ************************
--
-- ALTER TABLE ONLY travelreport
--     ADD CONSTRAINT fk_travelreport_project_id FOREIGN KEY (project_id) REFERENCES project(id);

ALTER TABLE ONLY accessrights
  ADD CONSTRAINT fk_accessrights_ownergroup_id FOREIGN KEY (ownergroup_id)
      REFERENCES securitygroup (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_accessrights_owneruser_id FOREIGN KEY (owneruser_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY check_in_out
  ADD CONSTRAINT fk_check_in_out_project_id FOREIGN KEY (project_id)
      REFERENCES project (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_check_in_out_userstate_id FOREIGN KEY (userstate_id)
      REFERENCES tmsuserstate (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY organisation
  ADD CONSTRAINT fk_serviceowner_id FOREIGN KEY (serviceowner_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY project
  ADD CONSTRAINT fk_projectmanager_id FOREIGN KEY (projectmanager_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY projecttask
  ADD CONSTRAINT fk_projecttask_project_id FOREIGN KEY (project_id)
      REFERENCES project (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_projecttask_task_id FOREIGN KEY (task_id)
      REFERENCES task (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY securitygroup
  ADD CONSTRAINT fk_securitygroup_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY task
  ADD CONSTRAINT fk_task_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY taskreport
  ADD CONSTRAINT fk_taskreport_projecttask_id FOREIGN KEY (projecttask_id)
      REFERENCES projecttask (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_taskreport_user_id FOREIGN KEY (user_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY taxreport
  ADD CONSTRAINT fk_taxreport_org_id FOREIGN KEY (organisation_id)
      REFERENCES organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY tmsaccount
  ADD CONSTRAINT fk_tmsaccount_tmsuser_id FOREIGN KEY (tmsuser_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY tmstransactions
  ADD CONSTRAINT fk_tmstransactions_tmsuser_id FOREIGN KEY (tmsuser_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY tmsuser
  ADD CONSTRAINT fk_tmsuser_manager_id FOREIGN KEY (manager_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_tmsuser_secgroup_id FOREIGN KEY (secgroup_id)
      REFERENCES securitygroup (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_tmsuser_userstate_id FOREIGN KEY (userstate_id)
      REFERENCES tmsuserstate (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY tmsuserstate
  ADD CONSTRAINT fk_tmsuserstate_check_in_out_id FOREIGN KEY (check_in_out_id)
      REFERENCES check_in_out (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE ONLY travelreport
  ADD CONSTRAINT fk_travelreport_project_id FOREIGN KEY (project_id)
      REFERENCES project (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT fk_travelreport_user_id FOREIGN KEY (user_id)
      REFERENCES tmsuser (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


--




--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

