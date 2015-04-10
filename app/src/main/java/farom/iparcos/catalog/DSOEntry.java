package farom.iparcos.catalog;

/**
 * Created by farom on 10/04/15.
 */
public class DSOEntry extends CatalogEntry {
    protected String name;
    protected String type;
    protected String size;
    protected String magnitude;
    protected Coordinates coordinates;



    public DSOEntry(CharSequence data){
        name = "test name";
        type = "it's a test";
        size = "";
        magnitude = "";
        coordinates = new Coordinates();
    }

    /**
     * Return the object coordinates
     *
     * @return
     */
    @Override
    public Coordinates getCoordinates() {
        return null;
    }

    /**
     * Return the object description
     *
     * @return
     */
    @Override
    public String getDescription() {
        return type+", "+size+", "+magnitude;
    }

    /**
     * Return the object name
     * @return
     */
    @Override
    public String getName(){
        return name;
    }
}
