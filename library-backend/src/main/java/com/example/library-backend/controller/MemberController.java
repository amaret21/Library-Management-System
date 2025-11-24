package com.example.library-backend.controller;

import com.example.library-backend.dto.ApiResponse;
import com.example.library-backend.entity.Member;
import com.example.library-backend.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {
    @Autowired private MemberService memberService;

    @GetMapping public ResponseEntity<?> getAllMembers(@RequestParam(required = false) String search) {
        try { List<Member> members; 
            if (search != null && !search.isBlank()) members = memberService.searchMembers(search);
            else members = memberService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", members));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve members: " + e.getMessage())); }
    }

    @GetMapping("/{id}") public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        try { Member member = memberService.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id)); return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", member));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve member: " + e.getMessage())); }
    }

    @PostMapping public ResponseEntity<?> createMember(@RequestBody Member member) {
        try { Member savedMember = memberService.save(member); return ResponseEntity.ok(ApiResponse.success("Member created successfully", savedMember));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create member: " + e.getMessage())); }
    }

    @PutMapping("/{id}") public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody Member member) {
        try { Member updatedMember = memberService.updateMember(id, member); return ResponseEntity.ok(ApiResponse.success("Member updated successfully", updatedMember));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update member: " + e.getMessage())); }
    }

    @DeleteMapping("/{id}") public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try { memberService.deleteById(id); return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete member: " + e.getMessage())); }
    }

    @GetMapping("/search") public ResponseEntity<?> searchMembers(@RequestParam String keyword) {
        try { List<Member> members = memberService.searchMembers(keyword); return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", members));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to search members: " + e.getMessage())); }
    }

    @GetMapping("/email/{email}") public ResponseEntity<?> getMemberByEmail(@PathVariable String email) {
        try { Member member = memberService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email)); return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", member));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve member: " + e.getMessage())); }
    }

    @GetMapping("/membership/{membershipId}") public ResponseEntity<?> getMemberByMembershipId(@PathVariable String membershipId) {
        try { Member member = memberService.findByMembershipId(membershipId).orElseThrow(() -> new IllegalArgumentException("Member not found with membership ID: " + membershipId)); return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", member));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve member: " + e.getMessage())); }
    }

    @GetMapping("/count") public ResponseEntity<?> getMemberCount() {
        try { Long count = memberService.getTotalMemberCount(); return ResponseEntity.ok(ApiResponse.success("Member count retrieved successfully", count));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve member count: " + e.getMessage())); }
    }
}