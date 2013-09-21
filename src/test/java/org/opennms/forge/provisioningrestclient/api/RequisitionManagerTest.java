/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.forge.provisioningrestclient.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennms.forge.restclient.utils.OnmsRestConnectionParameter;
import org.opennms.forge.restclient.utils.RestConnectionParameter;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import org.junit.Ignore;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionCategory;

/**
 * <p>RequisitionManagerTest class.</p>
 *
 * @author <a href="mailto:markus@opennms.org">Markus Neumann</a>*
 * @author <a href="mailto:ronny@opennms.org">Ronny Trommer</a>
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class RequisitionManagerTest {

    private static Logger logger = LoggerFactory.getLogger(RequisitionManagerTest.class);

    private String baseUrl = "http://localhost:8980/opennms/";
    private String username = "admin";
    private String password = "admin";

    private RequisitionManager m_manager;
    private RestConnectionParameter restConnectionParameter;
    
    
    @Before
    public void setup() {
        try {
            restConnectionParameter = new OnmsRestConnectionParameter(baseUrl, username, password);
            m_manager = new RequisitionManager(restConnectionParameter, "TestRequisition");
        } catch (MalformedURLException e) {
            logger.error("baseUrl is malformed", e);
        }
    }

    @Test
    @Ignore
    public void testReadingNodeFromRequisition() {
        RequisitionNode reqNode = m_manager.getRequisitionNode("TestNode");
        Assert.assertNotNull(reqNode);
    }
    
    @Test
    @Ignore
    public void testSyncronizeRequisition() {
        RequisitionCategory testCategory = new RequisitionCategory("2");
        Requisition requisition = m_manager.getRequisition();
        RequisitionNode requisitionNode = requisition.getNode("TestNode");
        requisitionNode.getCategories().add(testCategory);
        m_manager.sendManagedRequisitionToOpenNMS(requisition.getNodes());
        m_manager.synchronizeManagedRequisitionOnOpenNMS();
    }
    
    @Test
    @Ignore
    public void testAddingCategoryToNode() {
        
    }
}
