/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartnotes.service;

import com.smartnotes.domain.TeamMember;
import com.smartnotes.repository.TeamMemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public List<TeamMember> findAll() {
        return teamMemberRepository.findAll();
    }

    public List<TeamMember> findAllActive() {
        return teamMemberRepository.findByActivoTrue();
    }

    public TeamMember findById(Integer id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Miembro del equipo no encontrado: " + id));
    }
}
