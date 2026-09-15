@echo off
REM Marks the exercise workbook on Windows.
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > .sources.txt
javac -d out @.sources.txt
if errorlevel 1 goto :error
del .sources.txt
java -cp out pokemon.exercises.ExerciseRunner
goto :eof
:error
echo Build failed - read the error messages above.
