package net.waret.demo.rls.service;

import net.waret.demo.rls.domain.ObjectType;
import net.waret.demo.rls.domain.Organization;
import net.waret.demo.rls.domain.Permission;
import net.waret.demo.rls.domain.Role;
import net.waret.demo.rls.domain.Team;
import net.waret.demo.rls.domain.User;
import net.waret.demo.rls.repository.ObjectTypeRepository;
import net.waret.demo.rls.repository.OrganizationRepository;
import net.waret.demo.rls.repository.PermissionRepository;
import net.waret.demo.rls.repository.RoleRepository;
import net.waret.demo.rls.repository.TeamRepository;
import net.waret.demo.rls.repository.UserRepository;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static net.waret.demo.rls.domain.Role.SYSTEM_ADMINISTRATOR;
import static net.waret.demo.rls.domain.Role.SYSTEM_AUDITOR;

@Slf4j
@Getter
@Setter
@Service
public class UserRoleService {

    private final ObjectTypeRepository objectTypeRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final TeamRepository teamRepository;

    private Role systemAdministrator;
    private Role systemAuditor;
    private User admin;
    private Organization defaultOrganization;
    private ObjectType userObjectType;
    private ObjectType roleObjectType;
    private ObjectType teamObjectType;
    private ObjectType organizationObjectType;
    private ObjectType permissionObjectType;
    private ObjectType projectObjectType;

    public UserRoleService(ObjectTypeRepository objectTypeRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            OrganizationRepository organizationRepository,
            TeamRepository teamRepository) {
        this.objectTypeRepository = objectTypeRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.teamRepository = teamRepository;
    }

    public void preloadData() {
        log.info("--------------- initDatabase");
        addObjectTypeAndPermission();
        addSystemRole();
        addAdminUser();
        addDefaultOrganization();
    }

    private void addDefaultOrganization() {
        defaultOrganization = addOrganization("Default", "Default Organization description", admin);
    }

    private Role _newRole(String roleField, ObjectType objectType, Long objectId, Role... parents) {
        return new Role().setRoleField(roleField)
                .setObjectType(objectType)
                .setObjectId(objectId)
                .addParents(parents)
                .updateImplicitParents()
                .updateAncestors();
    }

    public Organization addOrganization(String displayName, String description, User user) {

        return roleRepository.findByRoleField(SYSTEM_ADMINISTRATOR).map(
                systemAdministrator -> roleRepository.findByRoleField(SYSTEM_AUDITOR).map(
                        systemAuditor -> {
                            if (user.getGrantedRoles().stream().anyMatch(role -> role.getDescentdents().contains(systemAdministrator))) {
                                Organization organization = organizationRepository.save(
                                        new Organization().setDisplayName(displayName)
                                                .setDescription(description)
                                                .setCreatedByUser(user)
                                                .setModifiedByUser(user));

                                Role orgAdminRole = roleRepository.save(
                                        _newRole(Organization.ADMIN_ROLE, organizationObjectType, organization.getOrganizationId(),
                                                systemAdministrator));
                                organization.setAdminRole(orgAdminRole);
                                log.debug("Role orgAdminRole: " + orgAdminRole);

                                Role orgAuditorRole = roleRepository.save(
                                        _newRole(Organization.AUDITOR_ROLE, organizationObjectType, organization.getOrganizationId(),
                                                systemAuditor));
                                organization.setAuditorRole(orgAuditorRole);
                                log.debug("Role orgAuditorRole: " + orgAuditorRole);

                                Role orgMemberRole = roleRepository.save(
                                        _newRole(Organization.MEMBER_ROLE, organizationObjectType, organization.getOrganizationId(),
                                                orgAdminRole));
                                organization.setMemberRole(orgMemberRole);
                                log.debug("Role orgMemberRole: " + orgMemberRole);

                                Role orgRepoAdminRole = roleRepository.save(
                                        _newRole(Organization.REPO_ADMIN_ROLE, organizationObjectType, organization.getOrganizationId(),
                                                orgAdminRole));
                                organization.setRepoAdminRole(orgRepoAdminRole);
                                log.debug("Role orgMemberRole: " + orgRepoAdminRole);

                                Role orgProjectAdminRole = roleRepository.save(
                                        _newRole(Organization.PROJECT_ADMIN_ROLE, organizationObjectType, organization.getOrganizationId(),
                                                orgAdminRole));
                                organization.setProjectAdminRole(orgProjectAdminRole);
                                log.debug("Role orgMemberRole: " + orgProjectAdminRole);

                                Role orgReadRole = roleRepository.save(
                                        _newRole(Organization.READ_ROLE, organizationObjectType, organization.getOrganizationId(),
                                                orgMemberRole, orgAuditorRole, orgAdminRole, orgRepoAdminRole, orgProjectAdminRole));
                                organization.setReadRole(orgReadRole);
                                log.debug("Role orgReadRole: " + orgReadRole);

                                return organization;
                            } else {
                                throw new RuntimeException("no authority");
                            }

                        }).orElseThrow(() -> new RuntimeException("no " + SYSTEM_AUDITOR)))
                .orElseThrow(() -> new RuntimeException("aa"));
    }

