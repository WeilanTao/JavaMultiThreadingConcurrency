package ThreadCoordination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {
    public static void main(String[] args) throws InterruptedException {

        /**
         * Here we want to calculate the factorials of the numbers in parallel
         *
         * Factorial calculation (is CPU instensive task)
         */
        List<Long> inputNumbers = Arrays.asList(10000000000L, 3435L, 35435L, 2324L, 4656L, 23L, 2435L, 5566L);

        List<FactorialThread> threads = new ArrayList<>();

        for (long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }

        for(Thread thread: threads){
            thread.setDaemon(true);
            thread.start();
        }

        //To solve the RACE CONDITION between the main thread and the Factorial Threads:
        //by adding the join(), we force the main method to wait till all the factorial threads are finished
        for(Thread thread:threads){
            //the thread joined method will only return only when that thread has terminated
            thread.join(2000); //If after two seconds a thread hasn't terminated then the join method will return.

            //by the time the main thread finished the loop, all the factorial threads are guaranteed to have finished.

        }

        for (int i = 0; i < inputNumbers.size(); i++){
            FactorialThread factorialThread=threads.get(i);
            if(factorialThread.isFinished()){
                System.out.println("Factorial of "+ inputNumbers.get(i)+" is "+ factorialThread.getResult());

            }else {
                System.out.println("The calculation for "+ inputNumbers.get(i)+" is still in progress");

            }
        }
    }

    public static class FactorialThread extends Thread {
        private long inputNumber;
        private BigInteger result = BigInteger.ZERO;
        private boolean isFinished = false;

        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        public BigInteger factorial(long n) {
            BigInteger temp = BigInteger.ONE;
            for (long i = n; i > 0; i--) {
                temp = temp.multiply(new BigInteger(Long.toString(i)));
            }
            return temp;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }

    }
}
