@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out @fuentes.txt
if errorlevel 1 (pause & exit /b 1)
java -Xmx1g -cp out Main
pause