    public void deleteOrganizationById(Long organizationId) {
        organizationRepository.findById(organizationId).ifPresent(this::deleteOrganization);
    }

    public void deleteOrganization(Organization organization) {
        organization.getTeams().forEach(this::deleteTeam);
        deleteRole(organization.getAdminRole());
        deleteRole(organization.getAuditorRole());
        deleteRole(organization.getMemberRole());
        deleteRole(organization.getReadRole());
        deleteRole(organization.getProjectAdminRole());
        organizationRepository.delete(organization);
    }

    public void deleteTeam(Team team) {
        deleteRole(team.getReadRole());
        deleteRole(team.getMemberRole());
        deleteRole(team.getAdminRole());
        teamRepository.delete(team.clear());
    }

    public void deleteTeamById(Long teamId) {
        teamRepository.findById(teamId).ifPresent(this::deleteTeam);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void deleteUserById(Long userId) {
        userRepository.findById(userId).ifPresent(this::deleteUser);
    }

    private void deleteRole(Role role) {
        log.debug("Deleted role: " + role);
        roleRepository.delete(role.clear());
    }

    public void deleteRoleById(Long roleId) {
        log.debug("Deleted roleId: " + roleId);
        roleRepository.findById(roleId).ifPresent(this::deleteRole);
    }

    private void addObjectTypeAndPermission() {
        userObjectType = objectTypeRepository.save(
                new ObjectType("auth", "user")
                        .addPermission(new Permission("Can add user", "add_user"))
                        .addPermission(new Permission("Can delete user", "delete_user"))
                        .addPermission(new Permission("Can change user", "change_user")));
        roleObjectType = objectTypeRepository.save(
                new ObjectType("main", "role")
                        .addPermission(new Permission("Can add role", "add_role"))
                        .addPermission(new Permission("Can change role", "change_role"))
                        .addPermission(new Permission("Can delete role", "delete_role")));
        teamObjectType = objectTypeRepository.save(
                new ObjectType("main", "team")
                        .addPermission(new Permission("Can add team", "add_team"))
                        .addPermission(new Permission("Can change team", "change_team"))
                        .addPermission(new Permission("Can delete team", "delete_team")));
        organizationObjectType = objectTypeRepository.save(
                new ObjectType("main", "organization")
                        .addPermission(new Permission("Can add organization", "add_organization"))
                        .addPermission(new Permission("Can change organization", "change_organization"))
                        .addPermission(new Permission("Can delete organization", "delete_organization")));
        permissionObjectType = objectTypeRepository.save(
                new ObjectType("auth", "permission")
                        .addPermission(new Permission("Can change permission", "change_permission"))
                        .addPermission(new Permission("Can delete permission", "delete_permission"))
                        .addPermission(new Permission("Can add permission", "add_permission")));
        projectObjectType = objectTypeRepository.save(
                new ObjectType("main", "project")
                        .addPermission(new Permission("Can add project", "add_project"))
                        .addPermission(new Permission("Can change project", "change_project"))
                        .addPermission(new Permission("Can delete project", "delete_project")));
    }

    private void addSystemRole() {
        systemAdministrator = roleRepository.save(new Role().setRoleField(SYSTEM_ADMINISTRATOR)
                .setSingletonName(SYSTEM_ADMINISTRATOR).updateAncestors());
        log.debug("Role systemAdministrator: " + systemAdministrator);

        systemAuditor = roleRepository.save(new Role().setRoleField(SYSTEM_AUDITOR)
                .setSingletonName(SYSTEM_AUDITOR).updateAncestors());
        log.debug("Role systemAuditor: " + systemAuditor);
    }

    private void addAdminUser() {
        admin = userRepository.save(new User().setUsername("admin").addRole(systemAdministrator));
        log.info("User admin: " + admin);
    }

    public Team addTeam(String name, Long organizationId, Long userId) {
        return organizationRepository.findById(organizationId)
                .map(organization -> userRepository.findById(userId)
                        .map(user -> addTeam(name, organization, user)).orElse(null))
                .orElse(null);
    }

    private Team addTeam(String name, Organization organization, User user) {
        //  判断用户是否具备创建Team的权限
        if (user.getGrantedRoles().stream()
                .anyMatch(role -> role.getDescentdents().stream()
                        .filter(descentdent -> descentdent.getObjectType().equals(organizationObjectType)
                                && descentdent.getObjectId().equals(organization.getOrganizationId()))
                        .anyMatch(orgDescentdent -> organization.getMemberRole().equals(orgDescentdent)))) {
            log.debug("User {} has authority to create team {} on organization {}", user.getUserId(), name, organization.getDisplayName());

            Team team = teamRepository.save(
                    new Team().setName(name)
                            .setOrganization(organization)
                            .setCreatedBy(user)
                            .setModifiedBy(user));
            Role teamAdminRole = roleRepository.save(
                    new Role().setRoleField(Team.ADMIN_ROLE)
                            .setObjectType(teamObjectType)
                            .setObjectId(team.getTeamId())
                            .addParent(systemAdministrator)
                            .addParent(organization.getMemberRole())
                            .updateImplicitParents()
                            .updateAncestors());
            team.setAdminRole(teamAdminRole);
            log.debug("Role teamAdminRole: " + teamAdminRole);

            Role teamMemberRole = roleRepository.save(
                    new Role().setRoleField(Team.MEMBER_ROLE)
                            .setObjectType(teamObjectType)
                            .setObjectId(team.getTeamId())
                            .addParent(teamAdminRole)
                            .updateImplicitParents()
                            .updateAncestors());
            team.setMemberRole(teamMemberRole);
            log.debug("Role teamMemberRole: " + teamMemberRole);

            Role teamReadRole = roleRepository.save(
                    new Role().setRoleField(Team.READ_ROLE)
                            .setObjectType(teamObjectType)
                            .setObjectId(team.getTeamId())
                            .addParent(teamMemberRole)
                            .updateImplicitParents()
                            .updateAncestors());
            team.setReadRole(teamReadRole);
            log.debug("Role teamReadRole: " + teamReadRole);

            // 创建者具有 AdminRole
            user.addRole(teamAdminRole);
            return team;
        } else {
            throw new RuntimeException("no authority");
        }
    }

    public User addUser(String username, Long organizationId) {
        return organizationRepository.findById(organizationId).map(organization -> addUser(username, organization)).orElse(null);
    }

    public User addUser(String username, Organization organization) {
        return userRepository.save(new User().setUsername(username).addOrganization(organization));
    }

    public void grantRole(Long userId, Long roleId) {
        roleRepository.findById(roleId).ifPresent(
                role -> userRepository.findById(userId).ifPresent(
                        user -> user.addRole(role)));
    }

}