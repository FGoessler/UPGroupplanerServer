package de.unipotsdam.cs.groupplaner.user.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.unipotsdam.cs.groupplaner.auth.SecurityContextFacade;
import de.unipotsdam.cs.groupplaner.config.PathConfig;
import de.unipotsdam.cs.groupplaner.domain.BlockedDate;
import de.unipotsdam.cs.groupplaner.user.dao.BlockedDatesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConfig.BLOCKED_DATES_RESOURCE_PATH)
public class BlockedDatesResource {

	@Autowired
	private BlockedDatesDAO blockedDatesDAO;
	@Autowired
	private SecurityContextFacade securityContextFacade;

	@RequestMapping(method = RequestMethod.GET)
	public List<BlockedDate> getAllBlockedDates(@RequestParam("source") final String sourceFilter) {
		final ImmutableList<BlockedDate> blockedDates;
		if (sourceFilter == null) {
			blockedDates = blockedDatesDAO.getBlockedDates(securityContextFacade.getCurrentUserEmail());
		} else {
			blockedDates = blockedDatesDAO.getBlockedDates(securityContextFacade.getCurrentUserEmail(), sourceFilter);
		}

		return blockedDates;
	}

	// TODO: handle overlapping dates

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public BlockedDate createBlockedDate(@RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));
		Preconditions.checkNotNull(data.get("source"));

		BlockedDate newBlockedDate = new BlockedDate((Integer) data.get("start"), (Integer) data.get("end"), securityContextFacade.getCurrentUserEmail(), (String) data.get("source"));

		return blockedDatesDAO.getBlockedDate(blockedDatesDAO.createBlockedDate(newBlockedDate));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BlockedDate getBlockedDate(@PathVariable("id") final Integer id) {
		return checkAndGetBlockedDate(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public BlockedDate updateBlockedDate(@PathVariable("id") final Integer id, @RequestBody final Map<String, Object> data) {
		Preconditions.checkNotNull(data.get("start"));
		Preconditions.checkNotNull(data.get("end"));
		Preconditions.checkNotNull(data.get("source"));

		checkAndGetBlockedDate(id);

		BlockedDate modifiedBlockedDate = new BlockedDate(id, (Integer) data.get("start"), (Integer) data.get("end"), securityContextFacade.getCurrentUserEmail(), (String) data.get("source"));
		final Boolean updateSuccessful = blockedDatesDAO.updateBlockedDate(modifiedBlockedDate);
		if (!updateSuccessful) {
			throw new EmptyResultDataAccessException(1);
		}
		modifiedBlockedDate = blockedDatesDAO.getBlockedDate(modifiedBlockedDate.getId());

		return modifiedBlockedDate;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBlockedDate(@PathVariable("id") final Integer id) {
		checkAndGetBlockedDate(id);

		blockedDatesDAO.deleteBlockedDate(id);
	}

	private BlockedDate checkAndGetBlockedDate(final Integer id) {
		final BlockedDate blockedDate = blockedDatesDAO.getBlockedDate(id);

		if (!blockedDate.getUserEmail().equals(securityContextFacade.getCurrentUserEmail())) {
			throw new AccessDeniedException("This date does not belong to the specified user.");
		}

		return blockedDate;
	}
}
