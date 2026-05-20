package com.campaign.analytics.resource;

import com.campaign.analytics.dto.*;
import com.campaign.analytics.repository.MetricsRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    @Inject
    private MetricsRepository repository;

    @GET
    @Path("/timeseries")
    public Response getTimeseries(@QueryParam("tag") String tag) {
        List<TimeseriesMetricRow> data = repository.findTimeseriesMetrics(java.util.Optional.ofNullable(tag));
        return Response.ok(data).build();
    }

    @GET
    @Path("/site")
    public Response getSiteMetrics(@QueryParam("tag") String tag) {
        List<SiteMetricRow> data = repository.findSiteMetrics(java.util.Optional.ofNullable(tag));
        return Response.ok(data).build();
    }

    @GET
    @Path("/dma")
    public Response getDmaMetrics(@QueryParam("tag") String tag) {
        List<DmaMetricRow> data = repository.findDmaMetrics(java.util.Optional.ofNullable(tag));
        return Response.ok(data).build();
    }

    @GET
    @Path("/events/types")
    public Response getEventTypes() {
        List<String> types = repository.findEventTypes();
        return Response.ok(types).build();
    }
}