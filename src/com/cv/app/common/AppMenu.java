/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandGroupFactoryBean;

/**
 *
 * @author WSwe
 */
public class AppMenu extends CommandGroupFactoryBean{
    @Override
    protected void initCommandGroupMembers(CommandGroup group){
        Object[] members = super.getMembers();
        List<Object> listMember = new ArrayList();
        
        for(Object member : members){
            //System.out.println(((AbstractCommand)member).getId());
            if(member instanceof AbstractCommand){
                AbstractCommand cmd = (AbstractCommand)member;
                
                if(Util1.hashPrivilege(cmd.getId())){
                    listMember.add(member);
                }
            }
        }
        
        super.setMembers(listMember.toArray());
        super.initCommandGroupMembers(group);
    }
}
