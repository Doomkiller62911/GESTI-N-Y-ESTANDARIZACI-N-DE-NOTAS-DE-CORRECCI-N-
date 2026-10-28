package com.smartnotes.repository;

import com.smartnotes.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByActivoTrueOrderByNombreAsc();
}
