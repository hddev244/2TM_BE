package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.account.id = ?1")
    List<Address> getAddressByAccountId(String id);
}
