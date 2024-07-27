
package mz.co.fer.Relatorios;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

/**
 *
 * @author Deockilion
 */
public class DocumentFactory {
    
    private static DocumentFactory instance;

    private DocumentFactory() {
    }

    public static DocumentFactory getInstance() {
        if (instance == null) {
            instance = new DocumentFactory();
        }
        return instance;
    }

    public Document newDocument() throws DocumentException {
        return new com.itextpdf.text.Document();
    }
    
}
