package com.zabisoft.research_paper_system_project.interfaces;

import com.zabisoft.research_paper_system_project.entities.User;

public interface AuthenticatedUserService {
    User getCurrentUser();
}
