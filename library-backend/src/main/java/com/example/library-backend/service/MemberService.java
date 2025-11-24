package com.example.library-backend.service;

import com.example.library-backend.entity.Member;
import com.example.library-backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MemberService {
    @Autowired private MemberRepository memberRepository;

    public List<Member> findAll() { return memberRepository.findByActiveTrue(); }
    public Optional<Member> findById(Long id) { return memberRepository.findByIdAndActiveTrue(id); }
    public Member save(Member member) { if (member.getMembershipId() == null) member.setMembershipId(generateMembershipId()); return memberRepository.save(member); }
    public void deleteById(Long id) { memberRepository.findById(id).ifPresent(member -> { member.setActive(false); memberRepository.save(member); }); }
    public List<Member> searchMembers(String keyword) { return memberRepository.searchMembers(keyword); }
    public Optional<Member> findByEmail(String email) { return memberRepository.findByEmail(email); }
    public Optional<Member> findByMembershipId(String membershipId) { return memberRepository.findByMembershipId(membershipId); }
    public Long getTotalMemberCount() { Long count = memberRepository.countActiveMembers(); return count != null ? count : 0L; }
    private String generateMembershipId() { return "MEM" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); }
    public Member updateMember(Long id, Member memberDetails) {
        Optional<Member> memberOpt = memberRepository.findById(id); if (memberOpt.isPresent()) {
            Member member = memberOpt.get(); member.setFirstName(memberDetails.getFirstName()); member.setLastName(memberDetails.getLastName()); member.setEmail(memberDetails.getEmail());
            member.setPhone(memberDetails.getPhone()); member.setAddress(memberDetails.getAddress()); member.setDateOfBirth(memberDetails.getDateOfBirth()); return memberRepository.save(member);
        } throw new IllegalArgumentException("Member not found with id: " + id);
    }
}