package com.projects.taxiservice.model.queries;

/**
 * This class represents all statuses that Query object can have.
 * The flow can go ACTIVE -> ACCEPTED -> EXECUTING -> FINISHED   | driver's standard flow
 *                 ACTIVE -> ACCEPTED -> DISCARDED               | driver's invalid flow
 *                 ACTIVE -> CANCELLED                           | user's flow
 */
public enum QueryStatus {
    ACTIVE,
    ACCEPTED,
    DISCARDED,
    EXECUTING,
    CANCELLED,
    FINISHED;

    /**
     * @param query the current <code>UserQuery</code>
     * @return query position according to first (standard) flow.
     */
    public static int getUserQueryWeight(UserQuery query){
        switch(query.getStatus().toString()){
            case "ACTIVE": return 1;
            case "ACCEPTED": return 2;
            case "EXECUTING": return 3;
            default: return 0;
        }
    }
}
