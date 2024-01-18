# ----------------------------------------------------- #
# 	Picross Solver using Constraint Programming Tools	#
# ----------------------------------------------------- #

# CPLEX Installation folder
CPLEXDIR        = /opt/ibm/ILOG/CPLEX_Studio2211
OPLALL          = $(CPLEXDIR)/opl/lib/oplall.jar
GLOBALLIBRARY   = $(CPLEXDIR)/opl/bin/x86-64_linux/

# Compiler and link options
JAVAC           = javac
JAVA            = java
JOPT = -classpath $(OPLALL):$(GLOBALLIBRARY)

# Source directories
SRCDIR     		= src

# Useful notes :
# 1. You might need to update LD_LIBRARY_PATH
#	 Add the following at the end of your .bashrc
# 	 export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:/opt/ibm/ILOG/CPLEX_Studio2211/opl/bin/x86-64_linux/

all:
	@clear
	@echo 🟥🟩🟦 Picross Solver 🟦🟩🟥
	@echo 
	@echo Here are the following available commands
	@echo -e '\t'1. make onelinesolver
	@echo -e '\t\t' Solve the 1-line problem described in $(SRCDIR)/OneLineSolver.java'\n'
	@echo -e '\t'2. make buildsolver
	@echo -e '\t\t' Build necessary files for the solver.'\n'
	@echo -e '\t'3. make solve GRID=\"picross/godzilla.px\"
	@echo -e '\t\t' Solve the grid located at \"picross/godzilla.px\"'\n'
	@echo -e '\t'4. make benchmark
	@echo -e '\t\t' Solve every grid in the picross folder'\n'
	@echo -e '\t'5. make clean
	@echo -e '\t\t' Remove .class files


onelinesolver:
	@$(JAVAC) $(JOPT) $(SRCDIR)/OneLineSolver.java
	@$(JAVA) -Xmx4g $(JOPT) $(SRCDIR)/OneLineSolver.java

buildsolver:
	@$(JAVAC) $(JOPT) $(SRCDIR)/OneLineSolver.java
	@$(JAVAC) $(JOPT) $(SRCDIR)/picross.java $(SRCDIR)/OneLineSolver.java
	@$(JAVAC) $(JOPT) $(SRCDIR)/picrossSlv.java $(SRCDIR)/picross.java $(SRCDIR)/OneLineSolver.java

solve: buildsolver
	@$(JAVA) -Xmx1024M -Xms1024M $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SRCDIR) picrossSlv $(GRID)

benchmark: buildsolver
	@clear; for grid in picross/*.px; do echo Running solver on file "$$grid"...; timeout 10 $(JAVA) $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SRCDIR) picrossSlv "$$grid"; echo; done


clean:
	@rm -f $(SRCDIR)/*.class

.PHONY: default clean
