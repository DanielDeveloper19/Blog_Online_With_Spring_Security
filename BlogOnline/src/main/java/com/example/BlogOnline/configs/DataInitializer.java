package com.example.BlogOnline.configs;

import com.example.BlogOnline.Model.Permission;
import com.example.BlogOnline.Model.Role;
import com.example.BlogOnline.Model.UserSec;
import com.example.BlogOnline.Repository.PermissionRepo;
import com.example.BlogOnline.Repository.RoleRepository;
import com.example.BlogOnline.Repository.UserSecRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Component
public class DataInitializer implements ApplicationRunner { //Completado

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepo permissionRepository;

    @Autowired
    private UserSecRepo userSecRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 1. Crear permisos
            createPermissionIfNotExists("READ");
            createPermissionIfNotExists("UPDATE");
            createPermissionIfNotExists("DELETE");
            createPermissionIfNotExists("CREATE");

            // 2. Crear roles
            createRoleIfNotExists("ADMIN");
            createRoleIfNotExists("USER");

            // 3. Crear usuario admin
            createUserSecIfNotExists("ADMINISTRADOR");

            System.out.println("Inicialización de datos completada");
        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    public void createPermissionIfNotExists(String name) {
        if (!permissionRepository.existsByName(name)) {
            Permission p = new Permission();
            p.setName(name);
            permissionRepository.save(p);

            System.out.println("creando permisos " + name);
            System.out.println("creando permisos " + name);
            System.out.println("creando permisos " + name);
            System.out.println("creando permisos " + name);

        }
    }



    public void createRoleIfNotExists(String name) {
        if (!roleRepository.existsByName(name)) {
            Role r = new Role();
            r.setName(name);
            Set<Permission> roles = new HashSet<>();

            if (name.equals("ADMIN")) { roles.add(permissionRepository.findByName("READ") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));

                roles.add(permissionRepository.findByName("UPDATE") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));

                roles.add(permissionRepository.findByName("DELETE") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));

                roles.add(permissionRepository.findByName("CREATE") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));

                System.out.println("creando role Admin");
            }

            if(name.equals("USER")){
                roles.add(permissionRepository.findByName("UPDATE") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));

                roles.add(permissionRepository.findByName("DELETE") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));

                roles.add(permissionRepository.findByName("CREATE") .orElseThrow(() -> new RuntimeException("Permiso  no encontrado al arrancar")));
            }




            r.setPermissions(roles);

            roleRepository.save(r);
        }
    }


    public void createUserSecIfNotExists(String name) {

        if(!userSecRepo.findByUsername(name).isPresent()){
            UserSec u = new UserSec();
            u.setUsername(name);
            u.setPassword( passwordEncoder.encode("123456"));

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("ROLE  no encontrado al arrancar")));

            u.setRoles(roles);
            userSecRepo.save(u);

            System.out.println("UserSec Administrador creado");
        }

    }


}
