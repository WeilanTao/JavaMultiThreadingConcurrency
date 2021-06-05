package ThreadTermination;

import java.math.BigInteger;

public class main {
    public static void main(String[] args) {
        Thread t=new Thread(new BlockingTask());
        t.setName("HelloThread");
        t.start();
        t.interrupt();



        Thread thread=new Thread(new LongComputationTask(new BigInteger("2000000"),new BigInteger("1000000")));

        thread.setName("hiThread");
        thread.start();
        thread.interrupt();
    }

    public static class BlockingTask implements Runnable{
        @Override
        public void run(){
            try{
                Thread.sleep(100000);

            }catch(InterruptedException exception){
                //As a rule of thumb, never leave a catch block empty,
                System.out.println("Exiting blocking thread");
            }
        }
    }

    public static class LongComputationTask implements Runnable{
        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power){
            this.base=base;
            this.power=power;
        }

        @Override
        public void run(){

            System.out.println(base+" ^ "+power+" = "+ pow(base,power));

        }

        private BigInteger pow(BigInteger base, BigInteger power){
            BigInteger result =BigInteger.ONE;

            for(BigInteger i =BigInteger.ZERO; i.compareTo(power) !=0; i=i.add(BigInteger.ONE) ){

                //If the method does not respond to the interrupt signal by throwing InterruptException, we need to check for that signal and handle it ourselves
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }

                result=result.multiply(base);
            }

            return result;
        }
    }
}

