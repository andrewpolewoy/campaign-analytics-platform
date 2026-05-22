package com.campaign.analytics.resource;

import com.campaign.analytics.service.CsvImportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

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
            return Response.ok("{\"imported\":" + count + "}").build();
        } catch (Exception e) {
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
            return Response.ok("{\"imported\":" + count + "}").build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}