package com.campaign.analytics.resource;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.campaign.analytics.service.CsvImportService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/metrics/import")
public class MetricsImportResource {

    @Inject
    CsvImportService csvImportService;

    @POST
    @Path("/impressions")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importImpressions(@FormDataParam("file") InputStream file) {
        try {
            long count = csvImportService.importImpressions(file);
            csvImportService.refreshMaterializedViews();
            return Response.ok("{\"imported\":" + count + ", \"mvRefreshed\": true}").build();
        }
        catch (Exception e) {
            return Response.serverError()
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/events")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importEvents(@FormDataParam("file") InputStream file) {
        try {
            long count = csvImportService.importEvents(file);
            csvImportService.refreshMaterializedViews();
            return Response.ok("{\"imported\":" + count + ", \"mvRefreshed\": true}").build();
        }
        catch (Exception e) {
            return Response.serverError()
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}