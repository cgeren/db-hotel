all:
	javac -d ./ src/Interfaces.java
	jar cfmv California.jar Manifest.txt Interfaces.class
clean:
	rm California.jar
	rm Interfaces.class