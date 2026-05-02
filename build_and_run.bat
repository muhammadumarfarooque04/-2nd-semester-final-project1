@echo off
REM ============================================================
REM  Cafeteria Management System – Build Script (Windows)
REM  Course: SW121 OOP | Batch: K25SW
REM ============================================================

echo =============================================
echo  Cafeteria Management System – Build Script
echo =============================================

REM Check for MySQL Connector JAR in lib/ folder
IF NOT EXIST "lib\mysql-connector-j-8.x.x.jar" (
    echo.
    echo [WARNING] MySQL Connector JAR not found in lib/
    echo Please download from: https://dev.mysql.com/downloads/connector/j/
    echo Place the JAR in the lib/ folder and rename it to mysql-connector-j-8.x.x.jar
    echo Continuing with compilation anyway...
    echo.
)

echo [1/3] Compiling Java sources...
javac -cp "lib\*" -d out\production\CafeteriaMS -sourcepath src ^
    src\cafeteria\Main.java ^
    src\cafeteria\model\*.java ^
    src\cafeteria\dao\*.java ^
    src\cafeteria\auth\*.java ^
    src\cafeteria\util\*.java ^
    src\cafeteria\ui\*.java

IF ERRORLEVEL 1 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [2/3] Creating JAR...
jar cfm CafeteriaMS.jar MANIFEST.MF -C out\production\CafeteriaMS .

echo [3/3] Launching application...
java -cp "CafeteriaMS.jar;lib\*" cafeteria.Main

pause
