package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingRegisteration {

    private String name;

    private String email;

    private String password;

    private Role role;
}