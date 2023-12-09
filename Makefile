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

solve:
	$(JAVAC) $(JOPT) $(SRCDIR)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(SRCDIR)/picross.java $(SRCDIR)/AllowedTupleSearcher.java
	$(JAVAC) $(JOPT) $(SRCDIR)/picrossSlv.java $(SRCDIR)/picross.java $(SRCDIR)/AllowedTupleSearcher.java

.PHONY: default solve