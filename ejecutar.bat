@echo off
cd /d "%~dp0"
if not exist out mkdir out

echo Buscando archivos fuente en src...
dir /s /b src\*.java > out\fuentes.txt

echo Compilando proyecto SmartETL-DS + IA...
javac -encoding UTF-8 -d out @out\fuentes.txt
if errorlevel 1 (
    echo [ERROR] La compilacion fallo. Revisa los mensajes de arriba.
    pause
    exit /b 1
)

echo Ejecutando Main...
java -Xmx1g -cp out Main
pause