package es.moldovan.givrsapp.objs;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class Join extends Operation {
    private String project, item, givr;

    public Join(String project, String item, String givr){
        super("join");
        this.project = project;
        this.item = item;
        this.givr = givr;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getGivr() {
        return givr;
    }

    public void setGivr(String givr) {
        this.givr = givr;
    }
}
