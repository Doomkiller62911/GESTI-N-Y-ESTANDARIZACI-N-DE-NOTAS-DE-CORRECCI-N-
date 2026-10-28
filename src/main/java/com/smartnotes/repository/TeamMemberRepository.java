package com.smartnotes.repository;

import com.smartnotes.domain.TeamMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    public List<TeamMember> findByActivoTrue();

}
