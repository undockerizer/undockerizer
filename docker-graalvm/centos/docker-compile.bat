@echo Off
CALL :NORMALIZEPATH "..\..\"
SET PROJECT_PATH=%RETVAL%

ECHO Project path: %PROJECT_PATH%

docker run --volume %PROJECT_PATH%:/project tools/graalvm-jdk8-centos:20.1.0

EXIT /B

:: ========== FUNCTIONS ==========
:NORMALIZEPATH
  SET RETVAL=%~dpfn1
  EXIT /B
  