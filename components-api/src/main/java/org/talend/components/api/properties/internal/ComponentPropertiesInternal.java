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
package org.talend.components.api.properties.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.components.api.ComponentDesigner;
import org.talend.components.api.properties.Property;
import org.talend.components.api.properties.PropertyValueEvaluator;
import org.talend.components.api.properties.ValidationResult;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.schema.Schema;
import org.talend.components.api.schema.SchemaElement;
import org.talend.components.api.schema.SchemaElement.Type;
import org.talend.components.api.schema.SchemaFactory;

public class ComponentPropertiesInternal {

    protected String name;

    protected String title;

    protected ComponentDesigner designer;

    protected boolean runtimeOnly;

    protected List<Form> forms;

    protected ValidationResult validationResult;

    protected Map<SchemaElement, Object> propertyValues;

    transient private PropertyValueEvaluator propertyValueEvaluator;

    public ComponentPropertiesInternal() {
        forms = new ArrayList<>();
        propertyValues = new HashMap<>();
    }

    public void setRuntimeOnly() {
        runtimeOnly = true;
    }

    public boolean isRuntimeOnly() {
        return runtimeOnly;
    }

    public void resetForms() {
        forms = new ArrayList();
    }

    public void setforms(List<Form> forms) {
        this.forms = forms;
    }

    public List<Form> getForms() {
        return forms;
    }

    public Form getForm(String name) {
        for (Form f : forms) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void doSetValue(SchemaElement property, Object value) {
        propertyValues.put(property, value);
    }

    public Object getValue(Property property) {
        Object storedValue = getStoredValue(property);
        if (propertyValueEvaluator != null) {
            return propertyValueEvaluator.evaluate(property, storedValue);
        }
        return storedValue;
    }

    public ComponentDesigner getDesigner() {
        return designer;
    }

    public void setDesigner(ComponentDesigner designer) {
        this.designer = designer;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * Returns the {@link ValidationResult} for the property being validated if requested.
     *
     * @return a ValidationResult
     */
    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public void setValue(Property property, Object value) {
        Object valueToSet = value;
        if (property.getType() == Type.SCHEMA && value instanceof String) {
            valueToSet = SchemaFactory.fromSerialized((String) value);
        }
        doSetValue(property, valueToSet);
    }

    public boolean getBooleanValue(Property property) {
        Boolean value = (Boolean) getValue(property);
        return value != null && value;
    }

    public String getStringValue(Property property) {
        Object value = getValue(property);
        if (value != null) {
            if (value instanceof Schema) {
                return ((Schema) value).toSerialized();
            }
            return value.toString();
        }
        return null;
    }

    public int getIntValue(Property namedThing) {
        Object value = getValue(namedThing);
        if (value == null) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(value));
    }

    public Calendar getCalendarValue(Property property) {
        return (Calendar) getValue(property);
    }

    /**
     * DOC sgandon Comment method "setValueEvaluator".
     * 
     * @param ve
     */
    public void setValueEvaluator(PropertyValueEvaluator ve) {
        this.propertyValueEvaluator = ve;
    }

    /**
     * @return the stored value without any evaluation.
     */
    public Object getStoredValue(Property property) {
        return propertyValues.get(property);
    }

}
