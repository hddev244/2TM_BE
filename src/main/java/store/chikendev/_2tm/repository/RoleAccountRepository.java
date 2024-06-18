package store.chikendev._2tm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.RoleAccount;
import java.util.List;

@Repository
public interface RoleAccountRepository extends JpaRepository<RoleAccount, Long> {
    List<RoleAccount> findByAccount(Account account);
}
