--

-- Create user and database
create user tms with password 'tms';
create database tms owner tms;
grant all privileges on database tms to tms;
-- Create test user for Junit and assign proper roles
create user testtms with password 'testtms';
-- create database testtms owner testtms;
-- grant all privileges on database testtms to testtms;
-- grant all privileges on database testtms to testtms;
alter role testtms createdb;
alter role testtms superuser;

