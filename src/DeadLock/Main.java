package DeadLock;

import java.util.Random;

/**
 * 死鎖：多個綫程互相抱著對方需要的資源， 然後形成僵持
 */
public class Main {
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

    /**
     * trainAThread拿到了roadA 的鎖（roadA的單獨使用權限）并休息1000ms
     * 此時trainBThread 拿到了roadB 的鎖 （roadB的單獨使用權限） 并休息2000ms
     * 此時trainAThread再想拿roadB 的鎖； 但因爲roadB的鎖/ 權限在trainBThread手上 所以trainAThread綫程無法結束 而無法釋放 roadA 的鎖
     * 此時trainBThread 想拿 roadA 的鎖 同理也無法拿到
     * 此時程序卡住
     */
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
            synchronized (roadB){
                System.out.println("Road B is locked by thread"+Thread.currentThread().getName());
                Thread.sleep(2000);

                synchronized (roadA){
                    System.out.println("Train is passing road B");
                }
            }
        }
    }
}

