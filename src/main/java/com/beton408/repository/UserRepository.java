package com.beton408.repository;

import com.beton408.entity.FaqEntity;
import com.beton408.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);

    List<UserEntity> findByRole(String role);
    Boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable paging);
}
