package com.example.maraon.entity.user;

import lombok.Getter;

@Getter
public enum UserRoleType {

    ADMIN("관리자"),
    USER("유저");

    private final String description;

    UserRoleType(String description) {
        this.description = description;
    }
}
