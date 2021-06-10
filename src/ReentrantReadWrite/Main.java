package ReentrantReadWrite;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    private static final int Highest_PRICE = 1000;
    public static void main(String[] args) {
        InventoryDB inventoryDB=new InventoryDB();

        Random random=new Random();

        for(int i =0; i<100000; i++){
            inventoryDB.addItem(random.nextInt(Highest_PRICE));
        }

        Thread writer =new Thread(()->{
            while(true) {
                inventoryDB.addItem(random.nextInt(Highest_PRICE));
                inventoryDB.removeItem(random.nextInt(Highest_PRICE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                }
            }
        });
        writer.setDaemon(true);
        writer.start();
        int numberOfReaderThread = 7;
        List<Thread> readers=new ArrayList<>();

        for (int i =0; i<numberOfReaderThread; i++){
            Thread t=new Thread(()->{
                int highBound=random.nextInt(100000);
                int lowBound=highBound>0?random.nextInt(highBound):0;

                inventoryDB.getNumberOfItemsInPriceRange(lowBound,highBound);

            });
            //we don't sleep between the read operations and we make the read operation as fast as possible
            t.setDaemon(true);
            readers.add(t);
        }

        long startReadingT=System.currentTimeMillis();
        for(Thread t:readers){
            t.start();
        }
        for(Thread t:readers){
            try {
                t.join();
            }catch (InterruptedException ie){
            }
        }
        long endReadingT=System.currentTimeMillis();
        System.out.println(String.format("Reading took %d ms", endReadingT-startReadingT));

    }

    public static class InventoryDB{
        private TreeMap<Integer,Integer> priceToCountMap=new TreeMap<>();
        private ReentrantLock lock=new ReentrantLock();
        private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
        private Lock readLock=rwlock.readLock();
        private Lock writeLock=rwlock.writeLock();


        public int getNumberOfItemsInPriceRange(int loweBound, int upperBound){
            readLock.lock();
            try {

                Integer fromKey = priceToCountMap.ceilingKey(loweBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;

                for (int numberOfItemForPrice : rangeOfPrices.values()) {
                    sum += numberOfItemForPrice;
                }
                return sum;
            }finally {
                readLock.unlock();
            }
        }

        public void addItem(int price){
            writeLock.lock();
            try{
                priceToCountMap.put(price, priceToCountMap.getOrDefault(price,0)+1);
            }finally {
                writeLock.unlock();
            }

        }

        public void removeItem(int price){
            writeLock.lock();
            try{
                Integer numberOfItemForPrice=priceToCountMap.get(price);
                if(numberOfItemForPrice==null){
                    priceToCountMap.remove(price);
                }else{
                    priceToCountMap.put(price, numberOfItemForPrice-1);
                }
            }finally {
                writeLock.unlock();
            }

        }

    }
}
