package Productor_Consumer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MainApplication {
    private static final int N = 10;
    private static final String INPUT_FILE = "./resource/matrices.txt";
    private static final String OUT_FILE = "./resource/matrices_resoult.txt";

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File input = new File(INPUT_FILE);
        File output = new File(OUT_FILE);

        MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer(new FileReader(input), threadSafeQueue);
        MatricesMulyiplierConsumer matricesMulyiplierConsumer = new MatricesMulyiplierConsumer(new FileWriter(output), threadSafeQueue);

        matricesReaderProducer.start();
        matricesMulyiplierConsumer.start();

    }

    /**
     * 消費者
     */
    private static class MatricesMulyiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;

        public MatricesMulyiplierConsumer(FileWriter fileWriter, ThreadSafeQueue threadSafeQueue) {
            this.queue = threadSafeQueue;
            this.fileWriter = fileWriter;
        }

        private static void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%2f", matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }

            fileWriter.write('\n');
        }

        @Override
        public void run() {
            while (true) {
                MatricesPair matricesPair = queue.remove();

                //如果容器是空的， 那麽消費者就不能取東西
                if (matricesPair == null) {
                    System.out.println("No more matrices to read from the queue");
                    break;
                }

                float[][] result = multiplyMatrices(matricesPair.m1, matricesPair.m2);

                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float res[][] = new float[N][N];
            for (int r = 0; r < N; r++) {
                for (int c = 0; r < N; r++) {
                    for (int k = 0; k < N; k++) {
                        res[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }
            return res;
        }

    }

    /**
     * 生產者
     */
    public static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader fileReader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(fileReader);
            this.queue = queue;

        }

        @Override
        public void run() {
            while (true) {
                float[][] m1 = readMetrix();
                float[][] m2 = readMetrix();

                if (m1 == null || m2 == null) {
                    queue.terminate();
                    System.out.println("No more metrices to read. Produce is terminated");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.m1 = m1;
                matricesPair.m2 = m2;
                queue.add(matricesPair);

            }
        }

        private float[][] readMetrix() {
            float[][] matrix = new float[N][N];

            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");

                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }

    }

    /**
     * 緩衝容器
     */
    public static class ThreadSafeQueue {
        private static final int CAPACITY = 5;
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;

        private synchronized void add(MatricesPair matricesPair) {

            while (queue.size() == CAPACITY) {
                // 如果容器滿了， 就無法再往裏面添加； 此時add 就要讓生產者綫程等待
                try {
                    wait();
                } catch (InterruptedException ie) {

                }
            }
            //如果容器沒有滿； 生產者就可以往容器裏面加東西； 並通知消費者
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        private synchronized MatricesPair remove() {
            MatricesPair matricesPair = null;
            while (isEmpty && !isTerminate) {
                //如果容器是空的， 消費者要等待， 此時不能從容器裏面取東西
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //如果容器不爲空， 消費者可以從裏面取東西並返回取出的產品
            if (queue.size() == 1) {
                isEmpty = true;
            }
            if (queue.size() == 0 && isTerminate) {
                return null;
            }

            System.out.println("queue size is " + queue.size());

            matricesPair = queue.remove();
            if (queue.size() == CAPACITY - 1) {
                notifyAll();
            }
            return matricesPair;
        }

        private synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }


    /**
     * 產品
     */
    private static class MatricesPair {
        public float[][] m1;
        public float[][] m2;
    }
}
