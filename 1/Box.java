import java.util.*;

public class Box <T extends Fruit> {

    private int weightBox = 0;
    private List<T> fruits = new ArrayList<>();

    public void getCountFruits() {
        System.out.println("В коробке " + fruits.size());
    }

    public void putInAnotherBox(Box<T> anotherBox){
        for(T fruit : fruits) {
            anotherBox.putFruit(fruit);
        }
        this.fruits.clear();
        getWeight();
    }

    public void putFruit(T fruit){
        this.fruits.add(fruit);
        getWeight();
    }

    public void removeFruit(){
        if(!fruits.isEmpty()){
            fruits.remove(0);
        }
        getWeight();
    }

    public int getWeight() {
        weightBox = 0;
        for(T fruit : fruits) {
            weightBox += fruit.getWeight();
        }
        return weightBox;
    }

    public boolean compare(Box another){
        return Math.abs(weightBox - another.weightBox) < 0.0001;
    }

}
