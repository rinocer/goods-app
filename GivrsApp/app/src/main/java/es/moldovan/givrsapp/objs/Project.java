package es.moldovan.givrsapp.objs;

import java.util.Arrays;
import java.util.List;

/**
 * Created by marian.claudiu on 26/2/16.
 */
public class Project extends Operation {
    private String name, image, description, initiator, instructions;
    private Double[] location;
    private List<Item> items;
    private String _id;

    public Project(String name, String image, String description, String initiator, String instructions, Double[] location, List<Item> items) {
        super("create");
        this.name = name;
        this.image = image;
        this.description = description;
        this.initiator = initiator;
        this.instructions = instructions;
        this.location = location;
        this.items = items;
    }

    public Project() {
        super("create");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Double[] getLocation() {
        return location;
    }

    public void setLocation(Double[] location) {
        this.location = location;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", initiator='" + initiator + '\'' +
                ", instructions='" + instructions + '\'' +
                ", location=" + Arrays.toString(location) +
                ", items=" + items +
                ", _id='" + _id + '\'' +
                '}';
    }
}