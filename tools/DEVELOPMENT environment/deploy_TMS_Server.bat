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

set app_dir=tms
set app_name=%app_dir%.war

set source=C:\Users\sevastia\workspace_tms

set dest_srv=ubuntu@development.uisko.com

set dest=/opt/apache-tomcat-7.0.41
set dest_dir_archive=/opt/archive/tms_server

set dest_key=C:\Users\sevastia\keys\tmskeys\tmskey.ppk
echo .              ... configured!

echo 2. Validate tms.war existence
IF NOT EXIST %source%\%app_name% (
  echo File '%app_name%' was not found. Deployment failed!
  goto exit
)

echo 3. Stop Tomcat ...
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% "sudo %dest%/bin/shutdown.sh"
echo .              ... stopped!

echo 4. Backup existing WAR ...

"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo mkdir -p %dest_dir_archive%

"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo mv %dest_dir_archive%/%app_name%.4 %dest_dir_archive%/%app_name%.5
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo mv %dest_dir_archive%/%app_name%.3 %dest_dir_archive%/%app_name%.4
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo mv %dest_dir_archive%/%app_name%.2 %dest_dir_archive%/%app_name%.3
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo mv %dest_dir_archive%/%app_name%.1 %dest_dir_archive%/%app_name%.2

echo    rem Put existing to .1
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo cp %dest%/webapps/%app_name% %dest_dir_archive%/%app_name%.1

echo .                      ... WAR backup succeeded!

echo 5. TMS directory ...

"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo rm -r %dest%/webapps/%app_dir%

echo .                ... deleted!

echo 6. Copy WAR file to TEST server ...

"C:\Program Files\PuTTY"\pscp -i %dest_key% %source%\%app_name% %dest_srv%:
"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo mv %app_name% %dest%/webapps/

echo .                               ... new WAR copied!

echo 7. Start Tomcat ...

"C:\Program Files\PuTTY"\plink -ssh -i %dest_key% %dest_srv% sudo %dest%/bin/startup.sh

echo .               ... Tomcat started!

:exit

pause
