/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author WSwe
 */
public class LongDoubleConverter extends Converter<Double, Long>{
    @Override
    public Long convertForward(Double arg) {
        return Long.parseLong(arg.toString());
    }

    @Override
    public Double convertReverse(Long arg) {
        return Double.parseDouble(arg.toString());
    }
}
