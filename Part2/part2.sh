#!/bin/bash

if [ "$1" != '' ]
then
	chmod 700 add_header_and_split.sh
	chmod 700 import.sh
	echo "adding header to data file and splitting records for faster load"
	time ./add_header_and_split.sh "$1"
	echo "created data files"
	echo "dropping database"
	mongo a2 -eval "db.dropDatabase()"
	echo "importing files into mongodb"
	time ./import.sh
	echo "DONE!"
	echo "Create secondary index"
	mongo a2 -eval "db.a2.createIndex({"DeviceId": "-1"})"
	mongo a2 -eval "db.a2.createIndex({"Area":"-1"})"
	mongo a2 -eval "db.a2.createIndex({'Street.StreetName':"-1"})"
	
	echo "Create secondary index complete!"
else
	echo "Usage: bash part2.sh input_csv_file"
fi
