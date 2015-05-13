@echo off
set JAR_DIRECTORY=%~dp0
set JAVAUTO="%JAR_DIRECTORY%javauto.jar"
set JAVAUTO_HELPER="%JAR_DIRECTORY%javauto-helper.jar"
set JAVAUTO_LOOKUP="%JAR_DIRECTORY%javauto-lookup.jar"
set JAVAUTO_FILE=java -jar %JAVAUTO% -nc %%*
set JAVAUTO_HELPER_FILE=java -jar %JAVAUTO_HELPER% %%*
set JAVAUTO_LOOKUP_FILE=java -jar %JAVAUTO_LOOKUP% %%*
echo Creating javauto.bat in %JAR_DIRECTORY%...
echo @echo off > %JAR_DIRECTORY%javauto.bat
echo %JAVAUTO_FILE% >> %JAR_DIRECTORY%javauto.bat
echo Creating javauto-helper.bat in %JAR_DIRECTORY%...
echo @echo off > %JAR_DIRECTORY%javauto-helper.bat
echo %JAVAUTO_HELPER_FILE% >> %JAR_DIRECTORY%javauto-helper.bat
echo Creating javauto-lookup.bat in %JAR_DIRECTORY%...
echo @echo off > %JAR_DIRECTORY%javauto-lookup.bat
echo %JAVAUTO_LOOKUP_FILE% >> %JAR_DIRECTORY%javauto-lookup.bat
echo Adding %JAR_DIRECTORY% to the system path...
setx path "%PATH%;%JAR_DIRECTORY%"
echo Install completed successfully...
pause 
