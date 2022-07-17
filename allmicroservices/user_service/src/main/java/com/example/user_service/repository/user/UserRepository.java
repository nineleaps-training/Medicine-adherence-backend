package com.example.user_service.repository.user;

import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserMailDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

    @Query("select u from UserEntity u where lower(u.userName) like lower(concat(?1,'%'))")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "userDetail_graph")
    List<UserEntity> findByNameIgnoreCase(String userName);

    @Query("select new com.example.user_service.pojos.dto.user.UserMailDto(u.userName,u.email,d.picPath) from UserEntity u INNER JOIN u.userDetails  d where lower(u.email) like lower(?1)")
    UserMailDto searchByMail(String email);

    @Query("SELECT u from UserEntity u where u.userId = ?1")
    UserEntity getUserById(String userId);

    @Query("select u from UserEntity u where lower(u.email) like lower(?1)")
    UserEntity findByMail(String email);

    @Query("SELECT user from UserEntity user")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,value = "userDetail_graph")
    List<UserEntity> findAllUsers(Pageable pageable);

}