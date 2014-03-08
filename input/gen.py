#! /usr/bin/env python

import random
import sys

if len(sys.argv) == 1:
	print("Usage: ./gen.py <Number of samples>")
	sys.exit()

for i in range(int(sys.argv[1])):
	yearFirst = random.randint(10,20) 
	yearSecond = random.randint(0, 99)
	if yearSecond < 10:
		year = str(yearFirst) + "0" + str(yearSecond)
	else:
		year = str(yearFirst) + str(yearSecond)
	temp = random.randint(32, 131)
	print(year + "," + str(temp)) 
