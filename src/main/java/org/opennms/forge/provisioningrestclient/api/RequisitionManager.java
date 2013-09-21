/**
 * *****************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc. OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenNMS(R). If not, see:
 * http://www.gnu.org/licenses/
 *
 * For more information contact: OpenNMS(R) Licensing <license@opennms.org> http://www.opennms.org/ http://www.opennms.com/
 ******************************************************************************
 */
package org.opennms.forge.provisioningrestclient.api;

import java.util.Collection;
import java.util.Date;
import org.opennms.forge.restclient.api.RestRequisitionProvider;
import org.opennms.forge.restclient.utils.RestConnectionParameter;
import org.opennms.netmgt.provision.persist.requisition.Requisition;
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>RequisitionManager class.</p>
 *
 * @author <a href="mailto:markus@opennms.org">Markus Neumann</a>*
 * @author <a href="mailto:ronny@opennms.org">Ronny Trommer</a>
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class RequisitionManager {

    private static Logger logger = LoggerFactory.getLogger(RequisitionManager.class);
    private Map<String, RequisitionNode> m_reqNodesByLabel = new HashMap<>();
    private RestRequisitionProvider m_restRequisitionProvider;
    private Requisition m_requisition;

    /**
     * <p>RequisitionManager</p>
     * <p/>
     * Constructor to handle the data structure for an OpenNMS provisioning ReST service.
     *
     * @param restConnectionParameter Connection parameter for HTTP ReST client
     * @param foreignSource Name of the requisition as {@link java.lang.String}
     */
    public RequisitionManager(RestConnectionParameter restConnectionParameter, String foreignSource) {
        m_restRequisitionProvider = new RestRequisitionProvider(restConnectionParameter);
        preLoadRequisition(foreignSource);
    }

    /**
     * <p>preLoadRequisition</p>
     * <p/>
     * Load a given requisition identified by foreign source and stores it for later use.
     * Without this preLoad the most operations will fail.
     * @param foreignSource Name of the requisition as {@link java.lang.String}
     */
    private void preLoadRequisition(String foreignSource) {
        m_requisition = m_restRequisitionProvider.getRequisition(foreignSource, "");
        for (RequisitionNode reqNode : m_requisition.getNodes()) {
            m_reqNodesByLabel.put(reqNode.getNodeLabel(), reqNode);
        }
    }

    public void sendManagedRequisitionToOpenNMS() {
        logger.info("Updating Requisition '{}'", m_requisition.getForeignSource());
        m_requisition.setDate(new Date());
        m_restRequisitionProvider.pushRequisition(m_requisition);
    }

    public void sendManagedRequisitionToOpenNMS(Collection<RequisitionNode> requisitionNodes) {
        for (RequisitionNode reqNode : requisitionNodes) {
            m_requisition.setDate(new Date());
            if(m_reqNodesByLabel.containsKey(reqNode.getNodeLabel())) {
                logger.info("Send Node '{}' of the Requisition '{}' to OpenNMS", reqNode.getNodeLabel(), m_requisition.getForeignSource());
                m_restRequisitionProvider.pushNodeToRequisition(m_requisition.getForeignSource(), reqNode);
            } else {
                logger.info("Node '{}' is not part of the Requisition '{}'", reqNode.getNodeLabel(), m_requisition.getForeignSource());
            }
        }
    }

    public void synchronizeManagedRequisitionOnOpenNMS() {
        logger.info("synchronizing Requisition '{}' on OpenNMS", m_requisition.getForeignSource());
        m_restRequisitionProvider.synchronizeRequisition(m_requisition.getForeignSource());
    }
    
    /**
     * <p>getRequisitionNode</p>
     * <p/>
     * Get a requisition node identified by node label
     *
     * @param nodeLabel Node label as {@link java.lang.String}
     * @return Requisition node as @{link org.opennms.netmgt.provision.persist.requisition.RequisitionNode}
     */
    public RequisitionNode getRequisitionNode(String nodeLabel) {
        return m_reqNodesByLabel.get(nodeLabel);
    }

    /**
     * <p>getRequisition</p>
     * <p/>
     * Get the whole previously loaded requisition.
     *
     * @return Requisition loaded into the requisition manager as {@link org.opennms.netmgt.provision.persist.requisition.Requisition}
     */
    public Requisition getRequisition() {
        return m_requisition;
    }

    /**
     * <p>getRestRequisitionProvider</p>
     * <p/>
     * Get the ReST requisition provider to use ReST calls
     *
     * @return ReST requisition provider as {@link org.opennms.forge.restclient.api.RestRequisitionProvider}
     */
    public RestRequisitionProvider getRestRequisitionProvider() {
        return this.m_restRequisitionProvider;
    }
}