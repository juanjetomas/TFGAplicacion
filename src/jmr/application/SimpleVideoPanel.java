/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmr.application;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Set;
import javax.swing.JOptionPane;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 * Panel simple de reproducción de vídeo
 *
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Mayo de 2017
 */
public class SimpleVideoPanel extends javax.swing.JPanel {

    /**
     * Reproductor
     */
    private EmbeddedMediaPlayer mediaPlayer;
    /**
     * Fichero de vídeo asociado a la ventana
     */
    private File file;
    /**
     * Canvas en el que se muestra el vídeo
     */
    private Canvas canvas;

    /**
     * Indica si se está reproduciendo el video
     */
    private boolean playing;

    /**
     * Creates new form SimpleVideoPanel
     */
    public SimpleVideoPanel() {
        initComponents();
    }

    /**
     * Asigna un vídeo al panel
     * @param f Fichero que apunta al vídeo
     * @param distance Distancia obtenida con el vídeo fuente
     */
    public void setVideo(File f, Double distance) {

        canvasGrafico.setBackground(Color.black);
        MediaPlayerFactory mpf = new MediaPlayerFactory();
        mediaPlayer = mpf.newEmbeddedMediaPlayer();
        CanvasVideoSurface cvs = mpf.newVideoSurface(canvasGrafico);

        mediaPlayer.setVideoSurface(cvs);
        mediaPlayer.getPosition();
        mediaPlayer.getTime();
        mediaPlayer.setPlaySubItems(true);
        canvasGrafico.setVisible(true);
        this.setVisible(true);
        file = f;
        playing = false;

        //Permite manejar eventos de ratón en lugar de que lo haga vlc
        mediaPlayer.setEnableMouseInputHandling(false);
        mediaPlayer.setEnableKeyInputHandling(false);

        MouseAdapterMod mouseAdapter = new MouseAdapterMod();
        cvs.canvas().addMouseListener(mouseAdapter);
        if(distance>=0){
            labelDistancia.setText(distance.toString());
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        canvasGrafico = new java.awt.Canvas();
        jPanel1 = new javax.swing.JPanel();
        labelDistancia = new javax.swing.JLabel();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        canvasGrafico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                canvasGraficoMouseClicked(evt);
            }
        });
        add(canvasGrafico, java.awt.BorderLayout.CENTER);

        jPanel1.add(labelDistancia);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    }//GEN-LAST:event_formMouseClicked

    private void canvasGraficoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasGraficoMouseClicked
    }//GEN-LAST:event_canvasGraficoMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
    }//GEN-LAST:event_formMousePressed

    /**
     * Inicializa la clase
     */
    public void init() {
        mediaPlayer.startMedia(file.toString(), "");
        mediaPlayer.pause(); //This way the first frame is loaded instead a black screen
    }

    /**
     * Adaptador de ratón que permite manejar eventos en 
     * <code>CanvasVideoSurface</code>
     */
    public class MouseAdapterMod extends MouseAdapter {
        /**
         * Evento generado cuando se clicka el ratón
         * @param e Evento de ratón generado
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (playing) {
                mediaPlayer.pause();
                playing = false;
            } else {
                mediaPlayer.play();
                playing = true;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvasGrafico;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelDistancia;
    // End of variables declaration//GEN-END:variables
}
