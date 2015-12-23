5688043 Nut	  Janekitiworapong	1	nutjane131@gmail.com
5688076 Rata	  Kittipol		1	eeye_@hotmail.com
5688112 Shotirose Poramesanaporn	1	mp.shotirose@hotmail.com
5688172 Kamon	  Tuanghirunvimon	1	kamontuang@gmail.com

How to configure program?
- Through Eclipse program -
1. Open Eclipse for Java program
2. At menu bar, click at File >> Import
3. Choose General then click at the small triangle symbol and select Existing Projects into Workspace and click next
4. Tick at Select root directory and enter the path of the project then click open
5. At projects text area, select all
6. Click finish




How to compile the program?

1 At project explorer tab, choose our project and click the small triangle symbol and so do at the default package then double click at ‘Main.java’ file

2. Compile by clicking at project on menu bar then select Build Project



How to run the program?

1. At the tool bar, you can select ‘run Main’ to run the program
2. Then, you can type the query through a console (If the console panel didn't appear, you can get it from menubar; Windows>>Show view>>Console)
3. There are 4 main functions in our program which are create, update, save and search;


————————————-create——————————————

In order to create any index file, this format has to be typed in

Format: create <index> from <FILES> 
where 	
	<index> is the name of the index file 
	
	<FILES> is a list of names of text files that you want to create an index

For example, “create index from doc1 doc2 doc20”

This means that you are creating a file named index which consists of list of words comes from files named doc1 doc2 and doc20.



————————————-update——————————————

In order to update any index file, this format has to be typed in

Format: update <index> from <FILES> 
where 	
	<index> is the name of the index file that you want to update
	
	<FILES> is a name(s) of text file(s) that you want the index to update

For example, “update index from doc2”

This means that you have updated file named doc2; therefore, you want to update the index that you have already created.






————————————-save——————————————

In order to save any index file, this format has to be typed in

Format: save <index>
 where 	
	<index> is the name of the index file that you want to save or else the created or updated index would be lost after closing the program.

For example, “save index”

This means that file named index will be saved in the folder of project’s file.

Note that the saved name must be the same as the file that has already been created or else the error message will be shown.

————————————-search——————————————

In order to search any index file, this format has to be typed in

Format: search [Boolean_Query]
where 	
	[Boolean_Query] is the word(s) that you want to search which is written in the form of boolean query with & or | or ~ notation.

For example, “search [aaron& alternative]”

This means that you want to know the word “aaron” and “alternative” are occurred in which document(s).

Note that the search algorithm will be applied on the index file named “index” only and the program will retrieve index from index.text file.