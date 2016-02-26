package es.moldovan.givrsapp.objs;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class Item{
    private boolean given;
    private String name, givr;

    public Item(boolean given, String name, String givr) {
        this.given = given;
        this.name = name;
        this.givr = givr;
    }
}
