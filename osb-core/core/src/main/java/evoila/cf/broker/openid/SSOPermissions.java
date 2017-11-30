package evoila.cf.broker.openid;

public class SSOPermissions {
    public enum PermissionType { USER, NONE };
    private PermissionType permission;

    public PermissionType getPermission () {
        return permission;
    }

    public void setPermission (PermissionType permission) {
        this.permission = permission;
    }
}
