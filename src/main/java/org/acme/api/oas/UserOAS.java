package org.acme.api.oas;

import org.acme.api.dto.UserDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class UserOAS {
    @Schema(name = "AddCityOAS.Request")
    public class Request extends UserDTO {
    }
}
