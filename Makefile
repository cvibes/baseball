all:	BaseballElimination.java
	checkstyle-algs4 BaseballElimination.java
	javac-algs4 BaseballElimination.java
run:	BaseballElimination.class
	java-algs4 BaseballElimination baseball/teams4.txt
