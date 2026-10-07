#!/bin/sh
# Compiles MainMenu and everything it uses, then starts the program.
# Run it from the project folder (config.properties is read from there).
mkdir -p out
javac -encoding UTF-8 -d out -cp "lib/*" -sourcepath src src/gui/MainMenu.java || exit 1
java -cp "out:lib/*" gui.MainMenu
