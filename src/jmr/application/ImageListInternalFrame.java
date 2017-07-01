package jmr.application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.event.InternalFrameEvent;
import jmr.result.ResultMetadata;

/**
 *
 * @author Original: Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 * Autor extendido: Juan Jesús Tomás Rojas
 */
public class ImageListInternalFrame extends javax.swing.JInternalFrame {

    /**
     * Enlace a la ventana que contiene a esta
     */
    private JMRFrame padre;
    
    /**
     * Lista de variaciones asociadas a los keyframes
     */
    private ArrayList<Double> variations;
    
    /**
     * Nombre del vídeo asociado a la lista de imágenes
     */
    private String nombreVideo;
    
    /**
     * Constructor por defecto
     */
    public ImageListInternalFrame() {
        initComponents();     
    }
    
    /**
     * Crea una ventana con un componente padre asociado
     * @param pad Componente padre asociado
     */
    public ImageListInternalFrame(JMRFrame pad) {
        this();     
        if(pad!=null){
            padre = pad;
        }
    }
    
    /**
     * Asigna las variaciones de un vídeo como estructura intermedia para su 
     * uso en una gráfica
     * @param variaciones Variaciones de un vídeo
     * @param nombre Nombre del vídeo
     */
    public ImageListInternalFrame(ArrayList<Double> variaciones, String nombre){
        this();
        variations = variaciones;
        nombreVideo = nombre;
    }
    
    /**
     * Crea una ventana con la lista indicada
     * @param list Lista de imágenes
     */
    public ImageListInternalFrame(List<ResultMetadata> list){ //ResultList list
        this();
        if(list!=null)
            imageListPanel.add(list);
    }

    /**
     * Evento que se lanza cuando se realiza algún cambio en la ventana
     * @param id Identificador del evento
     */
    @Override
    protected void fireInternalFrameEvent(int id){
        super.fireInternalFrameEvent(id);
        if (id == InternalFrameEvent.INTERNAL_FRAME_ACTIVATED){ //Se ha activado la ventana
            padre.enableSegmentationTools(true);
        }else if(id == InternalFrameEvent.INTERNAL_FRAME_DEACTIVATED){ //Se ha desactivado
            padre.enableSegmentationTools(false);
        }
    }

    /**
     * Añade una imagen a la ventana
     * @param image Imagen que se añade a la ventana
     */
    public void add(BufferedImage image){
        imageListPanel.add(image);
    }
    
    /**
     * Añade una imagen a la ventana con una etiqueta
     * @param image Imagen que se añade
     * @param label Etiqueta asignada a la imagen
     */
    public void add(BufferedImage image, String label){
        imageListPanel.add(image, label);
    }
    
    /**
     * Añade una imagen desde una URL
     * @param imageURL URL que apunta a la imagen
     * @param label Etiqueda asociada a la imagen
     */
    public void add(URL imageURL, String label){
        BufferedImage image;
        try {
            image = ImageIO.read(imageURL);
            if (image != null) {
                imageListPanel.add(image, label);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }        
    }
    
    /**
     * Devuelve la lista de variaciones
     * @return ArrayList<Double> de variaciones
     */
    public ArrayList<Double> getVariations(){
        return variations;
    }
    
    /**
     * Devuelve el nombre del vídeo
     * @return Nombre del vídeo
     */
    public String getNombreVideo(){
        return nombreVideo;
    }
    
    /**
     * Asigna las variaciones a la ventana
     * @param vars Variaciones
     */
    public void setVariations(ArrayList<Double> vars){
        variations = vars;
    }
    
    /**
     * Asigna un nombre de vídeo
     * @param nom Nombre de vídeo
     */
    public void setNombreVideo(String nom){
        nombreVideo = nom;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imageListPanel = new jmr.iu.ImageListPanel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Result");
        getContentPane().add(imageListPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jmr.iu.ImageListPanel imageListPanel;
    // End of variables declaration//GEN-END:variables
}
