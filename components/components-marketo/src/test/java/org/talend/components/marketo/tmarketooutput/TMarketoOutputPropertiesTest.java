// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.marketo.tmarketooutput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.avro.Schema;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.marketo.MarketoConstants;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties.APIMode;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties.OperationType;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties.OutputOperation;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties.RESTLookupFields;
import org.talend.daikon.properties.presentation.Form;

public class TMarketoOutputPropertiesTest {

    TMarketoOutputProperties props;

    @Before
    public void setup() {
        props = new TMarketoOutputProperties("test");
        props.connection.setupProperties();
        props.connection.setupLayout();
        props.schemaInput.setupProperties();
        props.schemaInput.setupLayout();
        props.setupProperties();
        props.setupLayout();
    }

    @Test
    public void testGetAllSchemaPropertiesConnectors() throws Exception {
        Set<PropertyPathConnector> connectors = new HashSet<>(Arrays.asList(props.FLOW_CONNECTOR, props.REJECT_CONNECTOR));
        assertEquals(connectors, props.getAllSchemaPropertiesConnectors(true));
        assertEquals(Collections.singleton(props.MAIN_CONNECTOR), props.getAllSchemaPropertiesConnectors(false));
    }

    @Test
    public void testEnums() {
        assertEquals(OutputOperation.syncLead, OutputOperation.valueOf("syncLead"));
        assertEquals(OutputOperation.syncMultipleLeads, OutputOperation.valueOf("syncMultipleLeads"));

        assertEquals(OperationType.createOnly, OperationType.valueOf("createOnly"));
        assertEquals(OperationType.updateOnly, OperationType.valueOf("updateOnly"));
        assertEquals(OperationType.createOrUpdate, OperationType.valueOf("createOrUpdate"));
        assertEquals(OperationType.createDuplicate, OperationType.valueOf("createDuplicate"));

        assertEquals(RESTLookupFields.id, RESTLookupFields.valueOf("id"));
        assertEquals(RESTLookupFields.cookie, RESTLookupFields.valueOf("cookie"));
        assertEquals(RESTLookupFields.email, RESTLookupFields.valueOf("email"));
        assertEquals(RESTLookupFields.twitterId, RESTLookupFields.valueOf("twitterId"));
        assertEquals(RESTLookupFields.facebookId, RESTLookupFields.valueOf("facebookId"));
        assertEquals(RESTLookupFields.linkedInId, RESTLookupFields.valueOf("linkedInId"));
        assertEquals(RESTLookupFields.sfdcAccountId, RESTLookupFields.valueOf("sfdcAccountId"));
        assertEquals(RESTLookupFields.sfdcContactId, RESTLookupFields.valueOf("sfdcContactId"));
        assertEquals(RESTLookupFields.sfdcLeadId, RESTLookupFields.valueOf("sfdcLeadId"));
        assertEquals(RESTLookupFields.sfdcLeadOwnerId, RESTLookupFields.valueOf("sfdcLeadOwnerId"));
        assertEquals(RESTLookupFields.sfdcOpptyId, RESTLookupFields.valueOf("sfdcOpptyId"));
    }

