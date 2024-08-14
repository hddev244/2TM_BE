package store.chikendev._2tm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Address;
import store.chikendev._2tm.entity.ProvinceCity;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.account.id = ?1")
    List<Address> getAddressByAccountId(String id);
    List<Address> findByAccount(Account account);
    Page<Address> findByAccount(Account account, Pageable pageable);

    void deleteByIdAndAccount(Long addressId, Account account);
    Optional<Address> findAllByIdAndAccount(Long id, Account account);
    Optional<Address> findByAccountAndId(Account account, Long addressId);
    void deleteByAccountAndId(Account account, Long addressId);

}
