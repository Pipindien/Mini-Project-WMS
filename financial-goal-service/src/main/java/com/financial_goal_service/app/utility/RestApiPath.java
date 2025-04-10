package com.financial_goal_service.app.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestApiPath {
    public static final String FINANCIALGOAL_API_PATH = "/financial-goal";
    public static final String FINANCIALGOAL_SAVE = "/save";
    public static final String FINANCIALGOAL_UPDATE = "/update/{goalId}";
    public static final String FINANCIALGOAL_ARCHIVE = "/archive/{goalId}";

}
