#---------------------------------------------------#
# Picross Solver using Constraint Programming Tools #
# You might need to change the installation folder. #
#---------------------------------------------------#

SYSTEM		= x86_64_linux
LIBFORMAT	= static_pic

# CPLEX Installation folder
# NOTE : Modifications start here
CPLEXDIR	= /opt/ibm/ILOG/CPLEX_Studio2211/cplex
CONCERTDIR	= /opt/ibm/ILOG/CPLEX_Studio2211/concert
# Modifications end here

#	--------------------------------------------	#
#	    Compiler and link options, Libraries		#
#	--------------------------------------------	#
JAVAC			= javac
JOPT			= -classpath $(CPLEXDIR)/lib/cplex.jar

CPLEXBINDIR 	= $(CPLEXDIR)/bin/$(SYSTEM)
CPLEXJARDIR 	= $(CPLEXDIR)/lib/cplex.jar
CPLEXLIBDIR 	= $(CPLEXDIR)/lib/$(SYSTEM)/$(LIBFORMAT)
CONCERTLIBDIR	= $(CONCERTDIR)/lib/$(SYSTEM)/$(LIBFORMAT)

CPLEXLIBDIR		= cplex$(dynamic:yes=2211)
run				= $(dynamic:yes=LD_LIBRARY_PATH=$(CPLEXBINDIR))

CCLNDIRS  = -L$(CPLEXLIBDIR) -L$(CONCERTLIBDIR) $(dynamic:yes=-L$(CPLEXBINDIR))
CLNDIRS   = -L$(CPLEXLIBDIR) $(dynamic:yes=-L$(CPLEXBINDIR))
CCLNFLAGS = -lconcert -lilocplex -l$(CPLEXLIB) -lm -lpthread -ldl
CLNFLAGS  = -l$(CPLEXLIB) -lm -lpthread -ldl
JAVA      = java   -Djava.library.path=$(CPLEXDIR)/bin/x86-64_linux -classpath $(CPLEXJARDIR):

CONCERTINCDIR = $(CONCERTDIR)/include
CPLEXINCDIR   = $(CPLEXDIR)/include

JCFLAGS = $(JOPT)
