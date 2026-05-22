package com.campaign.analytics.resource;

import java.util.List;
import java.util.Optional;

import com.campaign.analytics.dto.DmaMetricRow;
import com.campaign.analytics.dto.SiteMetricRow;
import com.campaign.analytics.dto.TimeseriesMetricRow;
import com.campaign.analytics.repository.MetricsRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    @Inject
    private MetricsRepository repository;

    @GET
    @Path("/timeseries")
    public Response getTimeseries(@QueryParam("tag") String tag) {

        List<TimeseriesMetricRow> data =
                repository.findTimeseriesMetrics(Optional.ofNullable(tag));

        return Response.ok(data).build();
    }

    @GET
    @Path("/site")
    public Response getSiteMetrics(@QueryParam("tag") String tag) {

        List<SiteMetricRow> data =
                repository.findSiteMetrics(Optional.ofNullable(tag));

        return Response.ok(data).build();
    }

    @GET
    @Path("/dma")
    public Response getDmaMetrics(@QueryParam("tag") String tag) {

        List<DmaMetricRow> data =
                repository.findDmaMetrics(Optional.ofNullable(tag));

        return Response.ok(data).build();
    }

    @GET
    @Path("/events/types")
    public Response getEventTypes() {

        List<String> types = repository.findEventTypes();

        return Response.ok(types).build();
    }

    @OPTIONS
    @Path("{path: .*}")
    public Response preflight() {
        return Response.ok().build();
    }
}