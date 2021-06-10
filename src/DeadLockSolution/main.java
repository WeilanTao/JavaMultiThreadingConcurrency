package DeadLockSolution;

import java.util.Random;


public class main {
    public static void main(String[] args) {
        Intersection intersection =new Intersection();
        Thread trainAThread=new Thread(new TrainA(intersection));
        Thread trainBThread=new Thread(new TrainB(intersection));

        trainAThread.start();
        trainBThread.start();

    }

    public static class TrainA implements Runnable{
        private Intersection intersection;
        private Random random=new Random();

        public TrainA(Intersection intersection){
            this.intersection=intersection;
        }

        @Override
        public void run(){
            while(true){
                long sleepingTime=random.nextInt(5);

                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    intersection.takeRoadA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class TrainB implements Runnable{
        private Intersection intersection;
        private Random random=new Random();

        public TrainB(Intersection intersection){
            this.intersection=intersection;
        }

        @Override
        public void run(){
            while(true){
                long sleepingTime=random.nextInt(5);

                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    intersection.takeRoadB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class Intersection{
        private Object roadA=new Object();
        private Object roadB=new Object();

        public void takeRoadA() throws InterruptedException {
            synchronized (roadA){
                System.out.println("Road A is locked by thread "+ Thread.currentThread().getName());
                Thread.sleep(1000);

                synchronized (roadB){
                    System.out.println("Train is passing though road A");
                }
            }
        }

        public void takeRoadB()throws InterruptedException{
            synchronized (roadA){
                System.out.println("Road B is locked by thread"+Thread.currentThread().getName());
                Thread.sleep(2000);

                synchronized (roadB){
                    System.out.println("Train is passing road B");
                }
            }
        }
    }
}