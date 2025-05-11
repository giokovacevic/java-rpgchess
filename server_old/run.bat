@echo off
set PROJECT_PATH=C:\Users\Ogi\Documents\projects\java-chess\server

javac -d %PROJECT_PATH%\out %PROJECT_PATH%\main\App.java
java -cp %PROJECT_PATH%\out main.App
pause