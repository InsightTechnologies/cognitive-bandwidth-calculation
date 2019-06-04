package com.miracle.bandwidth.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miracle.bandwidth.utility.BandwidthUtility;
import com.miracle.common.response.FeatureResponse;
import com.miracle.database.bean.Release;
import com.miracle.database.bean.Resource;
import com.miracle.database.bean.Sprint;
import com.miracle.database.bean.Team;
import com.miracle.utility.DataUtility;

@RestController
public class BandwidthController {

	@Autowired
	private DataUtility dataUtility;
	@Autowired
	private BandwidthUtility bandwidthUtility;

	public static void main(String[] args) throws ParseException {
		Release release = new Release();
		String date1 = "2019-05-31T12:00:00.000";
		Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(date1);
		release.setStartDate(startDate);

		String date2 = "2019-06-10T12:00:00.000";
		Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(date2);
		release.setEndDate(endDate);
		release.setNoOfSprints(4);

		boolean isEndDate = false;
		List<SprintTest> releaseTest = new ArrayList<>();
		for (int i = 0; i < release.getNoOfSprints(); i++) {

			LocalDateTime localDateTime = release.getStartDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			localDateTime = localDateTime.plusDays(3);
			Date sprintEndDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

			if (release.getEndDate().compareTo(sprintEndDate) < 0) {
				sprintEndDate = release.getEndDate();
				isEndDate = true;
			}
			SprintTest release1 = new SprintTest();
			release1.setStartDate(release.getStartDate());
			release1.setEndDate(sprintEndDate);
			int days = (int) ChronoUnit.DAYS.between(release.getStartDate().toInstant(), sprintEndDate.toInstant());
			release1.setDuration(days);
			release1.setName("Sprint:" + i);
			releaseTest.add(release1);

			if (isEndDate) {
				break;
			}
			release.setStartDate(sprintEndDate);
		}

		List<Resource> resourceList = new ArrayList<Resource>();
		Resource resource = new Resource();
		resource.setResourceName("mani");
		String l1 = "2019-06-02T12:00:00.000";
		Date ld1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(l1);
		resource.setStartDate(ld1);

		String l2 = "2019-06-04T12:00:00.000";
		Date ld2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(l2);
		resource.setEndDate(ld2);
		resource.setTeamName("test");
		Resource resource1 = new Resource();
		resource.setResourceName("mani");
		String l3 = "2019-06-14T12:00:00.000";
		Date ld3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(l3);
		resource1.setStartDate(ld3);
		resource1.setTeamName("test");
		String l4 = "2019-06-15T12:00:00.000";
		Date ld4 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(l4);
		resource1.setResourceName("mani");
		resource1.setEndDate(ld4);
		resourceList.add(resource);
		resourceList.add(resource1);

	}

	@GetMapping(value = "/bandwidth")
	public ResponseEntity<FeatureResponse> buildFeatures(@RequestParam(value = "version") double version)
			throws ParseException {
		FeatureResponse response = new FeatureResponse();
		try {
			Release release = dataUtility.loadRelease(version);
			List<Sprint> sprintList = dataUtility.getSprintList();
			int duration = sprintList.get(0).getDuration();

			List<SprintTest> sprintDetails = bandwidthUtility.retrieveSprintDetails(release, duration);

			Team team = dataUtility.loadTeam(release.getTeamName());
			List<Resource> resourceList = dataUtility.getResourceList();
			Map<String, Integer> resourceDataMap = bandwidthUtility.getResourceLeaves(team.getTeamName(), resourceList,
					sprintDetails);

			Map<String, Double> velocityMap = bandwidthUtility.calcVelocity(team, resourceDataMap);
			response.setObject(velocityMap);
			response.setSuccess(true);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.OK);
		} catch (Exception exception) {
			exception.printStackTrace();
			response.setObject("Failed to build Backlog features");
			response.setSuccess(false);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_GATEWAY);
		}
	}

}
