package com.smartcampus.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {

        // 👇 Handle bad JSON specifically
        if (ex.getMessage() != null && ex.getMessage().contains("JSON")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid request format\"}")
                    .build();
        }

        // 👇 fallback for everything else
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Something went wrong\"}")
                .build();
    }
}