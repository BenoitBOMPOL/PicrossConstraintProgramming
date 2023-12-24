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
NAIVESRC		= src/naive
SMARTSRC 		= src/smarter

build_naive_solver:
	$(JAVAC) $(JOPT) $(NAIVESRC)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(NAIVESRC)/picross.java $(NAIVESRC)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(NAIVESRC)/picrossSlv.java $(NAIVESRC)/picross.java $(NAIVESRC)/AllowedTupleSearcher.java

run_naive_solver:
	$(JAVA) -Xmx1024M -Xms1024M $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(NAIVESRC) picrossSlv $(ARGS)

run_naive_benchmark: build_naive_solver
	echo "+++ Naive Solver Benchmark +++"; for grid in picross/*.px; do echo Running solver on file "$$grid"...; $(JAVA) $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(NAIVESRC) picrossSlv "$$grid"; echo; done

build_smart_solver:
	$(JAVAC) $(JOPT) $(SMARTSRC)/OneLineSolver.java
	$(JAVAC) $(JOPT) $(SMARTSRC)/picross.java $(SMARTSRC)/OneLineSolver.java
	$(JAVAC) $(JOPT) $(SMARTSRC)/picrossBetterSlv.java $(SMARTSRC)/picross.java $(SMARTSRC)/OneLineSolver.java

run_smart_solver:
	$(JAVA) -Xmx1024M -Xms1024M $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SMARTSRC) picrossBetterSlv $(ARGS)

run_smart_benchmark: build_smart_solver
	echo "+++ Smart Solver Benchmark +++"; for grid in picross/*.px; do echo Running solver on file "$$grid"...; $(JAVA) $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SMARTSRC) picrossBetterSlv "$$grid"; echo; done


clean:
	clear
	rm -f $(NAIVESRC)/*.class
	rm -f $(SMARTSRC)/*.class
	
.PHONY: default clean build_naive_solver run_naive_solver run_naive_benchmark build_smart_solver run_smart_solver run_smart_benchmark 