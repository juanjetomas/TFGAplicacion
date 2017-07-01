
package jmr.application;

import java.awt.Dimension;

/**
 * Diálogo para mostrar/modificar los parámetros globales (preferencias) de la
 * aplicación
 * 
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class PreferencesDialog extends javax.swing.JDialog {

    /**
     * Valor devuelto en caso de cancelación
     */
    public static final int CANCEL_OPTION = 1;
    /**
     * Valor devuelto en caso de aceptación
     */
    public static final int APPROVE_OPTION = 0;
    /**
     * Valor devuelto (por cefecto, cancelación)
     */
    private int returnStatus = CANCEL_OPTION;
    
    public PreferencesDialog(java.awt.Frame parent) {
        super(parent, true);           //Siempre modal
        initComponents();              //Inicializacón de componentes -> NetBeans 
        inicializaGlobalSetings();     //Inicializamos variables globales
        setLocationRelativeTo(parent); //Posicionamos en centro
        //Muestra los componentes en relación a que botones están seleccionados
        setVisibleKTools(botonSingleChange.isSelected());       
        setVisibleRedim(checkRedim.isSelected());       
        setVisibleThreshold(botonAcumulatedChange.isSelected());    
        
        
    }

    /**
     * Inicializa los campos del diálogo con los valores globales de la aplicación
     */
    private void inicializaGlobalSetings(){
        this.tileXSize.setValue(GlobalSettings.getGridSize().getWidth());  
        this.tileYSize.setValue(GlobalSettings.getGridSize().getHeight()); 
        this.botonAcumulatedChange.setSelected(GlobalSettings.isAcumulatedActivated());
        this.botonSingleChange.setSelected(GlobalSettings.isSingleActivated());
        this.spinnerFps.setValue(new Integer(GlobalSettings.getfps()));
        this.spinnerK.setValue(new Integer(GlobalSettings.getK()));
        this.checkRedim.setSelected(GlobalSettings.getRedim());
        this.spinnerTam.setValue(new Integer(GlobalSettings.getRedimSize()));
        this.botonTbajo.setSelected(GlobalSettings.getThBajo());
        this.botonTmedio.setSelected(GlobalSettings.getThMedio());
        this.botonTalto.setSelected(GlobalSettings.getThAlto());
        this.spinnerThresholdSingle.setValue(new Integer(GlobalSettings.getThresholdSingle()));
    }
    
    /**
     * Actualiza las preferencias de la aplicación (global settings) con los 
     * valores introducidos en los campos del diálogo.Este método se llama en 
     * caso de que el usuario pulse el botón 'Aceptar"
     */
    private void actualizaGlobalSetings(){  
        int w = ((Double)this.tileXSize.getValue()).intValue();
        int h = ((Double)this.tileYSize.getValue()).intValue();
        GlobalSettings.setGridDimension(new Dimension(w,h));
        GlobalSettings.setAcumulatedActivated(botonAcumulatedChange.isSelected());
        GlobalSettings.setSingleActivated(botonSingleChange.isSelected());
        GlobalSettings.setfps((int) spinnerFps.getValue());
        GlobalSettings.setK((int) spinnerK.getValue());
        GlobalSettings.setRedim(checkRedim.isSelected());
        GlobalSettings.setRedimSize((int) spinnerTam.getValue());
        GlobalSettings.setThBajo(botonTbajo.isSelected());
        GlobalSettings.setThMedio(botonTmedio.isSelected());
        GlobalSettings.setThAlto(botonTalto.isSelected());
        GlobalSettings.setThresholdSingle((int)spinnerThresholdSingle.getValue());
    }
    
    
    /**
     * Muestra este diálogo de forma modal
     * 
     * @return el estado final (aceptado o cancelado)
     */
    public int showDialog(){
        this.setVisible(true);
        return returnStatus; //Dialogo modal -> no ejecutará el return hasta que no se cierre el diálogo
    }
    
    /**
     * Cierra este diálogo, actualizando previamente el estado
     * @param retStatus el estado del diálogo (aceptado o cancelado)
     */
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    /*
     * Código generado por Netbeans para el diseño del interfaz
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoBotonesPxPo = new javax.swing.ButtonGroup();
        grupoMetSeg = new javax.swing.ButtonGroup();
        grupoThreshold = new javax.swing.ButtonGroup();
        panelBotones = new javax.swing.JPanel();
        botonAceptar = new javax.swing.JButton();
        botonCancelar = new javax.swing.JButton();
        panelTabulado = new javax.swing.JTabbedPane();
        panelShape = new javax.swing.JPanel();
        labelGridSize = new javax.swing.JLabel();
        tileXSize = new javax.swing.JSpinner();
        tileYSize = new javax.swing.JSpinner();
        label_x = new javax.swing.JLabel();
        label_parentesis = new javax.swing.JLabel();
        labelSegmentacion = new javax.swing.JLabel();
        botonSingleChange = new javax.swing.JRadioButton();
        botonAcumulatedChange = new javax.swing.JRadioButton();
        botonTbajo = new javax.swing.JRadioButton();
        labelFps = new javax.swing.JLabel();
        spinnerFps = new javax.swing.JSpinner();
        labelK = new javax.swing.JLabel();
        spinnerK = new javax.swing.JSpinner();
        checkRedim = new javax.swing.JCheckBox();
        labelTam = new javax.swing.JLabel();
        spinnerTam = new javax.swing.JSpinner();
        labelThreshold = new javax.swing.JLabel();
        botonTmedio = new javax.swing.JRadioButton();
        botonTalto = new javax.swing.JRadioButton();
        labelThresholdSingle = new javax.swing.JLabel();
        spinnerThresholdSingle = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Preferences");

        panelBotones.setPreferredSize(new java.awt.Dimension(100, 30));
        panelBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        botonAceptar.setText("OK");
        botonAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAceptarActionPerformed(evt);
            }
        });
        panelBotones.add(botonAceptar);

        botonCancelar.setText("Cancel");
        botonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCancelarActionPerformed(evt);
            }
        });
        panelBotones.add(botonCancelar);

        getContentPane().add(panelBotones, java.awt.BorderLayout.SOUTH);

        labelGridSize.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        labelGridSize.setText("Grid  size: (");

        tileXSize.setModel(new javax.swing.SpinnerNumberModel(4.0d, 1.0d, null, 1.0d));
        tileXSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tileXSizeStateChanged(evt);
            }
        });

        tileYSize.setModel(new javax.swing.SpinnerNumberModel(4.0d, 1.0d, null, 1.0d));
        tileYSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tileYSizeStateChanged(evt);
            }
        });

        label_x.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_x.setText("x");
        label_x.setMinimumSize(new java.awt.Dimension(5, 5));

        label_parentesis.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_parentesis.setText(")");

        labelSegmentacion.setText("Método de segmentación:");

        grupoMetSeg.add(botonSingleChange);
        botonSingleChange.setSelected(true);
        botonSingleChange.setText("Cambio en ventana");
        botonSingleChange.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                botonSingleChangeItemStateChanged(evt);
            }
        });
        botonSingleChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSingleChangeActionPerformed(evt);
            }
        });

        grupoMetSeg.add(botonAcumulatedChange);
        botonAcumulatedChange.setText("Cambio acumulado");
        botonAcumulatedChange.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                botonAcumulatedChangeItemStateChanged(evt);
            }
        });
        botonAcumulatedChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAcumulatedChangeActionPerformed(evt);
            }
        });

        grupoThreshold.add(botonTbajo);
        botonTbajo.setText("Bajo");
        botonTbajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTbajoActionPerformed(evt);
            }
        });

        labelFps.setText("FPS:");

        spinnerFps.setModel(new javax.swing.SpinnerNumberModel(24, 1, 24, 1));

        labelK.setText("Tamaño de ventana (K):");
        labelK.setOpaque(true);

        spinnerK.setModel(new javax.swing.SpinnerNumberModel(5, 3, null, 1));

        checkRedim.setText("Redimensionar imágenes");
        checkRedim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkRedimActionPerformed(evt);
            }
        });

        labelTam.setText("Tamaño de redimensión (px):");

        spinnerTam.setModel(new javax.swing.SpinnerNumberModel(128, 1, null, 1));

        labelThreshold.setText("Threshold");

        grupoThreshold.add(botonTmedio);
        botonTmedio.setText("Medio");

        grupoThreshold.add(botonTalto);
        botonTalto.setText("Alto");

        labelThresholdSingle.setText("Threshold (%):");

        spinnerThresholdSingle.setModel(new javax.swing.SpinnerNumberModel(80, 0, 99, 1));

        javax.swing.GroupLayout panelShapeLayout = new javax.swing.GroupLayout(panelShape);
        panelShape.setLayout(panelShapeLayout);
        panelShapeLayout.setHorizontalGroup(
            panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelShapeLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(labelThresholdSingle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spinnerThresholdSingle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(botonTbajo)
                        .addGap(18, 18, 18)
                        .addComponent(botonTmedio)
                        .addGap(18, 18, 18)
                        .addComponent(botonTalto))
                    .addComponent(labelThreshold)
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(labelTam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerTam, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkRedim)
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(labelK)
                        .addGap(18, 18, 18)
                        .addComponent(spinnerK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(labelFps)
                        .addGap(18, 18, 18)
                        .addComponent(spinnerFps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(botonSingleChange)
                        .addGap(18, 18, 18)
                        .addComponent(botonAcumulatedChange))
                    .addComponent(labelSegmentacion)
                    .addGroup(panelShapeLayout.createSequentialGroup()
                        .addComponent(labelGridSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tileXSize, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label_x, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tileYSize, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_parentesis, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        panelShapeLayout.setVerticalGroup(
            panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelShapeLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelGridSize, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tileXSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_x, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tileYSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_parentesis, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(labelSegmentacion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonSingleChange)
                    .addComponent(botonAcumulatedChange))
                .addGap(18, 18, 18)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelK)
                    .addComponent(spinnerK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelThreshold)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonTbajo)
                    .addComponent(botonTmedio)
                    .addComponent(botonTalto))
                .addGap(18, 18, 18)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelThresholdSingle)
                    .addComponent(spinnerThresholdSingle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFps)
                    .addComponent(spinnerFps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(checkRedim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTam)
                    .addComponent(spinnerTam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelTabulado.addTab("Image", panelShape);

        getContentPane().add(panelTabulado, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(416, 495));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void botonAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAceptarActionPerformed
        actualizaGlobalSetings();
        doClose(APPROVE_OPTION);
    }//GEN-LAST:event_botonAceptarActionPerformed

    private void botonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCancelarActionPerformed
        doClose(CANCEL_OPTION);
    }//GEN-LAST:event_botonCancelarActionPerformed

    private void tileXSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tileXSizeStateChanged
        //actualizaSegmentSize();
    }//GEN-LAST:event_tileXSizeStateChanged

    private void tileYSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tileYSizeStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tileYSizeStateChanged

    private void botonSingleChangeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_botonSingleChangeItemStateChanged
        setVisibleKTools(botonSingleChange.isSelected());
    }//GEN-LAST:event_botonSingleChangeItemStateChanged

    private void checkRedimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkRedimActionPerformed
        setVisibleRedim(checkRedim.isSelected());
    }//GEN-LAST:event_checkRedimActionPerformed

    private void botonSingleChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSingleChangeActionPerformed

    }//GEN-LAST:event_botonSingleChangeActionPerformed

    private void botonAcumulatedChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAcumulatedChangeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonAcumulatedChangeActionPerformed

    private void botonAcumulatedChangeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_botonAcumulatedChangeItemStateChanged
        setVisibleThreshold(botonAcumulatedChange.isSelected());
    }//GEN-LAST:event_botonAcumulatedChangeItemStateChanged

    private void botonTbajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTbajoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonTbajoActionPerformed

    private void setVisibleKTools(boolean visible){
        labelK.setVisible(visible);
        spinnerK.setVisible(visible);
        labelThresholdSingle.setVisible(visible);
        spinnerThresholdSingle.setVisible(visible);
    }
    
    private void setVisibleRedim(boolean visible){
        labelTam.setVisible(visible);
        spinnerTam.setVisible(visible);
    }
    
    private void setVisibleThreshold(boolean visible){
        botonTbajo.setVisible(visible);
        botonTmedio.setVisible(visible);
        botonTalto.setVisible(visible);
        labelThreshold.setVisible(visible);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAceptar;
    private javax.swing.JRadioButton botonAcumulatedChange;
    private javax.swing.JButton botonCancelar;
    private javax.swing.JRadioButton botonSingleChange;
    private javax.swing.JRadioButton botonTalto;
    private javax.swing.JRadioButton botonTbajo;
    private javax.swing.JRadioButton botonTmedio;
    private javax.swing.JCheckBox checkRedim;
    private javax.swing.ButtonGroup grupoBotonesPxPo;
    private javax.swing.ButtonGroup grupoMetSeg;
    private javax.swing.ButtonGroup grupoThreshold;
    private javax.swing.JLabel labelFps;
    private javax.swing.JLabel labelGridSize;
    private javax.swing.JLabel labelK;
    private javax.swing.JLabel labelSegmentacion;
    private javax.swing.JLabel labelTam;
    private javax.swing.JLabel labelThreshold;
    private javax.swing.JLabel labelThresholdSingle;
    private javax.swing.JLabel label_parentesis;
    private javax.swing.JLabel label_x;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelShape;
    private javax.swing.JTabbedPane panelTabulado;
    private javax.swing.JSpinner spinnerFps;
    private javax.swing.JSpinner spinnerK;
    private javax.swing.JSpinner spinnerTam;
    private javax.swing.JSpinner spinnerThresholdSingle;
    private javax.swing.JSpinner tileXSize;
    private javax.swing.JSpinner tileYSize;
    // End of variables declaration//GEN-END:variables
}
