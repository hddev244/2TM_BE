package store.chikendev._2tm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.RoleAccount;
import java.util.List;

@Repository
public interface RoleAccountRepository extends JpaRepository<RoleAccount, Long> {
    List<RoleAccount> findByAccount(Account account);

    @Query("SELECT r.account FROM RoleAccount r WHERE r.role.id NOT IN (:excludedRoles)  GROUP BY r.account")
    Page<Account> findByRoleStaff(@Param("excludedRoles") List<String> excludedRoles, Pageable pageable);

}
