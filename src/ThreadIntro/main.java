package ThreadIntro;

public class main {
    public static void main(String[] args) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                //code that will run in a new thread as soon as it's scheduled by the OS
                System.out.println("We are in thread: "+Thread.currentThread().getName());
                System.out.println("Current thread priority is "+Thread.currentThread().getPriority());

                throw new RuntimeException("Intentional Exception");
            }
        });

        thread.setName("New Worker Thread");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("AtomicInteger critical error happened in trhead"+t.getName()
                +"the error is "+e);
            }
        });


        thread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("We are in thread: "+Thread.currentThread().getName()+" before staring a new program");
        thread.start();
        System.out.println("We are in thread: "+Thread.currentThread().getName()+" after staring a new program");

        try {
            Thread.sleep(1000); //the sleep method insturcts the OS to not schedule the current till that time passes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
