package jmr.application;


import com.sun.glass.ui.Cursor;
import videomedia.comparator.AverageComparator;
import com.sun.jna.NativeLibrary;
import events.PixelEvent;
import events.PixelListener;
import iu.ImageInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ToggleButton;
import javax.imageio.ImageIO;
import javax.media.CannotRealizeException;
import javax.media.NoPlayerException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import jmr.db.ListDB;
import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.color.SingleColorDescriptor;
import jmr.initial.descriptor.mpeg7.MPEG7DominantColors;
import jmr.initial.descriptor.mpeg7.MPEG7DominantColors.MPEG7SingleDominatColor;
import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.initial.descriptor.mpeg7.MPEG7HomogeneousTexture;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.grid.SquareGrid;
import jmr.media.JMRExtendedBufferedImage;
import jmr.result.FloatResult;
import jmr.result.ResultMetadata;
import jmr.result.Vector;
import jmr.video.KeyFrameDescriptor;
import jmr.video.MinMinComparator;
import jmr.video.Video;
import jmr.video.VideoIterator;
import org.bytedeco.javacv.FrameGrabber;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import videomedia.*;
import videomedia.comparator.AverageEqualComparator;
import videomedia.comparator.AverageEqualOrderedComparator;
import videomedia.comparator.AverageOrderedComparator;
import videomedia.comparator.MaxComparator;
import videomedia.comparator.MaxEqualComparator;
import videomedia.comparator.MaxEqualOrderedComparator;
import videomedia.comparator.MaxOrderedComparator;
import videomedia.comparator.MedianComparator;
import videomedia.comparator.MedianEqualComparator;
import videomedia.comparator.MedianEqualOrderedComparator;
import videomedia.comparator.MedianOrderedComparator;
import videomedia.comparator.MinComparator;
import videomedia.comparator.MinOrderedComparator;

