package org.acme.api.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class UserDTO {

    @Schema(required = true, example = "Syahril")
    public int name;

    @Schema(required = true, example = "Bandung")
    public String city;
}
