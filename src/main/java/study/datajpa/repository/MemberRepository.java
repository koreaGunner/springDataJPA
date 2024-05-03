package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age") //이름이 없는 namedQuery라고 생각하면 된다(자주 쓰임)
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); //컬렉션 -> !!조회결과가 없더라고 null로 반환하지 않는다(empty collection으로 반환)
    Member findMemberByUsername(String username); //단건 -> !!조회결과가 없으면 null로 반환(JPA는 NoResultException이 뜬다. spring data JPA로 인해서 null 반환)
    Optional<Member> findOptionalByUsername(String username); //단건 Optional -> 단건 조회는 optional이 아니더라도 단건 조회가 아닐때 오류가 뜬다
}
