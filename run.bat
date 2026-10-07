@echo off
rem Compiles MainMenu and everything it uses, then starts the program.
rem Run it from the project folder (config.properties is read from there).
if not exist out mkdir out
javac -encoding UTF-8 -d out -cp "lib\*" -sourcepath src src\gui\MainMenu.java
if errorlevel 1 (
  echo Compile failed
  pause
  exit /b 1
)
java -cp "out;lib\*" gui.MainMenu
