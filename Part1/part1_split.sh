#!/bin/bash

if [ "$1" != '' ]
then
	tail -n +2 "$1" | split -a 1 -d -l 500000 --additional-suffix=.csv - sample
	for d in sample*
	do
		head -n 1 "$1" | awk -v FS=',' '{print $1","$2","$3","$4","$5","$6","$7","$8","$9","$10","$11","$12","$13;}' > tmp
		cat $d >> tmp
	   	mv -f tmp $d
	done
else
	echo "Usage: ./part1_split.sh input_csv_file"
fi
