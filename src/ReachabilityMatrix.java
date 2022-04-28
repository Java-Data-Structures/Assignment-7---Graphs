import java.util.Arrays;
import java.util.Scanner;

public class ReachabilityMatrix {

    //Vocab Definition:
    //A = Adjacency Matrix, A1 = initial adjacency matrix, A2 = A1 * A1
    //R = Reachability Matrix = A1+A2+A3... + AN
    //All As = 3D array containing all As from A1 to AN.

    private static int[][][] allAs = null;
    private static int[][] A1 = null;
    private static int[][] R = null;

    public static void main(String[] args) {
        boolean running = true;
        do{
            printMenu();
            switch (getValidateUserMenuInput()){
                case 1:
                    getNewA1();
                    break;
                case 2:
                    try{
                        printOutputs();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Terminating program, thank you.");
                    break;
            }
        }while (running);
    }

    //This method will get the A1 from the user and will store it in the class.
    public static void getNewA1(){
        int numNodes = getValidateUserInteger("Please enter the number of nodes: ");
        A1 = new int[numNodes][numNodes];

        //Fill A1 with the  user input.
        for(int i=0;i<numNodes;i++){
            for(int j=0;j<numNodes;j++){
                A1[i][j]=getValidateUserInteger("Enter A1["+i+","+j+"]: ");
            }
        }

        //Update the AllAs as well as R
        findAllAs(A1);
        findR(A1);
        System.out.println();
    }

    //This method will get an integer from the user and returns it back.
    private static int getValidateUserInteger(String prompt){
        boolean testPassed = false;
        int userInputInteger = 0;
        Scanner sc = new Scanner(System.in);
        do{
            System.out.print(prompt);
            try{
                userInputInteger = Integer.parseInt(sc.nextLine());
                if(userInputInteger>=0){ //if the number is actually a valid menu option, return that number.
                    testPassed = true;
                }else{
                    System.out.println("Please enter a positive number");
                }
            }catch (NumberFormatException e){
                System.out.println("Please enter an Integer.");
            }
        }while(!testPassed);
        return userInputInteger;
    }


    //This method prints out the menu for the user.
    public static void printMenu(){
        System.out.println("------MAIN MENU------");
        System.out.println("1. Enter graph data");
        System.out.println("2. Print outputs");
        System.out.println("3. Exit program\n");
    }

    //This method will get and validate the user menu input.
    private static int getValidateUserMenuInput(){
        boolean testPassed = false;
        int userInputInteger = 0;
        Scanner sc = new Scanner(System.in);
        do{
            System.out.print("Enter option number: ");
            try{
                userInputInteger = Integer.parseInt(sc.nextLine());
                if(userInputInteger>=1 && userInputInteger <= 3){ //if the number is actually a valid menu option, return that number.
                    testPassed = true;
                }else{
                    System.out.println("Please enter a number between 1-3.");
                }
            }catch (NumberFormatException e){
                System.out.println("Please enter an Integer.");
            }
        }while(!testPassed);
        return userInputInteger;
    }

    //This method will check to make sure that every single array that we will need is actually created before we start any method that depends on it.
    public static void checkPreReqArrays()throws Exception{
        if(A1 == null){ //Guard clause to make sure there is an actual A1
            throw new Exception("No A1 entered, please enter an A1 to print outputs.");
        }
        if(allAs == null){ //Make sure we have already calculated all the As
            findAllAs(A1);
        }
        if(R == null){ //Make sure we have already calculated the R (Reachability matrix).
            findR(A1);
        }
    }

    //This method will print out all the outputs, calling every single method from 1-10.
    public static void printOutputs()throws Exception{
        System.out.println();
        //Call all 10 methods each one will check if the arrays are made, but it is like that so that so every method can stand on it's own.
        printInputMatrix();
        computeAndPrintReachabilityMatrix();
        printInDegree();
        printOutDegree();
        printNumLoops();
        printNumCyclesLengthN();
        printNumPathsOfLength1();
        printNumPathsLengthNEdges();
        printNumPathsLength1toNEdges();
        printTotalNumCyclesLength1toNEdges();
        System.out.println("--------------------------------------------------\n");
    }

    //This method will print out the input matrix.
    public static void printInputMatrix()throws Exception{
        checkPreReqArrays();
        System.out.println("Input Matrix: ");
        for(int[] nodeOutConnections : A1){ //For every single node out connection for each node in the matrix.
            for(int nodeConnection : nodeOutConnections){ //For every single particular connection that each node has.
                System.out.print(nodeConnection+"\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    //This method will print out the reachability matrix given A1.
    //this method will use a space-for-time tradeoff and will calculate all the As to save time on later calculations.
    public static void computeAndPrintReachabilityMatrix(){
        //Print the reachability matrix to the console.
        System.out.println("Reachability Matrix: ");
        for(int i=0;i<R.length;i++){
            for(int j=0;j<R[i].length;j++){
                System.out.print(R[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    //This method will be used to make a copy of a 2D array.
    //The reason why we do this is that we do not want to preform matrix multiplication passing A1 by reference as we will destroy A1.
    private static int[][] copyArray(int[][] A1){
        int [][] auxiliaryA = new int[A1.length][];
        for(int i=0;i<A1.length;i++){
            int[] newArray  = Arrays.copyOf(A1[i],A1.length); //copy each row of A1 one by one and put it into the auxiliary array.
            auxiliaryA[i] = newArray;
        }
        return auxiliaryA;
    }

    //This method will find R and store it in the class.
    public static void findR(int[][] A1){
        R = new int[A1.length][A1.length];
        //Add up all the As from the 3D matrix.
        for(int[][] A : allAs){
            for(int i=0;i<A.length;i++){
                for(int j=0;j<A[i].length;j++){
                    R[i][j] += A[i][j];
                }
            }
        }
    }


    //This helper method will call the findAllAs method and return a 3D array that contains all of the As: A1,A2,A3...AN
    public static void findAllAs(int[][] A1){
        int[][] auxiliaryA =  copyArray(A1); //This will make a copy of A1 initially, so we can start the multiplication.
        allAs = new int[A1.length][A1.length][A1.length]; //Create a 3D matrix containing all As from A1 to AN
        allAs[0] = A1; //Initialize the first cell as A1 as you will always have A1 here no matter what.

        allAs = findAllAs(allAs,auxiliaryA,A1,A1.length,1);
    }

    //This recursive method will find all As and store them into a 3d Array A1 will be in cell 0 of the 3D array, AN will be stored in (n-1) of the 3D array.
    //This method will stop once it reaches AN where N is the number of nodes that are in the graph.
    //auxiliaryA is the next A that we have received from the last recursive call Ex: auxiliaryA would be A2 if we did A1 * A1 in the last call.
    //currentA is the currentA that we are on, this is used to check that we do not go over AN and preform redundant calculations.
    private static int[][][] findAllAs(int[][][] allAs,int[][] auxiliaryA,int[][]A1,int numNodes,int currentA){
        if (currentA >= numNodes) { //Base case: If the currentA is matching the num nodes, we can stop as there is no point in searching any further.
            return allAs;
        } else {
            //Find the next A and pass it through.
            int[][] nextA = findNextA(auxiliaryA,A1);
            //Add the A to the 3D matrix.
            allAs[currentA] = nextA;

            //recall the method until you get to AN, and ultimately return R back so that we do not have to recalculate.
            return findAllAs(allAs,nextA,A1,numNodes,++currentA);
        }
    }

    //This method will find the next A given an A as well as A1, Ex: if given A2 and A1, it will find A3.
    private static int[][] findNextA(int[][] auxiliaryA, int[][] A1){
        //Create a new 2d array to store the resulting matrix multiplication.
        //This value will be passed into the recursive call later to become auxiliaryA in the next recursive call.
        int[][] nextA = new int[auxiliaryA.length][auxiliaryA[0].length];

        //Preform matrix multiplication.
        for(int i=0;i<auxiliaryA.length;i++){
            for(int j=0;j< auxiliaryA[i].length;j++){
                for(int k=0;k< A1.length;k++){
                    nextA[i][j] +=  auxiliaryA[i][k] * A1[k][j];
                }
            }
        }

        return nextA;
    }

    //This method will find the number of nodes coming into a specified node (including itself) and prints it out.
    private static void printInDegree()throws Exception{
        checkPreReqArrays();
        System.out.println("In-degree");
        for(int i=0;i<A1[0].length;i++){ //We know we will not have a jagged array.
            int inDegree = 0;
            for(int j=0;j<A1.length;j++){
                inDegree += A1[j][i];
            }
            System.out.println("Node "+(i+1)+" in-degree is "+inDegree);
        }
        System.out.println();
    }

    //This method will find the number of nodes that a specified node connects to (including itself) and prints it out.
    private static void printOutDegree()throws Exception{
        checkPreReqArrays();
        System.out.println("Out-degree:");
        for(int i=0;i< A1.length;i++){
            int outDegree = 0;
            for(int j=0;j< A1[i].length;j++){
                outDegree+= A1[i][j]; //Find the out degree for each node by going horizontally.
            }
            System.out.println("Node "+(i+1)+" out-degree is "+outDegree); //Print out each node out degree (+1 since we're starting array at 0).
        }
        System.out.println();
    }

    //This method will find the number of self loops in the graph by looking across the diagonal in A1.
    public static void printNumLoops()throws Exception{
        checkPreReqArrays();
        int numLoops = 0;
        for(int i=0;i< A1.length;i++){
            numLoops+=A1[i][i];
        }
        System.out.println("Total number of self-loops is "+numLoops);
    }

    //This method will get AN and get all cycles for it by adding all the values diagonally.
    private static void printNumCyclesLengthN()throws Exception{
        checkPreReqArrays();
        int cycles = 0;

        int[][] A = allAs[A1.length-1];//This will find the A for the given length.

        for(int i=0;i< A.length;i++){
            cycles+=A[i][i];
        }

        System.out.println("Total number cycles of length "+A1.length+" edges: "+cycles);
    }


    //This method will print out all paths on A1. This is done by adding up all the values in A1.
    public static void printNumPathsOfLength1()throws Exception{
        checkPreReqArrays();
        int numPaths =0;
        for(int i=0;i< A1.length;i++){
            for(int j=0;j< A1[i].length;j++){
                numPaths+=A1[i][j];
            }
        }
        System.out.println("Total number of paths of length 1 edge: "+numPaths);
    }

    //This method will print out all the paths on AN. This is done by adding up all the values in AN.
    public static void printNumPathsLengthNEdges()throws Exception{
        checkPreReqArrays();
        int numPaths = 0;

        int[][] A = allAs[A1.length-1];//This will find the A for the given length.

        for(int i=0;i< A.length;i++){
            for(int j=0;j< A1[i].length;j++){
                numPaths+=A[i][j];
            }
        }

        System.out.println("Total number of paths of length "+A1.length+" edges: "+numPaths);
    }

    //This method will add up all the values in the Reachability Matrix and print it.
    public static void printNumPathsLength1toNEdges()throws Exception{
        checkPreReqArrays();
        int totalNumPaths = 0;
        for(int i=0;i<R.length;i++){
            for(int j=0;j<R.length;j++){
                totalNumPaths+=R[i][j];
            }
        }

        System.out.println("Total number of paths of length 1 to "+A1.length+" edges: "+totalNumPaths);
    }

    //This method will print out the total number of cycles from the Reachability Matrix by adding all the values diagonally.
    public static void printTotalNumCyclesLength1toNEdges()throws Exception{
        checkPreReqArrays();
        int totalNumCycles = 0;
        for(int i=0;i<R.length;i++){
            totalNumCycles+= R[i][i];
        }
        System.out.println("Total number of cycles of length 1 to "+A1.length+" edges: "+totalNumCycles);
    }

}
