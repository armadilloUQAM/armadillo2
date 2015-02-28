
package biologic.seqclasses.parserNewick;


/**
 * Edge in a Newick tree
 * @author Etienne Lord
 * @since June 2011
 */
public class edge implements Comparable {
    double lenght=1.0;   //edge lenght
    double bootstrap=1; //edge bootstrap
    String notes="";     //edge note
    node source;                //edge source node
    node dest;                  //edge dest nore
    int color=0;
    double height=0;

    public edge(node source, node dest){
        this.dest = dest;
        this.source=source;
    }

    public int compareTo(Object o) {
       edge e=(edge)o;
       if (e.source.equals(this.source)&&e.dest.equals(this.dest)) return 0;       
       //--source is the most important
      //--but, if equals, we compare the dest
       return (e.source.compareTo(this.source)==0?e.dest.compareTo(this.dest):e.source.compareTo(this.source));
    }

    @Override
    public boolean equals(Object obj) {
        return (compareTo(obj)==0);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 97 * hash + (this.dest != null ? this.dest.hashCode() : 0);
        return hash;
    }


       
}
