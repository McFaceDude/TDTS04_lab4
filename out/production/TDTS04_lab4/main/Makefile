# -*- Mode: Makefile -*-
#
# Makefile - Java version
#
# use: make 
# or:  make test
#

JAVA = C:/Program\ Files/Java/jdk1.8.0_25/bin/java.exe
JAVAC = C:/Program\ Files/Java/jdk1.8.0_25/bin/javac.exe

all: RouterSimulator.class

RouterSimulator.class: RouterSimulator.java
	-@$(JAVAC) RouterSimulator.java


clean:
	-@touch ./abc~ core
	-@rm -f *~ core 

clobber: clean
	-@touch ./abc.class 
	-@rm -f *.class 

test: RouterSimulator.class
	$(JAVA) -DTrace=3 RouterSimulator

install3:
	-@echo 
	-@echo "  Warning: this command will overwrite file ./RouterSimulator.java"
	-@echo 
	-@echo "Continue with ^D, exit with ^C"
	-@cat > /dev/null
	-@make clobber
	-@cp test/RouterSimulator3.java RouterSimulator.java

install4:
	-@echo 
	-@echo "  Warning: this command will overwrite file ./RouterSimulator.java"
	-@echo 
	-@echo "Continue with ^D, exit with ^C"
	-@cat > /dev/null
	-@make clobber
	-@cp test/RouterSimulator4.java RouterSimulator.java

install5:
	-@echo 
	-@echo "  Warning: this command will overwrite file ./RouterSimulator.java"
	-@echo 
	-@echo "Continue with ^D, exit with ^C"
	-@cat > /dev/null
	-@make clobber
	-@cp test/RouterSimulator5.java RouterSimulator.java

