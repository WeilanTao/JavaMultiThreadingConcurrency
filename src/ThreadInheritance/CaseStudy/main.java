package ThreadInheritance.CaseStudy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class main {
    public static final int MAX_PASSWORD=999;
    public static void main(String[] args) {
        Random random=new Random();
        Vault vault=new Vault(random.nextInt(MAX_PASSWORD));
        List<Thread> threads=new ArrayList<>();

        //Polymorphism--We can treat them all as threads regardless of their concrete type
        threads.add(new AssendingHacker(vault));
        threads.add(new DesendingHacker(vault));
        threads.add(new PoliceThread());

        for(Thread thread : threads){
            thread.start();
        }




    }

    public static class Vault{
        private int password;
        public Vault(int password){
            this.password=password;
        }

        public boolean isCorrectPassword(int guess){
            try {
                Thread.sleep(5);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            return guess==this.password;
        }
    }

    private static abstract class HackerThread extends Thread{
        protected Vault vault;

        public HackerThread(Vault vault){
            this.vault=vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start(){
            System.out.println("Starting thread: "+this.getName());
            super.start();
        }

    }

    private static class AssendingHacker extends HackerThread{
        public  AssendingHacker(Vault vault){
            super(vault);
        }

        @Override
        public void  run(){
            for(int i =0; i<MAX_PASSWORD;i++){
                if(vault.isCorrectPassword(i)){
                    System.out.println(this.getName()+" guessed the password "+ i);
                    System.exit(0);

                }
            }

        }
    }

    private static class DesendingHacker extends HackerThread{
        public  DesendingHacker(Vault vault){
            super(vault);
        }

        @Override
        public void  run(){
            for(int i =MAX_PASSWORD; i>=0;i--){
                if(vault.isCorrectPassword(i)){
                    System.out.println(this.getName()+" guessed the password "+ i);
                    System.exit(0);

                }
            }

        }
    }

    private static class PoliceThread extends Thread{
        @Override
        public void run() {
            System.out.println("start police");
            for (int i = 10; i > 0; i--) {
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(i);
            }
            System.out.println("Gameover for your hackers");
            System.exit(0);
        }
    }
}
