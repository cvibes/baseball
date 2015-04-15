all:	BaseballElimination.java
	checkstyle-algs4 BaseballElimination.java
	javac-algs4 BaseballElimination.java
run:	BaseballElimination.class
	java-algs4 BaseballElimination baseball/teams5.txt Boston
zip:	BaseballElimination.java FFPlus.java
	if [ -e baseball.zip ]; then rm baseball.zip; fi
	zip -r baseball.zip BaseballElimination.java FFPlus.java
