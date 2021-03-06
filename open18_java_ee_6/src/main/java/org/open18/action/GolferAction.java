/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.open18.action;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.open18.model.Golfer;
import org.open18.model.dao.GolferDao;

/**
 *
 */
@ConversationScoped
@Named
@Stateful
public class GolferAction implements Serializable {

    private static final long serialVersionUID = 2281839629956903065L;

    @Inject
    private GolferDao dao;

    @Inject
    private transient Conversation conversation;

    private Long golferId;

    private Golfer golfer;

    private boolean managed;

    @Inject
    private void init() {
        golfer = new Golfer();
    }

    public void loadCourse() {
        if (this.golferId != null && !FacesContext.getCurrentInstance().isPostback()) {
            this.golfer = this.dao.findBy(this.golferId);
            this.managed = true;
        }
    }

    public void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    public void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String save() {
        dao.saveAndFlush(golfer);
        endConversation();
        return "/admin/GolferList.xhtml";
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String update() {
        dao.saveAndFlush(golfer);
        return "/admin/GolferList.xhtml?golferId=" + golfer.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String remove() {
        dao.remove(golfer);
        endConversation();
        return "/admin/GolferList.xhtml";
    }

    public Long getGolferId() {
        return golferId;
    }

    public void setGolferId(Long newRoundId) {
        if (newRoundId != null && !newRoundId.equals(golfer.getId())) {
            golferId = newRoundId;
            golfer = dao.findBy(newRoundId);
            managed = true;

            if (golfer == null) {
                managed = false;
                golfer = new Golfer();
                final FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Round found with id " + newRoundId, ""));
            }

            this.beginConversation();
        }
    }

    public Golfer getGolfer() {
        return golfer;
    }

    public void setGolfer(Golfer newGolfer) {
        golfer = newGolfer;
    }

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean newManaged) {
        managed = newManaged;
    }
}
