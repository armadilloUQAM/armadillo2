package biologic.seqclasses.parserNewick;

import java.util.Vector;


/**
 *
 * @author Mickael Leclerc
 * @author Etienne Lord
 */
public class node implements Comparable {

    static  int index=-1;
    public String name="";// node note
    public String notes = "";
    public int id;         // node id
    public node parent;
    Vector<node> child=new Vector<node>();
    public boolean isleaf = true;
    public Object obj;

    double depth;   //Current node level from root
    double distance;//Current node distance from root

    ////////////////////////////////////////////////////////////////////////////
    //// Graphic variables

    boolean fixed=false;
    float x, y;
    float dx, dy;
     int color=-1;

    public node(String name) {
        this.id=index++;
        this.name = name.trim();
     }
    
    public int compareTo(Object o) {
        node n=(node)o;
        if (this.getName().isEmpty()) return (this.equals(n)?0:1);
        return (this.getName().compareTo(n.getName()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof node)) return false;
        node n=(node)obj;
        if (this.name.isEmpty()) return (this.id==n.id);
        return (this.name.equals(n.name));
    }

    @Override
    public int hashCode() {
         return id;
    }

    @Override
    public String toString() {
        return this.getName()+"("+this.id+")";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the parent
     */
    public node getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(node parent) {
        this.parent = parent;
    }

    /**
     * @return the isleaf
     */
    public boolean isIsleaf() {
        return isleaf;
    }

    /**
     * @param isleaf the isleaf to set
     */
    public void setIsleaf(boolean isleaf) {
        this.isleaf = isleaf;
    }

    /**
     * @return the obj
     */
    public Object getObj() {
        return obj;
    }

    /**
     * @param obj the obj to set
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }
    
    

}
