@echo off

set jdk_home="C:\Program Files\Java\jdk1.8.0_45"
set   source="D:\Development\TMS development\workspace_tms\TmsServer"
set     dest="D:\Development\TMS development\workspace_tms\TmsServer\src\com\c2point\tms\web\resources"


del %dest%\WebResources_en_FI.properties
del %dest%\WebResources_fi_FI.properties
del %dest%\WebResources_et_FI.properties
del %dest%\WebResources_ru_FI.properties


%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_en_FI.properties %dest%\WebResources_en_FI.properties
%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_fi_FI.properties %dest%\WebResources_fi_FI.properties
%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_et_FI.properties %dest%\WebResources_et_FI.properties
%jdk_home%\bin\native2ascii -encoding utf8 %source%\WebResources_ru_FI.properties %dest%\WebResources_ru_FI.properties


REM del %source%\StringResources.csv

del %source%\WebResources_en_FI.properties
del %source%\WebResources_fi_FI.properties
del %source%\WebResources_et_FI.properties
del %source%\WebResources_ru_FI.properties



pause
