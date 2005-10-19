@echo off
@rem set GUESS_HOME=c:\program files\GUESS
set GUESS_LIB=%GUESS_HOME%\lib
java "-Dpython.home=%GUESS_HOME%\src" -DgHome="%GUESS_HOME%" -jar guess.jar %1 %2 %3 %4 %5 %6 %7