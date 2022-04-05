/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import java.awt.*;
import java.util.Vector;
/**
 *
 * @author winswe 
 */
public class BestAppFocusTraversalPolicy extends FocusTraversalPolicy{
    private Vector<Component> order;

    public BestAppFocusTraversalPolicy(Vector<Component> order) {
        this.order = new Vector<Component>(order.size());
        this.order.addAll(order);
    }
    
    @Override
    public Component getComponentAfter(Container focusCycleRoot,
                                       Component aComponent)
    {
        int idx = (order.indexOf(aComponent) + 1) % order.size();
        return order.get(idx);
    }

    @Override
    public Component getComponentBefore(Container focusCycleRoot,
                                        Component aComponent)
    {
        int idx = order.indexOf(aComponent) - 1;
        if (idx < 0) {
            idx = order.size() - 1;
        }
        return order.get(idx);
    }

    @Override
    public Component getDefaultComponent(Container focusCycleRoot) {
        return order.get(0);
    }

    @Override
    public Component getLastComponent(Container focusCycleRoot) {
        return order.lastElement();
    }

    @Override
    public Component getFirstComponent(Container focusCycleRoot) {
        return order.get(0);
    }
}
