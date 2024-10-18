# Makefile for GridRouter Java Program

# Variables
JAVAC = javac
JAVA = java
MAIN_CLASS = GridRouter
SOURCE = $(MAIN_CLASS).java
OUTPUT = resultado_X.txt

# Default target to compile the program
all: $(MAIN_CLASS)

# Target to compile the Java program
$(MAIN_CLASS): $(SOURCE)
	$(JAVAC) $(SOURCE)

# Target to run the program with a grid file
run: $(MAIN_CLASS)
	$(JAVA) $(MAIN_CLASS) grid_0.txt

# Target to clean compiled class files and output
clean:
	rm -f *.class $(OUTPUT)

.PHONY: all clean run
