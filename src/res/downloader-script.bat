@echo off
set type=%1
set magnet=%2
set moviePath="D:\Media\Movies"
set tvPath="D:\Media\TV Shows"


IF "%type%"=="TV" (
	GOTO:TV
) ELSE (
	IF "%type%"=="MOVIE" (
		GOTO:MOVIE
	) ELSE (
			GOTO:end
	)
)	


:TV
echo A TV file was passed in.
aria2c.exe --seed-ratio=0.2 --seed-time=1 -d %tvPath% %magnet%
REM aria2c.exe --seed-ratio=0.2 --seed-time=1 -d %tvPath% %magnet%
GOTO:end

:MOVIE
echo A movie file was passed in.
aria2c.exe --seed-ratio=0.2 --seed-time=1 -d %moviePath% %magnet%
REM aria2c.exe --seed-ratio=0.2 --seed-time=1 -d %moviePath% %magnet%
GOTO:end

:end
echo Download complete!


