#############
# Variables #
#############

# Prefix for all packages
PKGPREFIX=$(shell grep 'PKGPREFIX' cc.properties | awk '{ print $$3 }')

# Prefix for source code folders
SRCPREFIX=$(shell grep 'SRCPREFIX' cc.properties | awk '{ print $$3 }')/$(PKGPREFIX)

# Folders
SRC=src
BIN=bin
DIST=dist

# Directory names for the steps for MapReduce implementation of Canopy Clustering
DIR1=$(shell grep 'CANOPYCENTERDIR' cc.properties | awk '{ print $$3 }')
DIR2=$(shell grep 'CANOPYASSIGNDIR' cc.properties | awk '{ print $$3 }')
DIR3=$(shell grep 'CLUSTERCENTERDIR' cc.properties | awk '{ print $$3 }')
DIR4=$(shell grep 'CLUSTERASSIGNDIR' cc.properties | awk '{ print $$3 }')

# Name of the Directory holding the DataPoint class
DATAPOINTDIR=$(shell grep 'DATAPOINTDIR' cc.properties | awk '{ print $$3 }')

# Name of java file holding the class modeling a data point/element in the data set
DATAPOINTCLASS=$(shell grep 'DATAPOINTFILE' cc.properties | awk '{ print $$3 }')

# Name of output jar file
OUTPUTJARNAME=$(shell grep 'JARFILE' cc.properties | awk '{ print $$3 }')

# Name of hadoop core jar file, the HADOOP_HOME environment variable must be set to your
HADOOPCOREJAR=hadoop-core-*.jar

#########
# Rules #
#########

# Build all steps
all: step1 step2 step3 step4
	mkdir -p $(BIN)
	mkdir -p $(BIN)/$(PKGPREFIX)/$(DIR1)/
	mkdir -p $(BIN)/$(PKGPREFIX)/$(DIR2)/
	mkdir -p $(BIN)/$(PKGPREFIX)/$(DIR3)/
	mkdir -p $(BIN)/$(PKGPREFIX)/$(DIR4)/
	mkdir -p $(BIN)/$(PKGPREFIX)/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR1)/*.class $(BIN)/$(PKGPREFIX)/$(DIR1)/
	mv $(SRCPREFIX)/$(DIR2)/*.class $(BIN)/$(PKGPREFIX)/$(DIR2)/
	mv $(SRCPREFIX)/$(DIR3)/*.class $(BIN)/$(PKGPREFIX)/$(DIR3)/
	mv $(SRCPREFIX)/$(DIR4)/*.class $(BIN)/$(PKGPREFIX)/$(DIR4)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class $(BIN)/$(PKGPREFIX)/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C $(BIN) .
	mkdir -p $(DIST)
	mv $(OUTPUTJARNAME) $(DIST)

# Compile step 1 files
step1:	$(SRCPREFIX)/$(DIR1)/CanopyCenterDriver.java \
		$(SRCPREFIX)/$(DIR1)/CanopyCenterMapper.java \
		$(SRCPREFIX)/$(DIR1)/CanopyCenterReducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)
	
	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR1)/CanopyCenterDriver.java \
	$(SRCPREFIX)/$(DIR1)/CanopyCenterMapper.java \
	$(SRCPREFIX)/$(DIR1)/CanopyCenterReducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

# Compile step 2 files
step2:	$(SRCPREFIX)/$(DIR2)/CanopyAssignDriver.java \
		$(SRCPREFIX)/$(DIR2)/CanopyAssignMapper.java \
		$(SRCPREFIX)/$(DIR2)/CanopyAssignReducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR2)/CanopyAssignDriver.java \
	$(SRCPREFIX)/$(DIR2)/CanopyAssignMapper.java \
	$(SRCPREFIX)/$(DIR2)/CanopyAssignReducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

# Compile step 3 files
step3:	$(SRCPREFIX)/$(DIR3)/ClusterCenterDriver.java \
		$(SRCPREFIX)/$(DIR3)/ClusterCenterMapper.java \
		$(SRCPREFIX)/$(DIR3)/ClusterCenterReducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR3)/ClusterCenterDriver.java \
	$(SRCPREFIX)/$(DIR3)/ClusterCenterMapper.java \
	$(SRCPREFIX)/$(DIR3)/ClusterCenterReducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

# Compile step 4 files
step4:	$(SRCPREFIX)/$(DIR4)/ClusterAssignDriver.java \
		$(SRCPREFIX)/$(DIR4)/ClusterAssignMapper.java \
		$(SRCPREFIX)/$(DIR4)/ClusterAssignReducer.java \
		$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)

	javac -classpath $(HADOOP_HOME)/$(HADOOPCOREJAR) \
	$(SRCPREFIX)/$(DIR4)/ClusterAssignDriver.java \
	$(SRCPREFIX)/$(DIR4)/ClusterAssignMapper.java \
	$(SRCPREFIX)/$(DIR4)/ClusterAssignReducer.java \
	$(SRCPREFIX)/$(DATAPOINTDIR)/$(DATAPOINTCLASS)


# Rules to build a particular step
# Build only step 1
canopycenter: step1
	mkdir -p $(BIN)/
	mkdir -p $(BIN)/$(DIR1)/
	mkdir -p $(BIN)/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR1)/*.class $(BIN)/$(DIR1)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class $(BIN)/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C $(BIN)/ .
	rm -r $(BIN)/

# Build only step 2
canopyassign: step2
	mkdir -p $(BIN)/
	mkdir -p $(BIN)/$(DIR2)/
	mkdir -p $(BIN)/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR2)/*.class $(BIN)/$(DIR2)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class $(BIN)/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C $(BIN)/ .
	rm -r $(BIN)/

# Build only step 3
clustercenter: step3
	mkdir -p $(BIN)/
	mkdir -p $(BIN)/$(DIR3)/
	mkdir -p $(BIN)/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR3)/*.class $(BIN)/$(DIR3)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class $(BIN)/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C $(BIN)/ .
	rm -r $(BIN)/

# Build only step 4
clusterassign: step4
	mkdir -p $(BIN)/
	mkdir -p $(BIN)/$(DIR4)/
	mkdir -p $(BIN)/$(DATAPOINTDIR)/
	mv $(SRCPREFIX)/$(DIR4)/*.class $(BIN)/$(DIR4)/
	mv $(SRCPREFIX)/$(DATAPOINTDIR)/*.class $(BIN)/$(DATAPOINTDIR)/
	jar -cvf $(OUTPUTJARNAME) -C $(BIN)/ .
	rm -r $(BIN)/


# Remove $(BIN)/ and .jar files
clean:
	if test -f $(SRCPREFIX)/$(DIR1)/*.class; then rm $(SRCPREFIX)/$(DIR1)/*.class; fi
	if test -f $(SRCPREFIX)/$(DIR2)/*.class; then rm $(SRCPREFIX)/$(DIR2)/*.class; fi
	if test -f $(SRCPREFIX)/$(DIR3)/*.class; then rm $(SRCPREFIX)/$(DIR3)/*.class; fi
	if test -f $(SRCPREFIX)/$(DIR4)/*.class; then rm $(SRCPREFIX)/$(DIR4)/*.class; fi
	if test -f $(SRCPREFIX)/$(DATAPOINTDIR)/*.class; then rm $(SRCPREFIX)/$(DATAPOINTDIR)/*.class; fi
	if test -d $(BIN); then rm -r $(BIN); fi
	if test -d $(DIST); then rm -r $(DIST); fi