package net.waret.demo.authz.service;

import net.waret.demo.authz.domain.Group;
import net.waret.demo.authz.domain.Resource;
import net.waret.demo.authz.domain.Role;
import net.waret.demo.authz.domain.User;
import net.waret.demo.authz.repository.GroupRepository;
import net.waret.demo.authz.repository.PermissionRepository;
import net.waret.demo.authz.repository.ResourceRepository;
import net.waret.demo.authz.repository.RoleRepository;
import net.waret.demo.authz.repository.UserRepository;
import net.waret.demo.authz.service.dto.RegistorResourceRequest;
import net.waret.demo.authz.service.dto.RegistorResourceResponse;
import net.waret.demo.authz.service.dto.Result;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private final ResourceRepository resourceRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

    private final GroupRepository groupRepository;

    public UserRoleService(
            ResourceRepository resourceRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PermissionRepository permissionRepository,
            GroupRepository groupRepository) {
        this.resourceRepository = resourceRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.groupRepository = groupRepository;
    }

    public RegistorResourceResponse registerResource(
            RegistorResourceRequest registorResourceRequest) {
        RegistorResourceResponse registorResourceResponse = new RegistorResourceResponse();

        Resource parentResource = resourceRepository.getOne(registorResourceRequest.getParentResourceId());
        Resource resource = new Resource()
                .setResourceName(registorResourceRequest.getResourceName())
                .setResourceType(registorResourceRequest.getResourceType())
                .setParentResource(parentResource);
        registorResourceResponse.setResource(resource);

        // 创建 Resource 关联的多个角色
        registorResourceRequest.getRoleParents().forEach((roleField, roleParent) ->
                roleRepository.save(new Role().setRoleField(roleField).setResource(resource))
        );
        // 添加角色之间的父子关系
        registorResourceRequest.getRoleParents().forEach((roleField, roleParent) ->
                roleRepository.findByRoleFieldAndResource(roleField, resource)
                        .ifPresent(role ->
                                roleParent.forEach(parent -> {
                                            if (parent.getResourceId() == null) {
                                                // 当前 Resource 的角色
                                                roleRepository.findByRoleFieldAndResource(parent.getRoleField(), resource).ifPresent(role::addParent);
                                            } else {
                                                // 指定 Resource 的角色
                                                resourceRepository.findById(parent.getResourceId()).ifPresent(parentRoleResource ->
                                                        roleRepository.findByRoleFieldAndResource(parent.getRoleField(), parentRoleResource)
                                                                .ifPresent(role::addParent));
                                            }
                                            role.updateAncestors();
                                            registorResourceResponse.addRole(role);
                                        }
                                )
                        )
        );

        return registorResourceResponse;
    }

    public Result checkPermission(String username, Long resourceId, String resourceType,
            String method) {
        return permissionRepository.findByMethodAndResourceType(method, resourceType)
                .map(permission -> resourceRepository.findById(resourceId)
                        .map(resource -> resource.getRoles().stream()
                                .filter(role -> role.getRoleField().equalsIgnoreCase(permission.getRoleField()))
                                .findFirst()
                                .map(role -> userRepository.findByUsername(username)
                                        .map(user -> user.getGrantedRoles().stream()
                                                .map(Role::getDescentdents)
                                                .reduce(new HashSet<>(), (roles, roles2) -> {
                                                    roles.addAll(roles2);
                                                    return roles;
                                                })
                                                .contains(role) ? Result.ok() : Result.failed("no permission")
                                        )
                                        .orElse(Result.failed("no user"))
                                )
                                .orElse(Result.failed("no role"))
                        )
                        .orElse(Result.failed("no resource"))
                )
                .orElse(Result.failed("no method"));
    }

    public User registorUser(String username, Long domainId) {
        return resourceRepository.findById(domainId)
                .map(domain -> userRepository.save(
                        new User().setUsername(username).setDomain(domain)))
                .orElseThrow(() -> new RuntimeException("no domain"));
    }

    public Group registorGroup(String groupName, Long parentId) {
        // TODO 添加 Group 关联的 Admin/Member/Read 等角色
        return resourceRepository.findById(parentId)
                .map(parent -> {
                    Group group = groupRepository.save(new Group().setGroupName(groupName));
                    RegistorResourceRequest registorResourceRequest = new RegistorResourceRequest();
                    registorResourceRequest.setResourceName(groupName);
                    registorResourceRequest.setResourceName("Group");
                    registorResourceRequest.setParentResourceId(parentId);

                    // admin_role
                    List<RegistorResourceRequest.RoleField> adminParents = new LinkedList<>();
                    // member_role
                    List<RegistorResourceRequest.RoleField> memberParents = new LinkedList<>();
                    memberParents.add(new RegistorResourceRequest.RoleField()
                            .setResourceType("Group")
                            .setRoleField("admin_role"));
                    // read_role
                    List<RegistorResourceRequest.RoleField> readParents = new LinkedList<>();
                    readParents.add(new RegistorResourceRequest.RoleField()
                            .setResourceType("Group")
                            .setRoleField("member_role"));

                    if (parent.getResourceType().equalsIgnoreCase("Group")) {
                        adminParents.add(new RegistorResourceRequest.RoleField()
                                .setResourceType(parent.getResourceType())
                                .setResourceId(parent.getResourceId())
                                .setRoleField("admin_role"));
                        readParents.add(new RegistorResourceRequest.RoleField()
                                .setResourceType(parent.getResourceType())
                                .setResourceId(parent.getResourceId())
                                .setRoleField("read_role"));
                    } else if (parent.getResourceType().equalsIgnoreCase("Domain")) {
                        adminParents.add(new RegistorResourceRequest.RoleField()
                                .setResourceType(parent.getResourceType())
                                .setResourceId(parent.getResourceId())
                                .setRoleField("system_administrator"));
                        readParents.add(new RegistorResourceRequest.RoleField()
                                .setResourceType(parent.getResourceType())
                                .setResourceId(parent.getResourceId())
                                .setRoleField("system_auditor"));
                    }
                    registorResourceRequest.addRoleParent("admin_role", adminParents);
                    registorResourceRequest.addRoleParent("member_role", memberParents);
                    registorResourceRequest.addRoleParent("read_role", readParents);
                    return group.setResource(registerResource(registorResourceRequest).getResource());
                })
                .orElseThrow(() -> new RuntimeException("no parent domain/group"));
    }

    public void addUserToGroup(String groupName, String username, String roleField) {
        groupRepository.findByGroupName(groupName).ifPresent(group ->
                group.getResource().getRoles().forEach(role -> {
                    if (role.getRoleField().equalsIgnoreCase(roleField)) {
                        userRepository.findByUsername(username).ifPresent(user -> user.getGrantedRoles().add(role));
                    }
                })
        );
    }

    public void grantUser(String username, Long resourceId, String roleField) {
        grantUser(username, resourceId, roleField, Instant.MAX, true);
    }

    private void grantUser(String username, Long resourceId, String roleField, Instant endtime,
            boolean logic) {
        resourceRepository.findById(resourceId)
                .ifPresent(resource -> resource.getRoles().stream()
                        .filter(role -> role.getRoleField().equalsIgnoreCase(roleField))
                        .findFirst()
                        .ifPresent(role -> userRepository.findByUsername(username).ifPresent(user ->
                                user.getGrantedRoles().add(role))
                        )
                );
    }

    public void grantGroup(String groupName, Long resourceId, String roleField) {
        grantGroup(groupName, resourceId, roleField, Instant.MAX, true);
    }

    public void grantGroup(String groupName, Long resourceId, String roleField, Instant endtime,
            boolean logic) {
        resourceRepository.findById(resourceId).ifPresent(resource -> resource.getRoles().stream()
                .filter(role -> role.getRoleField().equalsIgnoreCase(roleField))
                .findFirst()
                .ifPresent(role -> groupRepository.findByGroupName(groupName).ifPresent(group -> {
                    for (Role groupRole : group.getResource().getRoles()) {
                        if (groupRole.getRoleField().equalsIgnoreCase("admin_role")
                                || groupRole.getRoleField().equalsIgnoreCase("member_role")) {
                            role.addParent(groupRole).updateAncestors();
                        }
                    }
                })));
    }

    public void revokeUser(String username, Long resourceId, String roleField) {
        userRepository.findByUsername(username).ifPresent(user ->
                resourceRepository.findById(resourceId)
                        .ifPresent(resource -> resource.getRoles().stream()
                                .filter(role -> role.getRoleField().equalsIgnoreCase(roleField))
                                .findFirst()
                                .ifPresent(role -> user.getGrantedRoles().remove(role))
                        )
        );
    }

    public void revokeGroup(String groupName, Long resourceId, String roleField) {
        resourceRepository.findById(resourceId).ifPresent(resource -> resource.getRoles().stream()
                .filter(role -> role.getRoleField().equalsIgnoreCase(roleField))
                .findFirst()
                .ifPresent(role ->
                        groupRepository.findByGroupName(groupName).ifPresent(group -> {
                            for (Role groupRole : group.getResource().getRoles()) {
                                if (groupRole.getRoleField().equalsIgnoreCase("admin_role")
                                        || groupRole.getRoleField().equalsIgnoreCase("member_role")) {
                                    role.removeParent(groupRole).updateAncestors();
                                }
                            }
                        })
                )
        );
    }

    public void queryResourceList(String username, String resourceType, String roleField) {
        userRepository.findByUsername(username).map(user -> user.getGrantedRoles().stream()
                .map(Role::getDescentdents)
                .reduce(new HashSet<>(), (roles, roles2) -> {
                    roles.addAll(roles2);
                    return roles;
                })
                .stream()
                .filter(role -> role.getRoleField().equalsIgnoreCase(roleField)
                        && role.getResource().getResourceType().equalsIgnoreCase(resourceType))
                .map(Role::getResource)
                .collect(Collectors.toSet())
        ).orElseThrow(() -> new RuntimeException("no user"));
    }

}
