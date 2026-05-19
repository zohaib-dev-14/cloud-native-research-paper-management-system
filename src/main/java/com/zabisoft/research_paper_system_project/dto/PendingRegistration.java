package com.zabisoft.research_paper_system_project.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zabisoft.research_paper_system_project.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PendingRegistration implements Serializable {

    private String name;

    private String email;

    private String password;

    private Role role;
}