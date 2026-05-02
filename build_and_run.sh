#!/bin/bash
# ============================================================
#  Cafeteria Management System – Build Script (Linux/Mac)
#  Course: SW121 OOP | Batch: K25SW
# ============================================================

echo "============================================="
echo " Cafeteria Management System – Build Script"
echo "============================================="

CONNECTOR=$(ls lib/mysql-connector*.jar 2>/dev/null | head -1)
if [ -z "$CONNECTOR" ]; then
    echo ""
    echo "[WARNING] MySQL Connector JAR not found in lib/"
    echo "Download from: https://dev.mysql.com/downloads/connector/j/"
    echo "Place the JAR in the lib/ folder."
    exit 1
fi

echo "[1/3] Compiling Java sources..."
mkdir -p out/production/CafeteriaMS
javac -cp "lib/*" -d out/production/CafeteriaMS \
    src/cafeteria/Main.java \
    src/cafeteria/model/*.java \
    src/cafeteria/dao/*.java \
    src/cafeteria/auth/*.java \
    src/cafeteria/util/*.java \
    src/cafeteria/ui/*.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed!"
    exit 1
fi

echo "[2/3] Creating JAR..."
jar cfm CafeteriaMS.jar MANIFEST.MF -C out/production/CafeteriaMS .

echo "[3/3] Launching application..."
java -cp "CafeteriaMS.jar:lib/*" cafeteria.Main
