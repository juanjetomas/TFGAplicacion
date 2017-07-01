/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmr.application;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Ventana interna que muestra varios vídeos
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Mayo de 2017
 */
public class VideoListInternalFrame extends javax.swing.JInternalFrame {

    /**
     * Lista de paneles, uno por vídeo
     */
    ArrayList<SimpleVideoPanel> simpleVideoPanels;
    /**
     * Lista de ficheros, uno por vídeo
     */
    ArrayList<File> files;
    /**
     * Tamaño por defecto de la ventana de vídeo
     */
    private final static Dimension DEFAULT_COMMON_SIZE = new Dimension(200,200);
    
    /**
     * Crea una nueva VideoListInternalFrameAlternativa
     */
    public VideoListInternalFrame() {
        initComponents();
        simpleVideoPanels = new ArrayList<>();
        this.setSize(new Dimension(DEFAULT_COMMON_SIZE.width*5,DEFAULT_COMMON_SIZE.height+50));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        internalPanel = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        internalPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                internalPanelMouseClicked(evt);
            }
        });
        internalPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jScrollPane1.setViewportView(internalPanel);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void internalPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_internalPanelMouseClicked
    }//GEN-LAST:event_internalPanelMouseClicked

    /**
     * Añade un nuevo vídeo a la ventana
     * @param f Fichero que apunta al nuevo vídeo
     * @param dist Distancia a mostrar del nuevo vídeo
     */
    public void add(File f, Double dist){
        SimpleVideoPanel videoPanel = new SimpleVideoPanel();
        videoPanel.setPreferredSize(DEFAULT_COMMON_SIZE);
        internalPanel.add(videoPanel);
        simpleVideoPanels.add(videoPanel);
        videoPanel.setVideo(f, dist);
    }
    
    /**
     * Inicializa la ventana
     */
    public void init(){
        for (SimpleVideoPanel simpleVideoPanel : simpleVideoPanels) {
            simpleVideoPanel.init();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel internalPanel;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}