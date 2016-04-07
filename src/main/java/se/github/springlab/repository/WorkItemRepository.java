package se.github.springlab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.github.springlab.model.Team;
import se.github.springlab.model.User;
import se.github.springlab.model.WorkItem;

public interface WorkItemRepository extends PagingAndSortingRepository<WorkItem, Long>
{
	List<WorkItem> findByAssignedUser(User assignedUser);

	List<WorkItem> findByAssignedUser_Id(Long id);

	List<WorkItem> findByItemStatus(int itemStatus);

	@Query("SELECT w FROM WorkItem w WHERE w.description LIKE %:desc%")
	List<WorkItem> findByDescription(@Param("desc") String description);

	@Query("SELECT w FROM WorkItem w WHERE w.topic LIKE %:topic%")
	List<WorkItem> findByTopic(@Param("topic") String topic);

	List<WorkItem> findByAssignedUser_Team(Team team);

	List<WorkItem> findByAssignedUser_Team_Id(Long id);

}
