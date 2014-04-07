#! /usr/bin/env bash

# Base folder for input and output files in HDFS
export BASEFOLDER=`grep 'BASEFOLDER' cc.properties | awk '{ print $3 }'`

# Name of the JAR file to be run
export JARFILE=dist/`grep 'JARFILE' cc.properties | awk '{ print $3 }'`

# Input Data Set
export DATASET=$BASEFOLDER/input/`grep 'DATASETFILE' cc.properties | awk '{ print $3 }'`

# Inital file containing k-Means Centroids
export KCENTROIDSFILE=$BASEFOLDER/input/`grep 'CENTROIDSFILE' cc.properties | awk '{ print $3 }'`

export CANOPYCENTERSFOLDER=$BASEFOLDER/output1
export CANOPYASSIGNFOLDER=$BASEFOLDER/output2
export CLUSTERCENTERFOLDER=$BASEFOLDER/output3
export CLUSTERASSIGNFOLDER=$BASEFOLDER/output4

export CANOPYCENTERSFILE=$CANOPYCENTERSFOLDER/part-r-00000
export CANOPYASSIGNFILE=$CANOPYASSIGNFOLDER/part-r-00000
export CLUSTERCENTERFILE=$CLUSTERCENTERFOLDER/part-r-00000
export CLUSTERASSIGNFILE=$CLUSTERASSIGNFOLDER/part-r-00000

# Clean the folder
hadoop dfs -rmr $BASEFOLDER/output*

# CanopyCenter 
# Parameters: <Data Set> <Output Folder>
hadoop jar $JARFILE `grep 'CANOPYCENTERDIR' cc.properties | awk '{ print $3 }'`.CanopyCenterDriver $DATASET $CANOPYCENTERSFOLDER

# CanopyAssign
# Parameters: <Data Set> <Canopy Centers File> <Output Folder>
hadoop jar $JARFILE `grep 'CANOPYASSIGNDIR' cc.properties | awk '{ print $3 }'`.CanopyAssignDriver $DATASET $CANOPYCENTERSFILE $CANOPYASSIGNFOLDER

# ClusterCenter
# Parameters: <Canopy Assign File> <Canopy Centers File> <k-Means Centroids File> <Output Folder>
time hadoop jar $JARFILE `grep 'CLUSTERCENTERDIR' cc.properties | awk '{ print $3 }'`.ClusterCenterDriver $CANOPYASSIGNFILE $CANOPYCENTERSFILE $KCENTROIDSFILE $CLUSTERCENTERFOLDER

# ClusterAssign
# Parameters: <Data Set> <k-Means Centroids File> <Output File>
hadoop jar $JARFILE `grep 'CLUSTERASSIGNDIR' cc.properties | awk '{ print $3 }'`.ClusterAssignDriver $DATASET $CLUSTERCENTERFILE $CLUSTERASSIGNFOLDER
