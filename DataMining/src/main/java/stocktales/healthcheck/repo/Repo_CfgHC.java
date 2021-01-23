package stocktales.healthcheck.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stocktales.healthcheck.model.entity.Cfg_ScripHealthCheck;

@Repository
public interface Repo_CfgHC extends JpaRepository<Cfg_ScripHealthCheck, Integer>
{
	
}
