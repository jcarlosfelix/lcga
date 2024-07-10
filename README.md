# Life Cycle Genetic Algorithm (LCGA)
 Java Implementation of a nature inspired algorithm, to evolve a population of individuals continuously over time (on the cloud).
 
## Developed using any text editor, compile and run using Command Prompt (Windows) or Terminal (Mac). 
### Download, install and test the latest Java Development Kit (JDK) on your computer.
	
1. Download the latest JDK Development Kit version from [Oracle downloads](https://www.oracle.com/java/technologies/downloads/) 
2. Install JDK in your computer, open the Command Prompt or Terminal and test the installation with "java -version"
3. If an error ocurrs on Windows, you may need to add the Java home directory on the System Environment Variables. Recommended lectures on how to solve this issue: 
[How to set Java path in windows and linux](https://www.geeksforgeeks.org/how-to-set-java-path-in-windows-and-linux/) and [How to set Java home environment variable on windows 10](https://www.codejava.net/java-core/how-to-set-java-home-environment-variable-on-windows-10)

### Recommended steps to build and run the project on Command Prompt (Windows):

1. Clone this repository, and copy-paste to your designated folder. For the following examples we placed it inside the **D: drive**
2. Open the Command Prompt, and move to the folder with the instruction `cd D:\Documents\GitHub\lcga`
3. Compile the source code with the instruction `javac -cp lib/commons-math3-3.6.1.jar -d classes src/lcga/*.java`
4. Test the configuration with the instruction `java -cp "D:\Documents\GitHub\lcga\lib\commons-math3-3.6.1.jar;D:\Documents\GitHub\lcga\classes" lifecycle.Test_Configuration`
5. Test the compilation with the instruction `java -cp "D:\Documents\GitHub\lcga\lib\commons-math3-3.6.1.jar;D:\Documents\GitHub\lcga\classes" lifecycle.Test_BenchFunction`
6. Test the full algorithm with the instruction `java -cp "D:\Documents\GitHub\lcga\lib\commons-math3-3.6.1.jar;D:\Documents\GitHub\lcga\classes" lifecycle.Test_RunAlgorithm`

### Recommended steps to build and run the project on Terminal (Mac):

1. Clone this repository, and copy-paste to your designated folder. For the following examples we placed it inside **the drive**
2. Open the Terminal, and move to the folder with the instruction `cd lcga`
3. Compile the source code with the instruction `javac -cp lib/commons-math3-3.6.1.jar -d classes src/lcga/*.java`
4. Test the configuration with the instruction `java -cp lib/commons-math3-3.6.1.jar:classes lifecycle.Test_Configuration`
5. Test the compilation with the instruction `java -cp lib/commons-math3-3.6.1.jar:classes lifecycle.Test_BenchFunction`
6. Test the full algorithm with the instruction `java -cp lib/commons-math3-3.6.1.jar:classes lifecycle.Test_RunAlgorithm`


## Description.
This branch was created to submit all changes needed to perform the CEC-2017, the Java implementation of the IEEE Congress on Evolutionary Computation (CEC) 2017 benchmark functions, found on "liammcdevitt73/CEC2017".