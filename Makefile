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

AllowedTupleSearcher:
	$(JAVAC) $(JOPT) $(SRCDIR)/AllowedTupleSearcher.java

run_allowed_tuple_searcher: AllowedTupleSearcher
	$(JAVA) -Xmx4g $(JOPT) $(SRCDIR)/AllowedTupleSearcher.java

enumerate_tuple:
	$(JAVAC) $(JOPT) $(SRCDIR)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(SRCDIR)/picross.java $(SRCDIR)/AllowedTupleSearcher.java
	$(JAVA) $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SRCDIR) picross $(ARGS)

solve:
	$(JAVAC) $(JOPT) $(SRCDIR)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(SRCDIR)/picross.java $(SRCDIR)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(SRCDIR)/picrossSlv.java $(SRCDIR)/picross.java $(SRCDIR)/AllowedTupleSearcher.java

run_solver:
	$(JAVA) -Xmx4096M -Xms4096M $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SRCDIR) picrossSlv $(ARGS)

benchmark: solve
	for grid in picross/*.px; do echo Running solver on file "$$grid"...; $(JAVA) $(JOPT) -cp $(OPLALL):$(GLOBALLIBRARY):$(SRCDIR) picrossSlv "$$grid"; echo; done

clean:
	clear
	rm -f $(SRCDIR)/*.class

.PHONY: default solve AllowedTupleSearcher run_allowed_tuple_searcher clean benchmark
