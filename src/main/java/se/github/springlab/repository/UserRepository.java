package se.github.springlab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.github.springlab.model.Team;
import se.github.springlab.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>
{
	List<User> findByUsernameLike(String username);

	List<User> findByFirstNameLike(String firstName);

	List<User> findByLastNameLike(String lastName);

	@Query("SELECT u FROM #{#entityName} u WHERE u.userNumber = ?1")
	User findByUserNumber(String userNumber);

	List<User> findByTeam(Team team);

	@Query(value = "SELECT * FROM User u WHERE u.team_id = :id", nativeQuery = true)
	List<User> findByTeamId(@Param("id") Long id);
}
