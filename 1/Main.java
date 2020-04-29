
public class Main {
    public static void main(String[] args) {
        // Task 1
        Integer[] arrInt = {1, 2, 3, 4};
        String[] arrStr = {"a", "b", "c"};

        workWithArray<Integer> testInt = new workWithArray<>();
        workWithArray<String> testStr = new workWithArray<>();
        testInt.changeElements(arrInt);

        // Task 2
        System.out.println(testInt.transformation(arrInt));
        System.out.println(testStr.transformation(arrStr));

        //Task 3
        Box<Orange> boxOran1 = new Box<>();
        boxOran1.putFruit(new Orange(7));
        boxOran1.putFruit(new Orange(3));

        System.out.println("Вес коробки c апельсинами 1 = " + boxOran1.getWeight());

        Box<Apple> boxApp1 = new Box<>();
        boxApp1.putFruit(new Apple(4));
        boxApp1.putFruit(new Apple(6));
        System.out.println("Вес коробки c яблоками 1 = " + boxApp1.getWeight());

        System.out.println("Сранение коробок с яблоками и апельсинами " + boxApp1.compare(boxOran1));

        Box<Apple> boxApp2 = new Box<>();
        boxApp2.putFruit(new Apple(7));
        System.out.println("Вес коробки c яблоками 1 = " + boxApp1.getWeight());
        System.out.println("Вес коробки c яблоками 2 = " + boxApp2.getWeight());
        boxApp1.putInAnotherBox(boxApp2);
        System.out.println("Вес коробки c яблоками 2 = " + boxApp2.getWeight());
        System.out.println("Вес коробки c яблоками 1 = " + boxApp1.getWeight());

        boxApp1.putFruit(new Apple(5));
        System.out.println("Вес коробки c яблоками 1 = " + boxApp1.getWeight());
        boxApp1.removeFruit();
        System.out.println("Вес коробки c яблоками 1 = " + boxApp1.getWeight());
    }
}
