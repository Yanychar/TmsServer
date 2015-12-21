@echo off

REM The script does not accept parameters

echo   Bat file to deploy created WAR file in the TEST AMAZON environment
echo     1. Configuration
echo     2. Validate tms.war existence
echo     3. Stop Tomcat
echo     5. Backup locally existing WAR (5 last WAR)
echo     6. Delete TMS directory
echo     7. Copy WAR file from local
echo     8. Start Tomcat


echo 1. Configuration...

set app_name=tms
set app_file_name=%app_name%.war

set source="D:\Development\TMS development\workspace_tms"

set dest_srv=centos@54.194.127.157

set dest=/opt/apache-tomcat-8.0.30
set dest_dir_archive=/opt/archive/tms_server

set dest_key=C:\Users\sevastia\keys\tmskeys\tmskey.ppk
echo .              ... configured!

echo 2. Validate tms.war existence
IF NOT EXIST %source%\%app_file_name% (
  echo File '%app_file_name%' was NOT found. Deployment failed!
  goto exit
)
echo File '%app_file_name%' was found.

echo 3. Stop Tomcat ...
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "su -p tomcat %dest%/bin/shutdown.sh"
echo .              ... stopped!

echo 5. TMS directory ...

REM "C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "su -p tomcat rm -r %dest%/webapps/%app_name%"
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "rm -rf %dest%/webapps/%app_name%"

echo .                ... deleted!

echo 6. Copy WAR file to PROD server ...

"C:\Program Files\PuTTY"\pscp -i %dest_key% %source%\%app_file_name% %dest_srv%:%dest%/webapps/
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "chmod u+rwx %dest%/webapps/%app_filr_name%"
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "chmod u+rwx %dest%/webapps/%app_filr_name%"

echo .                               ... new WAR copied!

echo 7. Start Tomcat ...

"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "su -p tomcat %dest%/bin/startup.sh"
REM echo START TOMCAT shall be done manually!!!

echo .               ... Tomcat started!

:exit

pause
