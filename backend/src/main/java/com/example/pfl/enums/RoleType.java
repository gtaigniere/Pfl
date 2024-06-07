package com.example.pfl.enums;

//@AllArgsConstructor
//@Getter
public enum RoleType {
    USER
            /*(
            Set.of(PermissionType.USER_CREATE_CREATION)
    )*/,
    MANAGER
            /*(
            Set.of(
                    MANAGER_READ,
                    MANAGER_CREATE,
                    MANAGER_UPDATE,
                    MANAGER_DELETE_CREATION
            )
    )*/,
    ADMIN
            /*(
            Set.of(
                    ADMIN_READ,
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,

                    MANAGER_READ,
                    MANAGER_CREATE,
                    MANAGER_UPDATE,
                    MANAGER_DELETE_CREATION
            )
    );

//    final Set<PermissionType> permissions;

    /*
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<SimpleGrantedAuthority> grantedAuthorities = this.getPermissions().stream().map(
                permission -> new SimpleGrantedAuthority(permission.getLibelle())
        ).collect(Collectors.toList());

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return grantedAuthorities;
    }
    */
}
