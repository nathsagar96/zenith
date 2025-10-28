package com.zenith.dtos.requests;

import com.zenith.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for updating a role")
public record UpdateRoleRequest(
        @Schema(
                        description = "The role to be updated",
                        example = "ADMIN",
                        allowableValues = {"ADMIN", "USER"})
                @NotNull(message = "Role cannot be null")
                RoleType role) {}
