package com.smartcampus.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    public static Map<String, Room> rooms = new HashMap<>();

    // GET all rooms
    @GET
    public Collection<Room> getRooms() {
        return rooms.values();
    }

    // POST create room
    @POST
    public Response createRoom(Room room) {
        rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // GET single room
    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    // DELETE room
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
          //one of the main important requirements of the coursework
        if (!room.getSensorIds().isEmpty()) { //Part 5 exception
            throw new RoomNotEmptyException("Room has sensors assigned, cannot delete");
        }

        rooms.remove(id);
        return Response.ok("Room deleted").build();
    }
}