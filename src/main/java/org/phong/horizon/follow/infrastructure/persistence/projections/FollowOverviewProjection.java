package org.phong.horizon.follow.infrastructure.persistence.projections;

public interface FollowOverviewProjection {
    Boolean getIsMeFollowing();
    int getTotalFollowers();
    int getTotalFollowing();
}


