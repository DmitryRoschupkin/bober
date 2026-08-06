package me.dmitriy.bober.models;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
    SUDO,
    ADMIN,
    AUTHOR,
    USER;
}
