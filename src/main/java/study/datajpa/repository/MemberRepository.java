package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

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

    //페이징 처리
    //Page로 받을때는 기본적으로 totalCount를 제공하는데 totalCount 쿼리가 필요없는 경우(성능 이슈) @Query를 사용해 조회 쿼리와 count쿼리 분리
    //-> 실제 쿼리와 count쿼리 분리 : countQuery를 사용하지 않으면 원래 쿼리의 count를 가져오는데 join이 많을 경우 성능 최적화가 안된다
    //-> 그런 경우를 대비해 countQuery를 분리할 수 있게 돼있다(실무에서 매우 중요)
//    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    //슬라이스
    Slice<Member> findSliceByAge(int age, Pageable pageable);
    //-> 페이징 or 슬라이스 대신 List로 받아도 되지만 페이징 관련된 기능은 사용하지못한다(sort 기능만 가능)


    @Modifying(clearAutomatically = true) //excuteUpdate 실행을 위한 어노테이션, clearAutomatically = true : 벌크연산 후 영속성 컨텍스트를 자동으로 클리어해주는 옵션
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //---------------------------------------------------------
    //패치조인
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    //->
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //이렇게 fetch join을 사용할 수도 있다
    @EntityGraph(attributePaths = {"team"}) //엄청 복잡한 쿼리는 JPQL을 쓰지만 간단한건 EntityGraph 사용한다.
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //잘 쓰진 않는다.(Member Entity에 설정)
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //-------------------------------------------------------------------

    //성능최적화를 위해서 쓰는거지만 성능테스트 후 정말 필요한 경우에만 쓰는 것이 좋다
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

}
