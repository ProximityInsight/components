// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce.tsalesforceoutput;

import static org.talend.components.api.properties.presentation.Widget.*;
import static org.talend.components.api.schema.SchemaFactory.*;

import org.talend.components.api.i18n.I18nMessageProvider;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.schema.Schema;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.common.SchemaProperties;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.SalesforceModuleProperties;

public class TSalesforceOutputProperties extends ComponentProperties {

    public enum OutputAction {
                              INSERT,
                              UPDATE,
                              UPSERT,
                              DELETE
    }

    public SchemaElement outputAction = newProperty(SchemaElement.Type.ENUM, "outputAction"); //$NON-NLS-1$

    public SchemaElement upsertKeyColumn = newProperty("upsertKeyColumn"); //$NON-NLS-1$

    //
    // Advanced
    //
    public SchemaElement extendInsert = newProperty(SchemaElement.Type.BOOLEAN, "extendInsert"); //$NON-NLS-1$

    public SchemaElement ceaseForError = newProperty(SchemaElement.Type.BOOLEAN, "ceaseForError"); //$NON-NLS-1$

    public SchemaElement ignoreNull = newProperty(SchemaElement.Type.BOOLEAN, "ignoreNull"); //$NON-NLS-1$

    public SchemaElement retrieveInsertId = newProperty("retrieveInsertId"); //$NON-NLS-1$

    public SchemaElement commitLevel = newProperty("commitLevel"); //$NON-NLS-1$

    // FIXME - should be file
    public SchemaElement logFileName = newProperty("logFileName"); //$NON-NLS-1$

    // FIXME - need upsertRelation property which is a table

    //
    // Collections
    //
    public SalesforceConnectionProperties connection;

    public SalesforceModuleProperties module;

    public SchemaProperties schemaFlow;

    public SchemaProperties schemaReject;

    public TSalesforceOutputProperties(I18nMessageProvider i18nMessageProvider) {
        super(i18nMessageProvider, "org.talend.components.salesforce.message"); //$NON-NLS-1$
        schemaFlow = new SchemaProperties(i18nMessageProvider);
        schemaReject = new SchemaProperties(i18nMessageProvider);
        schemaReject.addChild(newProperty("errorCode")); //$NON-NLS-1$
        schemaReject.addChild(newProperty("errorFields")); //$NON-NLS-1$
        schemaReject.addChild(newProperty("errorMessage")); //$NON-NLS-1$
        connection = new SalesforceConnectionProperties(i18nMessageProvider);

        module = new SalesforceModuleProperties(i18nMessageProvider, connection);

        setupLayout();
        setupPropertiesWithI18n();

    }

    public static final String MAIN = "Main"; //$NON-NLS-1$

    public static final String ADVANCED = "Advanced"; //$NON-NLS-1$

    @Override
    public void setupLayout() {
        Form mainForm = Form.create(this, MAIN, "Salesforce Output");
        mainForm.addRow(connection.getForm(SalesforceConnectionProperties.MAIN));
        mainForm.addRow(module.getForm(SalesforceModuleProperties.REFERENCE));
        mainForm.addRow(outputAction);
        refreshLayout(mainForm);

        Form advancedForm = Form.create(this, ADVANCED, "Advanced");
        mainForm.addRow(extendInsert);
        mainForm.addRow(ceaseForError);
        mainForm.addRow(ignoreNull);
        mainForm.addRow(commitLevel);
        mainForm.addRow(logFileName);
        mainForm.addColumn(retrieveInsertId);
        mainForm.addRow(widget(schemaFlow.getForm(SchemaProperties.REFERENCE)).setName("SchemaFlow").setTitle("Schema Flow"));
        mainForm.addRow(
                widget(schemaReject.getForm(SchemaProperties.REFERENCE)).setName("SchemaReject").setTitle("Schema Reject"));
        refreshLayout(mainForm);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        ((Schema) schemaFlow.getValue(schemaFlow.schema)).setRoot(null);
        if (!getBooleanValue(extendInsert) && getStringValue(retrieveInsertId) != null
                && getValue(outputAction) == OutputAction.INSERT) {
            schemaFlow.addChild(newProperty("salesforce_id"));
        }

    }

}