/**
 * Ventana principal de la aplicación JMR
 * 
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class JMRFrame extends javax.swing.JFrame {
    
    
    /**
     * Crea una ventana principal
     */
    public JMRFrame() {
        initComponents();
        setIconImage((new ImageIcon(getClass().getResource("/icons/video_grande.png"))).getImage());
        this.useGridButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/gridOn.png")));
        this.useGridButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/gridOff.png")));
        //Desactivamos botonos de BD
        this.botonCloseDB.setEnabled(false);
        this.botonSaveDB.setEnabled(false);
        this.botonAddRecordDB.setEnabled(false);
        this.botonSearchDB.setEnabled(false);
        
        //Carga la librería de VLC
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "c:/program files (x86)/videolan/vlc");
        enableVideoTools(false);
        enableSegmentationTools(false);
        
        //Valores por defecto
        colorMedio.setSelected(false);
        colorEstructurado.setSelected(true);
        
        checkVideosActivos();
        
    }
    

    
    /**
     * Devuelve la ventana interna seleccionada de tipo imagen (null si no hubiese 
     * ninguna selecionada o si fuese de otro tipo) 
     * 
     * @return la ventana interna seleccionada de tipo imagen
     */
    public JMRImageInternalFrame getSelectedImageFrame() {
        JInternalFrame vi = escritorio.getSelectedFrame();
        if(vi instanceof JMRImageInternalFrame)
            return (JMRImageInternalFrame)escritorio.getSelectedFrame();
        else
            return null;
    }
    
    /**
     * Devuelve la ventana interna seleccionada de tipo video (null si no hubiese 
     * ninguna selecionada o si fuese de otro tipo) 
     * 
     * @return la ventana interna seleccionada de tipo video
     */
    public VideoInternalFrame getSelectedVideoFrame(){
        JInternalFrame vi = escritorio.getSelectedFrame();
        if(vi!=null && vi instanceof VideoInternalFrame){
            return (VideoInternalFrame)vi;
        }else{
            return null;
        }
    }
    
    /**
     * Devuelve una lista con todas las ventanas de vídeo abiertas
     * @return Lista de ventanas de vídeo abiertas
     */
    public ArrayList<VideoInternalFrame> getAllVideoFrames(){
        ArrayList<VideoInternalFrame> frames = new ArrayList<>();
        JInternalFrame[] allFrames = escritorio.getAllFrames();
        JInternalFrame frameActual;
        for (JInternalFrame frame : allFrames) {
            frameActual = frame;
            if(frameActual!=null && frameActual instanceof VideoInternalFrame){
                frames.add((VideoInternalFrame)frameActual);
            }
        }
        
        return frames;
    }
    
    /**
     * Devuelve una lista con las ventanas de imagen abiertas
     * @return Lista con las ventanas de imagen abiertas
     */
    public ArrayList<JMRImageInternalFrame> getAllImageFrames(){
        ArrayList<JMRImageInternalFrame> frames = new ArrayList<>();
        JInternalFrame[] allFrames = escritorio.getAllFrames();
        JInternalFrame frameActual;
        for (JInternalFrame frame : allFrames) {
            frameActual = frame;
            if(frameActual!=null && frameActual instanceof JMRImageInternalFrame){
                frames.add((JMRImageInternalFrame)frameActual);
            }
        }
        
        return frames;
    }
    
    /**
     * Devuelve la ventana interna seleccionada de tipo imageList (null si no hubiese 
     * ninguna selecionada o si fuese de otro tipo) 
     * 
     * @return la ventana interna seleccionada de tipo imageList
     */
    public ImageListInternalFrame getSelectedImageListFrame(){
        JInternalFrame vi = escritorio.getSelectedFrame();
        if(vi!=null && vi instanceof ImageListInternalFrame){
            return (ImageListInternalFrame)vi;
        }else{
            return null;
        }
    }
    
    /**
     * Devuelve la imagen de la ventana interna selecionada
     * 
     * @return la imagen seleccionada
     */
    private BufferedImage getSelectedImage(){
        BufferedImage img = null;
        ImageInternalFrame vi = this.getSelectedImageFrame();
        if (vi != null) {
            if (vi.getType() == ImageInternalFrame.TYPE_STANDAR) {
                img = vi.getImage();
            } 
            else {
                JOptionPane.showInternalMessageDialog(escritorio, "An image must be selected", "Image", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return img;
    }
        
    /**
     * Devuelve el título de la ventana interna selecionada
     * 
     * @return el título de la ventana interna selecionada
     */
    private String getSelectedFrameTitle(){
        String title = "";
        JInternalFrame vi = escritorio.getSelectedFrame();
        if (vi != null) {
            title = vi.getTitle();
        }
        return title;
    }
    
    /**
     * Sitúa la ventana interna <tt>vi</tt> debajo de la ventana interna activa 
     * y con el mismo tamaño.
     * 
     * @param vi la ventana interna
     */
    private void locateInternalFrame(JInternalFrame vi) {
        JInternalFrame vSel = escritorio.getSelectedFrame();
        if (vSel != null) {
            vi.setLocation(vSel.getX() + 20, vSel.getY() + 20);
            vi.setSize(vSel.getSize());
        }
    }
    
    /**
     * Muestra la ventana interna <tt>vi</tt> 
     * 
     * @param vi la ventana interna
     */
    private void showInternalFrame(JInternalFrame vi) {        
        if(vi instanceof ImageInternalFrame){
            ((ImageInternalFrame)vi).setGrid(this.verGrid.isSelected());
            ((ImageInternalFrame)vi).addPixelListener(new ManejadorPixel());
        }
        this.locateInternalFrame(vi);
        this.escritorio.add(vi);
        vi.setVisible(true);
    }  
    
    /**
     * Clase interna manejadora de eventos de pixel
     */
    private class ManejadorPixel implements PixelListener {
        /**
         * Gestiona el cambio de localización del pixel activo, actualizando
         * la información de la barra de tareas.
         * 
         * @param evt evento de pixel
         */
        @Override
        public void positionChange(PixelEvent evt) {
            String text = " ";
            Point p = evt.getPixelLocation();
            if (p != null) {
                Color c = evt.getRGB();
                Integer alpha = evt.getAlpha();
                text = "(" + p.x + "," + p.y + ") : [" + c.getRed() + "," + c.getGreen() + "," + c.getBlue();
                text += alpha == null ? "]" : ("," + alpha + "]");
            }
            posicionPixel.setText(text);
        }
    } 
    
    /*
     * Código generado por Netbeans para el diseño del interfaz
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenuPanelOutput = new javax.swing.JPopupMenu();
        clear = new javax.swing.JMenuItem();
        popupMenuSeleccionDescriptores = new javax.swing.JPopupMenu();
        colorDominante = new javax.swing.JRadioButtonMenuItem();
        colorEstructurado = new javax.swing.JRadioButtonMenuItem();
        colorEscalable = new javax.swing.JRadioButtonMenuItem();
        colorMedio = new javax.swing.JRadioButtonMenuItem();
        separadorDescriptores = new javax.swing.JPopupMenu.Separator();
        texturaHomogeneidad = new javax.swing.JRadioButtonMenuItem();
        texturaEdge = new javax.swing.JRadioButtonMenuItem();
        popupMenuGrid = new javax.swing.JPopupMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        popupMenuSeleccionDescriptoresDB = new javax.swing.JPopupMenu();
        colorDominanteDB = new javax.swing.JRadioButtonMenuItem();
        colorEstructuradoDB = new javax.swing.JRadioButtonMenuItem();
        colorEscalableDB = new javax.swing.JRadioButtonMenuItem();
        colorMedioDB = new javax.swing.JRadioButtonMenuItem();
        separadorDescriptoresDB = new javax.swing.JPopupMenu.Separator();
        texturaHomogeneidadDB = new javax.swing.JRadioButtonMenuItem();
        texturaEdgeDB = new javax.swing.JRadioButtonMenuItem();
        popSeleccionDescriptoresUnico = new javax.swing.JPopupMenu();
        colorEstructuradoUnico = new javax.swing.JRadioButtonMenuItem();
        colorEscalableUnico = new javax.swing.JRadioButtonMenuItem();
        colorMedioUnico = new javax.swing.JRadioButtonMenuItem();
        grupoBotonesDescriptorUnico = new javax.swing.ButtonGroup();
        splitPanelCentral = new javax.swing.JSplitPane();
        escritorio = new javax.swing.JDesktopPane();
        showPanelInfo = new javax.swing.JLabel();
        panelTabuladoInfo = new javax.swing.JTabbedPane();
        panelOutput = new javax.swing.JPanel();
        scrollEditorOutput = new javax.swing.JScrollPane();
        editorOutput = new javax.swing.JEditorPane();
        panelBarraHerramientas = new javax.swing.JPanel();
        barraArchivo = new javax.swing.JToolBar();
        botonAbrir = new javax.swing.JButton();
        botonGuardar = new javax.swing.JButton();
        botonPreferencias = new javax.swing.JButton();
        barraBD = new javax.swing.JToolBar();
        botonNewDB = new javax.swing.JButton();
        botonOpenDB = new javax.swing.JButton();
        botonSaveDB = new javax.swing.JButton();
        botonCloseDB = new javax.swing.JButton();
        botonAddRecordDB = new javax.swing.JButton();
        botonSearchDB = new javax.swing.JButton();
        barraImagen = new javax.swing.JToolBar();
        drawGridButton = new javax.swing.JButton();
        barraDescriptores = new javax.swing.JToolBar();
        useGridButton = new javax.swing.JToggleButton();
        botonDCD = new javax.swing.JButton();
        botonSingleColor = new javax.swing.JButton();
        botonCompara = new javax.swing.JButton();
        barraVideo = new javax.swing.JToolBar();
        botonPlaybarra = new javax.swing.JToggleButton();
        botonRewindBarra = new javax.swing.JButton();
        botonPrecarga = new javax.swing.JButton();
        botonSegmentar = new javax.swing.JButton();
        botonGrafica = new javax.swing.JButton();
        botonComparar2Videos = new javax.swing.JButton();
        comboComparador = new javax.swing.JComboBox<>();
        barraComparador = new javax.swing.JToolBar();
        labelVideoA = new javax.swing.JLabel();
        botonPerteneceIgual = new javax.swing.JButton();
        labelVideoB = new javax.swing.JLabel();
        restoBarraComparador = new javax.swing.JToolBar();
        toggleOrdered = new javax.swing.JToggleButton();
        barraEstado = new javax.swing.JPanel();
        posicionPixel = new javax.swing.JLabel();
        infoDB = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        menuAbrir = new javax.swing.JMenuItem();
        menuGuardar = new javax.swing.JMenuItem();
        separador1 = new javax.swing.JPopupMenu.Separator();
        closeAll = new javax.swing.JMenuItem();
        menuVer = new javax.swing.JMenu();
        verGrid = new javax.swing.JCheckBoxMenuItem();
        usarTransparencia = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        showResized = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuZoom = new javax.swing.JMenu();
        menuZoomIn = new javax.swing.JMenuItem();
        menuZoomOut = new javax.swing.JMenuItem();

        popupMenuPanelOutput.setAlignmentY(0.0F);
        popupMenuPanelOutput.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });
        popupMenuPanelOutput.add(clear);

        colorDominante.setText("Dominant color");
        popupMenuSeleccionDescriptores.add(colorDominante);

        colorEstructurado.setSelected(true);
        colorEstructurado.setText("Structured color");
        popupMenuSeleccionDescriptores.add(colorEstructurado);

        colorEscalable.setText("Scalable color");
        popupMenuSeleccionDescriptores.add(colorEscalable);

        colorMedio.setText("Mean color");
        popupMenuSeleccionDescriptores.add(colorMedio);
        popupMenuSeleccionDescriptores.add(separadorDescriptores);

        texturaHomogeneidad.setText("Homogeneous texture");
        texturaHomogeneidad.setEnabled(false);
        popupMenuSeleccionDescriptores.add(texturaHomogeneidad);

        texturaEdge.setText("Edge histogram");
        texturaEdge.setEnabled(false);
        popupMenuSeleccionDescriptores.add(texturaEdge);

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");
        popupMenuGrid.add(jRadioButtonMenuItem1);

        colorDominanteDB.setText("Dominant color");
        colorDominanteDB.setEnabled(false);
        popupMenuSeleccionDescriptoresDB.add(colorDominanteDB);

        colorEstructuradoDB.setText("Structured color");
        popupMenuSeleccionDescriptoresDB.add(colorEstructuradoDB);

        colorEscalableDB.setText("Scalable color");
        popupMenuSeleccionDescriptoresDB.add(colorEscalableDB);

        colorMedioDB.setSelected(true);
        colorMedioDB.setText("Mean color");
        popupMenuSeleccionDescriptoresDB.add(colorMedioDB);
        popupMenuSeleccionDescriptoresDB.add(separadorDescriptoresDB);

        texturaHomogeneidadDB.setText("Homogeneous texture");
        texturaHomogeneidadDB.setEnabled(false);
        popupMenuSeleccionDescriptoresDB.add(texturaHomogeneidadDB);

        texturaEdgeDB.setText("Edge histogram");
        texturaEdgeDB.setEnabled(false);
        popupMenuSeleccionDescriptoresDB.add(texturaEdgeDB);

        popSeleccionDescriptoresUnico.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        grupoBotonesDescriptorUnico.add(colorEstructuradoUnico);
        colorEstructuradoUnico.setSelected(true);
        colorEstructuradoUnico.setText("Color estructurado");
        colorEstructuradoUnico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorEstructuradoUnicoActionPerformed(evt);
            }
        });
        popSeleccionDescriptoresUnico.add(colorEstructuradoUnico);

        grupoBotonesDescriptorUnico.add(colorEscalableUnico);
        colorEscalableUnico.setText("Color escalable");
        colorEscalableUnico.setToolTipText("");
        colorEscalableUnico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorEscalableUnicoActionPerformed(evt);
            }
        });
        popSeleccionDescriptoresUnico.add(colorEscalableUnico);

        grupoBotonesDescriptorUnico.add(colorMedioUnico);
        colorMedioUnico.setText("Color Medio");
        colorMedioUnico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorMedioUnicoActionPerformed(evt);
            }
        });
        popSeleccionDescriptoresUnico.add(colorMedioUnico);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Reverse video search");
        setName("ventanaPrincipal"); // NOI18N

        splitPanelCentral.setDividerLocation(1.0);
        splitPanelCentral.setDividerSize(3);
        splitPanelCentral.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPanelCentral.setPreferredSize(new java.awt.Dimension(0, 0));
        splitPanelCentral.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitPanelCentralPropertyChange(evt);
            }
        });

        escritorio.setBackground(java.awt.Color.lightGray);
        escritorio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        showPanelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/desplegar20.png"))); // NOI18N
        showPanelInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showPanelInfoMousePressed(evt);
            }
        });

        escritorio.setLayer(showPanelInfo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, escritorioLayout.createSequentialGroup()
                .addGap(0, 1065, Short.MAX_VALUE)
                .addComponent(showPanelInfo))
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, escritorioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(showPanelInfo))
        );

        splitPanelCentral.setTopComponent(escritorio);

        panelTabuladoInfo.setMinimumSize(new java.awt.Dimension(0, 0));
        panelTabuladoInfo.setPreferredSize(new java.awt.Dimension(0, 0));

        panelOutput.setMinimumSize(new java.awt.Dimension(0, 0));
        panelOutput.setPreferredSize(new java.awt.Dimension(0, 0));
        panelOutput.setLayout(new java.awt.BorderLayout());

        scrollEditorOutput.setBorder(null);
        scrollEditorOutput.setMinimumSize(new java.awt.Dimension(0, 0));

        editorOutput.setBorder(null);
        editorOutput.setMinimumSize(new java.awt.Dimension(0, 0));
        editorOutput.setPreferredSize(new java.awt.Dimension(0, 0));
        editorOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                editorOutputMouseReleased(evt);
            }
        });
        scrollEditorOutput.setViewportView(editorOutput);

        panelOutput.add(scrollEditorOutput, java.awt.BorderLayout.CENTER);

        panelTabuladoInfo.addTab("Output", panelOutput);

        splitPanelCentral.setBottomComponent(panelTabuladoInfo);

        getContentPane().add(splitPanelCentral, java.awt.BorderLayout.CENTER);

        panelBarraHerramientas.setAlignmentX(0.0F);
        panelBarraHerramientas.setAlignmentY(0.0F);
        panelBarraHerramientas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        barraArchivo.setRollover(true);
        barraArchivo.setAlignmentX(0.0F);

        botonAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open24.png"))); // NOI18N
        botonAbrir.setToolTipText("Open");
        botonAbrir.setFocusable(false);
        botonAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirActionPerformed(evt);
            }
        });
        barraArchivo.add(botonAbrir);

        botonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save24.png"))); // NOI18N
        botonGuardar.setToolTipText("Save");
        botonGuardar.setFocusable(false);
        botonGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        barraArchivo.add(botonGuardar);

        botonPreferencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings24.png"))); // NOI18N
        botonPreferencias.setToolTipText("Configuration");
        botonPreferencias.setFocusable(false);
        botonPreferencias.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonPreferencias.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonPreferencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPreferenciasActionPerformed(evt);
            }
        });
        barraArchivo.add(botonPreferencias);

        panelBarraHerramientas.add(barraArchivo);

        barraBD.setRollover(true);

        botonNewDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/database.png"))); // NOI18N
        botonNewDB.setToolTipText("Create a new database");
        botonNewDB.setBorderPainted(false);
        botonNewDB.setComponentPopupMenu(popupMenuSeleccionDescriptoresDB);
        botonNewDB.setFocusable(false);
        botonNewDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonNewDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonNewDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonNewDBActionPerformed(evt);
            }
        });
        barraBD.add(botonNewDB);

        botonOpenDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/openDB.png"))); // NOI18N
        botonOpenDB.setToolTipText("Open a database");
        botonOpenDB.setFocusable(false);
        botonOpenDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonOpenDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonOpenDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonOpenDBActionPerformed(evt);
            }
        });
        barraBD.add(botonOpenDB);

        botonSaveDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/saveDB.png"))); // NOI18N
        botonSaveDB.setToolTipText("Save the database");
        botonSaveDB.setFocusable(false);
        botonSaveDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonSaveDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonSaveDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSaveDBActionPerformed(evt);
            }
        });
        barraBD.add(botonSaveDB);

        botonCloseDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/deleteBD.png"))); // NOI18N
        botonCloseDB.setToolTipText("Close the database");
        botonCloseDB.setFocusable(false);
        botonCloseDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonCloseDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonCloseDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCloseDBActionPerformed(evt);
            }
        });
        barraBD.add(botonCloseDB);

        botonAddRecordDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/addBD.png"))); // NOI18N
        botonAddRecordDB.setFocusable(false);
        botonAddRecordDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAddRecordDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonAddRecordDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAddRecordDBActionPerformed(evt);
            }
        });
        barraBD.add(botonAddRecordDB);

        botonSearchDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/seacrhDB.png"))); // NOI18N
        botonSearchDB.setFocusable(false);
        botonSearchDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonSearchDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonSearchDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSearchDBActionPerformed(evt);
            }
        });
        barraBD.add(botonSearchDB);

        panelBarraHerramientas.add(barraBD);

        barraImagen.setRollover(true);

        drawGridButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/grid.png"))); // NOI18N
        drawGridButton.setToolTipText("Show the gridded image");
        drawGridButton.setComponentPopupMenu(popupMenuGrid);
        drawGridButton.setFocusable(false);
        drawGridButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        drawGridButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        drawGridButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawGridButtonActionPerformed(evt);
            }
        });
        barraImagen.add(drawGridButton);

        panelBarraHerramientas.add(barraImagen);

        barraDescriptores.setRollover(true);

        useGridButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/gridOff.png"))); // NOI18N
        useGridButton.setSelected(true);
        useGridButton.setToolTipText("Grid flag");
        useGridButton.setFocusable(false);
        useGridButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        useGridButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        barraDescriptores.add(useGridButton);

        botonDCD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconDCD24.png"))); // NOI18N
        botonDCD.setToolTipText("DCD");
        botonDCD.setFocusable(false);
        botonDCD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonDCD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonDCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDCDActionPerformed(evt);
            }
        });
        barraDescriptores.add(botonDCD);

        botonSingleColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mean24.png"))); // NOI18N
        botonSingleColor.setToolTipText("Mean color descriptor");
        botonSingleColor.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        botonSingleColor.setDefaultCapable(false);
        botonSingleColor.setFocusable(false);
        botonSingleColor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonSingleColor.setMaximumSize(new java.awt.Dimension(31, 31));
        botonSingleColor.setMinimumSize(new java.awt.Dimension(31, 31));
        botonSingleColor.setPreferredSize(new java.awt.Dimension(31, 31));
        botonSingleColor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonSingleColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSingleColorActionPerformed(evt);
            }
        });
        barraDescriptores.add(botonSingleColor);

        botonCompara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/compare24.png"))); // NOI18N
        botonCompara.setToolTipText("Compare");
        botonCompara.setComponentPopupMenu(popupMenuSeleccionDescriptores);
        botonCompara.setFocusable(false);
        botonCompara.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonCompara.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonCompara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonComparaActionPerformed(evt);
            }
        });
        barraDescriptores.add(botonCompara);

        panelBarraHerramientas.add(barraDescriptores);

        barraVideo.setRollover(true);

        botonPlaybarra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/play_black.png"))); // NOI18N
        botonPlaybarra.setToolTipText("Reproduce el vídeo actual");
        botonPlaybarra.setBorder(null);
        botonPlaybarra.setBorderPainted(false);
        botonPlaybarra.setFocusable(false);
        botonPlaybarra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonPlaybarra.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/stop_black.png"))); // NOI18N
        botonPlaybarra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonPlaybarra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPlaybarraActionPerformed(evt);
            }
        });
        barraVideo.add(botonPlaybarra);

        botonRewindBarra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rewind_black.png"))); // NOI18N
        botonRewindBarra.setToolTipText("Inicia el vídeo desde el principio");
        botonRewindBarra.setBorder(null);
        botonRewindBarra.setFocusable(false);
        botonRewindBarra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonRewindBarra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonRewindBarra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRewindBarraActionPerformed(evt);
            }
        });
        barraVideo.add(botonRewindBarra);

        botonPrecarga.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/precarga.png"))); // NOI18N
        botonPrecarga.setToolTipText("Precarga los medios para un análisis más rápido");
        botonPrecarga.setFocusable(false);
        botonPrecarga.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonPrecarga.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonPrecarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPrecargaActionPerformed(evt);
            }
        });
        barraVideo.add(botonPrecarga);

        botonSegmentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/segmentar.png"))); // NOI18N
        botonSegmentar.setToolTipText("Segmenta el vídeo actual");
        botonSegmentar.setComponentPopupMenu(popSeleccionDescriptoresUnico);
        botonSegmentar.setFocusable(false);
        botonSegmentar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonSegmentar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonSegmentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSegmentarActionPerformed(evt);
            }
        });
        barraVideo.add(botonSegmentar);

        botonGrafica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/chart.png"))); // NOI18N
        botonGrafica.setToolTipText("Muestra la gráfica de variación tras una segmentación");
        botonGrafica.setFocusable(false);
        botonGrafica.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonGrafica.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonGrafica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGraficaActionPerformed(evt);
            }
        });
        barraVideo.add(botonGrafica);

        botonComparar2Videos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/comparar.png"))); // NOI18N
        botonComparar2Videos.setToolTipText("Compara los vídeos abiertos");
        botonComparar2Videos.setFocusable(false);
        botonComparar2Videos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonComparar2Videos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonComparar2Videos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonComparar2VideosActionPerformed(evt);
            }
        });
        barraVideo.add(botonComparar2Videos);

        comboComparador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Min", "Max", "Median", "Average" }));
        comboComparador.setToolTipText("Tipo de comparador");
        barraVideo.add(comboComparador);

        panelBarraHerramientas.add(barraVideo);

        barraComparador.setRollover(true);

        labelVideoA.setText("Video A");
        barraComparador.add(labelVideoA);

        botonPerteneceIgual.setFont(botonPerteneceIgual.getFont().deriveFont(botonPerteneceIgual.getFont().getSize()+4f));
        botonPerteneceIgual.setText("⊂");
        botonPerteneceIgual.setToolTipText("El vídeo pertenece o es igual a otro");
        botonPerteneceIgual.setFocusable(false);
        botonPerteneceIgual.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonPerteneceIgual.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonPerteneceIgual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPerteneceIgualActionPerformed(evt);
            }
        });
        barraComparador.add(botonPerteneceIgual);

        labelVideoB.setText("vídeo B");
        barraComparador.add(labelVideoB);

        panelBarraHerramientas.add(barraComparador);

        restoBarraComparador.setRollover(true);

        toggleOrdered.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/unordered.png"))); // NOI18N
        toggleOrdered.setSelected(true);
        toggleOrdered.setToolTipText("Consulta ordenada o desordenada");
        toggleOrdered.setFocusable(false);
        toggleOrdered.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleOrdered.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ordered.png"))); // NOI18N
        toggleOrdered.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        restoBarraComparador.add(toggleOrdered);

        panelBarraHerramientas.add(restoBarraComparador);

        getContentPane().add(panelBarraHerramientas, java.awt.BorderLayout.PAGE_START);

        barraEstado.setLayout(new java.awt.BorderLayout());

        posicionPixel.setText("  ");
        barraEstado.add(posicionPixel, java.awt.BorderLayout.LINE_START);

        infoDB.setText("Not open");
        barraEstado.add(infoDB, java.awt.BorderLayout.EAST);

        getContentPane().add(barraEstado, java.awt.BorderLayout.SOUTH);

        menuArchivo.setText("File");

        menuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open16.png"))); // NOI18N
        menuAbrir.setText("Open");
        menuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(menuAbrir);

        menuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save16.png"))); // NOI18N
        menuGuardar.setText("Save");
        menuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(menuGuardar);
        menuArchivo.add(separador1);

        closeAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/closeall16.png"))); // NOI18N
        closeAll.setText("Close all");
        closeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllActionPerformed(evt);
            }
        });
        menuArchivo.add(closeAll);

        menuBar.add(menuArchivo);

        menuVer.setText("View");

        verGrid.setSelected(true);
        verGrid.setText("Show grid");
        verGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verGridActionPerformed(evt);
            }
        });
        menuVer.add(verGrid);

        usarTransparencia.setSelected(true);
        usarTransparencia.setText("Use transparency");
        menuVer.add(usarTransparencia);
        menuVer.add(jSeparator2);

        showResized.setText("Show resized images");
        menuVer.add(showResized);
        menuVer.add(jSeparator1);

        menuZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom16.png"))); // NOI18N
        menuZoom.setText("Zoom");

        menuZoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0));
        menuZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom-in16.png"))); // NOI18N
        menuZoomIn.setText("Zoom in");
        menuZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoomInActionPerformed(evt);
            }
        });
        menuZoom.add(menuZoomIn);

        menuZoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0));
        menuZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom-out16.png"))); // NOI18N
        menuZoomOut.setText("Zoom out");
        menuZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoomOutActionPerformed(evt);
            }
        });
        menuZoom.add(menuZoomOut);

        menuVer.add(menuZoom);

        menuBar.add(menuVer);

        setJMenuBar(menuBar);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /*
    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        BufferedImage img;
        JFileChooser dlg = new JFileChooser();
        dlg.setMultiSelectionEnabled(true);
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {                
                File files[] = dlg.getSelectedFiles();              
                for (File f : files) {
                    img = ImageIO.read(f);
                    if (img != null) {
                        ImageInternalFrame vi = new JMRImageInternalFrame(this, img, f.toURI().toURL());
                        vi.setTitle(f.getName());
                        this.showInternalFrame(vi);
                    }
                }               
            } catch (Exception ex) {
                JOptionPane.showInternalMessageDialog(escritorio, "Error in image opening", "Image", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_menuAbrirActionPerformed
    */
    
    /**
     * Gestiona la apertura de archivos
     * @param evt Evento generardo al abrirse el menú 
     */
    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt){
        JFileChooser dlg = new JFileChooser();
        dlg.setAcceptAllFileFilterUsed(false); //No permite todos los formatos
        dlg.setMultiSelectionEnabled(true); //Selección múltiple
        
        String nombresExImagenes = "";
        
        for (String format : ImageIO.getReaderFormatNames()) {
            nombresExImagenes += format + ", ";
        }
        
        String[] extensionesVideo = {"avi", "mpg", "mpeg", "mov", "mp4", "mkv"};
        String nombresExVideos = "";
        
        for(String exVideo : extensionesVideo) {
            nombresExVideos += exVideo + ", ";
        }
        
        //Se genera el texto que se muestra en el diálogo de selección de tipo
        //y se le asignan los formatos admitidos.
        
        FileNameExtensionFilter filterImagen = new FileNameExtensionFilter("Archivos de imagen: (" + nombresExImagenes + ")", ImageIO.getReaderFormatNames());        
        FileNameExtensionFilter filterVideo = new FileNameExtensionFilter("Archivos de video: (" + nombresExVideos + ")", extensionesVideo);
        dlg.addChoosableFileFilter(filterVideo);
        dlg.addChoosableFileFilter(filterImagen);
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            if(dlg.getFileFilter() == filterImagen){
                abreImagen(dlg.getSelectedFiles());
            }else if(dlg.getFileFilter() == filterVideo){
                abreVideo(dlg.getSelectedFiles());
            }
        }
    }
    
    /**
     * A partir de un conjunto de imágenes, crea las ventanas de imagen y las asigna
     * @param files Conjunto de imágenes (ficheros)
     */
    private void abreImagen(File[] files){
        try {
            BufferedImage img;
            
            for (File f : files) {
                img = ImageIO.read(f);
                if (img != null) {
                    ImageInternalFrame vi = new JMRImageInternalFrame(this, img, f.toURI().toURL());
                    ((JMRImageInternalFrame)vi).setFile(f);
                    vi.setTitle(f.getName());
                    this.showInternalFrame(vi);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showInternalMessageDialog(escritorio, "Error in image opening", "Image", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * A partir de un conjunto de vídeos, crea ventanas internas de vídeo y las asigna
     * @param files Conjunto de vídeos (ficheros)
     */
    private void abreVideo(File [] files){
        Point pAnterior = null;
        try {            
            
            for (File f : files) {           

                VideoInternalFrame vi = new VideoInternalFrame(f, this);
                vi.setTitle(f.getName());
                this.showInternalFrame(vi);
                if(pAnterior!=null){
                    vi.setLocation(pAnterior.x + vi.getWidth(), 0);
                }
                
                pAnterior = vi.getLocation();
                vi.startPlaying();
                
            }
        } catch (Exception ex) {
            JOptionPane.showInternalMessageDialog(escritorio, "Error in video opening", "Video", JOptionPane.ERROR_MESSAGE);
            System.out.print(ex);
        }
    }
    
    /**
     * Activa las herramientas de vídeo
     * @param enable True si se activan las herramientas de vídeo y viceversa
     */
    public void enableVideoTools(Boolean enable){
        botonSegmentar.setEnabled(enable);
        botonPlaybarra.setEnabled(enable);
        botonRewindBarra.setEnabled(enable);
    }

    /**
     * Activa las herramientas de segmentación
     * @param enable True si se activan las herramientas de segmentación y viceversa
     */
    public void enableSegmentationTools(Boolean enable){
        botonGrafica.setEnabled(enable);
    }
    
    /**
     * Gestiona el guardado de archivos
     * @param evt Evento generado al solicitarse el guardado
     */
    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        BufferedImage img = this.getSelectedImage();
        if (img != null) {
            JFileChooser dlg = new JFileChooser();
            int resp = dlg.showSaveDialog(this);
            if (resp == JFileChooser.APPROVE_OPTION) {
                File f = dlg.getSelectedFile();
                try {
                    ImageIO.write(img, "png", f);
                    escritorio.getSelectedFrame().setTitle(f.getName());
                } catch (Exception ex) {
                    JOptionPane.showInternalMessageDialog(escritorio, "Error in image saving", "Image", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }      
    }//GEN-LAST:event_menuGuardarActionPerformed

    /**
     * Gestiona la opción de abrir
     * @param evt Evento generado al abrir
     */
    private void botonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirActionPerformed
        this.menuAbrirActionPerformed(evt);
    }//GEN-LAST:event_botonAbrirActionPerformed

    /**
     * Gestiona la opción de guardar
     * @param evt Evento generado al guardar
     */
    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarActionPerformed
        this.menuGuardarActionPerformed(evt);
    }//GEN-LAST:event_botonGuardarActionPerformed
    
    /**
     * Limpia el panel de salida
     * @param evt Evento generado al clickarse en limpiar
     */
    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        this.editorOutput.setText("");
    }//GEN-LAST:event_clearActionPerformed

    /**
     * Manejo del evento generado al cambiar las propiedades del panel de salida
     * @param evt Evento generado al modificarse el panel.
     */
    private void splitPanelCentralPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_splitPanelCentralPropertyChange
        if (evt.getPropertyName().equals("dividerLocation")) {
            float dividerLocation = (float) splitPanelCentral.getDividerLocation() / splitPanelCentral.getMaximumDividerLocation();
            if (dividerLocation >= 1) {//Está colapsada
                showPanelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/desplegar20.png")));
            } else {
                showPanelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cerrar16.png")));
            }
        }
    }//GEN-LAST:event_splitPanelCentralPropertyChange

    /**
     * Muestra informacion en el panel de salida cuando se suelta el ratón
     * @param evt Evento generado al soltar el ratón
     */
    private void editorOutputMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editorOutputMouseReleased
        if(evt.isPopupTrigger()){
            Point p = this.scrollEditorOutput.getMousePosition();
            this.popupMenuPanelOutput.show(this.panelOutput,p.x,p.y);
        }
    }//GEN-LAST:event_editorOutputMouseReleased

    /**
     * Muestra la información cuando se pulsa el ratón
     * @param evt Evento generado al pulsar el ratón
     */
    private void showPanelInfoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showPanelInfoMousePressed
        float dividerLocation = (float)splitPanelCentral.getDividerLocation()/splitPanelCentral.getMaximumDividerLocation();
        if(dividerLocation>=1) {//Está colapsada
            splitPanelCentral.setDividerLocation(0.8);
        } else{
            splitPanelCentral.setDividerLocation(1.0);
        }
    }//GEN-LAST:event_showPanelInfoMousePressed

    /**
     * Cierra todas las ventanas internas
     * @param evt Evento generado al llamar a limpiar
     */
    private void closeAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllActionPerformed
       escritorio.removeAll();
       escritorio.repaint();
    }//GEN-LAST:event_closeAllActionPerformed

    /**
     * Muestra el panel de preferencias
     * @param evt Evento generado al pulsar el botón
     */
    private void botonPreferenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPreferenciasActionPerformed
        PreferencesDialog dlg = new PreferencesDialog(this);
        dlg.showDialog();
    }//GEN-LAST:event_botonPreferenciasActionPerformed

    /**
     * Reduce el zoom
     * @param evt Evento generado al pulsar el botón
     */
    private void menuZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoomOutActionPerformed
        ImageInternalFrame vi = this.getSelectedImageFrame();
        if (vi != null) {
            int zoom = vi.getZoom();
            if(zoom>=2){
                vi.setZoom(zoom-1);
                vi.repaint();
            }
        }
    }//GEN-LAST:event_menuZoomOutActionPerformed

    /**
     * Aumenta el zoom
     * @param evt Evento generado al pulsar el botón
     */
    private void menuZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoomInActionPerformed
        ImageInternalFrame vi = this.getSelectedImageFrame();
        if (vi != null) {
            vi.setZoom(vi.getZoom()+1);
            vi.repaint();
        }
    }//GEN-LAST:event_menuZoomInActionPerformed

    /**
     * Activa el grid en las ventanas abiertas
     * @param evt Evento generado al pulsar el botón
     */
    private void verGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verGridActionPerformed
        JInternalFrame ventanas[] = escritorio.getAllFrames();
        for(JInternalFrame vi: ventanas){
            ((ImageInternalFrame)vi).setGrid(this.verGrid.isSelected());
            vi.repaint();
        }
    }//GEN-LAST:event_verGridActionPerformed

    /**
     * Genera un descriptor a partir de las imágenes abiertas
     * @param evt Evento generado al pulsar el botón
     */
    private void botonDCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDCDActionPerformed
        BufferedImage img = this.getSelectedImage();
        if (img != null) {  
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            MPEG7DominantColors dCDC = new MPEG7DominantColors();
            BufferedImage imgResized = dCDC.calculate(img,true); //Calculamos el desciptor
            setCursor(cursor);
            //Mostramos resultado
            JMRImageInternalFrame vi = this.getSelectedImageFrame();
            vi.setDominantColorDescriptor(dCDC);
            String text = editorOutput.getText();
            text += "DCD ("+this.getSelectedFrameTitle()+")\n";
            int colorIndex = 1;
            for(MPEG7SingleDominatColor c : dCDC.getDominantColors()){
                text += "   Color "+(colorIndex++)+": [" + c.getColor().getRed() + "," + c.getColor().getGreen() + "," + c.getColor().getBlue()+"]\n";
            }
            this.editorOutput.setText(text);            
            if(this.showResized.isSelected()){
                JMRImageInternalFrame vi_resized = new JMRImageInternalFrame(null, imgResized);
                vi_resized.setTitle(this.getSelectedFrameTitle()+" resized");
                this.showInternalFrame(vi_resized);     
            }
        }
    }//GEN-LAST:event_botonDCDActionPerformed

    /**
     * Realiza una búsqueda de la ventana interna seleccionada en el resto de ventanas
     * @param evt Evento generado al pulsar el botón
     */
    private void botonComparaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonComparaActionPerformed
        JMRImageInternalFrame viAnalyzed, viQuery = this.getSelectedImageFrame();
        if (viQuery != null) {
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                        
            //Calculamos descriptores en la imagen consulta
            ArrayList<MediaDescriptor> descriptores_query = new ArrayList();
            if (this.colorDominante.isSelected()) {
                MPEG7DominantColors dcd_query = viQuery.getDominantColorDescriptor();
                if (dcd_query == null) {
                    dcd_query = new MPEG7DominantColors();
                    dcd_query.calculate(this.getSelectedImage(), true);
                    viQuery.setDominantColorDescriptor(dcd_query);
                }
                descriptores_query.add(dcd_query);                
            }
            if (this.colorEstructurado.isSelected()) {
                JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(this.getSelectedImage());
                MPEG7ColorStructure dcs_query = new MPEG7ColorStructure(imgJMR);
                descriptores_query.add(dcs_query);
            }
            if (this.colorEscalable.isSelected()) {    
                JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(this.getSelectedImage());
                MPEG7ScalableColor dsc_query = new MPEG7ScalableColor(imgJMR);  
                descriptores_query.add(dsc_query);
            }
            if (this.colorMedio.isSelected()) {
                JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(this.getSelectedImage());
                SingleColorDescriptor dmean_query = new SingleColorDescriptor(imgJMR);
                descriptores_query.add(dmean_query);
            }
            if (this.texturaHomogeneidad.isSelected()) {
                MPEG7HomogeneousTexture htd_query = new MPEG7HomogeneousTexture();                
                JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(this.getSelectedImage());
                htd_query.init(imgJMR);
                descriptores_query.add(htd_query);
            }
            
            //Comparamos la imagen consulta con el resto de imágenes del escritorio                        
            Vector vresult;
            
            List<ResultMetadata> resultList = new LinkedList<>();
            
            String text = editorOutput.getText();
            JInternalFrame ventanas[] = escritorio.getAllFrames();           
            for (JInternalFrame vi : ventanas) {                
                if (vi instanceof JMRImageInternalFrame) {
                    viAnalyzed = (JMRImageInternalFrame) vi;

                    Iterator<MediaDescriptor> itQuery = descriptores_query.iterator();
                    MediaDescriptor current_descriptor;
                    vresult = new Vector(descriptores_query.size());
                    int index = 0;

                    //DCD
                    if (this.colorDominante.isSelected()) {
                        MPEG7DominantColors dcd_analyzed = viAnalyzed.getDominantColorDescriptor();
                        if (dcd_analyzed == null) {
                            dcd_analyzed = new MPEG7DominantColors();
                            dcd_analyzed.calculate(viAnalyzed.getImage(), true);
                            viAnalyzed.setDominantColorDescriptor(dcd_analyzed);
                        }
                        current_descriptor = itQuery.next();
                        FloatResult result = (FloatResult) current_descriptor.compare(dcd_analyzed);
                        vresult.setCoordinate(index++, result.toDouble());
                    }
                    //CSD
                    if (this.colorEstructurado.isSelected()) {
                        JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(viAnalyzed.getImage());
                        MPEG7ColorStructure dcs_analyzed = new MPEG7ColorStructure(imgJMR);
                        current_descriptor = itQuery.next();
                        Double result = (Double)current_descriptor.compare(dcs_analyzed);                        
                        vresult.setCoordinate(index++, result);
                    }
                    //SCD
                    if (this.colorEscalable.isSelected()) {        
                        JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(viAnalyzed.getImage());
                        MPEG7ScalableColor dsc_analyzed = new MPEG7ScalableColor(imgJMR);
                        current_descriptor = itQuery.next();
                        Double result = (Double) current_descriptor.compare(dsc_analyzed);
                        vresult.setCoordinate(index++, result);
                    }
                    // Mean color
                    if (this.colorMedio.isSelected()) {
                        JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(viAnalyzed.getImage());                        
                        SingleColorDescriptor dmean_analyzed = new SingleColorDescriptor(imgJMR);
                        current_descriptor = itQuery.next();
                        Double compare = (Double)current_descriptor.compare(dmean_analyzed);
                        FloatResult result = new FloatResult(compare.floatValue());
                        vresult.setCoordinate(index++, result.toDouble());
                    }
                    
                    //HTD
                    if (this.texturaHomogeneidad.isSelected()) {
                        MPEG7HomogeneousTexture htd_analyzed = new MPEG7HomogeneousTexture();
                        JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(viAnalyzed.getImage());
                        htd_analyzed.init(imgJMR);
                        current_descriptor = itQuery.next();
                        FloatResult result = (FloatResult) current_descriptor.compare(htd_analyzed);
                        vresult.setCoordinate(index++, result.toDouble());
                    }
                    resultList.add(new ResultMetadata(vresult, viAnalyzed.getImage()));
                    text += "Dist("+viQuery.getTitle()+","+viAnalyzed.getTitle()+") = ";
                    text += vresult!=null ? vresult.toString()+"\n" : "No calculado\n"; 
                }
            }            
            this.editorOutput.setText(text); 
            setCursor(cursor);     
            //Creamas la ventana interna con los resultados
            resultList.sort(null);
            ImageListInternalFrame listFrame = new ImageListInternalFrame(resultList);
            this.escritorio.add(listFrame);
            listFrame.setVisible(true);                
        }
    }//GEN-LAST:event_botonComparaActionPerformed

    /**
     * Genera el descriptor de color simple de la ventana abierta
     * @param evt Evento generado al pulsar el botón
     */
    private void botonSingleColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSingleColorActionPerformed
        BufferedImage img = this.getSelectedImage();
        if (img != null) {  
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            ColorSetPanel panelColor = new ColorSetPanel();
            if (this.useGridButton.isSelected()) {
                Dimension gridSize = GlobalSettings.getGridSize();                
                SquareGrid<BufferedImage> grid = new SquareGrid(img, gridSize);
                BufferedImage tileImage;
                for (int tileX = 0; tileX < grid.getGridWidth(); tileX++) {
                    for (int tileY = 0; tileY < grid.getGridHeight(); tileY++) {
                        tileImage = grid.getTile(tileX, tileY);
                        addMeanColorDescriptor(tileImage, panelColor);
                    }
                }
                
                //Pruebas
                GriddedDescriptor<BufferedImage> gd;
                //gd = new GriddedDescriptor(grid,MPEG7ColorStructure.class);
                gd = new GriddedDescriptor(img, gridSize, MPEG7ColorStructure.class);
                System.out.println(gd.toString());
                Double distance = gd.compare(gd);
                System.out.println("Distancia : "+distance);
                //fin pruebas
                
            } else {
                addMeanColorDescriptor(img, panelColor);
            }
            setCursor(cursor);
            //Mostramos resultado
            JMRImageInternalFrame vi = this.getSelectedImageFrame();                       
            vi.add(panelColor, BorderLayout.EAST);
            vi.validate();
            vi.repaint();             
        }
    }//GEN-LAST:event_botonSingleColorActionPerformed

    /**
     * Añade el descriptor medio de color
     * @param img Imagen sobre la que se calcula el descrittor
     * @param panelColor Panel de color en el que se añade el descriptor
     */
    private void addMeanColorDescriptor(BufferedImage img, ColorSetPanel panelColor){
            SingleColorDescriptor d = new SingleColorDescriptor(img);
            panelColor.addColor(d.getColor());
            String text = editorOutput.getText();
            text += "Single Color ("+this.getSelectedFrameTitle()+")";
            if(d!=null && d.getColor()!=null){
                text += " : [" + d.getColor().getRed() + "," + d.getColor().getGreen() + "," + d.getColor().getBlue()+"]\n";
            } else {
                text += "Null";
            }
            this.editorOutput.setText(text);      
    }
    
    /**
     * Dibuja el grid en una imagen seleccionada
     * @param evt Evento generado al pulsar el botón
     */
    private void drawGridButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawGridButtonActionPerformed
        BufferedImage img = this.getSelectedImage();
        if (img != null) {
            Dimension gridSize = GlobalSettings.getGridSize();
            SquareGrid grid = new SquareGrid(img, gridSize);
            BufferedImage griddedImage = new BufferedImage(img.getTileWidth() + grid.getGridWidth() - 1, img.getHeight() + grid.getGridHeight() - 1, img.getType());
            Graphics2D g = griddedImage.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, griddedImage.getWidth(), griddedImage.getHeight());
            BufferedImage tileImage;
            for (int tileX = 0; tileX < grid.getGridWidth(); tileX++) {
                for (int tileY = 0; tileY < grid.getGridHeight(); tileY++) {
                    tileImage = grid.getTile(tileX, tileY);
                    g.drawImage(tileImage, tileX * (grid.getTileWidth() + 1), tileY * (grid.getTileHeight() + 1), this);
                }
            }
            JMRImageInternalFrame vi_grid = new JMRImageInternalFrame(null, griddedImage);
            vi_grid.setTitle(this.getSelectedFrameTitle() + " gridded");
            this.showInternalFrame(vi_grid);
        }
    }//GEN-LAST:event_drawGridButtonActionPerformed

    /**
     * Activa o desactiva los botones de la base de datos
     * @param closed True si los botones se desactivan y viceversa
     */
    private void setDataBaseButtonStatus(boolean closed){
        this.botonNewDB.setEnabled(closed);
        this.botonOpenDB.setEnabled(closed);
        this.botonCloseDB.setEnabled(!closed);
        this.botonSaveDB.setEnabled(!closed);
        this.botonAddRecordDB.setEnabled(!closed);
        this.botonSearchDB.setEnabled(!closed);
    }
    
    /**
     *  Devuelve una lista de descriptores en función de los seleccionados en la base de datos
     * @return L Lista de descriptores en función de los seleccionados en la base de datos
     */
    private Class[] getDBDescriptorClasses(){
        ArrayList<Class> outputL = new ArrayList<>();
        if (this.colorEstructuradoDB.isSelected())
            outputL.add(MPEG7ColorStructure.class);
        if (this.colorEscalableDB.isSelected())
            outputL.add(MPEG7ScalableColor.class);
        if (this.colorMedioDB.isSelected())
            outputL.add(SingleColorDescriptor.class);
        Class output[] = new Class[outputL.size()];
        for(int i=0; i<outputL.size(); i++)
            output[i] = outputL.get(i);
        return output;
    }
    
    /**
     * Crea la base de datos
     * @param evt Evento generado a pulsar el botón
     */
    private void botonNewDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonNewDBActionPerformed
        // Creamos la base de datos vacía
        database = new ListDB(getDBDescriptorClasses());
        // Activamos/desactivamos botones
        setDataBaseButtonStatus(false);
        updateInfoDBStatusBar("New DB (not saved)");
    }//GEN-LAST:event_botonNewDBActionPerformed

    /**
     * Cierra la base de datos
     * @param evt Evento generado al pulsar el botón
     */
    private void botonCloseDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCloseDBActionPerformed
        database.clear();
        database = null;
        // Activamos/desactivamos botones
        setDataBaseButtonStatus(true);
        updateInfoDBStatusBar(null);
    }//GEN-LAST:event_botonCloseDBActionPerformed

    /**
     * Graba la base de datos
     * @param evt Evento generado al pulsar el botón
     */
    private void botonAddRecordDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAddRecordDBActionPerformed
        if (database != null) {
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            //Incorporamos a la BD todas las imágenes del escritorio
            JInternalFrame ventanas[] = escritorio.getAllFrames();
            JMRImageInternalFrame viAnalyzed;
            for (JInternalFrame vi : ventanas) {
                if (vi instanceof JMRImageInternalFrame) {
                    viAnalyzed = (JMRImageInternalFrame) vi;
                    database.add(viAnalyzed.getImage(),viAnalyzed.getURL());
                }
            }
            //System.out.println(database.toString());
            setCursor(cursor);
            updateInfoDBStatusBar("Updated DB (not saved)");
        }
    }//GEN-LAST:event_botonAddRecordDBActionPerformed

    /**
     * Busca un una base de datos existente
     * @param evt Evento generado al pulsar el botón
     */
    private void botonSearchDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSearchDBActionPerformed
        if (database != null) {
            BufferedImage img = this.getSelectedImage();
            if (img != null) {
                ListDB.Record q_record = database.new Record(img);
                //List<ResultMetadata> resultList = database.queryMetadata(q_record);
                List<ListDB.Record> queryResult = database.query(q_record,10);
                //List<ResultMetadata> resultList = new LinkedList<>();
                ImageListInternalFrame listFrame = new ImageListInternalFrame();
                for(ListDB.Record r: queryResult){
                    //resultList.add(new ResultMetadata(0.0, r.getSource()));
                    listFrame.add(r.getLocator(),r.getLocator().getFile());                   
                }                
                //Creamas la ventana interna con los resultados
                //ImageListInternalFrame listFrame = new ImageListInternalFrame(resultList);
                this.escritorio.add(listFrame);
                listFrame.setVisible(true);
            }
        }
    }//GEN-LAST:event_botonSearchDBActionPerformed

    /**
     * Actualiza la información dependiendo del estado de la BD
     * @param fichero Fichero de la base de datos
     */
    private void updateInfoDBStatusBar(String fichero) {
        String infoDB = "Not open";
        if (database != null) {
            infoDB = fichero + " [#" + database.size() + "] [";
            for (Class c : database.getDescriptorClasses()) {
                infoDB += c.getSimpleName() + ",";
            }
            infoDB = infoDB.substring(0, infoDB.length() - 1) + "]";
        }
        this.infoDB.setText(infoDB);
    }
    
    /**
     * Abre la base de datos
     * @param evt Evento generado al pulsar el botón
     */
    private void botonOpenDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonOpenDBActionPerformed
        String fichero = "prueba.jmr.db";
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero));
            database = (ListDB<BufferedImage>) ois.readObject();
            ois.close();
            setDataBaseButtonStatus(false);
            //Actualizamos info en barra de estado
            updateInfoDBStatusBar(fichero);     
        } catch (FileNotFoundException ex) {
             System.err.println(ex);
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }
    }//GEN-LAST:event_botonOpenDBActionPerformed

    /**
     * Guarda la base de datos
     * @param evt Evento generado al pulsar el botón
     */
    private void botonSaveDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSaveDBActionPerformed
        String fichero = "prueba.jmr.db";
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichero));
            oos.writeObject(database);
            oos.close();
            updateInfoDBStatusBar(fichero);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_botonSaveDBActionPerformed

    /**
     * Realiza la segmentación de un vídeo por escenas
     * @param evt Evento generado al pulsar el botón
     */
    private void botonSegmentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSegmentarActionPerformed
        VideoInternalFrame vif = getSelectedVideoFrame();
        if(vif != null) {
            File file = vif.getFile();
            SegmentationOp op = null;
            try {
                op = createSegmentationOp(file, false);
            } catch (NoSuchMethodException | FrameGrabber.Exception ex) {
                Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | NoPlayerException | CannotRealizeException ex) {
                Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
             ArrayList<BufferedImage> kframes;

            if (op != null) {               
                
                Thread t = new Thread(op); 
                
                t.start(); 

                JInternalFrame f = new JInternalFrame();
                Container content = f.getContentPane();
                JProgressBar progressBar = new JProgressBar();
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                Border border = BorderFactory.createTitledBorder("Procesando...");
                progressBar.setBorder(border);
                content.add(progressBar, BorderLayout.NORTH);
                f.setSize(300, 100);     
                f.setLocation(vif.getLocation().x+vif.getWidth(), vif.getLocation().y);
                this.escritorio.add(f);
                f.setVisible(true);
                
                
                final SegmentationOp opCopy = op;
                final JProgressBar progressCopy = progressBar;

                Thread th = new Thread() {
                    @Override
                    public void run() {                        
                        while(opCopy.getPercentageDone()<95){
                            progressCopy.setValue(opCopy.getPercentageDone());
                            System.out.println(opCopy.getPercentageDone());
                            try {
                                sleep(100);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        
                        try {
                            f.setVisible(false);
                            pintaKeyframes(opCopy, vif);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                };
                th.start();
                

            }
            
        }
    }//GEN-LAST:event_botonSegmentarActionPerformed

    /**
     * Espera a que la segmentación termine y muestra los keyframes generados
     * @param op Operador de segmentación
     * @param vif Ventana interna de video origen del medio
     * @throws InterruptedException  Excepción de interrupción
     */
    public void pintaKeyframes(SegmentationOp op, VideoInternalFrame vif) throws InterruptedException {
        ImageListInternalFrame listFrame = new ImageListInternalFrame(this);
        while (op.getImageKeyframes() == null) {
            sleep(500);
        }
        ArrayList<BufferedImage> kframes = op.getImageKeyframes();
        listFrame.setVariations(op.getVariations());
        listFrame.setNombreVideo(vif.getTitle());

        for (BufferedImage kframe : kframes) {
            listFrame.add(kframe);
        }
        listFrame.setTitle("Keyframes de " + vif.getTitle());
        listFrame.setLocation(vif.getLocation().x+vif.getWidth(), vif.getLocation().y);
        this.escritorio.add(listFrame);
        listFrame.setVisible(true);
    }

    /**
     * Dibuja una gráfica de variación tras segmentar un vídeo
     * @param evt Evento generado al pulsar el botón
     */
    private void botonGraficaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGraficaActionPerformed
        ImageListInternalFrame vi = getSelectedImageListFrame();
        if(vi!=null){
            GraphInternalFrame gif = new GraphInternalFrame(vi.getVariations(), vi.getNombreVideo());
            gif.setTitle("Variaciones");
            this.escritorio.add(gif);
            gif.setLocation(vi.getLocation().x, vi.getLocation().y+vi.getHeight());
            gif.setVisible(true);
        }else{
            VideoInternalFrame vif = getSelectedVideoFrame();
            if(vif!=null && vif.getKeyFrameDescriptor()!=null){
                SegmentationOp sOp = (SegmentationOp)vif.getVideoIterator();
                GraphInternalFrame gif = new GraphInternalFrame(sOp.getVariations(), vif.getTitle());
                gif.setTitle("Variaciones");
                this.escritorio.add(gif);
                gif.setLocation(vif.getLocation().x, vif.getLocation().y+vif.getHeight());
                gif.setVisible(true);
            }else{
                JOptionPane.showInternalMessageDialog(escritorio, "Debe precargar el vídeo para mostar la gráfica", "Ventana de vídeo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_botonGraficaActionPerformed

    /**
     * Compara 2 o más vídeos abiertos
     * @param evt Evento generado al pulsar el botón
     */
    private void botonComparar2VideosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonComparar2VideosActionPerformed
        ArrayList<VideoInternalFrame> vFrames = getAllVideoFrames();
        ArrayList<JMRImageInternalFrame> iFrames = getAllImageFrames();
        if(vFrames.size() + iFrames.size()<2){
            JOptionPane.showInternalMessageDialog(escritorio, "Debe abrir 2 vídeos", "Ventana de vídeo", JOptionPane.ERROR_MESSAGE);
        }else{
            precarga();
            ArrayList<VideoInternalFrame> videoFrames = getAllVideoFrames();
            ArrayList<JMRImageInternalFrame> imageFrames = getAllImageFrames();
            JMRImageInternalFrame selectedImageFrame = getSelectedImageFrame();
            VideoInternalFrame selectedVideoFrame = getSelectedVideoFrame();
            KeyFrameDescriptor kfdSeleccionado = null;
            String tituloSeleccionado = null;
            
            if(selectedImageFrame!=null){ //Hay una imagen seleccionada
                imageFrames.remove(selectedImageFrame);
                kfdSeleccionado = selectedImageFrame.getKeyFrameDescriptor();
                tituloSeleccionado = selectedImageFrame.getTitle();
                System.out.println(selectedImageFrame.getHeight());
            }else if(selectedVideoFrame!=null){//Hay un vídeo seleccionado
                videoFrames.remove(selectedVideoFrame);
                kfdSeleccionado = selectedVideoFrame.getKeyFrameDescriptor();
                tituloSeleccionado = selectedVideoFrame.getTitle();
            }else{
                JOptionPane.showMessageDialog(null, "No hay ninguna ventana seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            //Creamos el arraylist con los descriptores y archivos, para más
            //tarde añadir distancias
            ArrayList<KeyFrameDescriptorStruct> kfStruct = new ArrayList<>();
            
            for (JMRImageInternalFrame imageFrame : imageFrames) {
                KeyFrameDescriptorStruct kfds = new KeyFrameDescriptorStruct();
                kfds.keyFrameDescriptor = imageFrame.getKeyFrameDescriptor();
                kfds.file = imageFrame.getFile();  
                kfStruct.add(kfds);
            }
            
            for (VideoInternalFrame videoFrame : videoFrames) {
                KeyFrameDescriptorStruct kfds = new KeyFrameDescriptorStruct();
                kfds.keyFrameDescriptor = videoFrame.getKeyFrameDescriptor();
                kfds.file = videoFrame.getFile();   
                kfStruct.add(kfds);
            }
            
            //Realizamos la comparacion
            for (KeyFrameDescriptorStruct oneDescriptor : kfStruct) {
                oneDescriptor.distance = kfdSeleccionado.compare(oneDescriptor.keyFrameDescriptor);
            }
            
            //Ordenamos el resultado
            Collections.sort(kfStruct, new KeyFrameDescriptorStructComparator());
            muestraListaVideos(kfStruct, tituloSeleccionado);
        }
    }//GEN-LAST:event_botonComparar2VideosActionPerformed

    /**
     * Muestra una lista de vídeos tras su consulta
     * @param kfStruct Estructura con la información del descriptor de vídeo, distancia y fichero
     * @param titulo Título del vídeo consulta
     */
    private void muestraListaVideos(ArrayList<KeyFrameDescriptorStruct> kfStruct, String titulo){
        VideoListInternalFrame vi = new VideoListInternalFrame();               
        escritorio.add(vi);
        for (KeyFrameDescriptorStruct oneStruct : kfStruct) {
            vi.add(oneStruct.file, oneStruct.distance);
        }
        vi.setTitle("Búsqueda para " + titulo);
        vi.setLocation(0, 500);
        vi.setVisible(true);
        vi.init();
    }
    
    private void colorEstructuradoUnicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorEstructuradoUnicoActionPerformed
        
    }//GEN-LAST:event_colorEstructuradoUnicoActionPerformed
    
    /**
     * Devuelve el botón de rebobinado
     * @return Boón de rebobinado
     */
    public JButton getBotonRewind(){
        return botonRewindBarra;
    }
    
    /**
     * Devuelve el botón de play
     * @return Botón de play
     */
    public JToggleButton getBotonPlay(){
        return botonPlaybarra;
    }
    
    /**
     * Gestiona la reproducción y pause de un vídeo
     * @param evt Evento generado al pulsar el botón
     */
    private void botonPlaybarraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPlaybarraActionPerformed
        VideoInternalFrame vi = getSelectedVideoFrame();
        if(vi!=null){
            if(!botonPlaybarra.isSelected()){ //Si está reproduciendo
                vi.pause();
            }else{ //Si está pausado
                vi.play();
            }
        }
    }//GEN-LAST:event_botonPlaybarraActionPerformed

    /**
     * Rebobina un vídeo
     * @param evt Evento generado al pulsar el botón
     */
    private void botonRewindBarraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRewindBarraActionPerformed
        VideoInternalFrame vi = getSelectedVideoFrame();
        if(vi!=null){
            vi.rewind();
        }
    }//GEN-LAST:event_botonRewindBarraActionPerformed

    
    /**
     * Crea una barra de progreso mientras se segmenta un vídeo
     * @param vif Ventana fuente del vídeo a segmentar
     * @return Estructura con la barra de progreso
     */
    private JInternalFrame creaBarraProgresoIndefinida(VideoInternalFrame vif) {
        JInternalFrame f = new JInternalFrame();
        //final JDesktopPane copiaEscritorio = escritorio;
        Thread th = new Thread() {
            @Override
            public void run() {                
                Container content = f.getContentPane();
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                progressBar.setStringPainted(true);
                content.add(progressBar, BorderLayout.NORTH);
                f.setSize(300, 100);
                String titulo = "Procesando";
                if (vif != null) {
                    f.setLocation(vif.getLocation().x + vif.getWidth(), vif.getLocation().y);
                    titulo += vif.getName();
                }
                titulo += "...";
                Border border = BorderFactory.createTitledBorder(titulo);
                progressBar.setBorder(border);
                escritorio.add(f);
                f.setVisible(true);
            }
        };
        th.run();

        return f;
    }
    
    private void colorEscalableUnicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorEscalableUnicoActionPerformed
        
    }//GEN-LAST:event_colorEscalableUnicoActionPerformed

    private void colorMedioUnicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorMedioUnicoActionPerformed
        
    }//GEN-LAST:event_colorMedioUnicoActionPerformed

    /**
     * Gestiona el cambio de interfaz del botón perteneceIgual
     * @param evt Evento generado al pulsar el botón
     */
    private void botonPerteneceIgualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPerteneceIgualActionPerformed
        if(botonPerteneceIgual.getText().equals("⊂")){
            botonPerteneceIgual.setText("=");
        }else{
            botonPerteneceIgual.setText("⊂");
        }
    }//GEN-LAST:event_botonPerteneceIgualActionPerformed

    /**
     * Obtiene el descriptor de keyframe de los vídeos e imágenes abiertos
     */
    private void precarga(){
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        ArrayList<VideoInternalFrame> vFrames = getAllVideoFrames();
        ArrayList<JMRImageInternalFrame> iFrames = getAllImageFrames();
        for (VideoInternalFrame vFrame : vFrames) {
            try {
                try {
                    vFrame.preLoad();
                } catch (NoPlayerException | CannotRealizeException ex) {
                    Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (NoSuchMethodException | FrameGrabber.Exception ex) {
                Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(JMRImageInternalFrame iFrame : iFrames){
            try {
                try {
                    iFrame.preLoad();
                } catch (FrameGrabber.Exception | NoPlayerException | CannotRealizeException ex) {
                    Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (NoSuchMethodException | IOException ex) {
                Logger.getLogger(JMRFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Precarga los vídeos
     * @param evt Evento generado al pulsar el botón
     */
    private void botonPrecargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPrecargaActionPerformed
        this.precarga();
    }//GEN-LAST:event_botonPrecargaActionPerformed

    /**
     * Devuelve un solo descriptor en función del seleccionado en el botón de segmentar
     * @return Clase del descritpr que se desea usar
     */
    public Class getDescriptorUnico(){
        Class clase;
        
        if(colorEstructuradoUnico.isSelected()){
            clase = MPEG7ColorStructure.class;
        }else if(colorEscalableUnico.isSelected()){
            clase = MPEG7ScalableColor.class;
        }else if(colorMedioUnico.isSelected()){
            clase = SingleColorDescriptor.class;
        }else{
            clase = null;
        }
        
        return clase;
    }
    
    /**
     * Crea un comparador en función de la selección en la interfaz
     * @return Comparador creado
     */
    public Comparator createSelectedComparator(){
        int comparadorSeleccionado = comboComparador.getSelectedIndex();
        Boolean igual = botonPerteneceIgual.getText().equals("=");
        Boolean ordered = toggleOrdered.isSelected();
        Comparator comparator = null;
        switch (comparadorSeleccionado){
            case 0: //Min
                if(ordered){
                    comparator = new MinOrderedComparator();
                }else{
                    comparator = new MinComparator();
                }
                break;
            case 1: //Max
                if(ordered){
                    if(igual){
                        comparator = new MaxEqualOrderedComparator();
                    }else{ //No igual
                        comparator = new MaxOrderedComparator();
                    }
                }else{ //No ordered
                    if(igual){
                        comparator = new MaxEqualComparator();
                    }else{ //No igual
                        comparator = new MaxComparator();
                    }
                }
                break;
            case 2: //Median
                if(ordered){
                    if(igual){
                        comparator = new MedianEqualOrderedComparator();
                    }else{ //No igual
                        comparator = new MedianOrderedComparator();
                    }
                }else{ //No ordered
                    if(igual){
                        comparator = new MedianEqualComparator();
                    }else{ //No igual
                        comparator = new MedianComparator();
                    }
                }
                break;
            case 3: //Average
                if(ordered){
                    if(igual){
                        comparator = new AverageEqualOrderedComparator();
                    }else{ //No igual
                        comparator = new AverageOrderedComparator();
                    }
                }else{ //No ordered
                    if(igual){
                        comparator = new AverageEqualComparator();
                    }else{ //No igual
                        comparator = new AverageComparator();
                    }
                }
                break;
            default: throw new IllegalArgumentException("Malformed comparator: not found.");
        }
        
        return comparator;
    }
    
    /**
     * Crea el operador de segmentación según los parámetros seleccionados
     * @param f Fichero sobre el cual se crea el operador
     * @param esImagen True si es imagen y false si es vídeo
     * @return
     * @throws NoSuchMethodException
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     * @throws IOException
     * @throws NoPlayerException
     * @throws CannotRealizeException 
     */
    public SegmentationOp createSegmentationOp(File f, boolean esImagen) throws NoSuchMethodException, FrameGrabber.Exception, IOException, NoPlayerException, CannotRealizeException{
        Video vm ;
        if(esImagen){
            vm = VideoMediaCollectionIO.read(f);
        }else{
            vm = VideoMediaOpenCVIO.read(f);
        }        
        SegmentationOp so = null;
        int k = GlobalSettings.getK();
        int fps = GlobalSettings.getfps();
        int redim;
        Class descriptor;
        //Si la redimensión está activada
        if(GlobalSettings.getRedim()){
            redim = GlobalSettings.getRedimSize();
        }else{
            redim = 0; //Si no, se usa 0, que indica que no se redimensiona
        }
        
        double threshold;
        
        //Se obtiene el threshold
        if(GlobalSettings.getThBajo()){
            threshold = SegmentationOpAcumulatedChange.TH_LOW;
        }else if(GlobalSettings.getThMedio()){
            threshold = SegmentationOpAcumulatedChange.TH_MEDIUM;
        }else{
            threshold = SegmentationOpAcumulatedChange.TH_HIGH;
        }
        
        
        descriptor = getDescriptorUnico();
        if(descriptor==null){
            JOptionPane.showInternalMessageDialog(escritorio, "No hay ningún descriptor seleccionado", "Descriptor", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        System.out.println(descriptor);
        
        //Creamos el descriptor en función del método elegido
        if(GlobalSettings.isSingleActivated()){
            so = new SegmentationOpSingleChange(vm, descriptor, fps, redim, k);
            ((SegmentationOpSingleChange)so).setThreshold(GlobalSettings.getThresholdSingle()/100d);
        }else if(GlobalSettings.isAcumulatedActivated()){
            so = new SegmentationOpAcumulatedChange(vm, descriptor, fps, redim, threshold);
        }
        
        return so;
    }
    
    /**
     * Comprueba el número de vídeos e imágenes que hay abiertos para ajustar
     * la interaz
     */
    public void checkVideosActivos(){
        int numVentanas = getAllVideoFrames().size() + getAllImageFrames().size();
        botonComparar2Videos.setEnabled(numVentanas>=2);
        botonPrecarga.setEnabled(numVentanas>=1);
    }
    
    // Variables no generadas automáticamente 
    ListDB<BufferedImage> database = null; 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar barraArchivo;
    private javax.swing.JToolBar barraBD;
    private javax.swing.JToolBar barraComparador;
    private javax.swing.JToolBar barraDescriptores;
    private javax.swing.JPanel barraEstado;
    private javax.swing.JToolBar barraImagen;
    private javax.swing.JToolBar barraVideo;
    private javax.swing.JButton botonAbrir;
    private javax.swing.JButton botonAddRecordDB;
    private javax.swing.JButton botonCloseDB;
    private javax.swing.JButton botonCompara;
    private javax.swing.JButton botonComparar2Videos;
    private javax.swing.JButton botonDCD;
    private javax.swing.JButton botonGrafica;
    private javax.swing.JButton botonGuardar;
    private javax.swing.JButton botonNewDB;
    private javax.swing.JButton botonOpenDB;
    private javax.swing.JButton botonPerteneceIgual;
    private javax.swing.JToggleButton botonPlaybarra;
    private javax.swing.JButton botonPrecarga;
    private javax.swing.JButton botonPreferencias;
    private javax.swing.JButton botonRewindBarra;
    private javax.swing.JButton botonSaveDB;
    private javax.swing.JButton botonSearchDB;
    private javax.swing.JButton botonSegmentar;
    private javax.swing.JButton botonSingleColor;
    private javax.swing.JMenuItem clear;
    private javax.swing.JMenuItem closeAll;
    private javax.swing.JRadioButtonMenuItem colorDominante;
    private javax.swing.JRadioButtonMenuItem colorDominanteDB;
    private javax.swing.JRadioButtonMenuItem colorEscalable;
    private javax.swing.JRadioButtonMenuItem colorEscalableDB;
    private javax.swing.JRadioButtonMenuItem colorEscalableUnico;
    private javax.swing.JRadioButtonMenuItem colorEstructurado;
    private javax.swing.JRadioButtonMenuItem colorEstructuradoDB;
    private javax.swing.JRadioButtonMenuItem colorEstructuradoUnico;
    private javax.swing.JRadioButtonMenuItem colorMedio;
    private javax.swing.JRadioButtonMenuItem colorMedioDB;
    private javax.swing.JRadioButtonMenuItem colorMedioUnico;
    private javax.swing.JComboBox<String> comboComparador;
    private javax.swing.JButton drawGridButton;
    private javax.swing.JEditorPane editorOutput;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.ButtonGroup grupoBotonesDescriptorUnico;
    private javax.swing.JLabel infoDB;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel labelVideoA;
    private javax.swing.JLabel labelVideoB;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuGuardar;
    public javax.swing.JMenu menuVer;
    private javax.swing.JMenu menuZoom;
    private javax.swing.JMenuItem menuZoomIn;
    private javax.swing.JMenuItem menuZoomOut;
    private javax.swing.JPanel panelBarraHerramientas;
    private javax.swing.JPanel panelOutput;
    private javax.swing.JTabbedPane panelTabuladoInfo;
    private javax.swing.JPopupMenu popSeleccionDescriptoresUnico;
    private javax.swing.JPopupMenu popupMenuGrid;
    private javax.swing.JPopupMenu popupMenuPanelOutput;
    private javax.swing.JPopupMenu popupMenuSeleccionDescriptores;
    private javax.swing.JPopupMenu popupMenuSeleccionDescriptoresDB;
    public javax.swing.JLabel posicionPixel;
    private javax.swing.JToolBar restoBarraComparador;
    private javax.swing.JScrollPane scrollEditorOutput;
    private javax.swing.JPopupMenu.Separator separador1;
    private javax.swing.JPopupMenu.Separator separadorDescriptores;
    private javax.swing.JPopupMenu.Separator separadorDescriptoresDB;
    private javax.swing.JLabel showPanelInfo;
    private javax.swing.JCheckBoxMenuItem showResized;
    public javax.swing.JSplitPane splitPanelCentral;
    private javax.swing.JRadioButtonMenuItem texturaEdge;
    private javax.swing.JRadioButtonMenuItem texturaEdgeDB;
    private javax.swing.JRadioButtonMenuItem texturaHomogeneidad;
    private javax.swing.JRadioButtonMenuItem texturaHomogeneidadDB;
    private javax.swing.JToggleButton toggleOrdered;
    private javax.swing.JCheckBoxMenuItem usarTransparencia;
    private javax.swing.JToggleButton useGridButton;
    private javax.swing.JCheckBoxMenuItem verGrid;
    // End of variables declaration//GEN-END:variables

}
