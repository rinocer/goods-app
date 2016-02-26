package es.moldovan.givrsapp.objs;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class Item{
    private boolean given;
    private String name, givr;
    private String _id;

    public Item(boolean given, String name, String givr) {
        this.given = given;
        this.name = name;
        this.givr = givr;
    }

    public boolean isGiven() {
        return given;
    }

    public void setGiven(boolean given) {
        this.given = given;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivr() {
        return givr;
    }

    public void setGivr(String givr) {
        this.givr = givr;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
