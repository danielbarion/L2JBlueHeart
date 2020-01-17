@echo off
title L2J - SQL Account Manager
color 17
java -Djava.util.logging.config.file=console.cfg -Xbootclasspath/p:./../libs/l2ft.jar -cp config/xml;./../libs/*;login.jar l2r.tools.accountmanager.SQLAccountManager
if %errorlevel% == 0 (
echo.
echo Execution successful
echo.
) else (
echo.
echo An error has occurred while running the L2J Account Manager!
echo.
echo Possible reasons for this to happen:
echo.
echo - Missing .jar files or ../libs directory.
echo - MySQL server not running or incorrect MySQL settings:
echo    check ./config/loginserver.properties
echo - Wrong data types or values out of range were provided:
echo    specify correct values for each required field
echo.
)
pause