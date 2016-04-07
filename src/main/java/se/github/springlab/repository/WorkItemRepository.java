package se.github.springlab.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import se.github.springlab.model.Team;
import se.github.springlab.model.User;
import se.github.springlab.model.WorkItem;

public interface WorkItemRepository extends PagingAndSortingRepository<WorkItem, Long>
{
	List<WorkItem> findByAssignedUser(User assignedUser);

	List<WorkItem> findByAssignedUser(Long id);

	List<WorkItem> findByItemStatus(int itemStatus);

	List<WorkItem> findByDescriptionLike(String description);

	List<WorkItem> findByTopicLike(String topic);

	List<WorkItem> findByAssignedUser_Team(Team team);

	List<WorkItem> findByAssignedUser_Team(Long id);

}
