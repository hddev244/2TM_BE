package store.chikendev._2tm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Store;

import java.util.List;

@Repository
public interface AccountStoreRepository extends JpaRepository<AccountStore, Long> {
    Optional<AccountStore> findByAccount(Account account);

    List<AccountStore> findByStore(Store store);
}
