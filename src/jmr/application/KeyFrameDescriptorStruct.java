/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmr.application;

import java.io.File;
import java.util.Comparator;
import jmr.video.KeyFrameDescriptor;

/**
 * Estructura auxiliar para el almacenamiento y ordenación de descriptores de 
 * keyframe, distancias entre varios y archivos.
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Mayo de 2017
 */

class KeyFrameDescriptorStruct {
    public KeyFrameDescriptor keyFrameDescriptor;
    public double distance;
    public File file;
}


/**
 * Clase de comparación para la estructura previamente creada
 * @author Juan Jesús Tomás Rojas
 * @version 1.0 - Mayo de 2017
 */
 class KeyFrameDescriptorStructComparator implements Comparator<KeyFrameDescriptorStruct>{
    @Override
    /**
     * Comparador entre 2 keyframeDescriptorStruct
     */
    public int compare(KeyFrameDescriptorStruct k1, KeyFrameDescriptorStruct k2)
     {
         return Double.compare(k1.distance, k2.distance);
     }
}
