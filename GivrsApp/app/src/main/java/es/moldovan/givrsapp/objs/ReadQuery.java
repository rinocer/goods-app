package es.moldovan.givrsapp.objs;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class ReadQuery extends Operation{
    private String id;

    public ReadQuery(String id) {
        super("read");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
