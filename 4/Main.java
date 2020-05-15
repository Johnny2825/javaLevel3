
public class Main {
    public final Object lock = new Object();
    public char state = 'A';

    public static void main(String[] args) {
        Main m = new Main();
        Thread t1 = new Thread(() -> m.printA());
        Thread t2 = new Thread(() -> m.printB());
        Thread t3 = new Thread(() -> m.printC());
        t1.start();
        t2.start();
        t3.start();

    }

    public void printA(){
        synchronized (lock) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (state != 'A') {
                        lock.wait();
                        System.out.println('A');
                        state = 'B';
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printB(){
        synchronized (lock) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (state != 'B') {
                        lock.wait();
                        System.out.println('B');
                        state = 'C';
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printC(){
        synchronized (lock) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (state != 'C') {
                        lock.wait();
                        System.out.println('C');
                        state = 'A';
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
