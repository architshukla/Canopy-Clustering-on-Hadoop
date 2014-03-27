#! /usr/bin/env python

import sys
import random

# Change this according to the Data Set
OFFSET_OF_ATTRIBUTE_TO_SORT_ON=1

def getOneRecord():
	"""
		Function to generate one record of the dummy Data Set.
		Needs to be modified as per the required Data Set.
	"""
	year = random.randint(1000,2000)
	temp = random.randint(32, 131)
	return [year, temp]

def gen():
	"""
		Function to generate the dummy Data Set and k-Means Centroids
		
		Usage
		-----
			./gen.py [--centroids|-c] <Number of samples>
		If --centroids is passed, the generated points are sorted in a specific order
	"""
	# Check for arguments
	if len(sys.argv) < 2:
		print("Usage: ./gen.py [--centroids|-c] <Number of samples>")
		sys.exit()

	# Check if centroids need to be generated
	if sys.argv[1].lower() == "--centroids" or sys.argv[1].lower() == "-c":
		# List to hold centroids
		centroids=[]
		for i in range(int(sys.argv[2])):
			attributes = getOneRecord()
			centroids.append(attributes)
		# Sort Centroids
		centroids = sorted(centroids, key=lambda centroids: centroids[OFFSET_OF_ATTRIBUTE_TO_SORT_ON])
		# Output Centroids
		for centroid in centroids:
			print ",".join(map(str, centroid))
	else:
		# Print randomly generated Data Set
		for i in range(int(sys.argv[1])):
			attributes = getOneRecord()
			print ",".join(map(str, attributes))

if __name__ == "__main__":
	"""
		Main function.
		Calls gen() to generate a dummy Data Set."
	"""
	gen()