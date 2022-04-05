/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import com.cv.app.util.Util1;
import java.util.List;

import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class DemoPerspectiveFactory implements PerspectiveFactory, InitializingBean {

    private List<String> dockableIds;

    @Override
    public Perspective getPerspective(String perspectiveId) {
        Perspective perspective = new Perspective(perspectiveId, perspectiveId);
        LayoutSequence sequence = perspective.getInitialSequence(true);
        int addCount = 0;
        
        String prevDockableId = null;
        for (String dockableId : this.dockableIds) {
            if (Util1.hashPrivilege(dockableId)) {
                sequence.add(dockableId, prevDockableId);
                prevDockableId = dockableId;
                addCount++;
            }
        }
        
        if(addCount == 0){
            sequence.add("initialView", "initialView");
        }
        
        return perspective;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(this.dockableIds, "No dockable ids specified");
    }

    public List<String> getDockableIds() {
        return dockableIds;
    }

    public void setDockableIds(List<String> dockableIds) {
        this.dockableIds = dockableIds;
    }
}