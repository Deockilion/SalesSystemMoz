package mz.co.fer.DAO;

import java.util.List;

/**
 *
 * @author Deockilion
 * @param <Tipo>
 */
public interface IDAO<Tipo> {

    public void save(Tipo objeto);

    public void update(Tipo objeto);

    public void Delete(Tipo objeto);
    
    public List<Tipo> read();
    
    public Tipo retornar(String codigo);
    

}
