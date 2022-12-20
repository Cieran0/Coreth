default:
	@make all >> /dev/null
	@rm -r parser >> /dev/null

run:
	@make default >> /dev/null
	@java -jar Main.jar

all: 
	javac -d . *.java
	jar cmvf MANIFEST.MF Main.jar parser/

clean:
	-rm *.jar
	-rm *.class
	-rm -r parser