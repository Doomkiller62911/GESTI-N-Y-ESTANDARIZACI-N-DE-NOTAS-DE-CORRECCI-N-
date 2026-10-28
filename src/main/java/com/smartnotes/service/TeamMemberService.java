package com.smartnotes.service;

import com.smartnotes.domain.TeamMember;
import com.smartnotes.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public List<TeamMember> findAllActive() {
        return teamMemberRepository.findByActivoTrueOrderByNombreAsc();
    }

    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Miembro de equipo no encontrado: " + id));
    }
}
