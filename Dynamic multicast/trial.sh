#targetdir=/home/nishchay/Documents/Acads/3-2/SOP/ONE-implementation/Dynamic\ multicast/out
src=/home/nishchay/Documents/Acads/3-2/SOP/ONE-implementation/Dynamic\ multicast
#if [ ! -d "$targetdir" ]; then mkdir $targetdir; fi

javac -sourcepath src -cp lib/ECLA.jar:lib/DTNConsoleConnection.jar interfaces/*.java routing/*.java movement/*.java input/*.java report/*.java core/*.java gui/*.java applications/*.java
