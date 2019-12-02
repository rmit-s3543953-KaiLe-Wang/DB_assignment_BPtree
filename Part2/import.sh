#!/bin/bash
for d in sample_*.*
do
	mongoimport -d a2 -c park --type csv --file $d --headerline
done
