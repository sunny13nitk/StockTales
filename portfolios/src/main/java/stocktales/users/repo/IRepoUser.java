package stocktales.users.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.users.model.entity.UserPF;

@Repository
public interface IRepoUser extends JpaRepository<UserPF, Long>
{
	public UserPF findByUname(
	        String uname
	);
	
}
