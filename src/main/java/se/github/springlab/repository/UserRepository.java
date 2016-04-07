package se.github.springlab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.github.springlab.model.Team;
import se.github.springlab.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>
{
	@Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
	List<User> findByUsername(@Param("username") String username);

	@Query("SELECT u FROM User u WHERE u.firstName LIKE %:firstName%")
	List<User> findByFirstName(@Param("firstName") String firstName);

	@Query("SELECT u FROM User u WHERE u.lastName LIKE %:lastName%")
	List<User> findByLastName(@Param("lastName") String lastName);

	@Query("SELECT u FROM #{#entityName} u WHERE u.userNumber = :userNumber")
	User findByUserNumber(@Param("userNumber") String userNumber);

	List<User> findByTeam(Team team);

	@Query(value = "SELECT * FROM User u WHERE u.team_id = :id", nativeQuery = true)
	List<User> findByTeamId(@Param("id") Long id);
}
