targetdir=target

if [ ! -d "$targetdir" ]; then mkdir $targetdir; fi

javac -sourcepath src -d $targetdir -cp lib/ECLA.jar:lib/DTNConsoleConnection.jar /core/*.java /movement/*.java /report/*.java /routing/*.java /gui/*.java /input/*.java /applications/*.java /interfaces/*.java

#if [ ! -d "$targetdir/gui/buttonGraphics" ]; then cp -R src/gui/buttonGraphics target/gui/; fi
