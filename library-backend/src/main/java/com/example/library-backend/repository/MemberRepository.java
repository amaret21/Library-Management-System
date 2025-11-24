package com.example.library-backend.repository;

import com.example.library-backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByMembershipId(String membershipId);
    @Query("SELECT m FROM Member m WHERE m.active = true") List<Member> findByActiveTrue();
    @Query("SELECT m FROM Member m WHERE (LOWER(m.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.membershipId) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND m.active = true")
    List<Member> searchMembers(@Param("keyword") String keyword);
    @Query("SELECT COUNT(m) FROM Member m WHERE m.active = true") Long countActiveMembers();
    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.active = true") Optional<Member> findByIdAndActiveTrue(@Param("id") Long id);
}