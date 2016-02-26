package es.moldovan.givrsapp.objs;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class SearchQuery  extends Operation{

    private String query;

    public SearchQuery(String query) {
        super("search");
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
