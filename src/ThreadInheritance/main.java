package ThreadInheritance;

import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        Thread thread=new NewThread();
        thread.setName("Thread");
        Thread thread1=new NewThread();
        thread1.setName("1Thread");
        Thread thread2=new NewThread();
        thread2.setName("2Thread");

        //設置了priority之後 thread 運行時間就會固定；否則thread運行時間不確定
//        thread1.setPriority(Thread.MAX_PRIORITY);
//        thread2.setPriority(Thread.MAX_PRIORITY);


        List<Thread> threadList=new ArrayList<>();
        threadList.add(thread);
        threadList.add(thread1);
        threadList.add(thread2);

        for(Thread t :threadList){
            t.start();
        }


        Thread t=new Thread(new Task());
        t.start();

    }

    private static class NewThread extends Thread{
        @Override
        public void run() {
            System.out.println("Hello from the thread: " + this.getName());

        }
    }

    private static class Task implements Runnable{
        @Override
        public  void run(){
            System.out.println("Hello from the runnable-thread!");
        }
    }
}
