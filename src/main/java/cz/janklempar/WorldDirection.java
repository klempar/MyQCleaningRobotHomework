package cz.janklempar;

import java.util.ArrayList;
import java.util.Arrays;

public class WorldDirection<T> {

    public static final WorldDirection<Facing> FACE_MAP = new WorldDirection<Facing>(new ArrayList<>(Arrays.asList(Facing.values())));


    public enum Facing {
        // must stay in that order for iterations.
        N, E, S, W
    }
    // made for directions
    private ArrayList<T> list;

    public WorldDirection(ArrayList<T> list) {
        this.list = list;
    }

    public T next(int index) {


        index = (index + 1) % list.size(); // Circular logic
        T item = list.get(index);
        return item;
    }

    public T previous(int index) {
        if (index == 0) {
            index = list.size() - 1;
        } else {
            index--;
        }
        T item = list.get(index);
        return item;

    }



}
