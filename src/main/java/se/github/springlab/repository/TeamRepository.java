package se.github.springlab.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import se.github.springlab.model.Team;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long>
{

}
