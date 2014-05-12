#! /usr/bin/env bash

# Base folder for input and output files in HDFS
BASEFOLDER=`grep 'BASEFOLDER' cc.properties | awk '{ print $3 }'`

# Name of the JAR file to be run
JARFILE=dist/`grep 'JARFILE' cc.properties | awk '{ print $3 }'`

# Input Data Set
DATASET=$BASEFOLDER/input/`grep 'DATASETFILE' cc.properties | awk '{ print $3 }'`

# Inital file containing k-Means Centroids
KCENTROIDSFILE=$BASEFOLDER/input/`grep 'CENTROIDSFILE' cc.properties | awk '{ print $3 }'`

# Package Prefix
PKGPREFIX=`grep 'PKGPREFIX' cc.properties | awk '{ print $3 }'`

# Output folders in HDFS
CANOPYCENTERSFOLDER=$BASEFOLDER/output1
CANOPYASSIGNFOLDER=$BASEFOLDER/output2
CLUSTERCENTERFOLDER=$BASEFOLDER/output3
CLUSTERASSIGNFOLDER=$BASEFOLDER/output4

# Output part files
CANOPYCENTERSFILE=$CANOPYCENTERSFOLDER/part-r-00000
CANOPYASSIGNFILE=$CANOPYASSIGNFOLDER/part-r-00000
CLUSTERCENTERFILE=$CLUSTERCENTERFOLDER/part-r-00000
CLUSTERASSIGNFILE=$CLUSTERASSIGNFOLDER/part-r-00000

# Clean the folder
hadoop dfs -rmr $BASEFOLDER/output*

# CanopyCenter 
# Parameters: <Data Set> <Output Folder>
hadoop jar $JARFILE $PKGPREFIX/`grep 'CANOPYCENTERDIR' cc.properties | awk '{ print $3 }'`.CanopyCenterDriver $DATASET $CANOPYCENTERSFOLDER

# CanopyAssign
# Parameters: <Data Set> <Canopy Centers File> <Output Folder>
hadoop jar $JARFILE $PKGPREFIX/`grep 'CANOPYASSIGNDIR' cc.properties | awk '{ print $3 }'`.CanopyAssignDriver $DATASET $CANOPYCENTERSFILE $CANOPYASSIGNFOLDER

# ClusterCenter
# Parameters: <Canopy Assign File> <Canopy Centers File> <k-Means Centroids File> <Output Folder>
time hadoop jar $JARFILE $PKGPREFIX/`grep 'CLUSTERCENTERDIR' cc.properties | awk '{ print $3 }'`.ClusterCenterDriver $CANOPYASSIGNFILE $CANOPYCENTERSFILE $KCENTROIDSFILE $CLUSTERCENTERFOLDER

# ClusterAssign
# Parameters: <Data Set> <k-Means Centroids File> <Output File>
hadoop jar $JARFILE $PKGPREFIX/`grep 'CLUSTERASSIGNDIR' cc.properties | awk '{ print $3 }'`.ClusterAssignDriver $DATASET $CLUSTERCENTERFILE $CLUSTERASSIGNFOLDER
