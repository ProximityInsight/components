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

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;

import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.marketo.runtime.client.rest.response.CustomObjectResult;
import org.talend.components.marketo.runtime.client.type.MarketoError;
import org.talend.components.marketo.runtime.client.type.MarketoException;
import org.talend.components.marketo.runtime.client.type.MarketoRecordResult;
import org.talend.components.marketo.runtime.client.type.MarketoSyncResult;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties;

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
    public MarketoRecordResult getOpportunities(TMarketoInputProperties parameters) {
        String names = parameters.customObjectNames.getValue();

        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_OPPORTUNITIES)//
                .append(API_PATH_JSON_EXT)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true))//
                .append(fmtParams("names", names))//
                .append(fmtParams(QUERY_METHOD, QUERY_METHOD_GET));
        LOG.debug("listOpportunities : {}.", current_uri);
        MarketoRecordResult mkto = new MarketoRecordResult();
        mkto.setRemainCount(0);
        try {
            CustomObjectResult result = (CustomObjectResult) executeGetRequest(CustomObjectResult.class);
            mkto.setSuccess(result.isSuccess());
            mkto.setRequestId(REST + "::" + result.getRequestId());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().size());
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
            mkto.setErrors(asList(new MarketoError(REST, e.getMessage())));
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
        return null;
    }

}
