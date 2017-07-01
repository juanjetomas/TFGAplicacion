package jmr.application;

import java.awt.Dimension;

/**
 *
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class GlobalSettings {
    static private Dimension gridSize = new Dimension(4,4);
    static private Boolean singleChange = true;
    static private Boolean acumulatedChange = false;
    static private int fps = 24;
    static private int k = 5;
    static private Boolean redim = false;
    static private int redimSize = 128;
    static private Boolean thBajo = false;
    static private Boolean thMedio = true;
    static private Boolean thAlto = false;
    static private int thresholdSingle = 80;
    
    static public Dimension getGridSize(){
        return gridSize;
    }
    
    static public void setGridDimension(Dimension gridSize){
        if(gridSize.getWidth()>0 && gridSize.getHeight()>0)
            GlobalSettings.gridSize = gridSize;
    }
    
    static public boolean isSingleActivated(){
        return singleChange;
    }
    
    static public boolean isAcumulatedActivated(){
        return acumulatedChange;
    }
    
    static public void setSingleActivated(Boolean bol){
        singleChange = bol;
    }
    
    static public void setAcumulatedActivated(Boolean bol){
        acumulatedChange = bol;
    }
    
    static public void setfps(int fpsec){
        fps = fpsec;
    }
    
    static public void setK(int kval){
        k = kval;
    }
    
    static public int getfps(){
        return fps;
    }
    
    static public int getK(){
        return k;
    }
    
    static public void setRedim(boolean bol){
        redim = bol;
    }
    
    static public  boolean getRedim(){
        return redim;
    }
    
    static public void setRedimSize(int size){
        redimSize = size;
    }
    
    static public int getRedimSize(){
        return redimSize;
    }
    
    static public void setThBajo(Boolean bol){
        thBajo = bol;
    }
    
    static public void setThMedio(Boolean bol){
        thMedio = bol;
    }
    
    static public void setThAlto(Boolean bol){
        thAlto = bol;
    }
    
    static public boolean getThBajo(){
        return thBajo;
    }
    
    static public boolean getThMedio(){
        return thMedio;
    }
    
    static public boolean getThAlto(){
        return thAlto;
    }
    
    static public int getThresholdSingle(){
        return thresholdSingle;
    }
    
    static public void setThresholdSingle(int th){
        thresholdSingle = th;
    }   

}
