package com.example.SpringNe.repository;

import com.example.SpringNe.entity.Permisstion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permisstion, String> {
    Permisstion findByName(String permissionName);
}
