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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.marketo.runtime.client.type.MarketoError;
import org.talend.components.marketo.runtime.client.type.MarketoException;
import org.talend.components.marketo.runtime.client.type.MarketoRecordResult;
import org.talend.components.marketo.tmarketoconnection.TMarketoConnectionProperties;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;

public class MarketoCompanyClient extends MarketoCampaignClient {

    public static final String API_PATH_COMPANIES = "/v1/companies";

    public static final String API_PATH_COMPANIES_DESCRIBE = API_PATH_COMPANIES + API_PATH_URI_DESCRIBE;

    public static final String API_PATH_COMPANIES_GET = API_PATH_COMPANIES + API_PATH_JSON_EXT;

    public static final String API_PATH_COMPANIES_SYNC = API_PATH_COMPANIES + API_PATH_JSON_EXT;

    public static final String API_PATH_COMPANIES_DELETE = API_PATH_COMPANIES + API_PATH_URI_DELETE;

    private static final Logger LOG = LoggerFactory.getLogger(MarketoCompanyClient.class);

    public MarketoCompanyClient(TMarketoConnectionProperties connection) throws MarketoException {
        super(connection);
    }

    public MarketoRecordResult getCompanies(TMarketoInputProperties parameters) {
        return null;
    }

    public MarketoRecordResult describeCompanies(TMarketoInputProperties parameters) {
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();
        //
        current_uri = new StringBuilder(basicPath)//
                .append(API_PATH_COMPANIES_DESCRIBE)//
                .append(fmtParams(FIELD_ACCESS_TOKEN, accessToken, true))//
                .append(fmtParams(FIELD_BATCH_SIZE, batchLimit));
        LOG.debug("describeCompanies : {}.", current_uri);
        MarketoRecordResult mkto = new MarketoRecordResult();
        try {
            mkto = executeGetRequest(parameters.schemaInput.schema.getValue());
            LOG.debug("mkto = {}.", mkto);
        } catch (MarketoException e) {
            LOG.error("{}.", e);
            mkto.setSuccess(false);
            mkto.setRecordCount(0);
            mkto.setErrors(Arrays.asList(new MarketoError(REST, e.getMessage())));
        }
        return mkto;
    }

}
