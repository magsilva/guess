"c:\program files\7-zip\7z.exe" a guess.7z guess-install.jar compile-lib\launcher.ini compile-lib\launcher-Win32.exe
copy /B compile-lib\7zS.sfx + compile-lib\config.txt + guess.7z guess-install.exe
del guess.7z