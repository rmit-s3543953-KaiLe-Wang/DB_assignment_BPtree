What is it?
- This is my assignment work for a database course, the object is to compare and analysis the performance between different type of databases.
- 3 part of analysis: 1. use Derby database as relational database 2. use MongoDB as non-SQL database 3. use my own B+ tree indexing database that based on JAVA.
- Note that the environment for testing was in AWS EC2 instance which was running UNIX. The instruction below was only suitable for that environment.

---------------------------------------------------------------------------------------------------
NOTE: ALL BASH FILE NEEDS TO BE CONVERT TO UNIX FILE FORMAT AND "CHMOD 700"
1.VIM XXX.SH
2.:set ff:unix
3.:wq
---------------------------------------------------------------------------------------------------
PART1:
- HOW TO USE:
	1.USE ECLIPSE TO IMPORT "db_a2_part1.zip" AS AN EXSISTING WORKSPACE PROJECT
	2. ONCE IT IS OPENED, EXPORT RUNNABLE JAR FILES ACOORDINGLY.
		2.a CHOOSE "DRIVER" AS MAIN CLASS FOR MY IMPLEMENTATION OF DERBY LOAD.
		2.b CHOOSE "DB_TEST" AS MAIN CLASS FOR MY QUERY TEST.
		2.c OR SIMPLY USE THE JAR FILE PROVIDED IN THE ZIP.
	3. UPLOAD THESE JAR FILES, ALSO "PART1_SPLIT.SH" TO AWS [OPTIONAL]
	4. RUN "./PART1_SPLIT.SH YOUR_CSV_FILE_NAME.CSV" TO SPLIT THE FILE INTO SMALL PIECES.
	5. MOVE SPLITTED FILES INTO A FOLDER, AND REMOVE THE BASH AND ORIGINAL CSV FILE OUT.
	6. RUN "JAVA -JAR YOUR_JAR_FILE_NAME SPLITTED_FILES_FOLDER_NAME"
	7. TEST THE DATABASE BY USING "JAVA -JAR TEST.JAR derbyA2Test"
		7.a OR TEST ASSIGNMENT 1 BY TYPING "JAVA -JAR TEST.JAR derbyA1Test" 'derbyA1Test' IS THE ASSIGNMENT 1 DATABASE.
---------------------------------------------------------------------------------------------------
PART2:
- HOW TO USE:
	1. PUT "PART2.SH","IMPORT.SH","ADD_HEADER_AND_SPLIT.SH" ALSO DATA FILE TOGETHER IN THE SAME FOLDER.
	2. RUN "PART2.SH", IT WILL SPLIT CSV FILES LIKE PART1.SH BUT IT WILL ALSO IMPORT TO MONGO AND PRESENT THE TIME.
	3. DONE. CHANGE "*.SH" PERMISSION IF NEEDED
-----------------------------------------------------------------------------------------------------
PART3:
- HOW TO USE:
	1. RUN "JAVAC -Xlint *.JAVA", because there are some unsafe cast in b plus node class.
	2. RUN DBLOAD AS SAME IN ASSIGNMENT 1: "JAVA DBLOAD -P PAGESIZE DATAFILE"
	3. RUN 'Java TreeSave pagesize'
	4. RUN 'Java Treequery query1 pagesize tree_file_name'
		4.a OR RUN 'Java Treequery query1 query2 pagesize tree_file_name linked_list_file_name' FOR RANGE SEARCH.