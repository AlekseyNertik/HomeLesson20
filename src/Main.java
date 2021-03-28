public class Main {
    private static final Object monitor = new Object();
    static volatile char c = 'A';

    static class WaitNotify implements Runnable{
    private char current, next;

    public WaitNotify (char current, char next){
        this.current=current;
        this.next=next;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
//            System.out.print(i); почему тут получается 1A21B21C2A3B3C3A4B4C4A5B5C5ABC?
            synchronized (monitor){
                try {
                    while (c!=current) monitor.wait();
                    System.out.print(current);
                    c=next;
                    monitor.notifyAll();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

    static class MFU {
        Object lock1 = new Object();
        Object lock2 = new Object();

        public void print(String doc, int n){
            synchronized (lock1){
                System.out.println("Начинается печать документа: "+doc+" в количестве "+n+" страниц");
                for (int i = 0; i < n; i++) {
                    System.out.println("Документ "+doc+" cтраница "+ (i+1) +" печатается.");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }System.out.println("=========== Печать документа "+doc+" завершена. ===========");
            }
        }
        public void scan(String doc, int n){
            synchronized (lock2){
                System.out.println("Начинается сканирование документа: "+doc+"  ");
                for (int i = 0; i < n; i++) {
                    System.out.println("Документ "+doc+" cтраница "+ (i+1) +" сканируется...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }System.out.println("=========== Сканирование документа "+doc+" завершено. ==========");
            }
        }

    public void copy(String doc, int n){
        synchronized (lock1){
            System.out.println("------------Начинается копирование документа: ---------");
            for (int i = 0; i < n; i++) {
                System.out.println("Страница "+ (i+1) +" документа  копируется...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("================Копирование документа  завершено.===============");
    }
    }

    public static void main(String[] args) {

// 1. Задача. Вывести 5 раз ABC
        System.out.println("1. задача. Вывести ABC 5 раз.");

       Thread t1 = new Thread(new WaitNotify('A', 'B'));
       Thread t2 = new Thread(new WaitNotify('B', 'C'));
       Thread t3 = new Thread(new WaitNotify('C', 'A'));
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// МФУ. 1.сканирование, 2.копирование, 3.печать. Может выполняться только один процесс, кроме
// сканирования на почту.
// Ожидание - основной процесс, остальные - производные от него. вместе могут выполняться 1 и 3.
// 1 и 2 - взаимоисключающие. 2 и 3 - взаимоисключающие.

System.out.println("\n\n2. Задача. МФУ. 1.сканирование, 2.копирование, 3.печать.");

        MFU mfu = new MFU();
        new Thread(() -> mfu.scan("A", 3)).start();
        new Thread(() -> mfu.print("B", 6)).start();
        new Thread(() -> mfu.print("C", 4)).start();
        new Thread(() -> mfu.copy("E", 3)).start();

    }
}

