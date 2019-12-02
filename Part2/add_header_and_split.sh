#!/bin/bash

if [ "$1" != '' ]
then
	tail -n +2 "$1" | split -a 1 -d -l 500000 --additional-suffix=.csv - sample_
	for d in sample_*
	do
		head -n 1 "$1" | awk -v FS=',' '{print $1","$2","$3","$4","$5","$6","$7",Street."$8",Street."$9",Street."$10",Street."$11",Street."$12","$13;}' > tmp
		cat $d >> tmp
	   	mv -f tmp $d
	done
else
	echo "Usage: ./add_header_and_split.sh input_csv_file"
fi