    @Test
    public void testUpdateSchemaRelated() throws Exception {
        props.outputOperation.setValue(OutputOperation.syncLead);
        props.setupProperties();
        props.mappingInput.setupProperties();
        props.updateSchemaRelated();
        props.schemaListener.afterSchema();
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead(), props.schemaInput.schema.getValue());
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead().getFields(),
                props.schemaFlow.schema.getValue().getFields());
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead().getFields().size() + 1,
                props.schemaReject.schema.getValue().getFields().size());
        props.outputOperation.setValue(OutputOperation.syncMultipleLeads);
        props.batchSize.setValue(1);
        props.afterOutputOperation();
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead(), props.schemaInput.schema.getValue());
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead().getFields(),
                props.schemaFlow.schema.getValue().getFields());
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead().getFields().size() + 1,
                props.schemaReject.schema.getValue().getFields().size());

        props.connection.apiMode.setValue(APIMode.SOAP);
        props.updateSchemaRelated();
        props.outputOperation.setValue(OutputOperation.syncLead);
        props.afterOutputOperation();
        assertEquals(MarketoConstants.getSOAPOutputSchemaForSyncLead(), props.schemaInput.schema.getValue());
        assertEquals(MarketoConstants.getSOAPOutputSchemaForSyncLead().getFields(),
                props.schemaFlow.schema.getValue().getFields());
        assertEquals(MarketoConstants.getSOAPOutputSchemaForSyncLead().getFields().size() + 1,
                props.schemaReject.schema.getValue().getFields().size());
        props.outputOperation.setValue(OutputOperation.syncMultipleLeads);
        props.afterOutputOperation();
        assertEquals(MarketoConstants.getSOAPOutputSchemaForSyncLead(), props.schemaInput.schema.getValue());
        assertEquals(MarketoConstants.getSOAPOutputSchemaForSyncLead().getFields(),
                props.schemaFlow.schema.getValue().getFields());
        assertEquals(MarketoConstants.getSOAPOutputSchemaForSyncLead().getFields().size() + 1,
                props.schemaReject.schema.getValue().getFields().size());
    }

    @Test
    public void testDefaultShema() throws Exception {
        assertEquals(MarketoConstants.getRESTOutputSchemaForSyncLead(), props.schemaInput.schema.getValue());
    }

    @Test
    public void testTDI38543() throws Exception {
        props.outputOperation.setValue(OutputOperation.syncMultipleLeads);
        props.afterOutputOperation();
        assertFalse(props.deDupeEnabled.getValue());
        assertTrue(props.getForm(Form.MAIN).getWidget(props.operationType.getName()).isVisible());
        assertTrue(props.getForm(Form.MAIN).getWidget(props.lookupField.getName()).isVisible());
        props.deDupeEnabled.setValue(true);
        props.afterOutputOperation();
        assertTrue(props.deDupeEnabled.getValue());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.operationType.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.lookupField.getName()).isVisible());
        props.connection.apiMode.setValue(APIMode.SOAP);
        props.updateSchemaRelated();
        assertTrue(props.deDupeEnabled.getValue());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.operationType.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.lookupField.getName()).isVisible());
        props.deDupeEnabled.setValue(false);
        props.afterDeDupeEnabled();
        assertFalse(props.deDupeEnabled.getValue());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.operationType.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.lookupField.getName()).isVisible());
    }

    @Test
    public void testDeleteLeads() throws Exception {
        props.outputOperation.setValue(OutputOperation.deleteLeads);
        props.afterOutputOperation();
        assertFalse(props.getForm(Form.MAIN).getWidget(props.mappingInput.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.operationType.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.lookupField.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.deDupeEnabled.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.customObjectDedupeBy.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.customObjectDeleteBy.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.customObjectName.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.customObjectSyncAction.getName()).isVisible());
        //
        assertTrue(props.getForm(Form.MAIN).getWidget(props.deleteLeadsInBatch.getName()).isVisible());
        assertFalse(props.getForm(Form.MAIN).getWidget(props.batchSize.getName()).isVisible());
        //
        props.deleteLeadsInBatch.setValue(true);
        props.afterDeleteLeadsInBatch();
        assertTrue(props.getForm(Form.MAIN).getWidget(props.deleteLeadsInBatch.getName()).isVisible());
        assertTrue(props.getForm(Form.MAIN).getWidget(props.batchSize.getName()).isVisible());
    }

    @Test
    public void testDeleteLeadsSchemas() throws Exception {
        props.outputOperation.setValue(OutputOperation.deleteLeads);
        props.afterOutputOperation();
        assertEquals(MarketoConstants.getDeleteLeadsSchema(), props.schemaInput.schema.getValue());
        Schema flow = props.schemaFlow.schema.getValue();
        assertNotNull(flow.getField("Status"));
        props.deleteLeadsInBatch.setValue(true);
        props.afterDeleteLeadsInBatch();
        assertEquals(MarketoConstants.getDeleteLeadsSchema(), props.schemaInput.schema.getValue());
        assertEquals(MarketoConstants.getEmptySchema(), props.schemaFlow.schema.getValue());
        // assertEquals(MarketoConstants.getDeleteLeadsSchema().getFields(),
        // props.schemaFlow.schema.getValue().getFields());
    }

}
