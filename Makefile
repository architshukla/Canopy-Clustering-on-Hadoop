#############
# Variables #
#############

# Prefix for source code folders
SRCPREFIX=$(shell grep 'SRCPREFIX' config | awk '{ print $$3 }')

# Directory names for the steps for MapReduce implementation of Canopy Clustering
DIR1=$(shell grep 'CANOPYCENTERDIR' config | awk '{ print $$3 }')
DIR2=$(shell grep 'CANOPYASSIGNDIR' config | awk '{ print $$3 }')
DIR3=$(shell grep 'CLUSTERCENTERDIR' config | awk '{ print $$3 }')
DIR4=$(shell grep 'CLUSTERASSIGNDIR' config | awk '{ print $$3 }')

# Name of the Directory holding the DataPoint class
DATAPOINTDIR=$(shell grep 'DATAPOINTDIR' config | awk '{ print $$3 }')

# Name of java file holding the class modeling a data point/element in the data set
DATAPOINTCLASS=$(shell grep 'DATAPOINTFILE' config | awk '{ print $$3 }')

# Name of output jar file
OUTPUTJARNAME=$(shell grep 'JARFILE' config | awk '{ print $$3 }')

# Name of hadoop core jar file, the HADOOP_HOME environment variable must be set to your
HADOOPCOREJAR=hadoop-core-*.jar

# Current Working Directory
CWD=$(shell pwd)

#########
# Rules #
#########

# Build all steps
all: step1 step2 step3 step4
	mkdir -p bin/
	mkdir -p bin/$(DIR1)/
	mkdir -p bin/$(DIR2)/
	mkdir -p bin/$(DIR3)/
	mkdir -p bin/$(DIR4)/
	mkdir -p bin/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR1)/*.class bin/$(DIR1)/
	mv $(SRCPREFIX)/$(DIR2)/*.class bin/$(DIR2)/
	mv $(SRCPREFIX)/$(DIR3)/*.class bin/$(DIR3)/
	mv $(SRCPREFIX)/$(DIR4)/*.class bin/$(DIR4)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class bin/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C bin/ .
	rm -r bin/

# Compile step 1 files
step1:	$(SRCPREFIX)/$(DIR1)/$(DIR1)Driver.java \
		$(SRCPREFIX)/$(DIR1)/$(DIR1)Mapper.java \
		$(SRCPREFIX)/$(DIR1)/$(DIR1)Reducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)
	
	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR1)/$(DIR1)Driver.java \
	$(SRCPREFIX)/$(DIR1)/$(DIR1)Mapper.java \
	$(SRCPREFIX)/$(DIR1)/$(DIR1)Reducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

# Compile step 2 files
step2:	$(SRCPREFIX)/$(DIR2)/$(DIR2)Driver.java \
		$(SRCPREFIX)/$(DIR2)/$(DIR2)Mapper.java \
		$(SRCPREFIX)/$(DIR2)/$(DIR2)Reducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR2)/$(DIR2)Driver.java \
	$(SRCPREFIX)/$(DIR2)/$(DIR2)Mapper.java \
	$(SRCPREFIX)/$(DIR2)/$(DIR2)Reducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

# Compile step 3 files
step3:	$(SRCPREFIX)/$(DIR3)/$(DIR3)Driver.java \
		$(SRCPREFIX)/$(DIR3)/$(DIR3)Mapper.java \
		$(SRCPREFIX)/$(DIR3)/$(DIR3)Reducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR3)/$(DIR3)Driver.java \
	$(SRCPREFIX)/$(DIR3)/$(DIR3)Mapper.java \
	$(SRCPREFIX)/$(DIR3)/$(DIR3)Reducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

# Compile step 4 files
step4:	$(SRCPREFIX)/$(DIR4)/$(DIR4)Driver.java \
		$(SRCPREFIX)/$(DIR4)/$(DIR4)Mapper.java \
		$(SRCPREFIX)/$(DIR4)/$(DIR4)Reducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR4)/$(DIR4)Driver.java \
	$(SRCPREFIX)/$(DIR4)/$(DIR4)Mapper.java \
	$(SRCPREFIX)/$(DIR4)/$(DIR4)Reducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)


# Rules to build a particular step
# Build only step 1
canopycenter: step1
	mkdir -p bin/
	mkdir -p bin/$(DIR1)/
	mkdir -p bin/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR1)/*.class bin/$(DIR1)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class bin/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C bin/ .
	rm -r bin/

# Build only step 2
canopyassign: step2
	mkdir -p bin/
	mkdir -p bin/$(DIR2)/
	mkdir -p bin/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR2)/*.class bin/$(DIR2)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class bin/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C bin/ .
	rm -r bin/

# Build only step 3
clustercenter: step3
	mkdir -p bin/
	mkdir -p bin/$(DIR3)/
	mkdir -p bin/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR3)/*.class bin/$(DIR3)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class bin/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C bin/ .
	rm -r bin/

# Build only step 4
clusterassign: step4
	mkdir -p bin/
	mkdir -p bin/$(DIR4)/
	mkdir -p bin/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR4)/*.class bin/$(DIR4)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class bin/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C bin/ .
	rm -r bin/


# Remove bin/ and .jar files
clean:
	if test -f $(SRCPREFIX)/$(DIR1)/*.class; then rm $(SRCPREFIX)/$(DIR1)/*.class; fi
	if test -f $(SRCPREFIX)/$(DIR2)/*.class; then rm $(SRCPREFIX)/$(DIR2)/*.class; fi
	if test -f $(SRCPREFIX)/$(DIR3)/*.class; then rm $(SRCPREFIX)/$(DIR3)/*.class; fi
	if test -f $(SRCPREFIX)/$(DIR4)/*.class; then rm $(SRCPREFIX)/$(DIR4)/*.class; fi
	if test -f $(SRCPREFIX)/$(DATAPOINTDIR)/*.class; then rm $(SRCPREFIX)/$(DATAPOINTDIR)/*.class; fi
	if test -d bin; then rm -r bin/; fi
	if test -f *.jar; then rm *.jar; fi
