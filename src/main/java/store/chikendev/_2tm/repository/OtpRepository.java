package store.chikendev._2tm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Otp;
import java.util.List;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
    List<Otp> findByAccount(Account account);
}
