package com.example.pfl.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PermissionType {
    ADMIN_READ,
    ADMIN_CREATE,
    ADMIN_UPDATE,
    ADMIN_DELETE,

    MANAGER_READ,
    MANAGER_CREATE,
    MANAGER_UPDATE,
    MANAGER_DELETE_CREATION,

    USER_CREATE_CREATION;

    private String libelle;
}
