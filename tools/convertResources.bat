@echo off


set source=C:\Users\sevastia\workspace_tms


set jdk_home="\Program Files\Java\jdk1.7.0_10"
set source="C:\Users\sevastia\workspace_tms\TMS Vaadin 7"
set dest="C:\Users\sevastia\workspace_tms\TMS Vaadin 7\src\com\c2point\tms\web\resources"


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
