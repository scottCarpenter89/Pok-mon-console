@echo off
REM Runs the test suite on Windows.
cd /d "%~dp0"
if not exist out mkdir out
if not exist test-out mkdir test-out
dir /s /b src\*.java > .sources.txt
javac -d out @.sources.txt
if errorlevel 1 goto :error
dir /s /b test\*.java > .testsources.txt
javac -cp out -d test-out @.testsources.txt
if errorlevel 1 goto :error
del .sources.txt
del .testsources.txt
java -cp "out;test-out" pokemon.TestRunner
goto :eof
:error
echo Build failed - read the error messages above.
