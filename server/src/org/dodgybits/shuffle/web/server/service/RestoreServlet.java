package org.dodgybits.shuffle.web.server.service;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;
import org.dodgybits.shuffle.dto.ShuffleProtos.Context;

@SuppressWarnings("serial")
public class RestoreServlet extends UploadAction {

    private static final Logger logger = Logger.getLogger(RestoreServlet.class.getName());
    
    /**
     * Override executeAction to save the received files in a custom place and
     * delete this items from session.
     */
    @Override
    public String executeAction(HttpServletRequest request,
            List<FileItem> sessionFiles) throws UploadActionException {
        assert (sessionFiles.size() == 1);
        Catalogue catalogue;
        
        FileItem item = sessionFiles.iterator().next();
        try {
            // the uploaded seems to load extra 0s at end of byte stream
            int size = (int)item.getSize();
            byte[] data = item.get();
            byte[] reduced = new byte[size];
            System.arraycopy(data, 0, reduced, 0, size);
            catalogue = Catalogue.parseFrom(reduced);
        } catch (Exception e) {
            throw new UploadActionException(e.getMessage());
        }
        removeSessionFileItems(request);
        saveAll(catalogue);
        return null;
    }
    
    private void saveAll(Catalogue catalogue) {
        List<Context> contexts = catalogue.getContextList();
        
        logger.info("Saving backup: " + catalogue.toString());
    }

}
