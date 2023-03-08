package com.beton408.security;

import com.beton408.entity.UserEntity;
import com.beton408.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    UserRepository repository;
//    @Autowired
//    RoleRepository roleRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = getByUsername(username);
        return UserDetailsImpl.build(user);
    }
    public UserEntity get(Long id){
        return repository.findById(id).orElse(new UserEntity());
    }
    public UserEntity getByUsername(String username){
        return repository.findByUsername(username);
    }
    public UserEntity getByEmail(String email){
        return repository.findByEmail(email);
    }
    public void addUser(UserEntity user){
        save(user);
    }
    public boolean isUsernameExist(String username){
        return repository.existsByUsername(username);
    }
    UserEntity save(UserEntity user){
        return repository.save(user);
    }
    public boolean isEmailExist(String email) {
        return repository.existsByEmail(email);
    }
    public UserEntity getUserById(Long id) {
        Optional<UserEntity> user = repository.findById(id);
        return user.orElse(null);
    }

}
