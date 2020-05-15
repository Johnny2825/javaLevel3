import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {
    private Semaphore sem;
    public Tunnel(int numberOfCars) {
        sem = new Semaphore(numberOfCars);
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }
    @Override
    public void go(Car c) {
        try {
            try {
                if(!sem.tryAcquire()){
                    System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                    sem.acquire();
                }
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                sem.release();
                System.out.println(c.getName() + " закончил этап: " + description);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}