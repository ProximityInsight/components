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
package org.talend.components.marketo.runtime.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.marketo.MarketoConstants;
import org.talend.components.marketo.runtime.client.rest.response.CustomObjectResult;
import org.talend.components.marketo.runtime.client.rest.response.SyncResult;
import org.talend.components.marketo.runtime.client.type.MarketoError;
import org.talend.components.marketo.runtime.client.type.MarketoException;
import org.talend.components.marketo.runtime.client.type.MarketoRecordResult;
import org.talend.components.marketo.runtime.client.type.MarketoSyncResult;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Marketers deliver leads to sales in the form of an opportunity. An opportunity represents a potential sales deal and
 * is associated with a lead or contact and an organization in Marketo. An opportunity role is the intersection between
 * a given lead and an organization. The opportunity role pertains to a lead’s function within the organization.
 *
 */
public class MarketoOpportunityClient extends MarketoCompanyClient {

    public static final String API_PATH_OPPORTUNITIES = "/v1/opportunities";

    public static final String API_PATH_OPPORTUNITY_ROLE = API_PATH_OPPORTUNITIES + "/roles";

    private static final Logger LOG = LoggerFactory.getLogger(MarketoOpportunityClient.class);

    public MarketoOpportunityClient(TMarketoConnectionProperties connection) throws MarketoException {
        super(connection);
    }

    /**
     * Returns object and field metadata for Opportunity type records in the target instance.
     *
     * @param parameters
     * @return
     */
    public MarketoRecordResult describeOpportunity(TMarketoInputProperties parameters) {
        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_OPPORTUNITIES)//
                .append(API_PATH_URI_DESCRIBE)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true));
        LOG.warn("describeOpportunity : {}.", current_uri);
        MarketoRecordResult mkto = new MarketoRecordResult();
        mkto.setRemainCount(0);
        try {
            CustomObjectResult result = (CustomObjectResult) executeGetRequest(CustomObjectResult.class);
            mkto.setSuccess(result.isSuccess());
            mkto.setRequestId(REST + "::" + result.getRequestId());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(1);
                mkto.setRecords(result.getRecords());
            } else {
                if (result.getErrors() != null) {
                    mkto.setErrors(result.getErrors());
                }
            }
        } catch (MarketoException e) {
            LOG.error("{}.", e);
            mkto.setSuccess(false);
            mkto.setRecordCount(0);
            mkto.setErrors(Arrays.asList(new MarketoError(REST, e.getMessage())));
        }
        return mkto;
    }

    /**
     * Returns a list of opportunities based on a filter and set of values.
     *
     * @param parameters
     * @return
     */
    public MarketoRecordResult getOpportunities(TMarketoInputProperties parameters, String offset) {
        String filterType = parameters.customObjectFilterType.getValue();
        String filterValues = parameters.customObjectFilterValues.getValue();
        // if fields is unset : marketoGuid, dedupeFields (defined in mkto), updatedAt, createdAt will be returned.
        List<String> fields = new ArrayList<>();
        for (String f : parameters.getSchemaFields()) {
            if (!f.equals(MarketoConstants.FIELD_MARKETO_GUID) && !f.equals(MarketoConstants.FIELD_SEQ)) {
                fields.add(f);
            }
        }
        //
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();

        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_OPPORTUNITIES)//
                .append(API_PATH_JSON_EXT)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true))//
                .append(fmtParams(QUERY_METHOD, QUERY_METHOD_GET)).append(fmtParams("filterType", filterType))//
                .append(fmtParams("filterValues", filterValues))//
                .append(fmtParams(FIELD_BATCH_SIZE, batchLimit));
        if (!fields.isEmpty()) {
            current_uri.append(fmtParams(FIELD_FIELDS, csvString(fields.toArray())));
        }
        if (offset != null) {
            current_uri.append(fmtParams(FIELD_NEXT_PAGE_TOKEN, offset));
        }
        LOG.debug("getOpportunities : {}.", current_uri);
        MarketoRecordResult mkto = new MarketoRecordResult();
        try {
            mkto = executeGetRequest(parameters.schemaInput.schema.getValue());
        } catch (MarketoException e) {
            LOG.error("{}.", e);
            mkto.setSuccess(false);
            mkto.setRecordCount(0);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;

    }

    /**
     * Allows inserting, updating, or upserting of opportunity records into the target instance.
     * 
     * @param parameters
     * @param records
     * @return
     */
    public MarketoSyncResult syncOpportunities(TMarketoOutputProperties parameters, List<IndexedRecord> records) {
        return null;
    }

    /**
     * Deletes a list of opportunity records from the target instance. Input records should only have one member, based
     * on the value of 'dedupeBy'.
     *
     * @param parameters
     * @param records
     * @return
     */
    public MarketoSyncResult deleteOpportunities(TMarketoOutputProperties parameters, List<IndexedRecord> records) {
        String deleteBy = parameters.customObjectDeleteBy.getValue().name();
        JsonObject inputJson = new JsonObject();
        Gson gson = new Gson();
        if (!deleteBy.isEmpty()) {
            inputJson.addProperty("deleteBy", deleteBy);
        }
        List<Map<String, Object>> opportunities = new ArrayList<>();
        for (IndexedRecord r : records) {
            Map<String, Object> lead = new HashMap<>();
            for (Field f : r.getSchema().getFields()) {
                lead.put(f.name(), r.get(f.pos()));
            }
            opportunities.add(lead);
        }
        inputJson.add(FIELD_INPUT, gson.toJsonTree(opportunities));
        //
        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_OPPORTUNITIES)//
                .append(API_PATH_URI_DELETE)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true));
        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            LOG.debug("deleteOpportunity {}{}.", current_uri, inputJson);
            SyncResult rs = (SyncResult) executePostRequest(SyncResult.class, inputJson);
            mkto.setRequestId(REST + "::" + rs.getRequestId());
            mkto.setStreamPosition(rs.getNextPageToken());
            mkto.setSuccess(rs.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(rs.getResult().size());
                mkto.setRemainCount(mkto.getStreamPosition() != null ? mkto.getRecordCount() : 0);
                mkto.setRecords(rs.getResult());
            } else {
                mkto.setRecordCount(0);
                mkto.setErrors(Arrays.asList(new MarketoError(REST, "Could not delete Opportunity.")));
            }
        } catch (MarketoException e) {
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

}
