
package mz.co.fer.DTO;

/**
 *
 * @author Deockilion
 */
public abstract class Id {
    
    
    private int id;
    
    public Id(){
        this.id = 0;
        
    }
    public Id(int id){
        this.id = id;
    }
    

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
}
