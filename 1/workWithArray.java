import java.util.ArrayList;
import java.util.List;

public class workWithArray <T> {

    public void printArray(T... array) {
        for(int i = 0; i < array.length; i++){
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    public T[] changeElements(T... array){
        T tmp;
        printArray(array);
        for (int i = 0; i < array.length/2; i++){
            tmp = array[i];
            array[i] = array[array.length/2 + i];
            array[array.length/2 + i] = tmp;
        }
        printArray(array);
        return array;
    }

    public List transformation(T[] array){
        List<T> list = new ArrayList<>();
        for(int i = 0; i < array.length; i++){
            list.add(array[i]);
        }
        return list;
    }
}
