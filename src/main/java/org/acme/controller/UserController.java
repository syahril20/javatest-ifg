package org.acme.controller;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.api.oas.UserOAS;
import org.acme.services.UserService;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    @POST
    public Response getAllUser(){
        try {
            return userService.getAllUser();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }

    @POST
    @Path("/addUser")
//    @Consumes(MediaType.APPLICATION_JSON)
    @RequestBody(content = {
            @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserOAS.Request.class))
    })
    public Response addUser(Object param){
        return userService.addUser(param);
    }

    @POST
    @Path("/uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFatCode(@MultipartForm MultipartFormDataInput form){
        return userService.uploadFile(form);
    }

    @POST
    @Path("/download")
    @Produces("text/csv")
    public Response download() {
        try {
            return userService.getUser();
        } catch (Exception e) {
            return Response.ok(e.getMessage()).build();
        }
    }

}
