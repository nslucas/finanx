package com.example.prospera.repositories;

import com.example.prospera.Entities.ConnectionStatus;
import com.example.prospera.Entities.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserConnectionRepository extends JpaRepository<UserConnection, Integer> {
    List<UserConnection> findByTargetUserIdAndStatusOrderByRequestedAtAsc(Integer targetUserId,
                                                                           ConnectionStatus status);

    @Query("SELECT c FROM UserConnection c WHERE " +
            "(c.requesterUserId = :userId OR c.targetUserId = :userId) AND c.status = :status " +
            "ORDER BY c.requestedAt ASC")
    List<UserConnection> findForUserByStatus(@Param("userId") Integer userId,
                                             @Param("status") ConnectionStatus status);

    @Query("SELECT c FROM UserConnection c WHERE " +
            "((c.requesterUserId = :firstUserId AND c.targetUserId = :secondUserId) " +
            "OR (c.requesterUserId = :secondUserId AND c.targetUserId = :firstUserId)) " +
            "AND c.status IN :statuses")
    List<UserConnection> findBetweenUsersWithStatuses(@Param("firstUserId") Integer firstUserId,
                                                      @Param("secondUserId") Integer secondUserId,
                                                      @Param("statuses") Collection<ConnectionStatus> statuses);

    @Query("SELECT c FROM UserConnection c WHERE c.id = :id " +
            "AND (c.requesterUserId = :userId OR c.targetUserId = :userId)")
    Optional<UserConnection> findByIdForUser(@Param("id") Integer id, @Param("userId") Integer userId);
}
