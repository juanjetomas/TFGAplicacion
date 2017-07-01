package jmr.application;

import iu.ImageInternalFrame;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.media.CannotRealizeException;
import javax.media.NoPlayerException;
import javax.swing.JFrame;
import javax.swing.event.InternalFrameEvent;
import jmr.initial.descriptor.mpeg7.MPEG7DominantColors;
import jmr.video.KeyFrameDescriptor;
import jmr.video.VideoIterator;
import org.bytedeco.javacv.FrameGrabber;

/**
 *
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class JMRImageInternalFrame extends ImageInternalFrame {

    private MPEG7DominantColors dcd = null;
    ColorSetPanel panelDCD = null;
    private URL locator = null;
    
    /**
     * Descriptor del vídeo usado para su comparación
     */
    
    private KeyFrameDescriptor keyFrameDescriptor;
    
    /**
     * Operador de iteración y segmentación
     */
    private VideoIterator videoIterator;
    
    /**
     * File associated with the image
     */
    private File file;
    
    /**
     * Creates new form JMRImageInternalFrame
     * @param parent
     * @param img
     */
    public JMRImageInternalFrame(JFrame parent, BufferedImage img) {
        super(parent,img);
        initComponents();
        keyFrameDescriptor = null; 
        videoIterator = null;
    }

    public JMRImageInternalFrame(JFrame parent, BufferedImage img, URL locator) {
        this(parent,img);
        this.locator = locator;
    }
    
    public URL getURL(){
        return locator;
    }
    
    public void setDominantColorDescriptor(MPEG7DominantColors dcd){
        this.dcd = dcd;
        addColorPanel();
    }
    
    public MPEG7DominantColors getDominantColorDescriptor(){
        return dcd;
    }
    
    private void addColorPanel() {
        if (dcd != null) {
            panelDCD = new ColorSetPanel();
            ArrayList<MPEG7DominantColors.MPEG7SingleDominatColor> list = dcd.getDominantColors();
            for (MPEG7DominantColors.MPEG7SingleDominatColor c : list) {
                panelDCD.addColor(c.getColor());
            }
            this.add(panelDCD, BorderLayout.EAST);
            this.validate();
            this.repaint();
        }
    }
    
    public void setFile(File f){
        file = f;
    }
    
    public File getFile(){
        return file;
    }
    
    @Override
    protected void fireInternalFrameEvent(int id){
        super.fireInternalFrameEvent(id);
        if (id == InternalFrameEvent.INTERNAL_FRAME_ACTIVATED){ //Se ha activado la ventana            
            ((JMRFrame)parent).checkVideosActivos();
        }else if(id == InternalFrameEvent.INTERNAL_FRAME_DEACTIVATED){ //Se ha desactivado  
            ((JMRFrame)parent).checkVideosActivos();
        }
    }
    
    public void preLoad() throws NoSuchMethodException, IOException, FrameGrabber.Exception, NoPlayerException, CannotRealizeException{
        if(keyFrameDescriptor==null){ //No se ha precargado el vídeo
            videoIterator =((JMRFrame)parent).createSegmentationOp(file, true);
            Class descriptor = ((JMRFrame)parent).getDescriptorUnico();
            keyFrameDescriptor = new KeyFrameDescriptor(videoIterator.getVideo(), videoIterator, descriptor);            
        }
        keyFrameDescriptor.setComparator(((JMRFrame)parent).createSelectedComparator());
    }
    
    public KeyFrameDescriptor getKeyFrameDescriptor(){
        return keyFrameDescriptor;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
