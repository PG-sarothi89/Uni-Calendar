@echo off
:: ============================================================
::  UniTrack — Smart Academic Calendar
::  run.bat  — Launches the compiled desktop application
:: ============================================================

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

if not exist "out\MainApp.class" (
    echo [ERROR] Build output not found. Please run build.bat first.
    pause
    exit /b 1
)

echo Starting UniTrack Smart Academic Calendar...
java -cp out MainApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [NOTICE] Application terminated.
    pause
)
