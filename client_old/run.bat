@echo off
set JAVA_FX_PATH=C:\javafx-sdk-21.0.5\lib
set PROJECT_PATH=C:\Users\Ogi\Documents\projects\java-chess\client

javac --module-path %JAVA_FX_PATH% --add-modules javafx.controls,javafx.fxml -d %PROJECT_PATH%\out %PROJECT_PATH%\src\main\Client.java
java --module-path %JAVA_FX_PATH% --add-modules javafx.controls,javafx.fxml -cp %PROJECT_PATH%\out main.Client
pause