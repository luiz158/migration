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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.open18.model.Course;
import org.open18.model.Facility;
import org.open18.model.dao.FacilityDao;

/**
 *
 */
@Stateful
@ConversationScoped
@Named
public class FacilityAction implements Serializable {

    private static final long serialVersionUID = 6201511634440442162L;

    @PersistenceContext
    private transient EntityManager em;

    @Inject
    private transient FacilityDao dao;

    @Inject
    private transient Conversation conversation;

    private Long facilityId;

    private Facility facility;

    private boolean managed;

    private boolean enterCourse;

    private List<Facility> resultList;

    @Inject
    private void init() {
        facility = new Facility();

        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.loadResults();
        }
    }

    public void loadResults() {
        resultList = dao.findBy(facility);
    }

    public void search() {
        loadResults();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String save() {
        dao.saveAndFlush(facility);

        if (!enterCourse) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Please enter course information for " + facility.getName(), null));
            return "/CourseEdit.xhtml?facilityId=" + facility.getId();
        } else {
            this.endConversation();
            return "/FacilityList.xhtml";
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String update() {
        dao.saveAndFlush(facility);
        this.endConversation();
        return "/Facility.xhtml?facilityId=" + facility.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String remove() {
        dao.remove(facility);
        this.endConversation();
        return "/FacilityList.xhtml";
    }

    public void beginConversation() {
        if (this.conversation.isTransient()) {
            this.conversation.begin();
        }
    }

    public void endConversation() {
        if (!this.conversation.isTransient()) {
            this.conversation.end();
        }
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        if (!facilityId.equals(this.facility.getId())) {
            this.facilityId = facilityId;
            this.facility = dao.findBy(facilityId);
            managed = true;

            if (this.facility == null) {
                managed = false;
                this.facility = new Facility();
                final FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Facility found with id " + facilityId, ""));
            }
        }
    }

    public List<Facility> getResultList() {
        return resultList;
    }

    public void setResultList(List<Facility> resultList) {
        this.resultList = resultList;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Produces
    @Named("facilityCourses")
    @RequestScoped
    public List<Course> getCourses() {
        if (!this.dao.isManaged(facility)) {
            facility = this.dao.findBy(facility.getId());
        }

        if (facility == null || facility.getCourses() == null || facility.getCourses().isEmpty()) {
            return Collections.<Course>emptyList();
        } else {
            return new ArrayList<Course>(facility.getCourses());
        }
    }

    public boolean isManaged() {
        return managed;
    }

    public boolean isEnterCourse() {
        return enterCourse;
    }

    public void setEnterCourse(boolean enterCourse) {
        this.enterCourse = enterCourse;
    }
}
