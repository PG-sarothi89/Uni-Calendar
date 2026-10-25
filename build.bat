@echo off
:: ============================================================
::  UniTrack — Smart Academic Calendar
::  build.bat  — Compiles all Java source files into out\
:: ============================================================

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

echo [1/3] Cleaning previous build directory...
if exist out rmdir /s /q out
mkdir out

echo [2/3] Compiling Java sources with javac...
javac -encoding UTF-8 -d out -sourcepath src src\MainApp.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please check Java errors above.
    pause
    exit /b 1
)

echo [3/3] Build successful! Compiled classes saved to out\
echo.
echo You can now launch the application using: run.bat
pause
