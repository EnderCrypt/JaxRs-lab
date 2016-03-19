package se.github.springlab.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import se.github.springlab.model.Issue;
import se.github.springlab.model.WorkItem;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long>
{
	List<Issue> findByWorkItem(WorkItem workItem);
}
