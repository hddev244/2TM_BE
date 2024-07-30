package store.chikendev._2tm.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.RoleAccount;

@Repository
public interface RoleAccountRepository
    extends JpaRepository<RoleAccount, Long> {
    List<RoleAccount> findByAccount(Account account);

    List<RoleAccount> findByRole(Role role);

    @Query(
        "SELECT r.account FROM RoleAccount r WHERE r.role.id NOT IN (:excludedRoles) AND r.account.email NOT LIKE :email  GROUP BY r.account"
    )
    Page<Account> findByRoleStaff(
        @Param("excludedRoles") List<String> excludedRoles,
        @Param("email") String emailIgnore,
        Pageable pageable
    );
}
