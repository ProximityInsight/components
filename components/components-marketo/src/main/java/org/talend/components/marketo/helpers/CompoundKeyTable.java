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
package org.talend.components.marketo.helpers;

import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CompoundKeyTable extends ComponentPropertiesImpl {

    private static final TypeLiteral<List<String>> LIST_STRING_TYPE = new TypeLiteral<List<String>>() {// empty
    };

    public static final String ADD_QUOTES = "ADD_QUOTES";

    public Property<List<String>> keyName = newProperty(LIST_STRING_TYPE, "keyName");

    public Property<List<String>> keyValue = newProperty(LIST_STRING_TYPE, "keyValue");

    public CompoundKeyTable(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addColumn(keyName);
        mainForm.addColumn(keyValue);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        keyName.setTaggedValue(ADD_QUOTES, true);
        keyValue.setTaggedValue(ADD_QUOTES, true);
    }

    public Map<String, String> getKeyValues() {
        Map<String, String> result = new HashMap<>();
        List<String> values = keyName.getValue();
        if (values == null || values.isEmpty()) {
            return result;
        }
        for (int i = 0; i < values.size(); i++) {
            String keyName = this.keyName.getValue().get(i);
            String keyValue = this.keyValue.getValue().get(i);
            if (keyValue == null || keyValue.isEmpty()) {
                keyValue = keyName;
            }
            result.put(keyName, keyValue);
        }
        return result;
    }

    public JsonElement getKeyValuesAsJson() {
        List<JsonObject> result = new ArrayList<>();
        List<String> values = keyName.getValue();
        if (values == null || values.isEmpty()) {
            return new Gson().toJsonTree(result);
        }
        for (int i = 0; i < values.size(); i++) {
            String keyName = this.keyName.getValue().get(i);
            String keyValue = this.keyValue.getValue().get(i);
            JsonObject resultj = new JsonObject();
            resultj.addProperty("name", this.keyName.getValue().get(i));
            resultj.addProperty("value", this.keyValue.getValue().get(i));
            result.add(resultj);
        }
        return new Gson().toJsonTree(result);
    }

    public int size() {
        if (keyName.getValue() == null) {
            return 0;
        }
        return keyName.getValue().size();
    }

}
