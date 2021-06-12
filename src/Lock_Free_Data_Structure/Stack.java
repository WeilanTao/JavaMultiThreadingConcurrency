package Lock_Free_Data_Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Stack {

    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack=new LockFreeStack<>();

        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushT = 2;
        int popT = 2;

        for(int i=0; i<pushT; i++){
            Thread thread=new Thread(()->{
                while(true){
                    stack.push(random.nextInt());
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }
        for(int i=0; i<popT; i++){
            Thread thread=new Thread(()->{
                while(true){
                    stack.pop();
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }

        for(Thread thread: threads){
            thread.start();
        }

        Thread.sleep(10000);

        System.out.println(String.format("%d operations were performed in 10 seconds.", stack.getCounter() ));
    }

    /**
     * Lock free
     *
     * @param <T>
     */
    public static class LockFreeStack<T> {
        private AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> newHeadNode = new StackNode<>(value);

            while (true) {
                StackNode<T> currentNode = head.get();
                newHeadNode.next = currentNode;
                if (head.compareAndSet(currentNode, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }

            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHeadNode = head.get();
            StackNode<T> newHeadNode;

            while (currentHeadNode != null) {
                newHeadNode = currentHeadNode.next;
                if (head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHeadNode = head.get();
                }
            }

            counter.incrementAndGet();

            return currentHeadNode != null ? currentHeadNode.value : null;

        }

        public int getCounter() {
            return counter.get();
        }
    }

    /**
     * With Lock
     *
     * @param <T>
     */
    public static class StandardStack<T> {
        private StackNode<T> head;
        private int counter = 0; //count the operations we perform

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if (head == null) {
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }

    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T val) {
            this.value = val;
        }
    }
}
